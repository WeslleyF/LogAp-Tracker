package br.com.logitrack.shared.crud;

import br.com.logitrack.shared.web.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Endpoints REST padrão; a subclasse só declara o {@code @RequestMapping} do recurso. */
public abstract class CrudController<Req, Res> {

  private final CrudService<?, Req, Res> service;

  protected CrudController(CrudService<?, Req, Res> service) {
    this.service = service;
  }

  @GetMapping
  public PageResponse<Res> listar(@PageableDefault(size = 20) Pageable pageable) {
    return service.listar(pageable);
  }

  @GetMapping("/{id}")
  public Res buscar(@PathVariable Integer id) {
    return service.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Res criar(@Valid @RequestBody Req request) {
    return service.criar(request);
  }

  @PutMapping("/{id}")
  public Res atualizar(@PathVariable Integer id, @Valid @RequestBody Req request) {
    return service.atualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Integer id) {
    service.excluir(id);
  }
}
