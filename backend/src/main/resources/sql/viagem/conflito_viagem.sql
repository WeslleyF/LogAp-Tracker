-- Conflito de agenda entre viagens do mesmo veículo.
-- Recebe a viagem nova ou editada (:dataSaida, :dataChegada, ...) e devolve uma outra viagem do
-- veículo que se sobrepõe a ela, se existir.
--
-- Regra: a viagem entra no conjunto de viagens do veículo (a versão antiga, em caso de edição,
-- sai por :id) e a chegada efetiva é recalculada com lead: chegada informada ou a saída da
-- próxima viagem. Sem nenhuma das duas, a viagem fica em aberto e vale "infinito".
-- Dois intervalos [saída, chegada efetiva) conflitam se se sobrepõem ou se têm a mesma saída.
with viagens_do_veiculo as (
    select id, data_saida, data_chegada, origem, destino
      from viagens
     where veiculo_id = :veiculoId
       and id is distinct from cast(:id as integer)
    union all
    select -1, cast(:dataSaida as timestamp), cast(:dataChegada as timestamp),
           cast(:origem as varchar), cast(:destino as varchar)
),
intervalos as (
    select id, origem, destino, data_saida,
           coalesce(data_chegada, lead(data_saida) over (order by data_saida, id)) as chegada_efetiva
      from viagens_do_veiculo
)
select outra.id, outra.origem, outra.destino, outra.data_saida, outra.chegada_efetiva
  from intervalos nova
  join intervalos outra on outra.id <> nova.id
 where nova.id = -1
   and (outra.data_saida = nova.data_saida
        or (outra.data_saida < coalesce(nova.chegada_efetiva, timestamp 'infinity')
            and nova.data_saida < coalesce(outra.chegada_efetiva, timestamp 'infinity')))
 order by outra.data_saida
 limit 1
