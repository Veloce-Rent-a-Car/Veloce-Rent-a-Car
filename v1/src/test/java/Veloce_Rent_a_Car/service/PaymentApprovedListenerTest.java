package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.events.PaymentApprovedEvent;
import Veloce_Rent_a_Car.listeners.PaymentApprovedListener;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.RentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentApprovedListenerTest {

    @Mock
    private RentRepository rentRepository;

    @InjectMocks
    private PaymentApprovedListener listener;

    @Test
    void confirmarAluguel_whenPaymentIsApproved_confirmsAndSavesRent() {
        RentModel rent = new RentModel();
        rent.setStatus(RentModel.RentStatus.PENDING);
        Payment payment = Payment.builder().rent(rent).build();
        PaymentApprovedEvent event = new PaymentApprovedEvent(payment);

        listener.confirmarAluguel(event);

        assertThat(rent.getStatus()).isEqualTo(RentModel.RentStatus.CONFIRMED);
        verify(rentRepository).save(rent);
    }
}
