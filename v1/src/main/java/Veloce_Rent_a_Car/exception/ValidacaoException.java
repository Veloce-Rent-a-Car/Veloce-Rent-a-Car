package Veloce_Rent_a_Car.exception;

/**
 * Exceção lançada quando os dados fornecidos não atendem às validações da aplicação.
 */
public class ValidacaoException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem de validação.
     *
     * @param message descrição do erro de validação
     */
    public ValidacaoException(String message) {
        super(message);
    }

    /**
     * Cria a exceção com a mensagem de validação e sua causa.
     *
     * @param message descrição do erro de validação
     * @param cause exceção que originou o erro de validação
     */
    public ValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}