package Veloce_Rent_a_Car.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Veloce_Rent_a_Car.models.Client;

@SpringBootApplication
public class V1Application {

    public static void main(String[] args) {

        SpringApplication.run(V1Application.class, args);

        Client client = new Client();

        client.setName("Ronaldo");
        client.setCpf("10428298451");
        client.setPhone("81999999999");
        client.setEmail("cliente@example.com");
        client.setAddress("Recife");

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
    }
}