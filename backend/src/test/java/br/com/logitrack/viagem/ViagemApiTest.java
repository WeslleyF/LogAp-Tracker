package br.com.logitrack.viagem;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.logitrack.IntegracaoBase;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de agenda. Usa o veículo 3 em 2035, longe de qualquer viagem ou manutenção do seed, e cria
 * os próprios cenários.
 */
@Transactional
class ViagemApiTest extends IntegracaoBase {

  static final int VEICULO = 3;

  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;
  @PersistenceContext EntityManager em;

  String json(int veiculoId, String saida, String chegada, String km) {
    return """
        {"veiculoId":%d,"dataSaida":"%s","dataChegada":%s,"origem":"Campinas","destino":"Santos","kmPercorrida":%s}"""
        .formatted(
            veiculoId,
            saida,
            chegada == null ? "null" : "\"" + chegada + "\"",
            km == null ? "null" : km);
  }

  ResultActions criar(int veiculoId, String saida, String chegada) throws Exception {
    return mvc.perform(
        post("/api/viagens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(veiculoId, saida, chegada, "100")));
  }

  int criarComSucesso(String saida, String chegada) throws Exception {
    String corpo =
        criar(VEICULO, saida, chegada)
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return JsonPath.read(corpo, "$.id");
  }

  void criarEEsperarConflito(String saida, String chegada) throws Exception {
    criar(VEICULO, saida, chegada)
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value(containsString("Conflito")));
  }

  // ---- conflito entre viagens

  @Test
  void viagemFechadaBloqueiaViagemSobreposta() throws Exception {
    criarComSucesso("2035-03-10T08:00:00", "2035-03-10T18:00:00");

    criarEEsperarConflito("2035-03-10T12:00:00", "2035-03-10T20:00:00");
    criarEEsperarConflito("2035-03-10T06:00:00", "2035-03-10T09:00:00");
    criarComSucesso(
        "2035-03-10T18:00:00", "2035-03-10T19:00:00"); // encostar na chegada não conflita
  }

  @Test
  void viagemAbertaEEncerradaPelaProximaSaida() throws Exception {
    int aberta = criarComSucesso("2035-03-10T08:00:00", null);
    criarComSucesso("2035-03-12T08:00:00", null); // encerra a primeira, sem conflito
    em.clear(); // a requisição de produção tem a própria transação; aqui forçamos a releitura

    mvc.perform(get("/api/viagens/" + aberta))
        .andExpect(jsonPath("$.chegadaEfetiva").value("2035-03-12T08:00:00"))
        .andExpect(jsonPath("$.emAndamento").value(false));

    criarComSucesso(
        "2035-03-11T08:00:00", null); // no meio: reencerra a primeira e vai até a terceira
  }

  @Test
  void viagemAbertaSoBloqueiaQuemSaiAntesETerminaDepoisDaSuaSaida() throws Exception {
    criarComSucesso("2035-03-10T08:00:00", null);

    criarEEsperarConflito("2035-03-09T08:00:00", "2035-03-10T09:00:00");
    criarComSucesso("2035-03-09T08:00:00", "2035-03-10T08:00:00");
    criarComSucesso("2035-03-11T08:00:00", "2035-03-11T09:00:00"); // depois da aberta: a encerra
  }

  @Test
  void chegadaInformadaDepoisDaSaidaDaSeguinteConflita() throws Exception {
    criarComSucesso("2035-03-12T08:00:00", "2035-03-12T10:00:00");

    criarEEsperarConflito("2035-03-10T08:00:00", "2035-03-12T09:00:00");
  }

  @Test
  void mesmaDataDeSaidaConflita() throws Exception {
    criarComSucesso("2035-03-10T08:00:00", null);

    criarEEsperarConflito("2035-03-10T08:00:00", null);
  }

  @Test
  void outroVeiculoNaMesmaDataNaoConflita() throws Exception {
    criarComSucesso("2035-03-10T08:00:00", "2035-03-10T18:00:00");

    criar(4, "2035-03-10T08:00:00", "2035-03-10T18:00:00").andExpect(status().isCreated());
  }

  @Test
  void editarNaoConflitaComASiMesma() throws Exception {
    int id = criarComSucesso("2035-03-10T08:00:00", "2035-03-10T18:00:00");

    mvc.perform(
            put("/api/viagens/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(VEICULO, "2035-03-10T09:00:00", "2035-03-10T20:00:00", "250")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.kmPercorrida").value(250))
        .andExpect(jsonPath("$.chegadaEfetiva").value("2035-03-10T20:00:00"));
  }

  @Test
  void editarParaSobreporOutraViagemConflita() throws Exception {
    criarComSucesso("2035-03-10T08:00:00", "2035-03-10T18:00:00");
    int outra = criarComSucesso("2035-03-12T08:00:00", "2035-03-12T18:00:00");

    mvc.perform(
            put("/api/viagens/" + outra)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(VEICULO, "2035-03-10T12:00:00", "2035-03-12T18:00:00", "10")))
        .andExpect(status().isConflict());
  }

  // ---- conflito com manutenção

  void criarManutencao(String inicio, String fim, String status) throws Exception {
    mvc.perform(
            post("/api/manutencoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"veiculoId":%d,"dataInicio":"%s","dataFinalizacao":%s,"tipoServico":"Motor","custoEstimado":10,"status":"%s"}"""
                        .formatted(
                            VEICULO, inicio, fim == null ? "null" : "\"" + fim + "\"", status)))
        .andExpect(status().isCreated());
  }

  @Test
  void manutencaoNaoRejeitadaBloqueiaViagem() throws Exception {
    criarManutencao("2035-05-10", "2035-05-11", "PENDENTE");

    criar(VEICULO, "2035-05-11T20:00:00", "2035-05-11T22:00:00")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value(containsString("manutenção")));
    criar(VEICULO, "2035-05-09T20:00:00", "2035-05-10T01:00:00").andExpect(status().isConflict());
    criar(VEICULO, "2035-05-12T00:00:00", "2035-05-12T08:00:00").andExpect(status().isCreated());
  }

  @Test
  void manutencaoSemFinalizacaoOcupaSoODiaDeInicio() throws Exception {
    criarManutencao("2035-05-10", null, "EM_REALIZACAO");

    criar(VEICULO, "2035-05-11T08:00:00", "2035-05-11T09:00:00").andExpect(status().isCreated());
    criar(VEICULO, "2035-05-10T08:00:00", "2035-05-10T09:00:00").andExpect(status().isConflict());
  }

  @Test
  void manutencaoRejeitadaNaoBloqueia() throws Exception {
    criarManutencao("2035-05-10", "2035-05-11", "REJEITADA");

    criar(VEICULO, "2035-05-10T08:00:00", "2035-05-10T18:00:00").andExpect(status().isCreated());
  }

  @Test
  void viagemEmAndamentoOcupaSoODiaDaSaidaNaChecagemDeManutencao() throws Exception {
    criarManutencao("2035-05-11", "2035-05-12", "PENDENTE");

    criar(VEICULO, "2035-05-10T08:00:00", null).andExpect(status().isCreated());
    criar(VEICULO, "2035-05-11T08:00:00", null).andExpect(status().isConflict());
  }

  @Test
  void viagemSemChegadaEncerradaPelaProximaOcupaOPeriodoAteAProximaSaida() throws Exception {
    criarManutencao("2035-05-11", "2035-05-11", "PENDENTE");
    criarComSucesso("2035-05-12T08:00:00", "2035-05-12T09:00:00");

    criar(VEICULO, "2035-05-10T08:00:00", null).andExpect(status().isConflict());
  }

  @Test
  void seedDoVeiculo4TemManutencaoFuturaQueBloqueia() throws Exception {
    LocalDate hoje = jdbc.queryForObject("select current_date", LocalDate.class);
    String saida = hoje.plusDays(3).atTime(10, 0).toString();
    String chegada = hoje.plusDays(3).atTime(12, 0).toString();

    criar(4, saida, chegada).andExpect(status().isConflict());
  }

  // ---- validação, listagem e cidades

  @Test
  void validaFormatoDosCampos() throws Exception {
    mvc.perform(
            post("/api/viagens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"veiculoId":3,"dataSaida":"2035-03-10T08:00:00","dataChegada":"2035-03-10T07:00:00","origem":"Santos","destino":"santos","kmPercorrida":0}"""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.erros.dataChegadaValida").exists())
        .andExpect(jsonPath("$.erros.destinoDiferenteDaOrigem").exists())
        .andExpect(jsonPath("$.erros.kmPercorrida").exists());
  }

  @Test
  void seedTemViagemEmAndamentoEViagemFechadaPelaProxima() throws Exception {
    mvc.perform(get("/api/viagens?size=100"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.content[?(@.emAndamento==true)].veiculo.placa").value(hasItem("ABC-1234")))
        .andExpect(
            jsonPath("$.content[?(@.dataChegada==null && @.emAndamento==false)]").isNotEmpty());
  }

  @Test
  void cidadesListaOrigensEDestinosSemRepetir() throws Exception {
    mvc.perform(get("/api/viagens/cidades"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(hasItem("São Paulo")))
        .andExpect(
            jsonPath("$.length()")
                .value(
                    jdbc.queryForObject(
                        "select count(*) from (select origem from viagens union select destino from viagens) c",
                        Integer.class)));
  }
}
