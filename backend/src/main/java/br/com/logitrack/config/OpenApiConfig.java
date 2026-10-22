package br.com.logitrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI openApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("LogiTrack Pro API")
                .version("1.0.0")
                .description("Gestão de frota: viagens, veículos, manutenções e dashboard."));
  }
}
