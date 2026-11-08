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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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

    private Client createClient(String nome, String cpf) {
        Client client = new Client(nome, cpf, "11999999999", "email@test.com", "Cidade");
        return clientRepository.save(client);
    }

    private Carro createCarro(String modelo, String placa) {
        Carro carro = new Carro(modelo, placa, "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        return carroRepository.save(carro);
    }

    private RentModel createRent(Client client, Carro carro) {
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        return rentRepository.save(rent);
    }

    private Payment createPayment(RentModel rent, MetodoPagamento metodo) {
        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setValorPago(new BigDecimal("300.00"));
        payment.setDataPagamento(LocalDateTime.now());
        payment.setMetodoPagamento(metodo);
        payment.setStatus(StatusPagamento.APROVADO);
        return paymentRepository.save(payment);
    }

    @Test
    void findByRentId_returnsPaymentsForRent() {
        Client client = createClient("Cliente", "66666666666");
        Carro carro = createCarro("Civic", "EEE5555");
        RentModel rent1 = createRent(client, carro);
        RentModel rent2 = createRent(client, carro);

        createPayment(rent1, MetodoPagamento.PIX);
        createPayment(rent1, MetodoPagamento.CARTAO_CREDITO);
        createPayment(rent2, MetodoPagamento.PIX);

        List<Payment> paymentsRent1 = paymentRepository.findByRentId(rent1.getId());
        List<Payment> paymentsRent2 = paymentRepository.findByRentId(rent2.getId());

        assertThat(paymentsRent1).hasSize(2);
        assertThat(paymentsRent2).hasSize(1);
    }

    @Test
    void existsByRentIdAndStatus_whenApprovedExists_returnsTrue() {
        Client client = createClient("Cliente", "77777777777");
        Carro carro = createCarro("Civic", "FFF6666");
        RentModel rent = createRent(client, carro);
        createPayment(rent, MetodoPagamento.PIX);

        boolean exists = paymentRepository.existsByRentIdAndStatus(rent.getId(), StatusPagamento.APROVADO);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByRentIdAndStatus_whenApprovedNotExists_returnsFalse() {
        Client client = createClient("Cliente", "88888888888");
        Carro carro = createCarro("Civic", "GGG7777");
        RentModel rent = createRent(client, carro);

        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setValorPago(new BigDecimal("300.00"));
        payment.setDataPagamento(LocalDateTime.now());
        payment.setMetodoPagamento(MetodoPagamento.PIX);
        payment.setStatus(StatusPagamento.PENDENTE);
        paymentRepository.save(payment);

        boolean exists = paymentRepository.existsByRentIdAndStatus(rent.getId(), StatusPagamento.APROVADO);

        assertThat(exists).isFalse();
    }

    @Test
    void save_andFindById_works() {
        Client client = createClient("Cliente", "99999999999");
        Carro carro = createCarro("Civic", "HHH8888");
        RentModel rent = createRent(client, carro);
        Payment payment = createPayment(rent, MetodoPagamento.PIX);

        Payment found = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(found.getValorPago()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(found.getMetodoPagamento()).isEqualTo(MetodoPagamento.PIX);
        assertThat(found.getStatus()).isEqualTo(StatusPagamento.APROVADO);
    }
}