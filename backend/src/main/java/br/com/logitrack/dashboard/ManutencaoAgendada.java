package br.com.logitrack.dashboard;

import br.com.logitrack.manutencao.StatusManutencao;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ManutencaoAgendada(
    Integer id,
    Integer veiculoId,
    String placa,
    String modelo,
    String tipoServico,
    LocalDate dataInicio,
    LocalDate dataFinalizacao,
    BigDecimal custoEstimado,
    StatusManutencao status) {}
