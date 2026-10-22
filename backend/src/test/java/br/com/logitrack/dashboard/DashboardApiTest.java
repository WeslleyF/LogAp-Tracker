package br.com.logitrack.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.logitrack.IntegracaoBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confere os números do seed. Viagens originais (2024) e relativas somam, por veículo: 1 = 865,50
 * km (a viagem em andamento fica de fora e a fechada pela próxima soma zero), 2 = 4170, 3 = 0, 4 =
 * 1080 (a viagem sem chegada, fechada pela próxima, conta com seus 700 km). Datas de manutenção são
 * relativas ao "hoje" do banco.
 */
@Transactional
class DashboardApiTest extends IntegracaoBase {

  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;

  LocalDate hoje() {
    return jdbc.queryForObject("select current_date", LocalDate.class);
  }

  @Test
  void totalKmDaFrotaIgnoraViagemEmAndamento() throws Exception {
    mvc.perform(get("/api/dashboard/total-km"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalKm").value(6115.5));
  }

  @Test
  void totalKmPorVeiculo() throws Exception {
    mvc.perform(get("/api/dashboard/total-km?veiculoId=1"))
        .andExpect(jsonPath("$.totalKm").value(865.5));
    mvc.perform(get("/api/dashboard/total-km?veiculoId=3"))
        .andExpect(jsonPath("$.totalKm").value(0));
    mvc.perform(get("/api/dashboard/total-km?veiculoId=4"))
        .andExpect(jsonPath("$.totalKm").value(1080));
  }

  @Test
  void volumePorCategoriaContaSoViagensRealizadas() throws Exception {
    mvc.perform(get("/api/dashboard/volume-categoria"))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].tipo").value("LEVE"))
        .andExpect(jsonPath("$[0].totalViagens").value(5))
        .andExpect(jsonPath("$[1].tipo").value("PESADO"))
        .andExpect(jsonPath("$[1].totalViagens").value(6));
  }

  @Test
  void volumeSempreTrazLeveEPesadoMesmoSemViagens() throws Exception {
    jdbc.update("delete from viagens");

    mvc.perform(get("/api/dashboard/volume-categoria"))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].totalViagens").value(0))
        .andExpect(jsonPath("$[1].totalViagens").value(0));
  }

  @Test
  void rankingOrdenaPorKmComDestaqueNoPrimeiro() throws Exception {
    mvc.perform(get("/api/dashboard/ranking-utilizacao"))
        .andExpect(jsonPath("$.length()").value(4))
        .andExpect(jsonPath("$[0].posicao").value(1))
        .andExpect(jsonPath("$[0].placa").value("XYZ-9876"))
        .andExpect(jsonPath("$[0].totalKm").value(4170))
        .andExpect(jsonPath("$[1].placa").value("LMN-4455"))
        .andExpect(jsonPath("$[2].placa").value("ABC-1234"))
        .andExpect(jsonPath("$[3].placa").value("KJG-1122"))
        .andExpect(jsonPath("$[3].totalKm").value(0));
  }

  @Test
  void cronogramaTraz5ProximasPendentesOuEmRealizacaoAPartirDeHoje() throws Exception {
    mvc.perform(get("/api/dashboard/cronograma"))
        .andExpect(jsonPath("$.length()").value(5))
        .andExpect(jsonPath("$[0].status").value("EM_REALIZACAO"))
        .andExpect(jsonPath("$[0].dataInicio").value(hoje().toString()))
        .andExpect(jsonPath("$[0].placa").value("XYZ-9876"))
        .andExpect(jsonPath("$[1].dataInicio").value(hoje().plusDays(3).toString()))
        .andExpect(jsonPath("$[1].placa").value("LMN-4455"))
        .andExpect(jsonPath("$[4].dataInicio").value(hoje().plusDays(30).toString()));
  }

  @Test
  void cronogramaRespeitaADataInicialEscolhida() throws Exception {
    mvc.perform(get("/api/dashboard/cronograma?dataInicial=" + hoje().plusDays(10)))
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].dataInicio").value(hoje().plusDays(15).toString()))
        .andExpect(jsonPath("$[2].dataInicio").value(hoje().plusDays(45).toString()));
    mvc.perform(get("/api/dashboard/cronograma?dataInicial=2024-06-01"))
        .andExpect(jsonPath("$.length()").value(5))
        .andExpect(jsonPath("$[0].dataInicio").value("2024-06-10"));
  }

  @Test
  void projecaoDoMesExcluiRejeitadaEQuebraPorStatus() throws Exception {
    BigDecimal esperado =
        jdbc.queryForObject(
            "select coalesce(sum(custo_estimado), 0) from manutencoes"
                + " where status <> 'REJEITADA' and to_char(data_inicio, 'YYYY-MM') = to_char(current_date, 'YYYY-MM')",
            BigDecimal.class);
    BigDecimal comRejeitada =
        jdbc.queryForObject(
            "select sum(custo_estimado) from manutencoes"
                + " where to_char(data_inicio, 'YYYY-MM') = to_char(current_date, 'YYYY-MM')",
            BigDecimal.class);

    String corpo =
        mvc.perform(get("/api/dashboard/projecao-financeira"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.porStatus.length()").value(3))
            .andExpect(jsonPath("$.porStatus[0].status").value("PENDENTE"))
            .andExpect(jsonPath("$.porStatus[1].status").value("EM_REALIZACAO"))
            .andExpect(jsonPath("$.porStatus[2].status").value("CONCLUIDA"))
            .andExpect(jsonPath("$.porStatus[2].total").value(3600))
            .andExpect(jsonPath("$.porStatus[2].quantidade").value(2))
            .andExpect(jsonPath("$.mesReferencia").value(hoje().withDayOfMonth(1).toString()))
            .andReturn()
            .getResponse()
            .getContentAsString();

    BigDecimal total =
        new BigDecimal(com.jayway.jsonpath.JsonPath.<Number>read(corpo, "$.total").toString());
    assertThat(total).isEqualByComparingTo(esperado);
    assertThat(comRejeitada.subtract(total)).isEqualByComparingTo("8000.00");
  }

  @Test
  void resumoTrazAsCincoMetricas() throws Exception {
    mvc.perform(get("/api/dashboard/resumo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalKm.totalKm").value(6115.5))
        .andExpect(jsonPath("$.volumeCategoria.length()").value(2))
        .andExpect(jsonPath("$.rankingUtilizacao[0].posicao").value(1))
        .andExpect(jsonPath("$.cronograma.length()").value(5))
        .andExpect(jsonPath("$.projecaoFinanceira.porStatus.length()").value(3));
  }
}
