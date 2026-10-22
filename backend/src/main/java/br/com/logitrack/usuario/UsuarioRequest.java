package br.com.logitrack.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** {@code senha} é obrigatória só na criação; na edição, vazia mantém a senha atual. */
public record UsuarioRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(min = 6, max = 72) String senha,
    @NotNull Perfil perfil,
    Boolean ativo) {}
