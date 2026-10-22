package br.com.logitrack.veiculo;

import br.com.logitrack.shared.crud.CrudService;
import org.springframework.stereotype.Service;

@Service
public class VeiculoService extends CrudService<Veiculo, VeiculoRequest, VeiculoResponse> {

  public VeiculoService(VeiculoRepository repository, VeiculoMapper mapper) {
    super(repository, mapper);
  }
}
