package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentId(Long rentId);
    boolean existsByRentIdAndStatus(Long rentId, StatusPagamento status);
}