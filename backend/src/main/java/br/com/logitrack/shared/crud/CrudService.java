package br.com.logitrack.shared.crud;

import br.com.logitrack.shared.exception.NaoEncontradoException;
import br.com.logitrack.shared.web.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD completo com transações e 404. Features com regra extra sobrescrevem os ganchos {@link
 * #validar} e {@link #antesDeExcluir}.
 */
@Transactional
public abstract class CrudService<E extends BaseEntity, Req, Res> {

  private final JpaRepository<E, Integer> repository;
  private final CrudMapper<E, Req, Res> mapper;

  protected CrudService(JpaRepository<E, Integer> repository, CrudMapper<E, Req, Res> mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  public PageResponse<Res> listar(Pageable pageable) {
    return PageResponse.de(repository.findAll(pageable).map(mapper::toResponse));
  }

  @Transactional(readOnly = true)
  public Res buscar(Integer id) {
    return mapper.toResponse(buscarEntidade(id));
  }

  public Res criar(Req request) {
    E entidade = mapper.toEntity(request);
    validar(entidade);
    return salvar(entidade);
  }

  public Res atualizar(Integer id, Req request) {
    E entidade = buscarEntidade(id);
    mapper.update(entidade, request);
    validar(entidade);
    return salvar(entidade);
  }

  public void excluir(Integer id) {
    E entidade = buscarEntidade(id);
    antesDeExcluir(entidade);
    repository.delete(entidade);
    repository.flush();
  }

  /** Gancho executado após gravar (ex.: reler colunas calculadas pelo banco). */
  protected void aposSalvar(E entidade) {}

  /** Gancho para regras de negócio ao criar ou atualizar; lança {@code NegocioException}. */
  protected void validar(E entidade) {}

  /** Gancho para regras de negócio antes de excluir; lança {@code NegocioException}. */
  protected void antesDeExcluir(E entidade) {}

  private Res salvar(E entidade) {
    E salva = repository.saveAndFlush(entidade);
    aposSalvar(salva);
    return mapper.toResponse(salva);
  }

  protected E buscarEntidade(Integer id) {
    return repository.findById(id).orElseThrow(() -> new NaoEncontradoException(id));
  }
}
