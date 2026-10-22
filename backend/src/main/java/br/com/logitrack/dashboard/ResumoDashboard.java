package br.com.logitrack.dashboard;

import java.util.List;

public record ResumoDashboard(
    TotalKm totalKm,
    List<VolumeCategoria> volumeCategoria,
    List<RankingVeiculo> rankingUtilizacao,
    List<ManutencaoAgendada> cronograma,
    ProjecaoFinanceira projecaoFinanceira) {}
