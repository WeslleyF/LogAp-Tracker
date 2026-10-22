package br.com.logitrack.dashboard;

import br.com.logitrack.veiculo.TipoVeiculo;
import java.math.BigDecimal;

/** Posição 1 é o destaque. */
public record RankingVeiculo(
    int posicao,
    Integer veiculoId,
    String placa,
    String modelo,
    TipoVeiculo tipo,
    BigDecimal totalKm) {}
