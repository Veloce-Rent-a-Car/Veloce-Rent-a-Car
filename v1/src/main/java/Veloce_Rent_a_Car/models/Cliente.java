package Veloce_Rent_a_Car.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;
}
