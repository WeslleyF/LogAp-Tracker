-- Volume de viagens realizadas por tipo de veículo.
-- LEVE e PESADO sempre aparecem, mesmo com zero viagens (a tabela de tipos vem de values).
-- Regra de viagem realizada (idêntica em total_km, volume_categoria e ranking_utilizacao):
-- chegada efetiva = data_chegada ou, se nula, a data_saida da próxima viagem do mesmo veículo (lead).
-- Sem nenhuma das duas, a viagem está em andamento e fica de fora das métricas.
with viagens_com_chegada as (
    select id, veiculo_id, km_percorrida,
           coalesce(data_chegada,
                    lead(data_saida) over (partition by veiculo_id order by data_saida, id)) as chegada_efetiva
      from viagens
),
viagens_realizadas as (
    select id, veiculo_id, km_percorrida, chegada_efetiva
      from viagens_com_chegada
     where chegada_efetiva is not null
)
select tipo.tipo, count(vr.id) as total_viagens
  from (values ('LEVE'), ('PESADO')) as tipo(tipo)
  left join veiculos ve on ve.tipo = tipo.tipo
  left join viagens_realizadas vr on vr.veiculo_id = ve.id
 group by tipo.tipo
 order by tipo.tipo
