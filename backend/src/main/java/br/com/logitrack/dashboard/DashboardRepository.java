package br.com.logitrack.dashboard;

import static br.com.logitrack.shared.sql.SqlLoader.carregar;

import br.com.logitrack.manutencao.StatusManutencao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** Executa os SQLs de {@code resources/sql/dashboard}; a regra de cada métrica está no arquivo. */
@Repository
public class DashboardRepository {

  private static final String TOTAL_KM = carregar("sql/dashboard/total_km.sql");
  private static final String VOLUME_CATEGORIA = carregar("sql/dashboard/volume_categoria.sql");
  private static final String RANKING = carregar("sql/dashboard/ranking_utilizacao.sql");
  private static final String CRONOGRAMA = carregar("sql/dashboard/cronograma.sql");
  private static final String PROJECAO = carregar("sql/dashboard/projecao_financeira.sql");

  /** Linha da projeção; {@code status} nulo é a linha de total do {@code rollup}. */
  private record LinhaProjecao(
      StatusManutencao status, long quantidade, BigDecimal total, LocalDate mesReferencia) {}

  private final JdbcClient jdbc;

  public DashboardRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public TotalKm totalKm(Integer veiculoId) {
    return jdbc.sql(TOTAL_KM).param("veiculoId", veiculoId).query(TotalKm.class).single();
  }

  public List<VolumeCategoria> volumeCategoria() {
    return jdbc.sql(VOLUME_CATEGORIA).query(VolumeCategoria.class).list();
  }

  public List<RankingVeiculo> rankingUtilizacao() {
    return jdbc.sql(RANKING).query(RankingVeiculo.class).list();
  }

  public List<ManutencaoAgendada> cronograma(LocalDate dataInicial) {
    return jdbc.sql(CRONOGRAMA)
        .param("dataInicial", dataInicial)
        .query(ManutencaoAgendada.class)
        .list();
  }

  public ProjecaoFinanceira projecaoFinanceira() {
    List<LinhaProjecao> linhas = jdbc.sql(PROJECAO).query(LinhaProjecao.class).list();
    LinhaProjecao total = linhas.stream().filter(l -> l.status() == null).findFirst().orElseThrow();
    List<ProjecaoFinanceira.PorStatus> porStatus =
        linhas.stream()
            .filter(l -> l.status() != null)
            .map(l -> new ProjecaoFinanceira.PorStatus(l.status(), l.quantidade(), l.total()))
            .toList();
    return new ProjecaoFinanceira(
        total.mesReferencia(), total.total(), total.quantidade(), porStatus);
  }
}
