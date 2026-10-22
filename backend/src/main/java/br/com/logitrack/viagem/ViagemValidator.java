package br.com.logitrack.viagem;

import static br.com.logitrack.shared.sql.SqlLoader.carregar;

import br.com.logitrack.shared.exception.NegocioException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/** Conflitos de agenda verificados em SQL; ver as regras nos arquivos {@code sql/viagem}. */
@Component
public class ViagemValidator {

  private static final DateTimeFormatter DATA_HORA =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private static final String CONFLITO_VIAGEM = carregar("sql/viagem/conflito_viagem.sql");
  private static final String CONFLITO_MANUTENCAO = carregar("sql/viagem/conflito_manutencao.sql");

  record ViagemConflitante(
      Integer id,
      String origem,
      String destino,
      LocalDateTime dataSaida,
      LocalDateTime chegadaEfetiva) {}

  record ManutencaoConflitante(
      Integer id, String tipoServico, LocalDate dataInicio, LocalDate dataFim) {}

  private final JdbcClient jdbc;

  public ViagemValidator(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public void validar(Viagem viagem) {
    String placa = viagem.getVeiculo().getPlaca();

    jdbc.sql(CONFLITO_VIAGEM)
        .param("veiculoId", viagem.getVeiculo().getId())
        .param("id", viagem.getId())
        .param("dataSaida", viagem.getDataSaida())
        .param("dataChegada", viagem.getDataChegada())
        .param("origem", viagem.getOrigem())
        .param("destino", viagem.getDestino())
        .query(ViagemConflitante.class)
        .optional()
        .ifPresent(
            v -> {
              throw new NegocioException(
                  "Conflito de agenda: o veículo %s já tem a viagem %s → %s, de %s a %s"
                      .formatted(
                          placa,
                          v.origem(),
                          v.destino(),
                          v.dataSaida().format(DATA_HORA),
                          v.chegadaEfetiva() == null
                              ? "em andamento"
                              : v.chegadaEfetiva().format(DATA_HORA)));
            });

    jdbc.sql(CONFLITO_MANUTENCAO)
        .param("veiculoId", viagem.getVeiculo().getId())
        .param("id", viagem.getId())
        .param("dataSaida", viagem.getDataSaida())
        .param("dataChegada", viagem.getDataChegada())
        .query(ManutencaoConflitante.class)
        .optional()
        .ifPresent(
            m -> {
              throw new NegocioException(
                  "Conflito com manutenção: o veículo %s está em manutenção (%s) de %s a %s"
                      .formatted(
                          placa,
                          m.tipoServico(),
                          m.dataInicio().format(DATA),
                          m.dataFim().format(DATA)));
            });
  }
}
