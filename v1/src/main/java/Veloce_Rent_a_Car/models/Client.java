
package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;

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

    public Client() {
    }

    public Client(String name, String cpf, String phone,
                  String email, String address) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNome() {
        return name;
    }

    public void setNome(String nome) {
        this.name = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelefone() {
        return phone;
    }

    public void setTelefone(String telefone) {
        this.phone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEndereco() {
        return address;
    }

    public void setEndereco(String endereco) {
        this.address = endereco;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
