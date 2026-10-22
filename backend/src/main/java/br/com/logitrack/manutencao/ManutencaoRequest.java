package br.com.logitrack.manutencao;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** {@code status} é opcional: quando ausente, a manutenção nasce PENDENTE. */
public record ManutencaoRequest(
    @NotNull Integer veiculoId,
    @NotNull LocalDate dataInicio,
    LocalDate dataFinalizacao,
    @NotBlank @Size(max = 100) String tipoServico,
    @NotNull @DecimalMin("0.00") BigDecimal custoEstimado,
    StatusManutencao status) {

  @AssertTrue(message = "Finalização não pode ser anterior ao início")
  public boolean isDataFinalizacaoValida() {
    return dataFinalizacao == null || dataInicio == null || !dataFinalizacao.isBefore(dataInicio);
  }
}
