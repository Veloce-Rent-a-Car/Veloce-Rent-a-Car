package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rents")
public class RentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private String carName;

    @Column(nullable = false)
    private String carModelType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentValue;

    @Column(nullable = false)
    private LocalDateTime rentDate;

    private LocalDateTime returnDate;
}
