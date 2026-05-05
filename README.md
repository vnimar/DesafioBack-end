# 🖥️ Desafio Backend – Sistema de Cadastro de Clientes

API RESTful desenvolvida como parte do processo seletivo para desenvolvedor backend da empresa SEA Tecnologia. O sistema permite o cadastro, edição, exclusão e consulta de clientes, com autenticação de usuários e integração com a API ViaCEP para preenchimento automático de endereço.

## 🚀 Tecnologias Utilizadas

- **Java 8** (projeto configurado com compatibilidade para Java 8)
- **Spring Boot 3.3.5**
- **Spring Data JPA** (Hibernate)
- **Spring Security** (autenticação Basic)
- **Maven**
- **H2 Database** (banco de dados em memória)
- **ViaCEP** (consulta de endereço por CEP)
- **Bean Validation** (validações)
- **Tratamento global de exceções** com respostas HTTP apropriadas
  
- ## 📋 Funcionalidades

- Autenticação com dois perfis:
  - **admin** – acesso total (CRUD completo)
  - **padrao** – acesso somente leitura (visualização)
- CRUD de clientes com:
  - Nome, CPF (com validação de dígitos verificadores)
  - Endereço completo (preenchimento automático via CEP, editável)
  - Múltiplos telefones (com tipo e número)
  - Múltiplos e-mails (com validação de formato)
- Máscaras aplicadas na visualização (CPF, CEP, telefone) e removidas na persistência
- Validações de campos (tamanhos, obrigatoriedade, unicidade de CPF, etc.)
- Consulta de CEP integrada ao ViaCEP
- Tratamento centralizado de erros com mensagens claras e status HTTP adequados (400, 404, 409, 500)
- Configuração de CORS para integração com frontend

  ## 🔐 Credenciais de Acesso

| Usuário | Senha      | Permissão         |
|---------|------------|-------------------|
| admin   | 123qwe!@# | Administrador     |
| padrao  | 123qwe123 | Usuário (leitura) |

## 📡 Endpoints da API

### Autenticação
A autenticação é do tipo **Basic Auth**. Inclua o cabeçalho `Authorization: Basic <base64>` em todas as requisições.

### Clientes

| Método   | Endpoint                   | Autorização     | Descrição                        |
|----------|----------------------------|-----------------|----------------------------------|
| `GET`    | `/clientes`                | admin / padrao  | Lista todos os clientes          |
| `GET`    | `/clientes/{id}`           | admin / padrao  | Busca um cliente por ID          |
| `POST`   | `/clientes`                | admin           | Cria um novo cliente             |
| `PUT`    | `/clientes/{id}`           | admin           | Atualiza um cliente existente    |
| `DELETE` | `/clientes/{id}`           | admin           | Remove um cliente                |
| `GET`    | `/clientes/cep/{numero}`   | admin / padrao  | Consulta endereço por CEP        |

### Exemplo de corpo para `POST /clientes`

```json
{
  "nome": "João Silva",
  "cpf": "123.456.789-09",
  "endereco": {
    "cep": "01001-000",
    "logradouro": "Praça da Sé",
    "bairro": "Sé",
    "cidade": "São Paulo",
    "uf": "SP",
    "complemento": "sala 101"
  },
  "telefones": [
    {
      "tipo": "CELULAR",
      "numero": "(11) 98765-4321"
    }
  ],
  "emails": [
    {
      "email": "joao@email.com"
    }
  ]
}

## Link para o Front-end
https://github.com/vnimar/DesafioFrontend
