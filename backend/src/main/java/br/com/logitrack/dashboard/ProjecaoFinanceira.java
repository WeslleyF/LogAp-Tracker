package br.com.logitrack.dashboard;

import br.com.logitrack.manutencao.StatusManutencao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjecaoFinanceira(
    LocalDate mesReferencia, BigDecimal total, long quantidade, List<PorStatus> porStatus) {

  public record PorStatus(StatusManutencao status, long quantidade, BigDecimal total) {}
}
