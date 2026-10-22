package br.com.logitrack.dashboard;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

  private final DashboardRepository repository;

  public DashboardService(DashboardRepository repository) {
    this.repository = repository;
  }

  public TotalKm totalKm(Integer veiculoId) {
    return repository.totalKm(veiculoId);
  }

  public List<VolumeCategoria> volumeCategoria() {
    return repository.volumeCategoria();
  }

  public List<RankingVeiculo> rankingUtilizacao() {
    return repository.rankingUtilizacao();
  }

  public List<ManutencaoAgendada> cronograma(LocalDate dataInicial) {
    return repository.cronograma(dataInicial);
  }

  public ProjecaoFinanceira projecaoFinanceira() {
    return repository.projecaoFinanceira();
  }

  public ResumoDashboard resumo(Integer veiculoId, LocalDate dataInicial) {
    return new ResumoDashboard(
        totalKm(veiculoId),
        volumeCategoria(),
        rankingUtilizacao(),
        cronograma(dataInicial),
        projecaoFinanceira());
  }
}
