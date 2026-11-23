package Veloce_Rent_a_Car.exception;

/**
 * Exceção lançada quando uma operação viola uma regra de negócio.
 */
public class RegraDeNegocioException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem da violação.
     *
     * @param message descrição da regra de negócio violada
     */
    public RegraDeNegocioException(String message) {
        super(message);
    }

    /**
     * Cria a exceção com a mensagem da violação e sua causa.
     *
     * @param message descrição da regra de negócio violada
     * @param cause exceção que originou a violação
     */
    public RegraDeNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}