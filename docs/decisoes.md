# Registro de decisões

Memória do projeto entre sessões. Regras: decisões são do dono do projeto e não se contrariam sem perguntar; nunca se apaga uma (se substituída, marcar "Substituída por Dxx"); decisão nova entra no mesmo commit em que é aplicada; `assumida` = tomada pelo agente, para revisão.

Formato: **ID** · data · origem · status. Decisão. *Motivo.*

## Modelagem

- **D01** · 22/10/2026 · modelagem. Implementar o Módulo 1 (Viagens) completo. Veículos e manutenções têm CRUD de apoio (manutenção básico, sem regras do módulo 2). *O dashboard depende desses dados.*
- **D02** · 22/10/2026 · modelagem. O banco é PostgreSQL. O script original em `scripts/` é preservado; toda mudança vem por migração Flyway justificada no README. *Rastreabilidade do que foi entregue pelo desafio.*
- **D03** · 22/10/2026 · modelagem. Origem e destino continuam texto livre, sem tabela de cidades; a tela sugere cidades já usadas. *Não obrigar cadastro prévio.*
- **D04** · 22/10/2026 · modelagem. `tipo_servico` continua texto livre; a tela oferece lista sugerida. *Flexibilidade sem tabela extra.*
- **D05** · 22/10/2026 · modelagem. Mantido o `ON DELETE CASCADE`, sem inativação e sem coluna `ativo` em veículos. *Simplicidade; a tela avisa na confirmação.*
- **D06** · 22/10/2026 · modelagem. Tipo de veículo e status de manutenção são enum no Java com CHECK no banco, sem tabela. *Conjuntos pequenos e fixos.*
- **D07** · 22/10/2026 · modelagem. Restrições do V2: `tipo` NOT NULL, `veiculo_id` NOT NULL nas duas tabelas, `custo_estimado` NOT NULL e ≥ 0, CHECKs de datas e km, CHECK de status e índices. *Integridade e consultas do dashboard.*
- **D08** · 22/10/2026 · modelagem. Chegada efetiva: `data_chegada`; se vazia, `data_saida` da próxima viagem do mesmo veículo (LEAD). Sem nenhuma das duas, em andamento. *Viagens sem chegada registrada ainda precisam ser contabilizadas.*
- **D09** · 22/10/2026 · modelagem. Métricas de viagem (km, volume, ranking) contam só viagens realizadas. Fechada pela próxima e sem km conta no volume e soma zero no km.
- **D10** · 22/10/2026 · modelagem. Novo status de manutenção REJEITADA.
- **D11** · 22/10/2026 · modelagem. Projeção financeira: manutenções com `data_inicio` no mês atual e status ≠ REJEITADA, com total e quebra por status.
- **D12** · 22/10/2026 · modelagem. Cronograma recebe data inicial (padrão hoje) e lista as 5 primeiras PENDENTE ou EM_REALIZACAO a partir dela, por data de início.
- **D13** · 22/10/2026 · modelagem. Bloquear viagem sobreposta a outra do mesmo veículo e viagem com o veículo em manutenção não rejeitada no período.
- **D14** · 22/10/2026 · modelagem. Seed: manter o original e acrescentar dados com datas relativas a `CURRENT_DATE`, mais usuários de teste. *Dashboard nunca vazio.*
- **D15** · 22/10/2026 · modelagem. Nova tabela `usuarios` (nome, email único, senha BCrypt, perfil ADMIN/OPERADOR, ativo). CRUD de usuários só para ADMIN. *Haverá login.*

## Arquitetura

- **D16** · 22/10/2026 · arquitetura. Diferenciais: SPA, login e Docker. Deploy em nuvem fica para depois.
- **D17** · 22/10/2026 · arquitetura. Back em Java 21, Spring Boot 3, Maven; JPA para o CRUD e `JdbcClient` com arquivos `.sql` para o dashboard.
- **D18** · 22/10/2026 · arquitetura. Front em React + TypeScript + Vite, com Mantine, TanStack Query, React Hook Form + Zod e Recharts.
- **D19** · 22/10/2026 · arquitetura. CRUD genérico: `CrudService` e `CrudController` no back, `CrudPage` configurada por entidade no front. *Código simples e reaproveitável.*
- **D20** · 22/10/2026 · arquitetura. Lombok só `@Getter`, `@Setter`, `@NoArgsConstructor` nas entidades.
- **D21** · 22/10/2026 · arquitetura. JWT stateless (Spring Security). ADMIN gerencia usuários; ADMIN e OPERADOR usam o resto.
- **D22** · 22/10/2026 · arquitetura. Monorepo (`backend/`, `frontend/`, `docs/`, `scripts/`) com Docker Compose (db, api, web).
- **D23** · 22/10/2026 · arquitetura. Domínio em português e Conventional Commits em português, commits incrementais.

## Assumidas (a revisar)

- **D24** · 22/10/2026 · prompt · `assumida`. Na checagem de conflito entre viagens, a viagem nova entra no cálculo do LEAD. Contra manutenção, viagem em andamento ocupa só o dia da saída.
- **D25** · 22/10/2026 · prompt · `assumida`. Usuários de teste: `admin@logitrack.com` / `admin123` e um OPERADOR.
- **D26** · 22/10/2026 · prompt · `assumida`. O volume por categoria sempre devolve LEVE e PESADO, mesmo com zero.
- **D27** · 22/10/2026 · prompt · `assumida`. Validações de formato: placa antiga ou Mercosul, ano 1980..ano atual+1, origem ≠ destino. Km opcional (> 0 quando informada).
- **D28** · 22/10/2026 · prompt · `assumida`. Nada de push sem pedido explícito.

## Decisões do agente durante a implementação

- **D29** · 22/10/2026 · fundação · `assumida`. Ambiente de desenvolvimento sem JDK/Node: build e testes rodam em containers Docker (`maven:3-eclipse-temurin-21`, `node:20`), e o `AGENTS.md` documenta os comandos diretos. *Só o Docker estava disponível.*
- **D30** · 22/10/2026 · backend base · `assumida`. Spring Boot 3.4.5, springdoc 2.8.6, Spotless 2.44.4 com Google Java Format 1.25.2, vinculado ao `verify` (`spotless:check`); `mvn spotless:apply` formata. *Versões estáveis e compatíveis entre si.*
- **D31** · 22/10/2026 · backend base · `assumida`. Operador de teste: `operador@logitrack.com` / `operador123`. Complementa D25.
- **D32** · 22/10/2026 · backend base · `assumida`. `BaseEntity` usa `Integer id` (colunas `serial` do script original são `integer`; com `Long` o `ddl-auto=validate` falharia). Em V2 não se tornou `status` NOT NULL; a obrigatoriedade fica na validação do request.
- **D33** · 22/10/2026 · veículos e manutenções · `assumida`. Validações entre campos usam `@AssertTrue` no record, e o erro volta com a chave do método (ex.: `dataFinalizacaoValida`); o front mostra chaves desconhecidas como alerta do formulário. Ano do veículo é obrigatório no request (o banco aceita nulo). `status` da manutenção é opcional no request e padroniza para PENDENTE. Veículo inexistente em viagem/manutenção responde 409 com mensagem. *Evita criar anotação de classe para cada regra.*
- **D34** · 22/10/2026 · veículos e manutenções · `assumida`. Listagens com associação usam `@EntityGraph` no `findAll(Pageable)` para evitar N+1. Testes de integração compartilham um único Postgres (Testcontainers) e usam `@Transactional` para reverter dados.
- **D35** · 22/10/2026 · viagens · `assumida`. `chegadaEfetiva` da resposta vem de `@Formula` (subconsulta da próxima saída) na entidade, relida com `refresh` após gravar (gancho `aposSalvar` no `CrudService`). Na checagem de conflito entre viagens, chegada efetiva nula (última viagem, aberta) vale "infinito"; viagem com chegada nula e próxima viagem existente ocupa até a próxima saída também na checagem com manutenção. A mesma saída conflita explicitamente. *Evita N+1 e mantém a regra do LEAD em SQL.*
