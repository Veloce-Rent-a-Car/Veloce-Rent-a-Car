package Veloce_Rent_a_Car.dto;

import Veloce_Rent_a_Car.models.enums.MetodoPagamento;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewPaymentDTO {
    private Long rentId;
    private BigDecimal valorPago;
    private MetodoPagamento metodoPagamento;
}
