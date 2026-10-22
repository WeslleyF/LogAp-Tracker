create table usuarios (
    id serial primary key,
    nome varchar(100) not null,
    email varchar(150) not null unique,
    senha_hash varchar(100) not null,
    perfil varchar(20) not null check (perfil in ('ADMIN', 'OPERADOR')),
    ativo boolean not null default true
);
