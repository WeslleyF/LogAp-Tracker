package br.com.logitrack.veiculo;

import br.com.logitrack.shared.crud.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo extends BaseEntity {

  private String placa;
  private String modelo;

  @Enumerated(EnumType.STRING)
  private TipoVeiculo tipo;

  private Integer ano;
}
