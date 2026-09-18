package Veloce_Rent_a_Car.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;

@SpringBootApplication
public class V1Application {

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

        Carro carro = new Carro();

        carro.modelo = "Civic";
        carro.placa = "PEF4A28";
        carro.categoria = "sedan";
        carro.valor_da_diaria = "300";
        carro.status_de_disponibilidade = "disponivel";

        if (carro.placa.length() == 7) {
            System.out.println("placa valida");
        } else {
            System.out.println("placa invalida");
        }
    }
}