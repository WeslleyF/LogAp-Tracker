package br.com.logitrack.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

  Optional<Usuario> findByEmailIgnoreCase(String email);
}
