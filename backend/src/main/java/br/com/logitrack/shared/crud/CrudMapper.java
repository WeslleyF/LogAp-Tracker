package br.com.logitrack.shared.crud;

/** Conversão manual entre entidade e DTOs; cada feature implementa a sua. */
public interface CrudMapper<E, Req, Res> {

  E toEntity(Req request);

  void update(E entity, Req request);

  Res toResponse(E entity);
}
