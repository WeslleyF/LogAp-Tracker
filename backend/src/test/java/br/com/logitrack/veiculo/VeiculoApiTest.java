package br.com.logitrack.veiculo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.logitrack.IntegracaoBase;
import java.time.Year;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class VeiculoApiTest extends IntegracaoBase {

  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;

  String json(String placa, int ano) {
    return """
        {"placa":"%s","modelo":"Iveco Daily","tipo":"LEVE","ano":%d}"""
        .formatted(placa, ano);
  }

  @Test
  void listaOsVeiculosDoSeedPaginados() throws Exception {
    mvc.perform(get("/api/veiculos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(4))
        .andExpect(jsonPath("$.content[0].placa").exists());
  }

  @Test
  void criaComPlacaMercosulEGuardaEmMaiusculas() throws Exception {
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("bra2e19", 2024)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.placa").value("BRA2E19"))
        .andExpect(jsonPath("$.id").isNumber());
  }

  @Test
  void rejeitaPlacaEAnoInvalidosComErrosPorCampo() throws Exception {
    int anoInvalido = Year.now().getValue() + 2;
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("12-ABCD", anoInvalido)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.placa").exists())
        .andExpect(jsonPath("$.erros.ano").exists());
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("ABC-1234", 1979)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.ano").exists());
  }

  @Test
  void placaDuplicadaResponde409() throws Exception {
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("ABC-1234", 2020)))
        .andExpect(status().isConflict());
  }

  @Test
  void atualizaEBuscaPorId() throws Exception {
    mvc.perform(
            put("/api/veiculos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("ABC-1234", 2023)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ano").value(2023));
    mvc.perform(get("/api/veiculos/1")).andExpect(jsonPath("$.modelo").value("Iveco Daily"));
  }

  @Test
  void idInexistenteResponde404() throws Exception {
    mvc.perform(get("/api/veiculos/9999")).andExpect(status().isNotFound());
  }

  @Test
  void excluirVeiculoApagaViagensEManutencoesEmCascata() throws Exception {
    mvc.perform(delete("/api/veiculos/1")).andExpect(status().isNoContent());

    assertThat(
            jdbc.queryForObject("select count(*) from viagens where veiculo_id = 1", Integer.class))
        .isZero();
    assertThat(
            jdbc.queryForObject(
                "select count(*) from manutencoes where veiculo_id = 1", Integer.class))
        .isZero();
  }
}
