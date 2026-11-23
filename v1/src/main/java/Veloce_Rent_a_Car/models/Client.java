
package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entidade que representa um cliente da locadora.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nome")
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, name = "telefone")
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(name = "endereco")
    private String address;

    @Column(nullable = false)
    private String password;

    /**
     * Cria uma instância vazia para uso da camada de persistência.
     */
    public Client() {
    }

    /**
     * Cria um cliente com seus dados cadastrais.
     *
     * @param name nome do cliente
     * @param cpf CPF único do cliente
     * @param phone telefone do cliente
     * @param email endereço de e-mail do cliente
     * @param address endereço residencial do cliente
     */
    public Client(String name, String cpf, String phone,
                  String email, String address) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    /**
     * Obtém o identificador do cliente.
     *
     * @return identificador do cliente
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do cliente.
     *
     * @param id identificador do cliente
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtém o nome do cliente.
     *
     * @return nome do cliente
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do cliente.
     *
     * @param name nome do cliente
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtém o CPF do cliente.
     *
     * @return CPF do cliente
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do cliente.
     *
     * @param cpf CPF do cliente
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Obtém o telefone do cliente.
     *
     * @return telefone do cliente
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Define o telefone do cliente.
     *
     * @param phone telefone do cliente
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Obtém o endereço de e-mail do cliente.
     *
     * @return endereço de e-mail do cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o endereço de e-mail do cliente.
     *
     * @param email endereço de e-mail do cliente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém o endereço residencial do cliente.
     *
     * @return endereço residencial do cliente
     */
    public String getAddress() {
        return address;
    }

    /**
     * Define o endereço residencial do cliente.
     *
     * @param address endereço residencial do cliente
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Obtém a senha do cliente.
     *
     * @return senha do cliente
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do cliente.
     *
     * @param password senha do cliente
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
