package br.com.logitrack.usuario;

import br.com.logitrack.shared.crud.CrudService;
import br.com.logitrack.shared.exception.NegocioException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends CrudService<Usuario, UsuarioRequest, UsuarioResponse> {

  public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
    super(repository, mapper);
  }

  @Override
  protected void validar(Usuario usuario) {
    if (usuario.getSenhaHash() == null) {
      throw new NegocioException("Informe a senha do novo usuário");
    }
  }
}
