package br.com.logitrack.shared.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Todo erro da API sai como ProblemDetail (RFC 7807). */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail validacao(MethodArgumentNotValidException ex) {
    Map<String, String> erros = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(erro -> erros.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
    ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "Dados inválidos");
    problema.setProperty("erros", erros);
    return problema;
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  ProblemDetail requisicaoInvalida(Exception ex) {
    return problema(HttpStatus.BAD_REQUEST, "Requisição inválida: corpo ou parâmetro malformado");
  }

  @ExceptionHandler(NaoEncontradoException.class)
  ProblemDetail naoEncontrado(NaoEncontradoException ex) {
    return problema(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(NegocioException.class)
  ProblemDetail negocio(NegocioException ex) {
    return problema(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ProblemDetail integridade(DataIntegrityViolationException ex) {
    return problema(
        HttpStatus.CONFLICT,
        "Operação viola uma restrição de dados (registro duplicado ou em uso)");
  }

  @ExceptionHandler(AuthenticationException.class)
  ProblemDetail naoAutenticado(AuthenticationException ex) {
    return problema(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ProblemDetail acessoNegado(AccessDeniedException ex) {
    return problema(HttpStatus.FORBIDDEN, "Seu perfil não tem acesso a este recurso");
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail inesperado(Exception ex) {
    log.error("Erro inesperado", ex);
    return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
  }

  private ProblemDetail problema(HttpStatus status, String detalhe) {
    return ProblemDetail.forStatusAndDetail(status, detalhe);
  }
}
