# Diagrama de Casos de Uso — CarRental API

## Atores

| Ator | Descrição |
|------|-----------|
| **Cliente** | Pessoa física que deseja alugar um veículo no sistema |
| **Administrador** | Funcionário/operador responsável pelo gerenciamento do sistema |

## Casos de Uso

| ID | Caso de Uso | Ator Principal | Descrição |
|----|-------------|----------------|-----------|
| UC01 | Cadastrar Cliente | Administrador | Registra um novo cliente com dados pessoais (nome, CPF, telefone, email, endereço) |
| UC02 | Consultar Cliente | Administrador | Busca dados de um cliente cadastrado pelo CPF ou ID |
| UC03 | Cadastrar Carro | Administrador | Registra um novo veículo com modelo, placa, categoria, valor da diária e status |
| UC04 | Consultar Carros Disponíveis | Cliente / Administrador | Lista todos os veículos com status disponível para locação |
| UC05 | Realizar Aluguel | Cliente / Administrador | Cria um novo aluguel associando um cliente a um carro disponível, registrando data de retirada e prevista de devolução |
| UC06 | Finalizar Aluguel | Administrador | Registra a devolução do veículo, calcula o valor total e atualiza o status do carro para disponível |
| UC07 | Consultar Histórico de Aluguéis | Cliente / Administrador | Lista todos os aluguéis realizados, filtrando por cliente ou período |

## Diagrama Mermaid

```mermaid
usecaseDiagram
    actor "Cliente" as C
    actor "Administrador" as A

    package "CarRental API" {
        usecase "Cadastrar Cliente" as UC01
        usecase "Consultar Cliente" as UC02
        usecase "Cadastrar Carro" as UC03
        usecase "Consultar Carros\nDisponíveis" as UC04
        usecase "Realizar Aluguel" as UC05
        usecase "Finalizar Aluguel" as UC06
        usecase "Consultar Histórico\nde Aluguéis" as UC07
    }

    C --> UC04
    C --> UC05
    C --> UC07

    A --> UC01
    A --> UC02
    A --> UC03
    A --> UC04
    A --> UC05
    A --> UC06
    A --> UC07

    UC05 ..> UC04 : <<inclui>>
    UC06 ..> UC03 : <<atualiza status>>
```

> **Nota:** O caso de uso *Realizar Aluguel* inclui a verificação de disponibilidade do carro (UC04), e o caso *Finalizar Aluguel* atualiza o status do veículo para disponível (relação com UC03).
