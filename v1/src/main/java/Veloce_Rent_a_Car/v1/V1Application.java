package Veloce_Rent_a_Car.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Veloce_Rent_a_Car.models.Client;

@SpringBootApplication
public class V1Application {

    public static void main(String[] args) {

        SpringApplication.run(V1Application.class, args);

        Client cliente = new Client();

        cliente.setNome("Ronaldo");
        cliente.setCpf("10428298451");
        cliente.setTelefone("81999999999");
        cliente.setEmail("cliente@example.com");
        cliente.setEndereco("Recife");

        if (cliente.getCpf().length() == 11) {
            System.out.println("CPF valido.");
        } else {
            System.out.println("CPF invalido.");
        }

        if (cliente.getEmail().contains("@")) {
            System.out.println("Email valido.");
        } else {
            System.out.println("Email invalido.");
        }
    }
}