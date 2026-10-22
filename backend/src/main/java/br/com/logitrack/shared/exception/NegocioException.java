package br.com.logitrack.shared.exception;

/** Regra de negócio violada; vira 409. */
public class NegocioException extends RuntimeException {

  public NegocioException(String mensagem) {
    super(mensagem);
  }
}
