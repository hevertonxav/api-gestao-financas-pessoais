INSERT INTO tb_categoria (id_categoria, nome) VALUES
('8c8c4c7b-5c6a-4d7b-b7df-3d1cb6e3a101', 'Moradia'),
('14c5a2db-0cfa-4d75-a6d0-82d47a6db9d2', 'Alimentação'),
('7f4d93af-efab-4f34-9d59-6c1d8e8a43c3', 'Transporte'),
('b2ef33b7-3a74-4382-9baf-6d8b0fd2d0e4', 'Lazer'),
('e63d0db0-8e8d-48bb-bd34-81dca0e991f5', 'Saúde'),
('59f52e75-d61d-4387-a5e5-71b34dbd54a6', 'Educação'),
('d85b8c7a-58a5-454e-ae2d-4a53d1cf79b7', 'Compras'),
('35e89a8f-84a1-4b90-9cb0-cd5d7b5eb7c8', 'Contas'),
('f2d5d4c7-6d34-49b6-8d4a-f78d5a1e6fd9', 'Investimentos')
ON CONFLICT (id_categoria) DO NOTHING;


INSERT INTO tb_transacao
(id_transacao, valor, tipo, descricao, data, data_criacao, categoria_id_categoria)
VALUES

-- Entradas
(
'0c0fbb37-5dd9-4f4e-91d3-f87b8c63b8b1',
10000.00,
'ENTRADA',
'Salário referente a junho/2025',
'2025-06-05',
'2025-06-05T08:00:00-03:00',
NULL
),

(
'53d8dc8b-7db2-4688-9db4-96c3d0d22a72',
1800.00,
'ENTRADA',
'Recebimento de aluguel do apartamento',
'2025-06-10',
'2025-06-10T10:30:00-03:00',
NULL
),

(
'cc88d9f3-5dcb-4d77-9170-fb9240eaf863',
350.00,
'ENTRADA',
'Estorno de compra no cartão de crédito',
'2025-06-18',
'2025-06-18T14:20:00-03:00',
NULL
),

(
'9a6bc73c-46d6-42d0-8a58-95dce2c9bc94',
1200.00,
'ENTRADA',
'Venda de bicicleta usada',
'2025-06-26',
'2025-06-26T16:00:00-03:00',
NULL
),

-- Saídas
(
'c63d3cb7-3dc5-4c28-ae75-1d1d0b0e7e51',
2500.00,
'SAIDA',
'Aluguel da residência',
'2025-06-06',
'2025-06-06T09:00:00-03:00',
'8c8c4c7b-5c6a-4d7b-b7df-3d1cb6e3a101'
),

(
'25d3f88f-cff8-41b3-a30e-c1c5fceff662',
780.45,
'SAIDA',
'Compras no supermercado',
'2025-06-08',
'2025-06-08T18:30:00-03:00',
'14c5a2db-0cfa-4d75-a6d0-82d47a6db9d2'
),

(
'9db4fd4b-52aa-46b5-9e55-f8c4b4a0f673',
420.00,
'SAIDA',
'Abastecimento do veículo',
'2025-06-12',
'2025-06-12T20:15:00-03:00',
'7f4d93af-efab-4f34-9d59-6c1d8e8a43c3'
),

(
'be4a6d84-8d3d-40d0-a9f6-cba54cbfb784',
185.90,
'SAIDA',
'Conta de energia elétrica',
'2025-06-15',
'2025-06-15T11:10:00-03:00',
'35e89a8f-84a1-4b90-9cb0-cd5d7b5eb7c8'
),

(
'68a7cb89-56d0-4db7-bb1c-c95cf2bb4e95',
120.00,
'SAIDA',
'Plano de internet fibra',
'2025-06-17',
'2025-06-17T08:45:00-03:00',
'35e89a8f-84a1-4b90-9cb0-cd5d7b5eb7c8'
),

(
'f5b9de4e-c6b8-4d89-bef5-c62fd9d3f2a6',
2500.00,
'SAIDA',
'Aplicação mensal em Tesouro Selic',
'2025-06-28',
'2025-06-28T13:00:00-03:00',
'f2d5d4c7-6d34-49b6-8d4a-f78d5a1e6fd9'
)
ON CONFLICT (id_transacao) DO NOTHING;