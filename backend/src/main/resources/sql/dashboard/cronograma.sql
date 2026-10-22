-- Cronograma de manutenções: as 5 primeiras PENDENTE ou EM_REALIZACAO com data_inicio a partir de
-- :dataInicial (padrão: hoje), por data de início. Usa o índice ix_manutencoes_data_inicio.
select m.id, m.veiculo_id, ve.placa, ve.modelo, m.tipo_servico,
       m.data_inicio, m.data_finalizacao, m.custo_estimado, m.status
  from manutencoes m
  join veiculos ve on ve.id = m.veiculo_id
 where m.data_inicio >= coalesce(cast(:dataInicial as date), current_date)
   and m.status in ('PENDENTE', 'EM_REALIZACAO')
 order by m.data_inicio, m.id
 limit 5
