package br.com.logitrack.viagem;

import br.com.logitrack.shared.crud.CrudMapper;
import br.com.logitrack.veiculo.VeiculoResolver;
import br.com.logitrack.veiculo.VeiculoResumo;
import org.springframework.stereotype.Component;

@Component
public class ViagemMapper implements CrudMapper<Viagem, ViagemRequest, ViagemResponse> {

  private final VeiculoResolver veiculos;

  public ViagemMapper(VeiculoResolver veiculos) {
    this.veiculos = veiculos;
  }

  @Override
  public Viagem toEntity(ViagemRequest request) {
    Viagem viagem = new Viagem();
    update(viagem, request);
    return viagem;
  }

  @Override
  public void update(Viagem viagem, ViagemRequest request) {
    viagem.setVeiculo(veiculos.obter(request.veiculoId()));
    viagem.setDataSaida(request.dataSaida());
    viagem.setDataChegada(request.dataChegada());
    viagem.setOrigem(request.origem().trim());
    viagem.setDestino(request.destino().trim());
    viagem.setKmPercorrida(request.kmPercorrida());
  }

  @Override
  public ViagemResponse toResponse(Viagem viagem) {
    return new ViagemResponse(
        viagem.getId(),
        VeiculoResumo.de(viagem.getVeiculo()),
        viagem.getDataSaida(),
        viagem.getDataChegada(),
        viagem.getChegadaEfetiva(),
        viagem.getChegadaEfetiva() == null,
        viagem.getOrigem(),
        viagem.getDestino(),
        viagem.getKmPercorrida());
  }
}
