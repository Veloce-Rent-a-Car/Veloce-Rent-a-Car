# 🚗 CarRental API

API REST para um sistema conceito de aluguel de carros, desenvolvida como projeto acadêmico.

## 📋 Sobre o projeto
Sistema back-end que gerencia clientes, veículos, locações (aluguéis) e pagamentos, com regras de negócio para controle de disponibilidade, cálculo de valores, status de reserva e processamento de transações.

## 🛠️ Stack
- **Java 17+**
- **Spring Boot** (Web, Data JPA, Validation)
- **PostgreSQL**
- **Maven**
- (Opcional) **Spring Security** para autenticação, caso o escopo permita

## 👥 Equipe
| Membro | Papel |
|---|---|
| **Rossini (Roberto Rossini)** | Dev responsável pela parte "pesada": arquitetura, regras de negócio, entidade central de Aluguel, integrações entre módulos |
| **Ronaldo Malta** | Dev iniciante em Java — CRUD de entidade simples (Cliente) |
| **Pedro Vieira** | Dev iniciante em Java — CRUD de entidade simples (Carro) |
| **Alexandre** | Dev iniciante em Java, bem aplicado — Módulo de Pagamento e regras de transação |

## 🧩 Divisão de tarefas

### 🔴 Rossini — Módulo de Aluguel (core do sistema)
- Modelagem da entidade `Aluguel` (relaciona Cliente + Carro)
- Regras de negócio: verificação de disponibilidade do carro, cálculo de valor total, datas de retirada/devolução
- Endpoints: criar aluguel, finalizar aluguel, listar aluguéis por cliente/carro
- Validações e tratamento de exceções (`GlobalExceptionHandler`)
- Configuração inicial do projeto (estrutura de pacotes, `application.properties`, conexão com o banco)
- Documentação da API (Swagger/OpenAPI)

### 🟢 Ronaldo — Módulo de Cliente
- Entidade `Cliente` (nome, CPF, telefone, email, endereço)
- CRUD completo (`ClienteController`, `ClienteService`, `ClienteRepository`)
- Validações básicas de campos (CPF, email)
- Testes manuais via Postman/Insomnia

### 🟡 Pedro — Módulo de Carro
- Entidade `Carro` (modelo, placa, categoria, valor da diária, status de disponibilidade)
- CRUD completo (`CarroController`, `CarroService`, `CarroRepository`)
- Endpoint para listar apenas carros disponíveis
- Testes manuais via Postman/Insomnia

### 🔵 Alexandre (Xande) — Módulo de Pagamento
- Modelagem da entidade `Pagamento` (vinculada ao `Aluguel`, com campos como `valorPago`, `dataPagamento`, `metodoPagamento` e `status`)
- Criação de **Enums** para simplificar o domínio: `MetodoPagamento` (PIX, CARTAO_CREDITO) e `StatusPagamento` (PENDENTE, APROVADO, RECUSADO)
- Camadas completas de processamento (`PagamentoController`, `PagamentoService`, `PagamentoRepository`)
- **Regras de negócio e Validações:**
  - Garantir que o valor do pagamento seja exatamente igual ao valor total do aluguel.
  - Impedir que um aluguel que já foi pago receba um segundo pagamento.
- **Integração:** Desenvolver a lógica que, ao confirmar o pagamento, aciona o módulo de `Aluguel` para mudar o status da reserva para "CONFIRMADO/PAGO".
- Testes manuais via Postman/Insomnia (simulando payloads de transação)

## 🗂️ Estrutura sugerida do projeto
```text
src/main/java/com/carrental/
├── cliente/
│   ├── Cliente.java
│   ├── ClienteController.java
│   ├── ClienteService.java
│   └── ClienteRepository.java
├── carro/
│   ├── Carro.java
│   ├── CarroController.java
│   ├── CarroService.java
│   └── CarroRepository.java
├── aluguel/
│   ├── Aluguel.java
│   ├── AluguelController.java
│   ├── AluguelService.java
│   └── AluguelRepository.java
├── pagamento/
│   ├── Pagamento.java
│   ├── PagamentoController.java
│   ├── PagamentoService.java
│   └── PagamentoRepository.java
└── exception/
    └── GlobalExceptionHandler.java
```

## ▶️ Como rodar o projeto
```bash
# clonar o repositório
git clone <url-do-repo>
cd carrental-api

# configurar o banco no application.properties
# (usuário, senha e URL do PostgreSQL local)

# rodar com Maven
./mvnw spring-boot:run
```

## 📌 Status
🚧 Em desenvolvimento — projeto acadêmico.

---

## 📚 Etapa 1 — Proposta e Escopo
Documentos gerados para a primeira etapa do Projeto Integrador:

| Documento | Descrição |
|---|---|
| [Proposta do Sistema](docs/etapa1/proposta.md) | Apresentação e objetivo do projeto |
| [Diagrama de Casos de Uso](docs/etapa1/diagrama-casos-de-uso.md) | Diagrama Mermaid com atores e funcionalidades |
| [Escopo e Domínio](docs/etapa1/escopo.md) | O que o sistema faz e o que não faz |
| [Justificativa Técnica](docs/etapa1/justificativa-tecnica.md) | Por que esse domínio e essa stack |
