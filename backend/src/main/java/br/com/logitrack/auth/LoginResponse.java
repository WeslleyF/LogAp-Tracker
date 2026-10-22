package br.com.logitrack.auth;

import br.com.logitrack.usuario.Perfil;
import java.time.Instant;

public record LoginResponse(
    String token, Instant expiraEm, Integer id, String nome, Perfil perfil) {}
