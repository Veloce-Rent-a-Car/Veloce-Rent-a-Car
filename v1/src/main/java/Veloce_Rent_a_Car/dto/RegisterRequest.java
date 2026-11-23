package Veloce_Rent_a_Car.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados para cadastro de um cliente e declara suas restrições de validação:
 * nome, CPF, telefone, email e senha são obrigatórios, o CPF deve ter 11 caracteres,
 * a senha deve ter ao menos 6 caracteres e o email deve ter formato válido.
 */
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    /**
     * Obtém o nome informado para o cliente.
     *
     * @return nome do cliente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do cliente, limitado pela validação a 100 caracteres.
     *
     * @param nome nome do cliente
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o CPF informado para o cliente.
     *
     * @return CPF do cliente
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF do cliente, cuja validação exige exatamente 11 caracteres.
     *
     * @param cpf CPF do cliente
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Obtém o telefone informado para o cliente.
     *
     * @return telefone do cliente
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone do cliente, limitado pela validação a 20 caracteres.
     *
     * @param telefone telefone do cliente
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Obtém o email informado para o cliente.
     *
     * @return email do cliente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o email do cliente, sujeito à validação de formato de email.
     *
     * @param email email do cliente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém o endereço informado para o cliente.
     *
     * @return endereço do cliente
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Define o endereço do cliente, limitado pela validação a 200 caracteres quando informado.
     *
     * @param endereco endereço do cliente
     */
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    /**
     * Obtém a senha informada para o cadastro.
     *
     * @return senha recebida na requisição
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do cadastro, cuja validação exige ao menos 6 caracteres.
     *
     * @param password senha do cliente
     */
    public void setPassword(String password) {
        this.password = password;
    }
}