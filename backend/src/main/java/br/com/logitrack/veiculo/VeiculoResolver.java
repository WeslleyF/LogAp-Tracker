package br.com.logitrack.veiculo;

import br.com.logitrack.shared.exception.NegocioException;
import org.springframework.stereotype.Component;

/** Resolve o veículo de um request de viagem ou manutenção, com mensagem clara se não existir. */
@Component
public class VeiculoResolver {

  private final VeiculoRepository repository;

  public VeiculoResolver(VeiculoRepository repository) {
    this.repository = repository;
  }

  public Veiculo obter(Integer veiculoId) {
    return repository
        .findById(veiculoId)
        .orElseThrow(
            () -> new NegocioException("Veículo informado não existe (id=" + veiculoId + ")"));
  }
}
