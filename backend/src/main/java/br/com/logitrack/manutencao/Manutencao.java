package br.com.logitrack.manutencao;

import br.com.logitrack.shared.crud.BaseEntity;
import br.com.logitrack.veiculo.Veiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manutencoes")
@Getter
@Setter
@NoArgsConstructor
public class Manutencao extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "veiculo_id")
  private Veiculo veiculo;

  private LocalDate dataInicio;
  private LocalDate dataFinalizacao;
  private String tipoServico;
  private BigDecimal custoEstimado;

  @Enumerated(EnumType.STRING)
  private StatusManutencao status;
}
