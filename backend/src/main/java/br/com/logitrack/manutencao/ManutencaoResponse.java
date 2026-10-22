package br.com.logitrack.manutencao;

import br.com.logitrack.veiculo.VeiculoResumo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ManutencaoResponse(
    Integer id,
    VeiculoResumo veiculo,
    LocalDate dataInicio,
    LocalDate dataFinalizacao,
    String tipoServico,
    BigDecimal custoEstimado,
    StatusManutencao status) {}
