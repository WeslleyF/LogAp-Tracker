package br.com.logitrack.veiculo;

import br.com.logitrack.shared.crud.CrudMapper;
import org.springframework.stereotype.Component;

@Component
public class VeiculoMapper implements CrudMapper<Veiculo, VeiculoRequest, VeiculoResponse> {

  @Override
  public Veiculo toEntity(VeiculoRequest request) {
    Veiculo veiculo = new Veiculo();
    update(veiculo, request);
    return veiculo;
  }

  @Override
  public void update(Veiculo veiculo, VeiculoRequest request) {
    veiculo.setPlaca(request.placa().toUpperCase());
    veiculo.setModelo(request.modelo().trim());
    veiculo.setTipo(request.tipo());
    veiculo.setAno(request.ano());
  }

  @Override
  public VeiculoResponse toResponse(Veiculo veiculo) {
    return new VeiculoResponse(
        veiculo.getId(),
        veiculo.getPlaca(),
        veiculo.getModelo(),
        veiculo.getTipo(),
        veiculo.getAno());
  }
}
