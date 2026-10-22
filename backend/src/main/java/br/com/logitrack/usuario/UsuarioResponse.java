package br.com.logitrack.usuario;

public record UsuarioResponse(
    Integer id, String nome, String email, Perfil perfil, boolean ativo) {}
