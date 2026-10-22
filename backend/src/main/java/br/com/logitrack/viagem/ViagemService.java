package br.com.logitrack.viagem;

import br.com.logitrack.shared.crud.CrudService;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViagemService extends CrudService<Viagem, ViagemRequest, ViagemResponse> {

  private final ViagemRepository repository;
  private final ViagemValidator validator;
  private final EntityManager entityManager;

  public ViagemService(
      ViagemRepository repository,
      ViagemMapper mapper,
      ViagemValidator validator,
      EntityManager entityManager) {
    super(repository, mapper);
    this.repository = repository;
    this.validator = validator;
    this.entityManager = entityManager;
  }

  @Override
  protected void validar(Viagem viagem) {
    validator.validar(viagem);
  }

  /** A chegada efetiva é calculada pelo banco; relê a viagem para a resposta não vir defasada. */
  @Override
  protected void aposSalvar(Viagem viagem) {
    entityManager.refresh(viagem);
  }

  @Transactional(readOnly = true)
  public List<String> cidades() {
    return repository.cidades();
  }
}
