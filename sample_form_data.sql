-- Script para criar um formulário de exemplo
-- Execute este script no seu banco de dados para ter dados de teste

-- Inserir formulário
INSERT INTO forms (title, description, created_at, updated_at, is_active) VALUES 
('Avaliação de Satisfação de Clientes', 'Formulário para avaliar a satisfação dos nossos clientes com nossos serviços', NOW(), NOW(), true);

-- Buscar o ID do formulário criado (assumindo que será 1)
SET @form_id = LAST_INSERT_ID();

-- Inserir perguntas do formulário
INSERT INTO form_questions (form_id, question_text, type, question_order, is_required, options) VALUES 
(@form_id, 'Como você avalia nosso atendimento?', 'RATING', 1, true, NULL),
(@form_id, 'Qual serviço você utilizou?', 'SINGLE_CHOICE', 2, true, '["Consultoria em Segurança", "Treinamento NR-10", "Assessoria em RH", "Psicologia Organizacional", "Saúde Ocupacional"]'),
(@form_id, 'Recomendaria nossos serviços para outras empresas?', 'SINGLE_CHOICE', 3, true, '["Sim, definitivamente", "Provavelmente sim", "Talvez", "Provavelmente não", "Não"]'),
(@form_id, 'Deixe um comentário sobre sua experiência:', 'TEXTAREA', 4, false, NULL),
(@form_id, 'Qual sua empresa?', 'TEXT', 5, true, NULL),
(@form_id, 'Seu email para contato:', 'EMAIL', 6, false, NULL);

-- Verificar se foi criado corretamente
SELECT 'Formulário criado com sucesso!' as message;
SELECT * FROM forms WHERE id = @form_id;
SELECT * FROM form_questions WHERE form_id = @form_id ORDER BY question_order;
