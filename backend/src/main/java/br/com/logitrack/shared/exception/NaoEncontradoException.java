package br.com.logitrack.shared.exception;

public class NaoEncontradoException extends RuntimeException {

  public NaoEncontradoException(Integer id) {
    super("Registro não encontrado (id=" + id + ")");
  }
}
