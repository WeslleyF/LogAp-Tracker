package br.com.logitrack.auth;

import br.com.logitrack.usuario.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final TokenService tokens;

  public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, TokenService tokens) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.tokens = tokens;
  }

  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    return usuarios
        .findByEmailIgnoreCase(request.email().trim())
        .filter(u -> u.isAtivo() && encoder.matches(request.senha(), u.getSenhaHash()))
        .map(tokens::gerar)
        .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));
  }
}
