package Veloce_Rent_a_Car.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem do recurso não encontrado.
     *
     * @param message descrição do recurso não encontrado
     */
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }

    /**
     * Cria a exceção com a mensagem do recurso não encontrado e sua causa.
     *
     * @param message descrição do recurso não encontrado
     * @param cause exceção que originou a falha de busca
     */
    public RecursoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}