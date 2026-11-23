package Veloce_Rent_a_Car.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Converte exceções da aplicação em respostas HTTP padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de recursos inexistentes.
     *
     * @param ex exceção que identifica o recurso não encontrado
     * @param req requisição que originou o erro
     * @return resposta HTTP 404 com os detalhes do erro
     */
    @ExceptionHandler({EntityNotFoundException.class, RecursoNaoEncontradoException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, WebRequest req) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /**
     * Trata argumentos inválidos e erros de validação da aplicação.
     *
     * @param ex exceção que descreve os dados inválidos
     * @param req requisição que originou o erro
     * @return resposta HTTP 400 com os detalhes do erro
     */
    @ExceptionHandler({IllegalArgumentException.class, ValidacaoException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, WebRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /**
     * Trata estados inválidos e violações de regras de negócio.
     *
     * @param ex exceção que descreve o conflito
     * @param req requisição que originou o erro
     * @return resposta HTTP 409 com os detalhes do erro
     */
    @ExceptionHandler({IllegalStateException.class, RegraDeNegocioException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, WebRequest req) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /**
     * Trata violações de integridade reportadas pela camada de persistência.
     *
     * @param ex exceção de integridade de dados
     * @param req requisição que originou o erro
     * @return resposta HTTP 409 com os detalhes da violação
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        if (msg != null && (msg.contains("cpf") || msg.contains("placa") || msg.contains("unique") || msg.contains("duplicate"))) {
            return buildError(HttpStatus.CONFLICT, "Registro duplicado: " + msg, req);
        }
        return buildError(HttpStatus.CONFLICT, "Violação de integridade: " + msg, req);
    }

    /**
     * Trata falhas de validação dos argumentos de controladores.
     *
     * @param ex exceção contendo os erros de validação dos campos
     * @param req requisição que originou o erro
     * @return resposta HTTP 400 com os erros dos campos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildError(HttpStatus.BAD_REQUEST, msg, req);
    }

    /**
     * Trata exceções sem um manipulador específico.
     *
     * @param ex exceção não tratada pelos demais manipuladores
     * @param req requisição que originou o erro
     * @return resposta HTTP 500 com uma mensagem genérica
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest req) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", req);
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String msg, WebRequest req) {
        ErrorResponse err = new ErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), msg,
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Representa o corpo padronizado de uma resposta de erro.
     *
     * @param timestamp instante em que o erro foi registrado
     * @param status código numérico do status HTTP
     * @param error descrição do status HTTP
     * @param message mensagem com os detalhes do erro
     * @param path caminho da requisição que originou o erro
     */
    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}
}