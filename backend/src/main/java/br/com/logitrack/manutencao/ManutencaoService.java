package br.com.logitrack.manutencao;

import br.com.logitrack.shared.crud.CrudService;
import org.springframework.stereotype.Service;

@Service
public class ManutencaoService
    extends CrudService<Manutencao, ManutencaoRequest, ManutencaoResponse> {

  public ManutencaoService(ManutencaoRepository repository, ManutencaoMapper mapper) {
    super(repository, mapper);
  }
}
