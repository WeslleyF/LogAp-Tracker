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

O Swagger fica em http://localhost:8080/swagger-ui.html. O restante (compose completo com `api` e `web`) é descrito ao fim do projeto.

## 2. Decisões técnicas e arquitetura

Stack: Java 21 + Spring Boot 3 + PostgreSQL 16 + Flyway no back; React + TypeScript + Vite + Mantine no front; Docker Compose para subir tudo. O registro completo das decisões está em [docs/decisoes.md](docs/decisoes.md).

**Camadas (back):** controller → service → repository, pacotes por feature em `br.com.logitrack`. DTOs são `record`s e a entidade nunca sai do controller.

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

Ver [docs/decisoes.md](docs/decisoes.md) (D08 a D13): chegada efetiva, viagem realizada, conflito de agenda e REJEITADA fora da projeção.

## 5. Métricas do dashboard

KM total, volume por categoria, ranking de utilização, cronograma de manutenções e projeção financeira do mês. Detalhes por arquivo `.sql` virão com a implementação do dashboard.

## 6. API

Swagger: http://localhost:8080/swagger-ui.html. Recursos de CRUD seguem o mesmo contrato (`GET` paginado com `page`, `size`, `sort`; `GET /{id}`; `POST` 201; `PUT /{id}`; `DELETE /{id}` 204):

| Recurso | Observações |
|---|---|
| `/api/veiculos` | placa `ABC-1234` ou Mercosul, tipo `LEVE`/`PESADO`, ano 1980..ano atual+1 |
| `/api/manutencoes` | `status` opcional (padrão `PENDENTE`); resposta traz o veículo resumido |

Erros: `ProblemDetail`; no 400 o campo `erros` mapeia campo → mensagem. Regras entre campos (ex.: finalização ≥ início) aparecem com chave `<campo>Valida`.

## 7. Demonstração

Prints esperados em `docs/prints/` (serão adicionados pelo dono do projeto):
- `login.png`, `dashboard.png`, `viagens-lista.png`, `viagem-conflito.png`, `veiculos.png`, `manutencoes.png`, `usuarios.png`.
