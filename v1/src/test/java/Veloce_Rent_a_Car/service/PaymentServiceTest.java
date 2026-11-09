package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.dto.NewPaymentDTO;
import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.exception.RegraDeNegocioException;
import Veloce_Rent_a_Car.exception.ValidacaoException;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.models.enums.MetodoPagamento;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;
import Veloce_Rent_a_Car.repositories.PaymentRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import Veloce_Rent_a_Car.services.PaymentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentRepository rentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void findAll_returnsAllPayments() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();
        when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentService.findAll();

        assertThat(result).hasSize(2);
        verify(paymentRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment result = paymentService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotExists_throwsRecursoNaoEncontradoException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findById(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Pagamento nao encontrado com id: 999");
    }

    @Test
    void findByRentId_returnsPaymentsForRent() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();
        when(paymentRepository.findByRentId(1L)).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentService.findByRentId(1L);

        assertThat(result).hasSize(2);
        verify(paymentRepository).findByRentId(1L);
    }

    @Test
    void create_whenValid_createsPayment() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setRentValue(new BigDecimal("300.00"));

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(1L);
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(paymentRepository.existsByRentIdAndStatus(1L, StatusPagamento.APROVADO)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        Payment result = paymentService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getValorPago()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(result.getMetodoPagamento()).isEqualTo(MetodoPagamento.PIX);
        assertThat(result.getStatus()).isEqualTo(StatusPagamento.APROVADO);
        assertThat(result.getDataPagamento()).isNotNull();
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void create_whenRentNotExists_throwsRecursoNaoEncontradoException() {
        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(999L);
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        when(rentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.create(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Aluguel nao encontrado com id: 999");
    }

    @Test
    void create_whenRentAlreadyPaid_throwsRegraDeNegocioException() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setRentValue(new BigDecimal("300.00"));

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(1L);
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(paymentRepository.existsByRentIdAndStatus(1L, StatusPagamento.APROVADO)).thenReturn(true);

        assertThatThrownBy(() -> paymentService.create(dto))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Este aluguel ja foi pago");
    }

    @Test
    void create_whenValorDifferentFromRentValue_throwsValidacaoException() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setRentValue(new BigDecimal("300.00"));

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(1L);
        dto.setValorPago(new BigDecimal("250.00")); // diferente!
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(paymentRepository.existsByRentIdAndStatus(1L, StatusPagamento.APROVADO)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.create(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("valor do pagamento deve ser exatamente igual");
    }

    @Test
    void create_whenValorIsNull_throwsValidacaoException() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setRentValue(new BigDecimal("300.00"));

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(1L);
        dto.setValorPago(null);
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(paymentRepository.existsByRentIdAndStatus(1L, StatusPagamento.APROVADO)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.create(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("valor do pagamento deve ser exatamente igual");
    }
}