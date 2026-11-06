package Veloce_Rent_a_Car.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
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

    public enum StatusCarro {
        DISPONIVEL,
        ALUGADO,
        MANUTENCAO
    }

    public Carro(String modelo, String placa, String categoria, BigDecimal valorDaDiaria, StatusCarro status) {
        this.modelo = modelo;
        this.placa = placa;
        this.categoria = categoria;
        this.valorDaDiaria = valorDaDiaria;
        this.statusDisponibilidade = status;
    }
}
