# Justificativa Técnica — CarRental API

## Relevância do Domínio

O mercado de locação de veículos é um dos setores de serviços mais consolidados no Brasil e no mundo. De acordo com dados da ABAL (Associação Brasileira das Locadoras de Veículos), o parque de frota alugada no Brasil cresce consistentemente a cada ano, movimentando bilhões de reais. Empresas como Localiza, Movida e Unidas operam com frota de dezenas de milhares de veículos e dependem de sistemas informatizados para gerenciar suas operações do dia a dia — desde o cadastro de clientes e veículos até o controle de aluguéis e cálculo de valores.

Esse cenário real torna o domínio de aluguel de carros extremamente adequado para um projeto acadêmico: ele possui regras de negócio claras, entidades bem definidas e relacionamentos que desafiam o estudante a pensar em modelagem de dados e comportamento de objetos de forma estruturada. Ao mesmo tempo, a complexidade é suficientemente contida para ser implementada integralmente em um único semestre letivo.

## Adequação para Desenvolvimento Orientado a Objetos

O domínio de aluguel de veículos apresenta características que se alinham diretamente com os pilares da programação orientada a objetos:

**Modelagem de entidades com relacionamentos naturais.** As três entidades principais — Cliente, Carro e Aluguel — possuem relacionamentos claros e sem ambiguidades. Um Aluguel depende de um Cliente e de um Carro (relacionamento N:1 com cada), o que permite explorar conceitos de associação, composição e injeção de dependência. A modelagem do banco de dados relacional espelha diretamente a hierarquia de objetos, facilitando a compreensão entre camadas de persistência e domínio.

**Encapsulamento de regras de negócio.** Regras como a verificação de disponibilidade de um carro antes de permitir um aluguel, o cálculo automático do valor total com base na duração e a atualização do status do veículo ao finalizar uma locação são responsabilidades que pertencem naturalmente ao domínio da entidade. Isso permite praticar o encapsulamento — regras de negócio ficam internas às classes de domínio, expostas via métodos públicos, sem que o reste do sistema precise conhecer os detalhes de implementação.

**Polimorfismo e evolução do sistema.** A definição de categorias de veículo (economy, sedan, SUV, etc.) pode ser modelada hierarquicamente, abrindo espaço para a aplicação de polimorfismo caso o projeto evolua. Da mesma forma, a引入 de novas regras — como cálculo diferenciado de diária por categoria ou política de desconto para aluguéis longos — pode ser feita de forma extensível sem alterar o código existente, demonstrando o princípio Aberto/Fechado.

**Separação de responsabilidades (SRP).** O sistema exige a separação clara entre camadas — controlador, serviço, repositório e modelo de domínio — que é uma das práticas fundamentais da arquitetura de software orientado a objetos. Cada camada tem uma responsabilidade distinta, o que facilita testes, manutenção e compreensão do código por outros membros da equipe.

## Stack Técnica

A escolha de **Java com Spring Boot** se justifica pela robustez do ecossistema Java para aplicações empresariais, a maturidade do Spring Boot para desenvolvimento de APIs REST e a vasta documentação e comunidade disponível. O **PostgreSQL** foi escolhido como banco de dados por ser open-source, robusto, amplamente utilizado em ambientes produtivos e por oferecer excelente suporte a restrições de integridade referencial, que são essenciais para o modelo de dados proposto.

## Conclusão

Em síntese, o domínio de aluguel de carros oferece o equilíbrio ideal entre relevância prática e viabilidade acadêmica. Ele permite ao estudante aplicar conceitos fundamentais de orientação a objetos — modelagem, encapsulamento, responsabilidade única e extensibilidade — em um problema concreto, com regras de negócio que podem ser validadas e testadas de forma clara e mensurável.
