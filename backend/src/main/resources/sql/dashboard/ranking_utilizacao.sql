-- Ranking de utilização: top 5 veículos pela soma de km das viagens realizadas.
-- Desempate por id; veículos sem viagens entram com zero. A posição 1 é o destaque.
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
select row_number() over (order by coalesce(sum(vr.km_percorrida), 0) desc, ve.id) as posicao,
       ve.id as veiculo_id, ve.placa, ve.modelo, ve.tipo,
       coalesce(sum(vr.km_percorrida), 0) as total_km
  from veiculos ve
  left join viagens_realizadas vr on vr.veiculo_id = ve.id
 group by ve.id
 order by posicao
 limit 5
