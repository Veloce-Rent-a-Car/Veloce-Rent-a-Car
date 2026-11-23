package Veloce_Rent_a_Car.dto;

/**
 * Transporta o CPF e a senha enviados no corpo de uma requisição de autenticação.
 */
public class LoginRequest {

    private String cpf;
    private String password;

    /**
     * Obtém o CPF informado para autenticação.
     *
     * @return CPF recebido na requisição
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF que será usado na autenticação.
     *
     * @param cpf CPF do usuário
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Obtém a senha informada para autenticação.
     *
     * @return senha recebida na requisição
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha que será usada na autenticação.
     *
     * @param password senha do usuário
     */
    public void setPassword(String password) {
        this.password = password;
    }
}