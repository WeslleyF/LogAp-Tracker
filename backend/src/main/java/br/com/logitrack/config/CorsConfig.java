package br.com.logitrack.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Só necessário se um navegador falar direto com a API; no compose e no Vite o proxy evita CORS.
 */
@Configuration
public class CorsConfig {

  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${logitrack.cors.origens}") List<String> origens) {
    CorsConfiguration configuracao = new CorsConfiguration();
    configuracao.setAllowedOrigins(origens);
    configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
    fonte.registerCorsConfiguration("/api/**", configuracao);
    return fonte;
  }
}
