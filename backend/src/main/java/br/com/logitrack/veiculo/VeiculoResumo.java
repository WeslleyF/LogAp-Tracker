package br.com.logitrack.veiculo;

/** Dados do veículo embutidos nas respostas de viagem e manutenção. */
public record VeiculoResumo(Integer id, String placa, String modelo) {

  public static VeiculoResumo de(Veiculo veiculo) {
    return new VeiculoResumo(veiculo.getId(), veiculo.getPlaca(), veiculo.getModelo());
  }
}
