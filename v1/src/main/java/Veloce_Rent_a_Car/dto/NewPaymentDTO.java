package Veloce_Rent_a_Car.dto;

import Veloce_Rent_a_Car.models.enums.MetodoPagamento;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Transporta os dados para registrar um pagamento: o identificador do aluguel,
 * o valor pago e o método de pagamento utilizado.
 */
@Data
public class NewPaymentDTO {
    private Long rentId;
    private BigDecimal valorPago;
    private MetodoPagamento metodoPagamento;
}