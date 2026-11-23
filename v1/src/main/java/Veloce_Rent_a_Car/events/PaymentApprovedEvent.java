package Veloce_Rent_a_Car.events;

import Veloce_Rent_a_Car.models.Payment;

/**
 * Evento que sinaliza a aprovação de um pagamento.
 *
 * @param payment pagamento aprovado associado ao evento
 */
public record PaymentApprovedEvent(Payment payment) {
}
