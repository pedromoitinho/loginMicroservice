# API de Formulários - Spring Boot

Este documento descreve como usar a API de formulários integrada ao sistema de autenticação JWT.

## Visão Geral

O sistema permite:
- **Usuários autenticados**: Visualizar formulários e responder (uma vez por formulário)
- **Administradores**: Criar formulários, visualizar analytics e gerenciar respostas

## Endpoints Disponíveis

### 📋 Formulários (Usuários Autenticados)

#### `GET /api/forms`
Lista todos os formulários ativos
- **Auth**: Token JWT obrigatório
- **Response**: Lista de formulários com id, título, descrição

#### `GET /api/forms/{id}`
Detalhes de um formulário específico
- **Auth**: Token JWT obrigatório
- **Response**: Formulário com todas as perguntas

#### `POST /api/forms/{id}/submit`
Submeter resposta ao formulário
- **Auth**: Token JWT obrigatório
- **Body**: 
```json
{
  "answers": [
    {
      "questionId": 1,
      "answerText": "Resposta em texto"
    },
    {
      "questionId": 2,
      "answerNumber": 5
    }
  ]
}
```

#### `GET /api/forms/{id}/check-response`
Verificar se o usuário já respondeu o formulário
- **Auth**: Token JWT obrigatório
- **Response**: `{"hasResponded": true/false}`

### 📊 Analytics (Apenas Admin)

#### `GET /api/forms/{id}/analytics`
Analytics detalhadas do formulário
- **Auth**: Token JWT obrigatório (usuário deve ser "admin")
- **Response**: Estatísticas completas, contadores, médias, etc.

### ⚙️ Administração (Apenas Admin)

#### `POST /api/admin/forms`
Criar novo formulário
- **Auth**: Token JWT obrigatório (usuário deve ser "admin")
- **Body**:
```json
{
  "title": "Título do Formulário",
  "description": "Descrição do formulário",
  "questions": [
    {
      "questionText": "Como você avalia nosso serviço?",
      "type": "RATING",
      "order": 1,
      "isRequired": true,
      "options": null
    },
    {
      "questionText": "Qual serviço você utilizou?",
      "type": "SINGLE_CHOICE",
      "order": 2,
      "isRequired": true,
      "options": "[\"Consultoria\", \"Treinamento\", \"Assessoria\"]"
    }
  ]
}
```

#### `DELETE /api/admin/forms/{id}`
Desativar formulário (soft delete)
- **Auth**: Token JWT obrigatório (usuário deve ser "admin")

## Tipos de Perguntas

- **TEXT**: Texto livre curto
- **TEXTAREA**: Texto livre longo
- **EMAIL**: Campo de email
- **PHONE**: Campo de telefone
- **NUMBER**: Número
- **RATING**: Avaliação numérica (1-5, 1-10, etc.)
- **SINGLE_CHOICE**: Múltipla escolha (uma opção)
- **MULTIPLE_CHOICE**: Múltipla escolha (várias opções)
- **DATE**: Data

## Estrutura do Banco de Dados

```sql
-- Tabela principal de formulários
CREATE TABLE forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Perguntas dos formulários
CREATE TABLE form_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    question_text VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    question_order INT NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    options TEXT,
    FOREIGN KEY (form_id) REFERENCES forms(id)
);

-- Respostas dos usuários
CREATE TABLE form_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    submitted_at TIMESTAMP NOT NULL,
    FOREIGN KEY (form_id) REFERENCES forms(id)
);

-- Respostas individuais às perguntas
CREATE TABLE form_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_response_id BIGINT NOT NULL,
    form_question_id BIGINT NOT NULL,
    answer_text TEXT,
    answer_number INT,
    FOREIGN KEY (form_response_id) REFERENCES form_responses(id),
    FOREIGN KEY (form_question_id) REFERENCES form_questions(id)
);
```

## Exemplo de Uso Completo

### 1. Login do usuário
```bash
curl -X POST https://loginmicroservice-clcl.onrender.com/api/auth/login \
  -d "username=admin&password=senha123"
```

### 2. Listar formulários
```bash
curl -X GET https://loginmicroservice-clcl.onrender.com/api/forms \
  -H "Authorization: Bearer SEU_JWT_TOKEN"
```

### 3. Ver detalhes do formulário
```bash
curl -X GET https://loginmicroservice-clcl.onrender.com/api/forms/1 \
  -H "Authorization: Bearer SEU_JWT_TOKEN"
```

### 4. Responder formulário
```bash
curl -X POST https://loginmicroservice-clcl.onrender.com/api/forms/1/submit \
  -H "Authorization: Bearer SEU_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      {"questionId": 1, "answerNumber": 5},
      {"questionId": 2, "answerText": "Consultoria"},
      {"questionId": 3, "answerText": "Sim, definitivamente"},
      {"questionId": 4, "answerText": "Excelente atendimento!"},
      {"questionId": 5, "answerText": "Minha Empresa LTDA"},
      {"questionId": 6, "answerText": "contato@minhaempresa.com"}
    ]
  }'
```

### 5. Ver analytics (apenas admin)
```bash
curl -X GET https://loginmicroservice-clcl.onrender.com/api/forms/1/analytics \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Dados de Exemplo

Execute o arquivo `sample_form_data.sql` no seu banco de dados para criar um formulário de exemplo com 6 perguntas de diferentes tipos.

## Integração com Frontend

Para integrar com o frontend React/TypeScript, você pode:

1. Criar um serviço `formService.ts` similar ao `authService.ts`
2. Adicionar os tipos TypeScript para as interfaces
3. Criar componentes para exibir formulários
4. Implementar gráficos para as analytics usando Chart.js ou similar

## Segurança

- Todos os endpoints exigem autenticação JWT
- Usuários só podem responder cada formulário uma vez
- Apenas admins podem criar formulários e ver analytics
- Soft delete para formulários (não remove dados, apenas desativa)
- Validação de dados tanto no frontend quanto no backend
