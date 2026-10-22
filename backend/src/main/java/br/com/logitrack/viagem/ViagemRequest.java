package br.com.logitrack.viagem;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViagemRequest(
    @NotNull Integer veiculoId,
    @NotNull LocalDateTime dataSaida,
    LocalDateTime dataChegada,
    @NotBlank @Size(max = 100) String origem,
    @NotBlank @Size(max = 100) String destino,
    @DecimalMin(value = "0.00", inclusive = false, message = "KM deve ser maior que zero")
        @Digits(integer = 8, fraction = 2)
        BigDecimal kmPercorrida) {

  @AssertTrue(message = "Chegada deve ser posterior à saída")
  public boolean isDataChegadaValida() {
    return dataChegada == null || dataSaida == null || dataChegada.isAfter(dataSaida);
  }

  @AssertTrue(message = "Destino deve ser diferente da origem")
  public boolean isDestinoDiferenteDaOrigem() {
    return origem == null || destino == null || !origem.trim().equalsIgnoreCase(destino.trim());
  }
}
