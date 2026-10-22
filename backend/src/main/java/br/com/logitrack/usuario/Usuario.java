package br.com.logitrack.usuario;

import br.com.logitrack.shared.crud.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

  private String nome;
  private String email;
  private String senhaHash;

  @Enumerated(EnumType.STRING)
  private Perfil perfil;

  private boolean ativo = true;
}
