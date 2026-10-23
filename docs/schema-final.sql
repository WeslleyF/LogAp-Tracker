-- Schema consolidado após as migrações V1 a V4 (gerado com pg_dump --schema-only).
-- Referência de leitura; a fonte da verdade são as migrações em backend/src/main/resources/db/migration/.
-- PostgreSQL database dump

\restrict wIf6uJDZQilbEKyKfPgbP1Bo2KSo2hzdpsK724sqXTP3Qtwp9KMR06gJxVcgj0B

-- Dumped from database version 16.15
-- Dumped by pg_dump version 16.15

-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);

-- Name: manutencoes; Type: TABLE; Schema: public; Owner: -

CREATE TABLE public.manutencoes (
    id integer NOT NULL,
    veiculo_id integer NOT NULL,
    data_inicio date NOT NULL,
    data_finalizacao date,
    tipo_servico character varying(100),
    custo_estimado numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying,
    CONSTRAINT ck_manutencoes_custo_nao_negativo CHECK ((custo_estimado >= (0)::numeric)),
    CONSTRAINT ck_manutencoes_finalizacao_apos_inicio CHECK (((data_finalizacao IS NULL) OR (data_finalizacao >= data_inicio))),
    CONSTRAINT ck_manutencoes_status CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'EM_REALIZACAO'::character varying, 'CONCLUIDA'::character varying, 'REJEITADA'::character varying])::text[])))
);

-- Name: manutencoes_id_seq; Type: SEQUENCE; Schema: public; Owner: -

CREATE SEQUENCE public.manutencoes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Name: manutencoes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -

ALTER SEQUENCE public.manutencoes_id_seq OWNED BY public.manutencoes.id;

-- Name: usuarios; Type: TABLE; Schema: public; Owner: -

CREATE TABLE public.usuarios (
    id integer NOT NULL,
    nome character varying(100) NOT NULL,
    email character varying(150) NOT NULL,
    senha_hash character varying(100) NOT NULL,
    perfil character varying(20) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    CONSTRAINT usuarios_perfil_check CHECK (((perfil)::text = ANY ((ARRAY['ADMIN'::character varying, 'OPERADOR'::character varying])::text[])))
);

-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: -

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;

-- Name: veiculos; Type: TABLE; Schema: public; Owner: -

CREATE TABLE public.veiculos (
    id integer NOT NULL,
    placa character varying(10) NOT NULL,
    modelo character varying(50) NOT NULL,
    tipo character varying(20) NOT NULL,
    ano integer,
    CONSTRAINT veiculos_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['LEVE'::character varying, 'PESADO'::character varying])::text[])))
);

-- Name: veiculos_id_seq; Type: SEQUENCE; Schema: public; Owner: -

CREATE SEQUENCE public.veiculos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Name: veiculos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -

ALTER SEQUENCE public.veiculos_id_seq OWNED BY public.veiculos.id;

-- Name: viagens; Type: TABLE; Schema: public; Owner: -

CREATE TABLE public.viagens (
    id integer NOT NULL,
    veiculo_id integer NOT NULL,
    data_saida timestamp without time zone NOT NULL,
    data_chegada timestamp without time zone,
    origem character varying(100),
    destino character varying(100),
    km_percorrida numeric(10,2),
    CONSTRAINT ck_viagens_chegada_apos_saida CHECK (((data_chegada IS NULL) OR (data_chegada > data_saida))),
    CONSTRAINT ck_viagens_km_positiva CHECK (((km_percorrida IS NULL) OR (km_percorrida > (0)::numeric)))
);

-- Name: viagens_id_seq; Type: SEQUENCE; Schema: public; Owner: -

CREATE SEQUENCE public.viagens_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Name: viagens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -

ALTER SEQUENCE public.viagens_id_seq OWNED BY public.viagens.id;

-- Name: manutencoes id; Type: DEFAULT; Schema: public; Owner: -

ALTER TABLE ONLY public.manutencoes ALTER COLUMN id SET DEFAULT nextval('public.manutencoes_id_seq'::regclass);

-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: -

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);

-- Name: veiculos id; Type: DEFAULT; Schema: public; Owner: -

ALTER TABLE ONLY public.veiculos ALTER COLUMN id SET DEFAULT nextval('public.veiculos_id_seq'::regclass);

-- Name: viagens id; Type: DEFAULT; Schema: public; Owner: -

ALTER TABLE ONLY public.viagens ALTER COLUMN id SET DEFAULT nextval('public.viagens_id_seq'::regclass);

-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);

-- Name: manutencoes manutencoes_pkey; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.manutencoes
    ADD CONSTRAINT manutencoes_pkey PRIMARY KEY (id);

-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);

-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);

-- Name: veiculos veiculos_pkey; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.veiculos
    ADD CONSTRAINT veiculos_pkey PRIMARY KEY (id);

-- Name: veiculos veiculos_placa_key; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.veiculos
    ADD CONSTRAINT veiculos_placa_key UNIQUE (placa);

-- Name: viagens viagens_pkey; Type: CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.viagens
    ADD CONSTRAINT viagens_pkey PRIMARY KEY (id);

-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);

-- Name: ix_manutencoes_data_inicio; Type: INDEX; Schema: public; Owner: -

CREATE INDEX ix_manutencoes_data_inicio ON public.manutencoes USING btree (data_inicio);

-- Name: ix_viagens_veiculo_saida; Type: INDEX; Schema: public; Owner: -

CREATE INDEX ix_viagens_veiculo_saida ON public.viagens USING btree (veiculo_id, data_saida);

-- Name: manutencoes manutencoes_veiculo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.manutencoes
    ADD CONSTRAINT manutencoes_veiculo_id_fkey FOREIGN KEY (veiculo_id) REFERENCES public.veiculos(id) ON DELETE CASCADE;

-- Name: viagens viagens_veiculo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -

ALTER TABLE ONLY public.viagens
    ADD CONSTRAINT viagens_veiculo_id_fkey FOREIGN KEY (veiculo_id) REFERENCES public.veiculos(id) ON DELETE CASCADE;

-- PostgreSQL database dump complete

\unrestrict wIf6uJDZQilbEKyKfPgbP1Bo2KSo2hzdpsK724sqXTP3Qtwp9KMR06gJxVcgj0B

