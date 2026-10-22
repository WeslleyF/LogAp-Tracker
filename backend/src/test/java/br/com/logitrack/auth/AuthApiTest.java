package br.com.logitrack.auth;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.logitrack.IntegracaoBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@WithAnonymousUser
@Transactional
class AuthApiTest extends IntegracaoBase {

  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;

  String corpoLogin(String email, String senha) {
    return """
        {"email":"%s","senha":"%s"}"""
        .formatted(email, senha);
  }

  String login(String email, String senha) throws Exception {
    String corpo =
        mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(corpoLogin(email, senha)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return JsonPath.read(corpo, "$.token");
  }

  String bearer(String token) {
    return "Bearer " + token;
  }

  @Test
  void semTokenResponde401EmTodaRotaDaApi() throws Exception {
    mvc.perform(get("/api/veiculos"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    mvc.perform(get("/api/viagens")).andExpect(status().isUnauthorized());
  }

  @Test
  void tokenInvalidoResponde401() throws Exception {
    mvc.perform(get("/api/veiculos").header("Authorization", "Bearer lixo"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginDoSeedFuncionaParaAdminEOperador() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoLogin("admin@logitrack.com", "admin123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.perfil").value("ADMIN"))
        .andExpect(jsonPath("$.nome").value("Administrador"))
        .andExpect(jsonPath("$.token").isNotEmpty());
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoLogin("OPERADOR@logitrack.com", "operador123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.perfil").value("OPERADOR"));
  }

  @Test
  void senhaErradaOuUsuarioInexistenteResponde401() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoLogin("admin@logitrack.com", "errada")))
        .andExpect(status().isUnauthorized());
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoLogin("ninguem@logitrack.com", "admin123")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void usuarioInativoNaoLogaNemRecebeToken() throws Exception {
    jdbc.update("update usuarios set ativo = false where email = 'operador@logitrack.com'");

    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpoLogin("operador@logitrack.com", "operador123")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void operadorUsaOResto_masNaoGerenciaUsuarios() throws Exception {
    String token = login("operador@logitrack.com", "operador123");

    mvc.perform(get("/api/veiculos").header("Authorization", bearer(token)))
        .andExpect(status().isOk());
    mvc.perform(get("/api/usuarios").header("Authorization", bearer(token)))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminListaUsuariosSemExporHashDaSenha() throws Exception {
    String token = login("admin@logitrack.com", "admin123");

    mvc.perform(get("/api/usuarios").header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(content().string(not(containsString("senha"))));
  }

  @Test
  void adminCriaUsuarioQueConsegueLogar() throws Exception {
    String admin = login("admin@logitrack.com", "admin123");

    mvc.perform(
            post("/api/usuarios")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nome":"Maria","email":"Maria@Logitrack.com","senha":"segredo1","perfil":"OPERADOR"}"""))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("maria@logitrack.com"))
        .andExpect(jsonPath("$.ativo").value(true));

    login("maria@logitrack.com", "segredo1");
  }

  @Test
  void criarUsuarioSemSenhaOuComEmailDuplicadoResponde409() throws Exception {
    String admin = login("admin@logitrack.com", "admin123");

    mvc.perform(
            post("/api/usuarios")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nome":"Sem Senha","email":"sem@logitrack.com","perfil":"OPERADOR"}"""))
        .andExpect(status().isConflict());
    mvc.perform(
            post("/api/usuarios")
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nome":"Dup","email":"admin@logitrack.com","senha":"segredo1","perfil":"ADMIN"}"""))
        .andExpect(status().isConflict());
  }
}
