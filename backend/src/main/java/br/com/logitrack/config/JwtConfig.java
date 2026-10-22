package br.com.logitrack.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** JWT HS256 com segredo simétrico: a mesma chave assina (login) e valida (resource server). */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  private final SecretKey chave;

  public JwtConfig(JwtProperties propriedades) {
    byte[] bytes = propriedades.secret().getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32) {
      throw new IllegalStateException("JWT_SECRET deve ter no mínimo 32 caracteres");
    }
    this.chave = new SecretKeySpec(bytes, "HmacSHA256");
  }

  @Bean
  JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(chave));
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
  }
}
