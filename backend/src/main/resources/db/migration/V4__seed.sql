-- Dados originais do desafio (copiados de scripts/, sem alteração)

INSERT INTO veiculos (placa, modelo, tipo, ano) VALUES 
('ABC-1234', 'Fiorino', 'LEVE', 2022),
('XYZ-9876', 'Volvo FH', 'PESADO', 2021),
('KJG-1122', 'Mercedes Sprinter', 'LEVE', 2020),
('LMN-4455', 'Scania R500', 'PESADO', 2023);

-- Inserindo Viagens (Para testar o Dashboard)
INSERT INTO viagens (veiculo_id, data_saida, data_chegada, origem, destino, km_percorrida) VALUES 
(1, '2024-05-01 08:00:00', '2024-05-01 18:00:00', 'São Paulo', 'Rio de Janeiro', 435.00),
(1, '2024-05-05 09:00:00', '2024-05-05 12:00:00', 'Rio de Janeiro', 'Niterói', 20.50),
(2, '2024-05-02 05:00:00', '2024-05-03 20:00:00', 'Curitiba', 'Belo Horizonte', 1000.00);

-- Inserindo Manutenções (Para testar o Cronograma e Custos)
INSERT INTO manutencoes (veiculo_id, data_inicio, data_finalizacao, tipo_servico, custo_estimado, status) VALUES 
(1, '2024-06-10', '2024-06-11', 'Troca de Óleo', 350.00, 'PENDENTE'),
(2, '2024-06-15', '2024-06-17', 'Revisão de Freios', 1500.00, 'PENDENTE'),
(3, '2024-05-20', '2024-05-20', 'Troca de Pneus', 2200.00, 'CONCLUIDA');
-- Viagens com datas relativas a hoje, para o dashboard sempre ter dados.
-- Respeitam as regras de conflito: nenhuma sobreposição por veículo e nenhuma viagem em período de manutenção.
-- Veículo 1: uma viagem fechada pela próxima saída (sem chegada e sem km) e uma em andamento.
-- Veículo 3 não tem viagens relativas: concentra as manutenções do mês corrente.
insert into viagens (veiculo_id, data_saida, data_chegada, origem, destino, km_percorrida) values
(1, current_date - 10 + time '08:00', current_date - 10 + time '18:00', 'São Paulo', 'Campinas', 95.00),
(1, current_date - 6 + time '06:00', null, 'Campinas', 'Ribeirão Preto', null),
(1, current_date - 4 + time '09:00', current_date - 4 + time '17:00', 'Ribeirão Preto', 'São Paulo', 315.00),
(1, current_date - 1 + time '08:00', null, 'São Paulo', 'Santos', null),
(2, current_date - 12 + time '05:00', current_date - 11 + time '20:00', 'Curitiba', 'Belo Horizonte', 1000.00),
(2, current_date - 8 + time '07:00', current_date - 7 + time '19:00', 'Belo Horizonte', 'Salvador', 1370.00),
(2, current_date - 3 + time '06:00', current_date - 2 + time '22:00', 'Salvador', 'Recife', 800.00),
(4, current_date - 9 + time '04:00', null, 'Santos', 'Uberlândia', 700.00),
(4, current_date - 5 + time '05:00', current_date - 4 + time '18:00', 'Uberlândia', 'Goiânia', 380.00);

-- Manutenções relativas: todos os status, inclusive REJEITADA no mês corrente.
-- Do mês corrente (base: primeiro dia do mês) e futuras (base: hoje).
insert into manutencoes (veiculo_id, data_inicio, data_finalizacao, tipo_servico, custo_estimado, status) values
(3, date_trunc('month', current_date)::date + 1, date_trunc('month', current_date)::date + 1, 'Revisão de Freios', 1200.00, 'CONCLUIDA'),
(3, date_trunc('month', current_date)::date + 3, date_trunc('month', current_date)::date + 4, 'Troca de Pneus', 2400.00, 'CONCLUIDA'),
(4, date_trunc('month', current_date)::date + 2, null, 'Motor', 8000.00, 'REJEITADA'),
(2, current_date, current_date + 1, 'Motor', 3200.00, 'EM_REALIZACAO'),
(4, current_date + 3, current_date + 4, 'Troca de Óleo', 450.00, 'PENDENTE'),
(1, current_date + 7, current_date + 8, 'Revisão de Freios', 900.00, 'PENDENTE'),
(4, current_date + 15, current_date + 16, 'Troca de Pneus', 2100.00, 'PENDENTE'),
(1, current_date + 30, null, 'Motor', 5000.00, 'PENDENTE'),
(3, current_date + 45, null, 'Troca de Óleo', 380.00, 'PENDENTE');

-- Usuários de teste (senhas: admin123 e operador123; hashes BCrypt)
insert into usuarios (nome, email, senha_hash, perfil) values
('Administrador', 'admin@logitrack.com', '$2b$10$MGgyd8GOgtE2OZKZdq8Ep.srLR9kMeVLHu9a8ZIxmMd5tZ/tpr.02', 'ADMIN'),
('Operador', 'operador@logitrack.com', '$2b$10$tUD2fx9dWlkHSda6Qp7mKuibQLU09uM6sfo/3NDbHlDaI59PXFtK.', 'OPERADOR');
