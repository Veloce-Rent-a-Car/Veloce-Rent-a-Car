package Veloce_Rent_a_Car.v1;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;

/**
 * Ponto de entrada da aplicação Spring Boot, configurado para descobrir os componentes, entidades
 * e repositórios do projeto Veloce Rent a Car.
 */
@SpringBootApplication(scanBasePackages = "Veloce_Rent_a_Car")
@EntityScan(basePackages = "Veloce_Rent_a_Car.models")
@EnableJpaRepositories(basePackages = "Veloce_Rent_a_Car.repositories")
public class V1Application {

    /**
     * Inicializa o contexto do Spring Boot e executa exemplos locais de validação de CPF, e-mail
     * e placa, imprimindo os resultados no console.
     *
     * @param args argumentos de linha de comando repassados à inicialização do Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(V1Application.class, args);

        Client client = new Client(
            "Ronaldo",
            "10428298451",
            "81999999999",
            "cliente@example.com",
            "Recife"
        );

        if (client.getCpf().length() == 11) {
            System.out.println("CPF valido.");
        } else {
            System.out.println("CPF invalido.");
        }

        if (client.getEmail().contains("@")) {
            System.out.println("Email valido.");
        } else {
            System.out.println("Email invalido.");
        }

        Carro carro = new Carro(
            "Civic",
            "PEF4A28",
            "sedan",
            new BigDecimal("300.00"),
            Carro.StatusCarro.DISPONIVEL
        );

        if (carro.getPlaca().length() == 7) {
            System.out.println("placa valida");
        } else {
            System.out.println("placa invalida");
        }
    }
}
