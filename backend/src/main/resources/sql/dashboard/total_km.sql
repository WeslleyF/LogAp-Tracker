-- Total de km percorridos: de um veículo (:veiculoId) ou de toda a frota (:veiculoId nulo).
-- Viagem fechada pela próxima saída e sem km soma zero.
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
select coalesce(sum(km_percorrida), 0) as total_km
  from viagens_realizadas
 where cast(:veiculoId as integer) is null
    or veiculo_id = :veiculoId
