package br.com.logitrack.manutencao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.logitrack.IntegracaoBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ManutencaoApiTest extends IntegracaoBase {

  @Autowired MockMvc mvc;

  String json(int veiculoId, String inicio, String fim, String custo) {
    return """
        {"veiculoId":%d,"dataInicio":"%s","dataFinalizacao":%s,"tipoServico":"Motor","custoEstimado":%s}"""
        .formatted(veiculoId, inicio, fim == null ? "null" : "\"" + fim + "\"", custo);
  }

  @Test
  void criaComStatusPendenteQuandoNaoInformadoEDevolveOVeiculo() throws Exception {
    mvc.perform(
            post("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(1, "2030-01-10", "2030-01-11", "100.50")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDENTE"))
        .andExpect(jsonPath("$.veiculo.placa").value("ABC-1234"));
  }

  @Test
  void rejeitaFinalizacaoAntesDoInicioECustoNegativo() throws Exception {
    mvc.perform(
            post("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(1, "2030-01-10", "2030-01-09", "-1")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.dataFinalizacaoValida").exists())
        .andExpect(jsonPath("$.erros.custoEstimado").exists());
  }

  @Test
  void veiculoInexistenteResponde409ComMensagem() throws Exception {
    mvc.perform(
            post("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(999, "2030-01-10", null, "10")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Veículo informado não existe (id=999)"));
  }

  @Test
  void listaComVeiculoEmCadaLinha() throws Exception {
    mvc.perform(get("/api/manutencoes?size=50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].veiculo.modelo").exists());
  }
}
