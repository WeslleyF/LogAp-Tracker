package br.com.logitrack.veiculo;

import br.com.logitrack.shared.crud.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController extends CrudController<VeiculoRequest, VeiculoResponse> {

  public VeiculoController(VeiculoService service) {
    super(service);
  }
}
