package Veloce_Rent_a_Car.models.enums;

/**
 * Situações possíveis de um pagamento.
 */
public enum StatusPagamento {
    /** Pagamento registrado, mas ainda não processado. */
    PENDENTE,
    /** Pagamento processado e aprovado. */
    APROVADO,
    /** Pagamento processado e recusado. */
    RECUSADO
}
