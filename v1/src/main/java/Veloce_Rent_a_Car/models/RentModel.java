package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="UserName")
    private String username;

    @ManyToOne
    @JoinColumn(name="cpf")
    private String cpf;

    private String CarName;
    private String CarModelType;
    private float RentValue;

}
