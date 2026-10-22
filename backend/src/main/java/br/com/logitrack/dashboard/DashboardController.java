package br.com.logitrack.dashboard;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService service;

  public DashboardController(DashboardService service) {
    this.service = service;
  }

  /** Sem {@code veiculoId}, soma a frota inteira. */
  @GetMapping("/total-km")
  public TotalKm totalKm(@RequestParam(required = false) Integer veiculoId) {
    return service.totalKm(veiculoId);
  }

  @GetMapping("/volume-categoria")
  public List<VolumeCategoria> volumeCategoria() {
    return service.volumeCategoria();
  }

  @GetMapping("/ranking-utilizacao")
  public List<RankingVeiculo> rankingUtilizacao() {
    return service.rankingUtilizacao();
  }

  /** Sem {@code dataInicial}, parte de hoje. */
  @GetMapping("/cronograma")
  public List<ManutencaoAgendada> cronograma(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataInicial) {
    return service.cronograma(dataInicial);
  }

  @GetMapping("/projecao-financeira")
  public ProjecaoFinanceira projecaoFinanceira() {
    return service.projecaoFinanceira();
  }

  @GetMapping("/resumo")
  public ResumoDashboard resumo(
      @RequestParam(required = false) Integer veiculoId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataInicial) {
    return service.resumo(veiculoId, dataInicial);
  }
}
