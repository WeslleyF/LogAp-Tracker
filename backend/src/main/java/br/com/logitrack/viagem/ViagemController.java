package br.com.logitrack.viagem;

import br.com.logitrack.shared.crud.CrudController;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController extends CrudController<ViagemRequest, ViagemResponse> {

  private final ViagemService service;

  public ViagemController(ViagemService service) {
    super(service);
    this.service = service;
  }

  @GetMapping("/cidades")
  public List<String> cidades() {
    return service.cidades();
  }
}
