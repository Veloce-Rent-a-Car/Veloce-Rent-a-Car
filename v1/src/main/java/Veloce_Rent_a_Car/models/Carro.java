package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidade que representa um carro disponível para aluguel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDaDiaria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCarro statusDisponibilidade;

    /**
     * Situações de disponibilidade de um carro.
     */
    public enum StatusCarro {
        /** Carro disponível para aluguel. */
        DISPONIVEL,
        /** Carro atualmente alugado. */
        ALUGADO,
        /** Carro indisponível por estar em manutenção. */
        MANUTENCAO
    }

    /**
     * Cria um carro com seus dados principais.
     *
     * @param modelo modelo do carro
     * @param placa placa única do carro
     * @param categoria categoria do carro
     * @param valorDaDiaria valor cobrado por diária
     * @param status situação inicial de disponibilidade
     */
    public Carro(String modelo, String placa, String categoria, BigDecimal valorDaDiaria, StatusCarro status) {
        this.modelo = modelo;
        this.placa = placa;
        this.categoria = categoria;
        this.valorDaDiaria = valorDaDiaria;
        this.statusDisponibilidade = status;
    }
}
