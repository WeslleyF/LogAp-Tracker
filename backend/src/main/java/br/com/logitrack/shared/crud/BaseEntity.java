package br.com.logitrack.shared.crud;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.Hibernate;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Override
  public boolean equals(Object outro) {
    if (this == outro) return true;
    if (!(outro instanceof BaseEntity that)) return false;
    return id != null
        && id.equals(that.id)
        && Hibernate.getClass(this).equals(Hibernate.getClass(that));
  }

  @Override
  public int hashCode() {
    return Hibernate.getClass(this).hashCode();
  }
}
