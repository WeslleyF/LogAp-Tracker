package br.com.logitrack.usuario;

import br.com.logitrack.shared.crud.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Acesso restrito a ADMIN, configurado no {@code SecurityConfig}. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends CrudController<UsuarioRequest, UsuarioResponse> {

  public UsuarioController(UsuarioService service) {
    super(service);
  }
}
