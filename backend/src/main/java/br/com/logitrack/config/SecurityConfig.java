package br.com.logitrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filtros(HttpSecurity http, ErrosDeSeguranca erros) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(HttpMethod.POST, "/api/auth/login")
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/usuarios/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .oauth2ResourceServer(
            o ->
                o.jwt(j -> j.jwtAuthenticationConverter(conversor()))
                    .authenticationEntryPoint(erros)
                    .accessDeniedHandler(erros))
        .exceptionHandling(e -> e.authenticationEntryPoint(erros).accessDeniedHandler(erros))
        .build();
  }

  /** O claim {@code perfil} (ADMIN/OPERADOR) vira a autoridade ROLE_ADMIN/ROLE_OPERADOR. */
  private JwtAuthenticationConverter conversor() {
    JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
    autoridades.setAuthoritiesClaimName("perfil");
    autoridades.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
    conversor.setJwtGrantedAuthoritiesConverter(autoridades);
    return conversor;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
