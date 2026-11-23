package Veloce_Rent_a_Car.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representa os dados de um cliente transferidos pela API, incluindo nome, CPF,
 * telefone, email e endereço. O identificador é exposto somente para leitura no JSON.
 */
@Data
public class ClientDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;
    private String cpf;
    private String phone;
    private String email;
    private String address;
}