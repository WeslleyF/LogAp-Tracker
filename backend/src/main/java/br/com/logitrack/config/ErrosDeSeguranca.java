package br.com.logitrack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** 401 e 403 gerados pelo filtro de segurança também saem como ProblemDetail. */
@Component
public class ErrosDeSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

  private final ObjectMapper mapper;

  public ErrosDeSeguranca(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
      throws IOException {
    escrever(response, HttpStatus.UNAUTHORIZED, "Autenticação necessária ou token inválido");
  }

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException {
    escrever(response, HttpStatus.FORBIDDEN, "Seu perfil não tem acesso a este recurso");
  }

  private void escrever(HttpServletResponse response, HttpStatus status, String detalhe)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    mapper.writeValue(
        response.getOutputStream(), ProblemDetail.forStatusAndDetail(status, detalhe));
  }
}
