-- Conflito entre uma viagem e as manutenções do mesmo veículo.
-- A manutenção ocupa do início do dia de data_inicio até o fim do dia de
-- coalesce(data_finalizacao, data_inicio); manutenção REJEITADA não bloqueia.
-- A viagem ocupa [saída, chegada efetiva); sem chegada e sem próxima viagem (em andamento),
-- ocupa só o dia da saída.
with nova as (
    select cast(:dataSaida as timestamp) as saida,
           coalesce(
               cast(:dataChegada as timestamp),
               (select min(data_saida)
                  from viagens
                 where veiculo_id = :veiculoId
                   and id is distinct from cast(:id as integer)
                   and data_saida > cast(:dataSaida as timestamp)),
               date_trunc('day', cast(:dataSaida as timestamp)) + interval '1 day') as chegada
)
select m.id, m.tipo_servico, m.data_inicio, coalesce(m.data_finalizacao, m.data_inicio) as data_fim
  from manutencoes m
 cross join nova
 where m.veiculo_id = :veiculoId
   and m.status <> 'REJEITADA'
   and nova.saida < cast(coalesce(m.data_finalizacao, m.data_inicio) + 1 as timestamp)
   and cast(m.data_inicio as timestamp) < nova.chegada
 order by m.data_inicio
 limit 1
