package Veloce_Rent_a_Car.models;

public class Payment {
    private Long id;
    private Aluguel aluguel;
    private BigDecimal valorPago;
    private LocalDateTime dataPagamento;
    private MetodoPagamento metodoPagamento;
    private StatusPagamento status;
    
}
