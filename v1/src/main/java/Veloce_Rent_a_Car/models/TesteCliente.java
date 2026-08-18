
    
package Veloce_Rent_a_Car.models;

public class TesteCliente {

    public static void main(String[] args) {

        String cpf = "10428298451";
        String email = "cliente@example.com";

        if (cpf.length() == 11) {
            System.out.println("CPF valido.");
        } else {
            System.out.println("CPF invalido.");
        }

        if (email.contains("@")) {
            System.out.println("Email valido.");
        } else {
            System.out.println("Email invalido.");
        }
    }
}
