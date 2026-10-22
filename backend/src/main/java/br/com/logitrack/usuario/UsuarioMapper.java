package br.com.logitrack.usuario;

import br.com.logitrack.shared.crud.CrudMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements CrudMapper<Usuario, UsuarioRequest, UsuarioResponse> {

  private final PasswordEncoder encoder;

  public UsuarioMapper(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  @Override
  public Usuario toEntity(UsuarioRequest request) {
    Usuario usuario = new Usuario();
    update(usuario, request);
    return usuario;
  }

  @Override
  public void update(Usuario usuario, UsuarioRequest request) {
    usuario.setNome(request.nome().trim());
    usuario.setEmail(request.email().trim().toLowerCase());
    usuario.setPerfil(request.perfil());
    usuario.setAtivo(request.ativo() == null || request.ativo());
    if (request.senha() != null && !request.senha().isBlank()) {
      usuario.setSenhaHash(encoder.encode(request.senha()));
    }
  }

  @Override
  public UsuarioResponse toResponse(Usuario usuario) {
    return new UsuarioResponse(
        usuario.getId(),
        usuario.getNome(),
        usuario.getEmail(),
        usuario.getPerfil(),
        usuario.isAtivo());
  }
}
