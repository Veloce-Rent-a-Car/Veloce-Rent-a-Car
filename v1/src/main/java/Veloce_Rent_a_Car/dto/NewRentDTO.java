package Veloce_Rent_a_Car.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Transporta os dados usados para criar um aluguel: CPF do cliente, nome do carro,
 * tipo ou modelo do carro e valor do aluguel.
 */
@Data
public class NewRentDTO {
    private String clientCpf;
    private String carName;
    private String carModelType;
    private BigDecimal rentValue;
}