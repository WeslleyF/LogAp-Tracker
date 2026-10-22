-- veiculos: sem tipo o veículo sumiria do volume por categoria
alter table veiculos alter column tipo set not null;

-- viagens: toda viagem pertence a um veículo e as datas/km precisam fazer sentido
alter table viagens alter column veiculo_id set not null;
alter table viagens add constraint ck_viagens_chegada_apos_saida
    check (data_chegada is null or data_chegada > data_saida);
alter table viagens add constraint ck_viagens_km_positiva
    check (km_percorrida is null or km_percorrida > 0);
-- somas por veículo e busca da próxima viagem (lead por veículo ordenado por saída)
create index ix_viagens_veiculo_saida on viagens (veiculo_id, data_saida);

-- manutencoes: veículo e custo obrigatórios; REJEITADA é um novo status
alter table manutencoes alter column veiculo_id set not null;
alter table manutencoes alter column custo_estimado set not null;
alter table manutencoes add constraint ck_manutencoes_custo_nao_negativo
    check (custo_estimado >= 0);
alter table manutencoes add constraint ck_manutencoes_finalizacao_apos_inicio
    check (data_finalizacao is null or data_finalizacao >= data_inicio);
alter table manutencoes add constraint ck_manutencoes_status
    check (status in ('PENDENTE', 'EM_REALIZACAO', 'CONCLUIDA', 'REJEITADA'));
-- cronograma e projeção filtram por data de início
create index ix_manutencoes_data_inicio on manutencoes (data_inicio);
