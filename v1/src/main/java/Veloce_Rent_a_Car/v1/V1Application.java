package Veloce_Rent_a_Car.v1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Veloce_Rent_a_Car.models.Cliente;


@SpringBootApplication
public class V1Application {

	public static void main(String[] args) {
		SpringApplication.run(V1Application.class, args);

		Cliente cliente = new Cliente();

		String cpf = "10428298451";
		String email = "cliente@example.com";

		if(cpf.length() == 11){
			System.out.println("CPF valido.");
		}else{
			System.out.println("CPF invalido.");
		}

		if(email.contains("@")){
			System.out.println("Email valido.");
	}else{
			System.out.println("Email invalido.");
		}
	}

}
