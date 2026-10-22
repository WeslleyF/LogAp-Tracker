package br.com.logitrack.manutencao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManutencaoRepository extends JpaRepository<Manutencao, Integer> {

  /** Traz o veículo junto para a listagem não gerar uma consulta por linha. */
  @Override
  @EntityGraph(attributePaths = "veiculo")
  Page<Manutencao> findAll(Pageable pageable);
}
