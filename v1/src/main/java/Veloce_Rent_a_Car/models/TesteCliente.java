package Veloce_Rent_a_Car.models;

public class TesteCliente {

    public static void main(String[] args) {

        Cliente cliente = new Cliente();

        cliente.setNome("Ronaldo");
        cliente.setCpf("10428298451");
        cliente.setTelefone("81999999999");
        cliente.setEmail("cliente@example.com");
        cliente.setEndereco("Recife");

        System.out.println("Nome: " + cliente.getNome());

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

        System.out.println("Nome: " + cliente.getNome());
        System.out.println("CPF: " + cliente.getCpf());
        System.out.println("Telefone: " + cliente.getTelefone());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Endereco: " + cliente.getEndereco());
    }
}