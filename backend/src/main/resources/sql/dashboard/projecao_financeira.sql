-- Projeção financeira do mês atual: soma de custo_estimado das manutenções com data_inicio no mês
-- corrente e status diferente de REJEITADA, com quebra por status.
-- rollup devolve uma linha extra com status nulo: o total (também quando não há manutenções).
-- Todos os status considerados aparecem, mesmo com zero.
with manutencoes_do_mes as (
    select status, custo_estimado
      from manutencoes
     where data_inicio >= date_trunc('month', current_date)
       and data_inicio < date_trunc('month', current_date) + interval '1 month'
       and status <> 'REJEITADA'
)
select s.status,
       count(m.status) as quantidade,
       coalesce(sum(m.custo_estimado), 0) as total,
       cast(date_trunc('month', current_date) as date) as mes_referencia
  from (values (1, 'PENDENTE'), (2, 'EM_REALIZACAO'), (3, 'CONCLUIDA')) as s(ordem, status)
  left join manutencoes_do_mes m on m.status = s.status
 group by rollup ((s.ordem, s.status))
 order by grouping(s.status), s.ordem
