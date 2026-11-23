package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para persistência e consulta de pagamentos.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * Lista os pagamentos vinculados a um aluguel.
     *
     * @param rentId identificador do aluguel
     * @return pagamentos do aluguel, ou uma lista vazia se não houver resultados
     */
    List<Payment> findByRentId(Long rentId);
    /**
     * Verifica se um aluguel possui pagamento com a situação informada.
     *
     * @param rentId identificador do aluguel
     * @param status situação do pagamento
     * @return {@code true} se houver ao menos um pagamento correspondente; caso contrário, {@code false}
     */
    boolean existsByRentIdAndStatus(Long rentId, StatusPagamento status);
}