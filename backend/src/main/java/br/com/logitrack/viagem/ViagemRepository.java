package br.com.logitrack.viagem;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViagemRepository extends JpaRepository<Viagem, Integer> {

  @Override
  @EntityGraph(attributePaths = "veiculo")
  Page<Viagem> findAll(Pageable pageable);

  /** Cidades já usadas como origem ou destino, para o autocomplete. */
  @Query(
      value =
          "select origem from viagens where origem is not null"
              + " union select destino from viagens where destino is not null order by 1",
      nativeQuery = true)
  List<String> cidades();
}
