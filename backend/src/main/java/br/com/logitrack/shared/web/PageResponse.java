package br.com.logitrack.shared.web;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements) {

  public static <T> PageResponse<T> de(Page<T> pagina) {
    return new PageResponse<>(
        pagina.getContent(), pagina.getNumber(), pagina.getSize(), pagina.getTotalElements());
  }
}
