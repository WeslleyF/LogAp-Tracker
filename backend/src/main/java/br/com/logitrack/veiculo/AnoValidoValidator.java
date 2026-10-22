package br.com.logitrack.veiculo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class AnoValidoValidator implements ConstraintValidator<AnoValido, Integer> {

  private static final int ANO_MINIMO = 1980;

  @Override
  public boolean isValid(Integer ano, ConstraintValidatorContext contexto) {
    return ano == null || (ano >= ANO_MINIMO && ano <= Year.now().getValue() + 1);
  }
}
