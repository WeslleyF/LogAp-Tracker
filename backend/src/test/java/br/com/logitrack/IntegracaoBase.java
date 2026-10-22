package br.com.logitrack;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Postgres real, único para toda a execução (o cache de contexto do Spring reaproveita a conexão),
 * com as migrações Flyway aplicadas. Testes que alteram dados usam {@code @Transactional}.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegracaoBase {

  @ServiceConnection
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

  static {
    POSTGRES.start();
  }
}
