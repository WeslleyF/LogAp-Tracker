package br.com.logitrack.veiculo;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Ano entre 1980 e o ano atual + 1 (limite superior móvel, por isso não é {@code @Max}). */
@Documented
@Constraint(validatedBy = AnoValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnoValido {

  String message() default "Ano deve estar entre 1980 e o ano atual + 1";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
