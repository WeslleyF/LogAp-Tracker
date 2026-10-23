# LogiTrack Pro

MVP do desafio técnico da LogAp: gestão de frota com **CRUD de Viagens**, cadastros de apoio (veículos, manutenções, usuários) e **dashboard com 5 métricas calculadas via SQL**.

## 1. Como rodar

Estado atual: banco e esqueleto da API. Pré-requisito: Docker.

```bash
cp .env.example .env
docker compose up -d db          # Postgres 16 em localhost:5432
cd backend && mvn verify         # testes com Testcontainers (precisa de Docker)
cd backend && mvn spring-boot:run   # API em http://localhost:8080 (Flyway migra e carrega o seed)
```

```bash
cd frontend && npm ci && npm run dev   # http://localhost:5173, com proxy de /api para localhost:8080
cd frontend && npm run build && npm run lint
```

O Swagger fica em http://localhost:8080/swagger-ui.html. Entre com `admin@logitrack.com` / `admin123`. O restante (compose completo com `api` e `web`) é descrito ao fim do projeto.

## 2. Decisões técnicas e arquitetura

Stack: Java 21 + Spring Boot 3 + PostgreSQL 16 + Flyway no back; React + TypeScript + Vite + Mantine no front; Docker Compose para subir tudo. O registro completo das decisões está em [docs/decisoes.md](docs/decisoes.md).

**Front:** SPA em React 18 + TypeScript + Vite. Mantine para a UI, TanStack Query + axios para dados, React Hook Form + Zod nos formulários e Recharts nos gráficos. O `axios` usa `baseURL=/api` e injeta o token; um 401 limpa a sessão e leva ao login. A sessão (token, nome, perfil) fica em memória e no `localStorage`, e o `ProtectedRoute` manda para `/login` quando não há sessão. Em dev o Vite faz proxy de `/api`, como o nginx em produção.

**CRUD genérico (front):** `CrudPage` recebe um objeto `CrudConfig` (colunas, campos, schema Zod, cliente REST de `createCrudApi`) e entrega tabela paginada, formulário em modal, confirmação de exclusão e notificações. As quatro telas de CRUD (`viagens`, `veiculos`, `manutencoes`, `usuarios`) são só um `*.config.ts` cada, em `frontend/src/features/`. O `EntityForm` mostra erros 400 por campo no próprio input (inclusive as regras entre campos da API, como `dataChegadaValida`) e o 409 de conflito de agenda vira notificação. A lista de viagens mostra "Em andamento" quando não há chegada efetiva; o menu de Usuários só aparece para ADMIN e a rota é protegida por perfil.

**Dashboard (front):** uma tela própria (`features/dashboard`) que consome `/api/dashboard/resumo` e mostra cinco cards: KM total com seletor de veículo, volume por categoria (barras, Recharts), ranking com destaque do 1º, cronograma com seletor de data inicial (padrão hoje) e projeção do mês com total e quebra por status. Cartões reutilizáveis em `components/dashboard`.

**Camadas (back):** controller → service → repository, pacotes por feature em `br.com.logitrack`. DTOs são `record`s e a entidade nunca sai do controller.

**Segurança:** Spring Security stateless com OAuth2 Resource Server. `POST /api/auth/login` confere e-mail e senha (BCrypt) e devolve um JWT HS256 de 8 h com id, nome e perfil. Todo `/api/**` exige `Authorization: Bearer <token>`, exceto login e Swagger. `/api/usuarios/**` é só ADMIN; ADMIN e OPERADOR usam o resto. 401 e 403 saem como `ProblemDetail`. O segredo vem de `JWT_SECRET` (mínimo 32 caracteres); sem a variável, vale um padrão que serve só para desenvolvimento local. Usuário inativo não loga, mas um token já emitido vale até expirar.

**CRUD genérico** (`shared/crud`): `CrudService<E, Req, Res>` (transações, 404, ganchos `validar` e `antesDeExcluir`), `CrudController<Req, Res>` (GET paginado, GET por id, POST 201, PUT, DELETE 204) e `CrudMapper` (mapeamento manual). Uma feature sem regra extra são só entidade, request/response, mapper, repository e duas classes de poucas linhas. Erros saem como `ProblemDetail` (RFC 7807) via `GlobalExceptionHandler`: 400 com erros por campo, 404, 409 para `NegocioException` e violação de integridade.

## 3. Mudanças no banco

O script original do desafio está em [scripts/](scripts/) e é preservado. As migrações Flyway (`backend/src/main/resources/db/migration/`) são **derivadas** dele; o Flyway não aponta para `scripts/`.

| Migração | O que faz | Por quê |
|---|---|---|
| `V1__script_original.sql` | DDL do script original, idêntico | fonte da verdade do schema |
| `V2__restricoes_e_indices.sql` | `veiculos.tipo` NOT NULL; `viagens.veiculo_id` NOT NULL; CHECK chegada > saída e km > 0; índice `viagens (veiculo_id, data_saida)`; `manutencoes.veiculo_id` e `custo_estimado` NOT NULL; CHECK custo ≥ 0, finalização ≥ início e status (`PENDENTE`, `EM_REALIZACAO`, `CONCLUIDA`, `REJEITADA`); índice `manutencoes (data_inicio)` | veículo sem tipo sumiria do volume por categoria; dados inconsistentes não entram; índices servem às somas por veículo, ao `LEAD` e aos filtros de data do cronograma e da projeção |
| `V3__usuarios.sql` | tabela `usuarios` (email único, perfil `ADMIN`/`OPERADOR`, `ativo`) | haverá login |
| `V4__seed.sql` | INSERTs originais (sem alteração) + viagens e manutenções com datas relativas a `current_date` + usuário admin e operador (hash BCrypt) | o dashboard sempre tem dados no mês atual e manutenções futuras, com todos os status e uma viagem em andamento; o seed relativo respeita as regras de conflito |

**Mantido de propósito:** origem, destino e `tipo_servico` seguem texto livre (a tela sugere valores já usados); o `ON DELETE CASCADE` permanece (excluir veículo apaga viagens e manutenções, e a tela avisa); não há coluna `ativo` em veículos.

Usuários de teste: `admin@logitrack.com` / `admin123` (ADMIN) e `operador@logitrack.com` / `operador123` (OPERADOR).

## 4. Regras de negócio

Registro completo em [docs/decisoes.md](docs/decisoes.md) (D08 a D13).

- **Chegada efetiva:** `data_chegada`; se nula, a `data_saida` da próxima viagem do mesmo veículo (`lead`). Sem nenhuma das duas, a viagem está **em andamento**. Na API, é calculada pelo banco (`@Formula` na entidade `Viagem`, sem N+1).
- **Viagem realizada:** tem chegada efetiva; só ela entra nas métricas. Fechada pela próxima e sem km conta no volume e soma zero km.
- **Conflito de agenda (409)**, verificado em SQL pelo `ViagemValidator`, nos arquivos `backend/src/main/resources/sql/viagem/`:
  - `conflito_viagem.sql`: inclui a viagem nova ou editada nas viagens do veículo, recalcula a chegada efetiva com `lead` e testa a sobreposição dos intervalos `[saída, chegada efetiva)`; mesma saída também conflita. Uma viagem aberta é encerrada pela próxima saída, então só bloqueia quem sai antes dela e termina depois da saída dela.
  - `conflito_manutencao.sql`: a viagem não pode se sobrepor a manutenção não REJEITADA do mesmo veículo (do início do dia de `data_inicio` ao fim do dia de `coalesce(data_finalizacao, data_inicio)`); viagem em andamento ocupa só o dia da saída.
  - A mensagem do erro diz com o que conflitou (placa, rota e datas).
- **REJEITADA** não bloqueia viagem e fica fora da projeção financeira.

## 5. Métricas do dashboard

Cada métrica é uma consulta SQL nativa comentada em `backend/src/main/resources/sql/dashboard/`, executada pelo `DashboardRepository` com `JdbcClient` (sem JPQL e sem cálculo em Java do que o SQL resolve). Só viagens **realizadas** entram nas de viagem.

| Métrica | Arquivo | O que calcula |
|---|---|---|
| KM total | `total_km.sql` | soma de `km_percorrida` das viagens realizadas, de um veículo (`veiculoId`) ou da frota |
| Volume por categoria | `volume_categoria.sql` | contagem de viagens realizadas por tipo; LEVE e PESADO sempre aparecem, mesmo com zero |
| Ranking de utilização | `ranking_utilizacao.sql` | top 5 veículos pela soma de km (desempate por id); a posição 1 é o destaque |
| Cronograma | `cronograma.sql` | 5 primeiras manutenções PENDENTE ou EM_REALIZACAO a partir de `dataInicial` (padrão: hoje), por data de início, com placa e modelo |
| Projeção financeira | `projecao_financeira.sql` | soma de `custo_estimado` das manutenções do mês atual, exceto REJEITADA, com total e quebra por status (`rollup`) |

**CTE ou VIEW?** A regra de viagem realizada é uma CTE (`viagens_com_chegada` → `viagens_realizadas`), repetida de forma idêntica nas três consultas de viagem. Optei pela CTE em vez de uma VIEW para manter a regra legível dentro de cada arquivo `.sql` e não acrescentar uma migração só para isso; o custo é a repetição de cerca de 12 linhas. As consultas usam os índices `ix_viagens_veiculo_saida` (partição e ordenação do `lead`) e `ix_manutencoes_data_inicio`.

Endpoints: `GET /api/dashboard/total-km`, `/volume-categoria`, `/ranking-utilizacao`, `/cronograma`, `/projecao-financeira` e `/resumo` (as cinco de uma vez).

## 6. API

Swagger: http://localhost:8080/swagger-ui.html. Recursos de CRUD seguem o mesmo contrato (`GET` paginado com `page`, `size`, `sort`; `GET /{id}`; `POST` 201; `PUT /{id}`; `DELETE /{id}` 204):

| Recurso | Observações |
|---|---|
| `/api/veiculos` | placa `ABC-1234` ou Mercosul, tipo `LEVE`/`PESADO`, ano 1980..ano atual+1 |
| `/api/viagens` | resposta traz `chegadaEfetiva` e `emAndamento`; `GET /api/viagens/cidades` lista origens e destinos já usados; conflito de agenda responde 409 |
| `POST /api/auth/login` | público; corpo `{email, senha}`; devolve `{token, expiraEm, id, nome, perfil}` |
| `/api/usuarios` | só ADMIN; `senha` obrigatória na criação e opcional na edição; a resposta nunca traz a senha |
| `GET /api/dashboard/...` | `total-km?veiculoId=`, `volume-categoria`, `ranking-utilizacao`, `cronograma?dataInicial=AAAA-MM-DD`, `projecao-financeira`, `resumo` |
| `/api/manutencoes` | `status` opcional (padrão `PENDENTE`); resposta traz o veículo resumido |

Erros: `ProblemDetail`; no 400 o campo `erros` mapeia campo → mensagem. Regras entre campos (ex.: finalização ≥ início) aparecem com chave `<campo>Valida`.

## 7. Demonstração

Prints esperados em `docs/prints/` (serão adicionados pelo dono do projeto):
- `login.png`, `dashboard.png`, `viagens-lista.png`, `viagem-conflito.png`, `veiculos.png`, `manutencoes.png`, `usuarios.png`.
