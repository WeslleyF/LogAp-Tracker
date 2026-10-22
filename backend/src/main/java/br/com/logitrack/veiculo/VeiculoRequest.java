package br.com.logitrack.veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VeiculoRequest(
    @NotBlank
        @Pattern(
            regexp = "(?i)[A-Z]{3}-\\d{4}|[A-Z]{3}\\d[A-Z]\\d{2}",
            message = "Placa deve ser ABC-1234 ou Mercosul ABC1D23")
        String placa,
    @NotBlank @Size(max = 50) String modelo,
    @NotNull TipoVeiculo tipo,
    @NotNull @AnoValido Integer ano) {}
