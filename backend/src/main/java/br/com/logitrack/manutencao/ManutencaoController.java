package br.com.logitrack.manutencao;

import br.com.logitrack.shared.crud.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manutencoes")
public class ManutencaoController extends CrudController<ManutencaoRequest, ManutencaoResponse> {

  public ManutencaoController(ManutencaoService service) {
    super(service);
  }
}
