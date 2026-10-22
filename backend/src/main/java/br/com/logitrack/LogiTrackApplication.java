package br.com.logitrack;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogiTrackApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    SpringApplication.run(LogiTrackApplication.class, args);
  }
}
