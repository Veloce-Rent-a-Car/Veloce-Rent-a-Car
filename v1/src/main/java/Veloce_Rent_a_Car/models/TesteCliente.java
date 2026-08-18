
    
package Veloce_Rent_a_Car.models;

public class TesteCliente {

    public static void main(String[] args) {

       Cliente cliente = new Cliente();
        cliente.nome = "Ronaldo";
        cliente.cpf = "10428298451";
        cliente.telefone = "81999999999";
        cliente.email = "cliente@example.com";
        cliente.endereco = "Recife";

        if (cliente.cpf.length() == 11) {
            System.out.println("CPF valido.");
        } else {
            System.out.println("CPF invalido.");
        }

        if (cliente.email.contains("@")) {
            System.out.println("Email valido.");
        } else {
            System.out.println("Email invalido.");
        }
    }
}
