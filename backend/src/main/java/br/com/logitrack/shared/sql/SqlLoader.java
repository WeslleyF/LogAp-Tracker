package br.com.logitrack.shared.sql;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

/** Carrega consultas SQL nativas versionadas em {@code src/main/resources/sql}. */
public final class SqlLoader {

  private SqlLoader() {}

  public static String carregar(String caminho) {
    try (var entrada = new ClassPathResource(caminho).getInputStream()) {
      return StreamUtils.copyToString(entrada, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Não foi possível ler o SQL " + caminho, e);
    }
  }
}
