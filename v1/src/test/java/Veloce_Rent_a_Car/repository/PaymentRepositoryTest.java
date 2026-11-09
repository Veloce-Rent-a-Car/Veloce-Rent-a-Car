
package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.models.enums.MetodoPagamento;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;

import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.PaymentRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import Veloce_Rent_a_Car.v1.V1Application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = V1Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private RentRepository rentRepository;

    private Client createClient(String name, String cpf) {
        Client client = new Client(
                name,
                cpf,
                "11999999999",
                "email@test.com",
                "Cidade"
        );

        client.setPassword("test-password-hash");

        return clientRepository.saveAndFlush(client);
    }

    private Carro createCarro(String model, String plate) {
        Carro car = new Carro(
                model,
                plate,
                "sedan",
                new BigDecimal("300.00"),
                Carro.StatusCarro.DISPONIVEL
        );

        return carroRepository.saveAndFlush(car);
    }

    private RentModel createRent(Client client, Carro car) {
        RentModel rent = new RentModel();

        rent.setClient(client);
        rent.setCar(car);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);

        return rentRepository.saveAndFlush(rent);
    }

    private Payment createPayment(
            RentModel rent,
            MetodoPagamento method,
            StatusPagamento status) {

        Payment payment = new Payment();

        payment.setRent(rent);
        payment.setValorPago(new BigDecimal("300.00"));
        payment.setDataPagamento(LocalDateTime.now());
        payment.setMetodoPagamento(method);
        payment.setStatus(status);

        return paymentRepository.saveAndFlush(payment);
    }

    @Test
    void findByRentId_returnsPaymentsForRent() {
        Client client = createClient(
                "Cliente",
                "66666666666"
        );

        Carro car = createCarro("Civic", "EEE5555");

        RentModel rent1 = createRent(client, car);
        RentModel rent2 = createRent(client, car);

        createPayment(
                rent1,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        createPayment(
                rent1,
                MetodoPagamento.CARTAO_CREDITO,
                StatusPagamento.APROVADO
        );

        createPayment(
                rent2,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        List<Payment> paymentsRent1 =
                paymentRepository.findByRentId(rent1.getId());

        List<Payment> paymentsRent2 =
                paymentRepository.findByRentId(rent2.getId());

        assertThat(paymentsRent1).hasSize(2);
        assertThat(paymentsRent2).hasSize(1);

        assertThat(paymentsRent1)
                .allMatch(payment ->
                        payment.getRent().getId()
                                .equals(rent1.getId())
                );

        assertThat(paymentsRent2)
                .allMatch(payment ->
                        payment.getRent().getId()
                                .equals(rent2.getId())
                );
    }

    @Test
    void existsByRentIdAndStatus_whenApprovedExists_returnsTrue() {
        Client client = createClient(
                "Cliente",
                "77777777777"
        );

        Carro car = createCarro("Civic", "FFF6666");
        RentModel rent = createRent(client, car);

        createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        boolean exists =
                paymentRepository.existsByRentIdAndStatus(
                        rent.getId(),
                        StatusPagamento.APROVADO
                );

        assertThat(exists).isTrue();
    }

    @Test
    void existsByRentIdAndStatus_whenApprovedNotExists_returnsFalse() {
        Client client = createClient(
                "Cliente",
                "88888888888"
        );

        Carro car = createCarro("Civic", "GGG7777");
        RentModel rent = createRent(client, car);

        createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.PENDENTE
        );

        boolean exists =
                paymentRepository.existsByRentIdAndStatus(
                        rent.getId(),
                        StatusPagamento.APROVADO
                );

        assertThat(exists).isFalse();
    }

    @Test
    void save_andFindById_works() {
        Client client = createClient(
                "Cliente",
                "99999999999"
        );

        Carro car = createCarro("Civic", "HHH8888");
        RentModel rent = createRent(client, car);

        Payment payment = createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        Payment found = paymentRepository
                .findById(payment.getId())
                .orElseThrow();

        assertThat(found.getValorPago())
                .isEqualByComparingTo(
                        new BigDecimal("300.00")
                );

        assertThat(found.getMetodoPagamento())
                .isEqualTo(MetodoPagamento.PIX);

        assertThat(found.getStatus())
                .isEqualTo(StatusPagamento.APROVADO);
    }
}
