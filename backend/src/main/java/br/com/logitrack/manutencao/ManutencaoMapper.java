package br.com.logitrack.manutencao;

import br.com.logitrack.shared.crud.CrudMapper;
import br.com.logitrack.veiculo.VeiculoResolver;
import br.com.logitrack.veiculo.VeiculoResumo;
import org.springframework.stereotype.Component;

@Component
public class ManutencaoMapper
    implements CrudMapper<Manutencao, ManutencaoRequest, ManutencaoResponse> {

  private final VeiculoResolver veiculos;

  public ManutencaoMapper(VeiculoResolver veiculos) {
    this.veiculos = veiculos;
  }

  @Override
  public Manutencao toEntity(ManutencaoRequest request) {
    Manutencao manutencao = new Manutencao();
    update(manutencao, request);
    return manutencao;
  }

  @Override
  public void update(Manutencao manutencao, ManutencaoRequest request) {
    manutencao.setVeiculo(veiculos.obter(request.veiculoId()));
    manutencao.setDataInicio(request.dataInicio());
    manutencao.setDataFinalizacao(request.dataFinalizacao());
    manutencao.setTipoServico(request.tipoServico().trim());
    manutencao.setCustoEstimado(request.custoEstimado());
    manutencao.setStatus(request.status() != null ? request.status() : StatusManutencao.PENDENTE);
  }

  @Override
  public ManutencaoResponse toResponse(Manutencao manutencao) {
    return new ManutencaoResponse(
        manutencao.getId(),
        VeiculoResumo.de(manutencao.getVeiculo()),
        manutencao.getDataInicio(),
        manutencao.getDataFinalizacao(),
        manutencao.getTipoServico(),
        manutencao.getCustoEstimado(),
        manutencao.getStatus());
  }
}
