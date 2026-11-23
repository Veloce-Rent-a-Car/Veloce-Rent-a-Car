package Veloce_Rent_a_Car.listeners;

import Veloce_Rent_a_Car.events.PaymentApprovedEvent;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.RentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentApprovedListener {

    private final RentRepository rentRepository;

    public PaymentApprovedListener(RentRepository rentRepository) {
        this.rentRepository = rentRepository;
    }

    @EventListener
    public void confirmarAluguel(PaymentApprovedEvent event) {
        RentModel rent = event.payment().getRent();
        rent.setStatus(RentModel.RentStatus.CONFIRMED);
        rentRepository.save(rent);
    }
}
