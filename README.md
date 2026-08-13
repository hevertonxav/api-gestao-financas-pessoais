![Status](https://img.shields.io/badge/Status-Conclu%C3%ADdo-success?style=flat-square) ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white) ![REST API](https://img.shields.io/badge/REST%20API-02569B?style=flat-square) ![IntelliJ IDEA](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white) ![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white) ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=flat-square&logo=junit5&logoColor=white)

# 💰 API de Gestão de Finanças Pessoais

API REST desenvolvida em Java com Spring Boot para gerenciamento de finanças pessoais, permitindo cadastrar categorias, registrar transações e consultar movimentações financeiras.

## 📇 Sumário
- [Objetivo do projeto](#-objetivo-do-projeto)
- [Tecnologias utilizadas](#-tecnologias-utilizadas)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Decisões de implementação](#-decisões-de-implementação)
- [Endpoints](#-endpoints)
- [Regras de negócio](#-regras-de-negócio)
- [Como executar](#-como-executar)
- [Exemplos de entrada e saída](#-exemplos-de-entrada-e-saída)
- [Aprendizados](#-aprendizados)

## 📌 Objetivo do projeto
O objetivo do projeto é desenvolver uma API REST para registrar e acompanhar movimentações financeiras pessoais, aplicando conceitos de arquitetura em camadas, regras de negócio, validações, persistência de dados e testes automatizados.

## 🛠️ Tecnologias Utilizadas
* **Linguagem:** Java 21
* **Framework:** Spring Boot (Spring Data JPA, Spring Web)
* **ORM:** Hibernate (Gerenciamento de persistência)
* **Banco de Dados:** PostgreSQL
* **Testes Unitários:** JUnit e Mockito
* **Testes de API:** Postman

## 🗂️ Estrutura do projeto

<details>
<summary><strong>👉🏼🚨 Clique para acessar o conteúdo técnico completo</strong></summary>

```text
├── 📁 src/main/java/com/heverton/api_gestao_financas_pessoais
│   ├── 📁 controllers                    # Endpoints REST para gerenciamento de finanças e consulta do extrato financeiro
│   │   └── 📁 handlers                    # Tratamento global de exceções da API
│   ├── 📁 dto                             # Objetos de transferência de dados (entrada e saída)
│   ├── 📁 entities                        # Entidades JPA mapeadas para o banco de dados
|   ├── 📁 exceptions                  # Exceções personalizadas da aplicação
│   ├── 📁 repositories                    # Interfaces JPA para acesso aos dados
│   ├── 📁 services                        # Regras de negócio da aplicação
│   │   
│   └── ☕ ApiGestaoFinancasPessoaisApplication  # Classe principal que inicia o Spring Boot
│
├── 📁 src/main/resources
│   ├── 📄 application.properties          # Configurações da aplicação
│   └── 📄 data.sql                      # Dados iniciais para popular o banco de dados
├── 📁 src/java/test 
|    ├── 📁 controllers # Testes dos controllers REST
|    ├── 📁 repositories # Testes dos repositories
|    ├── 📁 services # Testes das regras de negócios
|    └──  📁 utils  # Classes utilitárias utilizadas nos testes
├── 📁 src/main/resources
│   └──  📄 application-test.properties     # Configurações do ambiente de teste
│
├── 📄 LICENSE  # Licença MIT do projeto
├── 📄 pom.xml  # Gerenciador de dependências Maven (Spring Starter, PostgreSQL, JPA)
│
└── 📖 README.md # Documentação do projeto
```
</details>



## 💡 Decisões de Implementação

- Utilização de DTOs para evitar exposição direta das entidades.
- Separação da aplicação em camadas Controller, Service e Repository.
- Uso de Bean Validation para validação de entrada.
- Tratamento centralizado de exceções
- Implementação de **testes unitários com JUnit e Mockito** para validar as regras de negócio e os métodos da aplicação antes da realização dos testes dos endpoints.
- Uso de **Problem Details** para padronizar as respostas relacionadas a erros da API.

## 🔗 Endpoints

### 📁 Categorias

| Método   | Endpoint                       | Descrição                      |
| -------- |--------------------------------|--------------------------------|
| `POST`   | `/v1/categorias`               | Cadastra uma nova categoria    |
| `GET`    | `/v1/categorias`               | Lista todas as categorias      |
| `PUT`    | `/v1/categorias/{idCategoria}` | Atualiza uma categoria pelo ID |
| `DELETE` | `/v1/categorias/{idCategoria}` | Remove uma categoria           |

### 💰 Transações

| Método   | Endpoint                       | Descrição                      |
|----------|--------------------------------|--------------------------------|
| `POST`   | `/v1/transacoes/entrada`       | Registra uma entrada           |
| `POST`   | `/v1/transacoes/saida`         | Registra uma saída             |
| `GET`    | `/v1/transacoes`               | Lista as transações            |
| `GET`    | `/v1/transacoes/entradas`      | Lista as entradas              |
| `GET`    | `/v1/transacoes/saidas`        | Lista as saídas                |
| `PUT`    | `/v1/transacoes/{idTransacao}` | Atualiza uma transação pelo ID |
| `DELETE` | `/v1/transacoes/{idTransacao}` | Remove uma transação           |

### 🔎 Filtros e paginação

O endpoint `GET /v1/transacoes` permite realizar consultas utilizando filtros e paginação.

**Filtro por categoria:**

```http
GET /v1/transacoes?categoria=Contas
```

**Filtro por período:**

```http
GET /v1/transacoes?dataInicio=2025-06-01&dataFim=2025-06-30
```

**Paginação:**

```http
GET /v1/transacoes?page=0&size=10
```

Os parâmetros de data utilizam o formato `yyyy-MM-dd`.

### 📅 Consulta por período

Também é possível consultar entradas e saídas informando um período:

```http
GET /v1/transacoes/entradas?dataInicio=2025-06-01&dataFim=2025-06-30
```

```http
GET /v1/transacoes/saidas?dataInicio=2025-06-01&dataFim=2025-06-30
```
### 📅 Consulta por categoria e período:

Também é possível consultar usando categoria e períodos como filtro.

```http
GET /v1/transacoes?categoria=Contas&dataInicio=2025-06-01&dataFim=2025-06-
```
### 📊 Resumo Financeiro

O endpoint `GET /v1/resumo` permite consultar um resumo financeiro das transações, com o total de entradas, saídas e saldo do período.

**Consulta sem filtro de período:**

```http
GET /v1/resumo
```

**Consulta com filtro de período:**

```http
GET /v1/resumo?dataInicio=2025-06-01&dataFim=2025-06-30
```

Os parâmetros `dataInicio` e `dataFim` são opcionais e devem seguir o formato `yyyy-MM-dd`.

## 📋 Regras de Negócio

* O valor mínimo de uma transação é de `0.01`.
* O nome da Categoria é obrigatório.
* A descrição da transação é obrigatória.
* A data da transação não pode ser uma data futura.
* A data inicial do período não pode ser posterior à data final.
* Transações do tipo `SAIDA` devem estar associadas a uma categoria existente.
* Transações do tipo `ENTRADA` não precisam estar associadas a uma categoria.
* As transações são classificadas como `ENTRADA` ou `SAIDA`.
* As consultas de transações podem ser filtradas por categoria e/ou período.
* As consultas de transações suportam paginação.
* Quando `dataInicio` e `dataFim` não são informadas, são retornadas as transações de acordo com os demais filtros aplicados.
* Quando apenas `dataFim` é informada, a consulta considera o período de `2020-01-01` até a data informada.
* Quando apenas `dataInicio` é informada, a consulta considera o período entre a data informada e a data atual.

## 🚀 Como Executar

### Pré-requisitos
* Java 21 ou superior instalado.
* Maven, pois o projeto incluir o `mvnw`.
* Git instalado.
* PostgreSQL instalado e configurado.

### Passo a passo
1. **Clonar o repositório:**
```bash
git clone https://github.com/hevertonxav/api-gestao-financas-pessoais.git
```
2. **Configurar o banco de dados:**

Abra o arquivo:

```text
src/
└── main/
    └── resources/
        └── application.properties
```
Configure as informações de acesso ao seu banco de dados PostgreSQL:

```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```
3. **Executar a aplicação:**
```Bash
./mvnw spring-boot:run
```
4. **Testar os Endpoints:**

* A API estará disponível em ``http://localhost:8080``.
* Os endpoints podem ser testados utilizando ferramentas como **Postman** por exemplo.

## 📋 Exemplos de Entrada e Saída

### Entrada

```json
{
    "nome" : "Pets"
}
```
### Saída

```json
{
    "idCategoria": "7f1d091f-bbc5-4419-be95-06c179d410a2",
    "nome": "Pets"
}
```

### Exemplo de erro

```json
{
    "type": "https://api.financas.com.br/erros/recurso-ja-existe",
    "title": "Conflict",
    "status": 409,
    "detail": "Categoria Pets já existente",
    "instance": "/v1/categorias"
}
```

### Exemplo de erros de validações

#### Entrada

```json
{
   "valor": 0.00,
   "data": "2028-06-30",
   "descricao" : ""
}
```

#### Saída

```json
{
    "type": "https://api.financas.com.br/erros/dados-invalidos",
    "title": "Dados inválidos",
    "status": 400,
    "detail": "Um ou mais campos são inválidos.",
    "instance": "/v1/transacoes/entradas",
    "erros": [
        {
            "campo": "descricao",
            "mensagem": "Campo requerido"
        },
        {
            "campo": "valor",
            "mensagem": "O valor mínimo deve ser 0.01"
        },
        {
            "campo": "descricao",
            "mensagem": "O campo nome deve ter no mínimo 3 e no máximo 100 caracteres."
        },
        {
            "campo": "data",
            "mensagem": "Não pode ser uma data futura"
        }
    ]
}
```
### Consulta do resumo financeiro bem sucedida

**Endpoint:** `GET /v1/resumo?dataInicio=2025-06-01&dataFim=2025-07-01`

### Saída

```json
{
    "periodo": {
        "dataInicio": "2025-06-01",
        "dataFim": "2025-07-01"
    },
    "totalEntradas": 15050.00,
    "totalSaidas": 6506.35,
    "saldo": 8543.65
}
```
### Consulta do resumo financeiro com erro

**Endpoint:** `GET /v1/resumo?dataInicio=2025-07-01&dataFim=2025-06-01`

### Saída

```json
{
    "type": "https://api.financas.com.br/erros/regra-de-negocio",
    "title": "Regra de negócio",
    "status": 400,
    "detail": "Data inicial não pode ser depois da data final.",
    "instance": "/v1/resumo"
}
```
## 📚 Aprendizados

- Criação de APIs REST com Spring Boot.
- Uso de DTOs para transferência de dados.
- Validação de dados com Bean Validation.
-  Uso do JUnit e Mockito
- Uso do ProblemDetails
- Tratamento de exceções.
- Integração com banco de dados utilizando JPA/Hibernate.


## 📄 Licença
Este projeto está licenciado sob a **Licença MIT**. Veja o arquivo [LICENSE](./LICENSE) para detalhes sobre direitos autorais e permissões.
