# Documentação da API — CarRental API

## Visão Geral

A **CarRental API** é uma aplicação backend REST para gestão de locação de veículos, desenvolvida em Java 17 com Spring Boot 4 e PostgreSQL. O sistema gerencia clientes, veículos, aluguéis e pagamentos, com regras de negócio para disponibilidade, cálculo de valores e controle de status.

**Base URL:** `http://localhost:8080`

---

## Endpoints

### 🟢 Módulo de Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/clients` | Listar todos os clientes |
| `GET` | `/api/clients/{id}` | Buscar cliente por ID |
| `GET` | `/api/clients/cpf/{cpf}` | Buscar cliente por CPF |
| `POST` | `/api/clients` | Cadastrar novo cliente |

#### `POST /api/clients`

**Request body:**
```json
{
  "nome": "Ronaldo Malta",
  "cpf": "10428298451",
  "telefone": "81999999999",
  "email": "cliente@example.com",
  "endereco": "Recife"
}
```

**Respostas:**
- `200 OK` — cliente criado com sucesso (retorna o objeto criado com ID)
- `400 Bad Request` — CPF já cadastrado

---

### 🟡 Módulo de Carros

> **Nota:** O módulo de carros ainda não possui controller REST implementado. A entidade `Carro` existe com JPA e repositório, aguardando implementação do `CarroController`, `CarroService` e os endpoints correspondentes.

#### Entidade Carro (modelo)

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | `Long` | Identificador único (auto-incremento) |
| `modelo` | `String` | Modelo do veículo |
| `placa` | `String` | Placa (única, 7 caracteres) |
| `categoria` | `String` | Categoria (ex: sedan, SUV, economy) |
| `valorDaDiaria` | `BigDecimal` | Valor da diária |
| `statusDisponibilidade` | `StatusCarro` (enum) | DISPONIVEL / ALUGADO / MANUTENCAO |

---

### 🔴 Módulo de Aluguéis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/rentals` | Listar todos os aluguéis |
| `GET` | `/api/rentals/{id}` | Buscar aluguel por ID |
| `GET` | `/api/rentals/client/{clientId}` | Buscar aluguéis de um cliente |
| `POST` | `/api/rentals` | Criar novo aluguel |
| `PATCH` | `/api/rentals/{id}/status?status=...` | Atualizar status do aluguel |

#### `POST /api/rentals`

**Request body** (NewRentDTO):
```json
{
  "clientCpf": "10428298451",
  "carName": "Civic",
  "carModelType": "sedan",
  "rentValue": 300.00
}
```

**Respostas:**
- `200 OK` — aluguel criado com status PENDING
- `400 Bad Request` — cliente não encontrado com o CPF informado

#### `PATCH /api/rentals/{id}/status`

**Query param:** `status` — um dos valores do enum `RentStatus`:
- `PENDING`
- `CONFIRMED`
- `ACTIVE`
- `FINISHED`
- `CANCELLED`

**Respostas:**
- `200 OK` — status atualizado
- `404 Not Found` — aluguel não encontrado
- `400 Bad Request` — status inválido

---

### 🔵 Módulo de Pagamentos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/payments` | Listar todos os pagamentos |
| `GET` | `/payments/{id}` | Buscar pagamento por ID |
| `GET` | `/payments/rent/{rentId}` | Listar pagamentos de um aluguel |
| `POST` | `/payments` | Criar novo pagamento |

#### `POST /payments`

**Request body** (NewPaymentDTO):
```json
{
  "rentId": 1,
  "valorPago": 300.00,
  "metodoPagamento": "PIX"
}
```

**Respostas:**
- `201 Created` — pagamento criado com status APROVADO
- `400 Bad Request` — aluguel não encontrado OU valor diferente do valor do aluguel OU aluguel já pago

#### Regras de negócio do pagamento:

1. O valor pago **deve ser exatamente igual** ao `rentValue` do aluguel
2. Um aluguel só pode ter um pagamento com status APROVADO
3. Métodos de pagamento aceitos: `PIX`, `CARTAO_CREDITO`

---

## Entidades e Modelos

### Client

```java
@Entity @Table(name = "clients")
public class Client {
    Long id;
    String nome;
    String cpf;       // único
    String telefone;
    String email;
    String endereco;
}
```

### Carro

```java
@Entity @Table(name = "cars")
public class Carro {
    Long id;
    String modelo;
    String placa;     // único
    String categoria;
    BigDecimal valorDaDiaria;
    StatusCarro statusDisponibilidade; // enum: DISPONIVEL, ALUGADO, MANUTENCAO
}
```

### RentModel

```java
@Entity @Table(name = "rentals")
public class RentModel {
    Long id;
    Client client;           // @ManyToOne
    Carro car;               // @ManyToOne
    BigDecimal rentValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    RentStatus status;       // enum: PENDING, CONFIRMED, ACTIVE, FINISHED, CANCELLED
}
```

### Payment

```java
@Entity @Table(name = "payments")
public class Payment {
    Long id;
    RentModel rent;          // @ManyToOne
    BigDecimal valorPago;
    LocalDateTime dataPagamento;
    MetodoPagamento metodoPagamento; // enum: PIX, CARTAO_CREDITO
    StatusPagamento status;          // enum: PENDENTE, APROVADO, RECUSADO
}
```

---

## DTOs

### NewRentDTO
```json
{
  "clientCpf": "string",
  "carName": "string",
  "carModelType": "string",
  "rentValue": 0.00
}
```

### NewPaymentDTO
```json
{
  "rentId": 0,
  "valorPago": 0.00,
  "metodoPagamento": "PIX" | "CARTAO_CREDITO"
}
```

### ClientDTO
```json
{
  "id": 0,
  "nome": "string",
  "cpf": "string",
  "telefone": "string",
  "email": "string",
  "endereco": "string"
}
```

---

## Stack Tecnica

| Componente | Tecnologia |
|------------|-----------|
| Linguagem | Java 17 |
| Framework | Spring Boot 4.0.7 |
| ORM | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL 17 |
| Build | Maven (wrapper incluido) |
| Documentação API | SpringDoc OpenAPI (Swagger UI em `/swagger-ui.html`) |
| Segurança | Spring Security (emExercito) |

---

## Configuracao

Arquivo: `v1/src/main/resources/application.properties`

```properties
spring.application.name=v1
spring.datasource.url=jdbc:postgresql://localhost:5432/carrental
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Como Rodar

```bash
cd v1
chmod +x mvnw
./mvnw spring-boot:run
```

O servidor inicia na porta `8080`. A documentação interativa do Swagger fica disponivel em:

```
http://localhost:8080/swagger-ui.html
```

---

## CI/CD

O projeto possui pipeline GitHub Actions que roda em todo push/PR para `main`:

- **Arquivo:** `.github/workflows/ci.yml`
- **Ambiente:** Ubuntu + PostgreSQL 17 (container)
- **Passos:** checkout → JDK 17 → `./mvnw clean verify`
- **Variaveis de ambiente:** `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

---

## Equipe

| Membro | Papel |
|--------|-------|
| **Rossini (Roberto Rossini)** | Módulo de Aluguel (core): entidade RentModel, regras de negócio, endpoints, integração entre módulos |
| **Ronaldo Malta** | Módulo de Cliente: entidade Client, CRUD, validações |
| **Pedro Vieira** | Módulo de Carro: entidade Carro, repositório |
| **Alexandre** | Suporte administrativo e documentação |

---

## Estado Atual

| Modulo | Status |
|--------|--------|
| Client | ✅ Completo — entidade, repo, service, controller, endpoints REST |
| Carro | ⚙️ Parcial — entidade JPA + repo prontos; faltam controller, service e endpoints REST |
| Aluguel | ✅ Completo — entidade JPA, repo, service, controller, endpoints REST |
| Pagamento | ✅ Completo — entidade JPA, repo, service, controller, endpoints REST |

---

## Escopo (Resumo)

### Faz
- Cadastro e consulta de clientes (por ID e CPF)
- Cadastro de carros com status de disponibilidade
- Aluguel de veículos (criar, listar, alterar status)
- Pagamento de aluguéis (PIX e cartao de credito)
- Validação de CPF unico e valor exato no pagamento

### Nao faz (fora de escopo proposital)
- Gateway de pagamento real (Stripe, PagSeguro)
- Integração com seguradoras
- GPS / rastreamento de veiculos
- Multas de transito
- Controle de manutencao de veiculos
- Notificacoes (email, SMS, push)
