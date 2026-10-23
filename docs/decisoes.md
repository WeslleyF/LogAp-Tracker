# Decisões do projeto

Registro das decisões de modelagem, arquitetura e implementação do LogiTrack Pro, todas tomadas em 22/10/2026.

## Modelagem

- **D01** Implementar o Módulo 1 (Viagens) completo. Veículos e manutenções têm CRUD de apoio, porque o dashboard depende deles.
- **D02** Banco PostgreSQL. O script original fica preservado em `scripts/` e toda mudança vem por migração Flyway, justificada no README.
- **D03** Origem e destino são texto livre, sem tabela de cidades. A tela sugere cidades já usadas, para não obrigar cadastro prévio.
- **D04** `tipo_servico` é texto livre, com lista de sugestões na tela.
- **D05** Mantido o `ON DELETE CASCADE`, sem inativação e sem coluna `ativo` em veículos. A tela avisa na confirmação de exclusão.
- **D06** Tipo de veículo e status de manutenção são enums no Java com CHECK no banco, sem tabela.
- **D07** Restrições do V2: NOT NULLs, CHECKs de datas, km, custo e status, e índices para as consultas do dashboard.
- **D08** Chegada efetiva: a `data_chegada` ou, se vazia, a `data_saida` da próxima viagem do mesmo veículo (LEAD). Sem nenhuma das duas, a viagem está em andamento.
- **D09** Km, volume e ranking contam só viagens realizadas (com chegada efetiva). Uma viagem fechada pela próxima e sem km conta no volume e soma zero.
- **D10** Novo status de manutenção: REJEITADA.
- **D11** Projeção financeira: manutenções do mês atual, exceto REJEITADA, com total e quebra por status.
- **D12** Cronograma: as 5 primeiras manutenções PENDENTE ou EM_REALIZACAO a partir de uma data inicial (padrão hoje).
- **D13** Bloquear viagem que se sobreponha a outra do mesmo veículo ou a uma manutenção não rejeitada dele.
- **D14** Seed: dados originais mais dados com datas relativas a `CURRENT_DATE`, para o dashboard nunca ficar vazio.
- **D15** Nova tabela `usuarios` (e-mail único, senha BCrypt, perfil ADMIN ou OPERADOR). O CRUD de usuários é só para ADMIN.
- **D24** No conflito entre viagens, a viagem nova entra no cálculo do LEAD. Contra manutenção, uma viagem em andamento ocupa só o dia da saída.
- **D26** O volume por categoria sempre traz LEVE e PESADO, mesmo com zero.
- **D27** Validações de formato: placa antiga ou Mercosul, ano de 1980 ao ano atual + 1, origem diferente de destino, km opcional (maior que zero quando informada).
- **D33** Regras entre campos usam `@AssertTrue` e o erro volta com a chave do método (ex.: `dataFinalizacaoValida`); o front liga a chave ao campo. O ano do veículo é obrigatório e o `status` da manutenção, se omitido, vira PENDENTE. Veículo inexistente em viagem ou manutenção responde 409.
- **D35** `chegadaEfetiva` na resposta de viagem vem de um `@Formula` no banco, relido após gravar. No conflito entre viagens, a última viagem sem chegada vale "infinito", e viagens com a mesma saída conflitam.
- **D37** A regra de viagem realizada é uma CTE repetida nos três SQLs de viagem, em vez de uma VIEW. A projeção sempre traz os três status, zerados se vazios, e o total vem de `rollup`. "Hoje" e "mês atual" são os do banco, em `America/Sao_Paulo`.

## Arquitetura

- **D16** Diferenciais: SPA, login e Docker. Deploy em nuvem fica para depois.
- **D17** Java 21, Spring Boot 3 e Maven; JPA no CRUD e `JdbcClient` com arquivos `.sql` no dashboard.
- **D18** Front em React, TypeScript e Vite, com Mantine, TanStack Query, React Hook Form, Zod e Recharts.
- **D19** CRUD genérico: `CrudService` e `CrudController` no back, `CrudPage` configurada por objeto no front.
- **D20** Lombok só com `@Getter`, `@Setter` e `@NoArgsConstructor`, nas entidades.
- **D21** Autenticação JWT stateless com Spring Security. ADMIN gerencia usuários; ADMIN e OPERADOR usam o resto.
- **D22** Monorepo (`backend/`, `frontend/`, `docs/`, `scripts/`) com Docker Compose (db, api, web).
- **D23** Domínio em português e commits no padrão Conventional Commits, também em português.
- **D28** Nenhum push sem pedido explícito.

## Implementação

- **D25, D31** Há dois usuários de teste, um ADMIN e um OPERADOR; as credenciais estão no README.
- **D29** Build e testes podem rodar em containers Docker quando a máquina não tem JDK ou Node. O README traz também os comandos diretos.
- **D30** Spring Boot 3.4.5, springdoc 2.8.6 e Spotless com Google Java Format, executado no `verify`.
- **D32** `BaseEntity` usa `Integer id`, porque as colunas `serial` do script original são `integer` e o `ddl-auto=validate` falharia com `Long`.
- **D34** Listagens com associação usam `@EntityGraph` para evitar N+1. Os testes de integração compartilham um Postgres (Testcontainers) e revertem os dados com `@Transactional`.
- **D36** O segredo do JWT tem um padrão só para desenvolvimento; o compose exige `JWT_SECRET` no `.env`, com no mínimo 32 caracteres. A autorização é por URL no `SecurityConfig`. A senha só é obrigatória ao criar usuário. Um token já emitido vale até expirar (8 h), mesmo que o usuário seja inativado.
- **D38** Versões do front: React 18, Mantine 7, Zod 3, react-router 6, Vite 5, TypeScript 5.6 e ESLint 9. A sessão fica em um único item do `localStorage`, descartado quando expira.
- **D39** O `CrudConfig` do front ganhou `singular`, `valoresIniciais`, `paraForm`, `ordenacao` e `avisoExclusao`, e os campos ganharam os tipos `password` e `boolean`, além de `sugestoes` e `decimais`. Datas trafegam como string ISO e `''` vira `null` ao enviar. Páginas de 10 itens.
- **D40** Imagem da api com build Maven multi-stage (sem testes, que precisam de Docker) e JRE 21. O `web` é nginx com proxy de `/api`, `/swagger-ui` e `/v3/api-docs`. `docs/schema-final.sql` vem de `pg_dump --schema-only`.
