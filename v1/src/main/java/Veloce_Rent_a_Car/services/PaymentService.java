package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.NewPaymentDTO;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;
import Veloce_Rent_a_Car.repositories.PaymentRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRepository rentRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento nao encontrado com id: " + id));
    }

    public List<Payment> findByRentId(Long rentId) {
        return paymentRepository.findByRentId(rentId);
    }

    public Payment create(NewPaymentDTO dto) {
        RentModel rent = rentRepository.findById(dto.getRentId())
                .orElseThrow(() -> new EntityNotFoundException("Aluguel nao encontrado com id: " + dto.getRentId()));

        if (paymentRepository.existsByRentIdAndStatus(dto.getRentId(), StatusPagamento.APROVADO)) {
            throw new IllegalStateException("Este aluguel ja foi pago.");
        }

        if (dto.getValorPago() == null || dto.getValorPago().compareTo(rent.getRentValue()) != 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser exatamente igual ao valor do aluguel.");
        }

        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setValorPago(dto.getValorPago());
        payment.setDataPagamento(LocalDateTime.now());
        payment.setMetodoPagamento(dto.getMetodoPagamento());
        payment.setStatus(StatusPagamento.APROVADO);

        Payment saved = paymentRepository.save(payment);

        // TODO integracao com o modulo de Aluguel: quando o RentModel tiver um campo
        // de status de reserva (ex.: RentStatus), marcar aqui o aluguel como "CONFIRMADO/PAGO":
        // rent.setStatus(RentStatus.CONFIRMADO_PAGO);
        // rentRepository.save(rent);

        return saved;
    }
}
