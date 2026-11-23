package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa o aluguel de um carro por um cliente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rentals")
public class RentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Carro car;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentValue;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentStatus status;

    /**
     * Etapas do ciclo de vida de um aluguel.
     */
    public enum RentStatus {
        /** Aluguel aguardando confirmação. */
        PENDING,
        /** Aluguel confirmado. */
        CONFIRMED,
        /** Aluguel em andamento. */
        ACTIVE,
        /** Aluguel concluído. */
        FINISHED,
        /** Aluguel cancelado. */
        CANCELLED
    }
}
