package br.com.logitrack.viagem;

import br.com.logitrack.shared.crud.BaseEntity;
import br.com.logitrack.veiculo.Veiculo;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "viagens")
@Getter
@Setter
@NoArgsConstructor
public class Viagem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "veiculo_id")
  private Veiculo veiculo;

  private LocalDateTime dataSaida;
  private LocalDateTime dataChegada;
  private String origem;
  private String destino;
  private BigDecimal kmPercorrida;

  /** Chegada informada ou saída da próxima viagem do veículo; nula = em andamento. */
  @Formula(
      "coalesce(data_chegada, (select min(prox.data_saida) from viagens prox"
          + " where prox.veiculo_id = veiculo_id and prox.data_saida > data_saida))")
  @Setter(AccessLevel.NONE)
  private LocalDateTime chegadaEfetiva;
}
