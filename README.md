# LogiTrack Pro

MVP do desafio técnico da LogAp: gestão de frota com **CRUD de Viagens**, cadastros de apoio (veículos, manutenções, usuários) e **dashboard com 5 métricas calculadas via SQL**.

## 1. Como rodar

Em construção nesta fase: o projeto ainda está sendo montado. Pré-requisito já válido: Docker.

## 2. Decisões técnicas e arquitetura

Stack: Java 21 + Spring Boot 3 + PostgreSQL 16 + Flyway no back; React + TypeScript + Vite + Mantine no front; Docker Compose para subir tudo. O registro completo das decisões está em [docs/decisoes.md](docs/decisoes.md).

## 3. Mudanças no banco

O script original do desafio está em [scripts/](scripts/) e é preservado. As mudanças virão como migrações Flyway, justificadas aqui.

## 4. Regras de negócio

Ver [docs/decisoes.md](docs/decisoes.md) (D08 a D13): chegada efetiva, viagem realizada, conflito de agenda e REJEITADA fora da projeção.

## 5. Métricas do dashboard

KM total, volume por categoria, ranking de utilização, cronograma de manutenções e projeção financeira do mês. Detalhes por arquivo `.sql` virão com a implementação do dashboard.

## 6. API

Documentação Swagger será exposta em `/swagger-ui.html` quando a API existir.

## 7. Demonstração

Prints esperados em `docs/prints/` (serão adicionados pelo dono do projeto):
- `login.png`, `dashboard.png`, `viagens-lista.png`, `viagem-conflito.png`, `veiculos.png`, `manutencoes.png`, `usuarios.png`.
