package br.com.logitrack.shared.crud;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.logitrack.shared.exception.NaoEncontradoException;
import br.com.logitrack.shared.exception.NegocioException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.util.ReflectionTestUtils;

class CrudServiceTest {

  static class Coisa extends BaseEntity {
    String nome;
  }

  record CoisaRequest(String nome) {}

  record CoisaResponse(Integer id, String nome) {}

  static class CoisaMapper implements CrudMapper<Coisa, CoisaRequest, CoisaResponse> {
    public Coisa toEntity(CoisaRequest req) {
      Coisa c = new Coisa();
      c.nome = req.nome();
      return c;
    }

    public void update(Coisa c, CoisaRequest req) {
      c.nome = req.nome();
    }

    public CoisaResponse toResponse(Coisa c) {
      return new CoisaResponse(c.getId(), c.nome);
    }
  }

  /** Service de teste que registra a chamada dos ganchos e pode recusar. */
  static class CoisaService extends CrudService<Coisa, CoisaRequest, CoisaResponse> {
    final List<String> chamadas = new ArrayList<>();
    boolean recusar;

    CoisaService(JpaRepository<Coisa, Integer> repo) {
      super(repo, new CoisaMapper());
    }

    @Override
    protected void validar(Coisa c) {
      chamadas.add("validar:" + c.nome);
      if (recusar) throw new NegocioException("recusado");
    }

    @Override
    protected void antesDeExcluir(Coisa c) {
      chamadas.add("antesDeExcluir:" + c.nome);
      if (recusar) throw new NegocioException("em uso");
    }
  }

  @SuppressWarnings("unchecked")
  JpaRepository<Coisa, Integer> repo = mock(JpaRepository.class);

  CoisaService service;

  @BeforeEach
  void preparar() {
    service = new CoisaService(repo);
    when(repo.saveAndFlush(any(Coisa.class))).thenAnswer(i -> i.getArgument(0));
  }

  Coisa coisa(int id, String nome) {
    Coisa c = new Coisa();
    c.nome = nome;
    ReflectionTestUtils.setField(c, "id", id);
    return c;
  }

  @Test
  void listarConverteAPaginaParaResponses() {
    var pageable = PageRequest.of(1, 2);
    when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(coisa(3, "c")), pageable, 3));

    var pagina = service.listar(pageable);

    assertThat(pagina.content()).containsExactly(new CoisaResponse(3, "c"));
    assertThat(pagina.page()).isEqualTo(1);
    assertThat(pagina.size()).isEqualTo(2);
    assertThat(pagina.totalElements()).isEqualTo(3);
  }

  @Test
  void buscarInexistenteLancaNaoEncontrado() {
    when(repo.findById(9)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.buscar(9)).isInstanceOf(NaoEncontradoException.class);
  }

  @Test
  void criarValidaAntesDeSalvar() {
    var resposta = service.criar(new CoisaRequest("nova"));

    assertThat(resposta.nome()).isEqualTo("nova");
    assertThat(service.chamadas).containsExactly("validar:nova");
    verify(repo).saveAndFlush(any(Coisa.class));
  }

  @Test
  void criarNaoSalvaQuandoValidacaoRecusa() {
    service.recusar = true;

    assertThatThrownBy(() -> service.criar(new CoisaRequest("x")))
        .isInstanceOf(NegocioException.class);
    verify(repo, never()).saveAndFlush(any());
  }

  @Test
  void atualizarAplicaMudancasEValida() {
    when(repo.findById(1)).thenReturn(Optional.of(coisa(1, "velha")));

    var resposta = service.atualizar(1, new CoisaRequest("nova"));

    assertThat(resposta).isEqualTo(new CoisaResponse(1, "nova"));
    assertThat(service.chamadas).containsExactly("validar:nova");
  }

  @Test
  void atualizarInexistenteLancaNaoEncontrado() {
    when(repo.findById(1)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.atualizar(1, new CoisaRequest("x")))
        .isInstanceOf(NaoEncontradoException.class);
  }

  @Test
  void excluirChamaGanchoEDeleta() {
    Coisa existente = coisa(1, "a");
    when(repo.findById(1)).thenReturn(Optional.of(existente));

    service.excluir(1);

    assertThat(service.chamadas).containsExactly("antesDeExcluir:a");
    verify(repo).delete(existente);
  }

  @Test
  void excluirNaoDeletaQuandoGanchoRecusa() {
    when(repo.findById(1)).thenReturn(Optional.of(coisa(1, "a")));
    service.recusar = true;

    assertThatThrownBy(() -> service.excluir(1)).isInstanceOf(NegocioException.class);
    verify(repo, never()).delete(any());
  }
}
