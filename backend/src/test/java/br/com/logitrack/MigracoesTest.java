package br.com.logitrack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

class MigracoesTest extends IntegracaoBase {

  @Autowired JdbcTemplate jdbc;

  @Test
  void seedOriginalECarregadoJuntoComOsDadosRelativos() {
    assertThat(jdbc.queryForObject("select count(*) from veiculos", Integer.class)).isEqualTo(4);
    assertThat(jdbc.queryForObject("select count(*) from viagens", Integer.class)).isGreaterThan(3);
    assertThat(jdbc.queryForObject("select count(*) from usuarios", Integer.class)).isEqualTo(2);
  }

  @Test
  void seedTemManutencoesDeTodosOsStatus() {
    var status = jdbc.queryForList("select distinct status from manutencoes", String.class);
    assertThat(status)
        .containsExactlyInAnyOrder("PENDENTE", "EM_REALIZACAO", "CONCLUIDA", "REJEITADA");
  }

  @Test
  void restricoesDoV2BloqueiamDadosInvalidos() {
    assertThatThrownBy(
            () ->
                jdbc.update(
                    "insert into viagens (veiculo_id, data_saida, data_chegada) values (1, now(), now() - interval '1 hour')"))
        .isInstanceOf(DataIntegrityViolationException.class);
    assertThatThrownBy(
            () ->
                jdbc.update(
                    "insert into viagens (veiculo_id, data_saida, km_percorrida) values (1, now(), 0)"))
        .isInstanceOf(DataIntegrityViolationException.class);
    assertThatThrownBy(
            () ->
                jdbc.update(
                    "insert into manutencoes (veiculo_id, data_inicio, custo_estimado, status) values (1, current_date, 10, 'INVALIDO')"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
