# Guia de Teste Rápido — CarRental API

## Pré-requisitos

- Java 17 instalado
- PostgreSQL rodando na porta 5432 com banco `carrental` criado
- Maven (ou usar o wrapper `./mvnw`)

## 1. Subir o servidor

```bash
cd v1
./mvnw spring-boot:run
```

O servidor fica disponível em `http://localhost:8080`. Aguarde o log `Started V1Application in ... seconds`.

## 2. Testar os endpoints com curl

### 2.1. Criar um cliente

```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ronaldo Malta",
    "cpf": "10428298451",
    "telefone": "81999999999",
    "email": "cliente@example.com",
    "endereco": "Recife"
  }'
```

Resposta esperada: `200 OK` com o cliente criado (incluindo o `id` gerado).

### 2.2. Listar todos os clientes

```bash
curl http://localhost:8080/api/clients
```

### 2.3. Buscar cliente por CPF

```bash
curl http://localhost:8080/api/clients/cpf/10428298451
```

### 2.4. Criar um aluguel

```bash
curl -X POST http://localhost:8080/api/rentals \
  -H "Content-Type: application/json" \
  -d '{
    "clientCpf": "10428298451",
    "carName": "Civic",
    "carModelType": "sedan",
    "rentValue": 300.00
  }'
```

Resposta esperada: `200 OK` com o aluguel criado (status PENDING, id gerado).

### 2.5. Listar aluguéis

```bash
curl http://localhost:8080/api/rentals
```

### 2.6. Listar aluguéis de um cliente

```bash
curl http://localhost:8080/api/rentals/client/1
```

### 2.7. Alterar status do aluguel

```bash
curl -X PATCH "http://localhost:8080/api/rentals/1/status?status=CONFIRMED"
```

Status válidos: `PENDING`, `CONFIRMED`, `ACTIVE`, `FINISHED`, `CANCELLED`.

### 2.8. Criar um pagamento

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "rentId": 1,
    "valorPago": 300.00,
    "metodoPagamento": "PIX"
  }'
```

Resposta esperada: `201 Created` com o pagamento criado.

### 2.9. Listar pagamentos

```bash
curl http://localhost:8080/payments
```

## 3. Swagger UI (documentação interativa)

Abra no navegador:

```
http://localhost:8080/swagger-ui.html
```

Exporte a spec OpenAPI:

```
http://localhost:8080/v3/api-docs
```

---

## Erros comuns

| Erro | Causa | Solução |
|------|-------|---------|
| `Connection refused` no PostgreSQL | Banco não tá rodando ou credencial errada | Verifique `application.properties` e se o PostgreSQL tá na porta 5432 |
| `400 Bad Request` ao criar pagamento | Valor diferente do valor do aluguel, ou aluguel já pago | Certifique-se de que `valorPago == rentValue` e que o aluguel ainda não tem pagamento APROVADO |
| `404 Not Found` ao criar aluguel | Cliente com aquele CPF não existe | Crie o cliente primeiro |
| `400 Bad Request` ao alterar status do aluguel | Status inválido (escrito errado) | Use exatamente: PENDING, CONFIRMED, ACTIVE, FINISHED, CANCELLED |
