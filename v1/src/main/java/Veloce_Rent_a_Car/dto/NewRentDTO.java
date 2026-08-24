package Veloce_Rent_a_Car.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewRentDTO {
    private String clientCpf;
    private String carName;
    private String carModelType;
    private BigDecimal rentValue;
}
