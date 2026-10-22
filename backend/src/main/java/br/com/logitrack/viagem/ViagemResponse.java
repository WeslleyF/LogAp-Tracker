package br.com.logitrack.viagem;

import br.com.logitrack.veiculo.VeiculoResumo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@code chegadaEfetiva} é a chegada informada ou a saída da próxima viagem; nula = em andamento.
 */
public record ViagemResponse(
    Integer id,
    VeiculoResumo veiculo,
    LocalDateTime dataSaida,
    LocalDateTime dataChegada,
    LocalDateTime chegadaEfetiva,
    boolean emAndamento,
    String origem,
    String destino,
    BigDecimal kmPercorrida) {}
