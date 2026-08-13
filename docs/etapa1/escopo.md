# Escopo e Domínio do Sistema — CarRental API

## O que o sistema FAZ

A CarRental API é uma aplicação backend que gerencia as operações básicas de uma locadora de veículos. As funcionalidades incluídas no escopo são:

### Cadastro e Gestão de Clientes
- Cadastro de clientes com dados pessoais: nome, CPF, telefone, email e endereço.
- Consulta de clientes por CPF ou identificador.
- Validação de unicidade do CPF.

### Cadastro e Gestão de Veículos
- Cadastro de carros com modelo, placa, categoria (ex.: economy, sedan, SUV), valor da diária e status de disponibilidade.
- Consulta de veículos disponíveis para locação.
- Atualização do status do veículo (disponível / indispendonável) a partir de operações de aluguel.

### Controle de Aluguéis
- Realização de aluguel, associando um cliente a um carro disponível, com registro de data de retirada e data de devolução prevista.
- Finalização de aluguel com registro da data efetiva de devolução.
- Cálculo automático do valor total do aluguel com base no número de dias entre a retirada e a devolução (quantidade de dias × valor da diária).
- Consulta do histórico de aluguéis por cliente ou período.
- Status do aluguel: **ativo** ou **finalizado**.

### Regras de Negócio
- Um carro **só pode ser alugado** se estiver com status **disponível**.
- Ao finalizar o aluguel, o carro automaticamente **volta a ficar disponível**.
- O valor total é calculado automaticamente pelo sistema, não sendo informado manualmente.

---

## O que o sistema NÃO FAZ

As seguintes funcionalidades foram intencionalmente excluídas do escopo, por se tratar de um projeto acadêmico conceitual:

| Funcionalidade | Motivo da exclusão |
|---|---|
| **Pagamento online real** | Não há integração com gateways de pagamento (Stripe, PagSeguro, etc.). O sistema apenas registra o valor calculado. |
| **Integração com seguradoras** | Não é contemplada nenhuma gestão de seguro veicular ou cálculo de cobertura. |
| **Geolocalização / rastreamento** | Não há rastreamento de veículos por GPS nem controle de localização em tempo real. |
| **Multas de trânsito** | O sistema não registra nem gerencia multas associadas a veículos ou clientes. |
| **Manutenção de veículos** | Não há controle de revisões, manutenção preventiva ou corretiva dos carros. |
| **Notificações / comunicação** | Não envia emails, SMS ou notificações push para clientes. |

> **Observação:** A exclusão dessas funcionalidades é uma simplificação propositais para manter o projeto viável dentro do escopo acadêmico, sem comprometer a demonstração dos conceitos de orientação a objetos e desenvolvimento de APIs REST.
