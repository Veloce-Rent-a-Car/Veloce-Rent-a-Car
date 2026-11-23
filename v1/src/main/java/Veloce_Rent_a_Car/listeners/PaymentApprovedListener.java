package Veloce_Rent_a_Car.listeners;

import Veloce_Rent_a_Car.events.PaymentApprovedEvent;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.RentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reage à aprovação de pagamentos para atualizar os aluguéis associados.
 */
@Component
public class PaymentApprovedListener {

    private final RentRepository rentRepository;

    /**
     * Cria o ouvinte com o repositório de aluguéis.
     *
     * @param rentRepository repositório usado para persistir a confirmação do aluguel
     */
    public PaymentApprovedListener(RentRepository rentRepository) {
        this.rentRepository = rentRepository;
    }

    /**
     * Confirma o aluguel associado ao pagamento aprovado.
     *
     * @param event evento contendo o pagamento aprovado
     */
    @EventListener
    public void confirmarAluguel(PaymentApprovedEvent event) {
        RentModel rent = event.payment().getRent();
        rent.setStatus(RentModel.RentStatus.CONFIRMED);
        rentRepository.save(rent);
    }
}
