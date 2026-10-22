package br.com.logitrack.veiculo;

public record VeiculoResponse(
    Integer id, String placa, String modelo, TipoVeiculo tipo, Integer ano) {}
