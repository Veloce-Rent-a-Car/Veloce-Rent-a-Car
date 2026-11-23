package Veloce_Rent_a_Car.models;

import Veloce_Rent_a_Car.models.enums.MetodoPagamento;
import Veloce_Rent_a_Car.models.enums.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ModelBuilderTest {

    @Test
    void rentModelBuilder_createsRentWithProvidedValues() {
        Client client = new Client();
        Carro carro = new Carro();
        LocalDateTime startDate = LocalDateTime.now();

        RentModel rent = RentModel.builder()
                .client(client)
                .car(carro)
                .rentValue(new BigDecimal("300.00"))
                .startDate(startDate)
                .status(RentModel.RentStatus.PENDING)
                .build();

        assertThat(rent.getClient()).isSameAs(client);
        assertThat(rent.getCar()).isSameAs(carro);
        assertThat(rent.getRentValue()).isEqualByComparingTo("300.00");
        assertThat(rent.getStartDate()).isEqualTo(startDate);
        assertThat(rent.getStatus()).isEqualTo(RentModel.RentStatus.PENDING);
    }

    @Test
    void paymentBuilder_createsPaymentWithProvidedValues() {
        RentModel rent = new RentModel();
        LocalDateTime paymentDate = LocalDateTime.now();

        Payment payment = Payment.builder()
                .rent(rent)
                .valorPago(new BigDecimal("300.00"))
                .dataPagamento(paymentDate)
                .metodoPagamento(MetodoPagamento.PIX)
                .status(StatusPagamento.APROVADO)
                .build();

        assertThat(payment.getRent()).isSameAs(rent);
        assertThat(payment.getValorPago()).isEqualByComparingTo("300.00");
        assertThat(payment.getDataPagamento()).isEqualTo(paymentDate);
        assertThat(payment.getMetodoPagamento()).isEqualTo(MetodoPagamento.PIX);
        assertThat(payment.getStatus()).isEqualTo(StatusPagamento.APROVADO);
    }

    @Test
    void clientBuilder_createsClientForRegistration() {
        Client client = Client.builder()
                .name("João")
                .cpf("12345678901")
                .phone("11999999999")
                .email("joao@email.com")
                .address("Recife")
                .password("secret")
                .build();

        assertThat(client.getName()).isEqualTo("João");
        assertThat(client.getCpf()).isEqualTo("12345678901");
        assertThat(client.getPhone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
        assertThat(client.getAddress()).isEqualTo("Recife");
        assertThat(client.getPassword()).isEqualTo("secret");
    }

    @Test
    void carroBuilder_createsCarFromCsvValues() {
        Carro carro = Carro.builder()
                .modelo("Civic")
                .placa("ABC1234")
                .categoria("sedan")
                .valorDaDiaria(new BigDecimal("300.00"))
                .statusDisponibilidade(Carro.StatusCarro.DISPONIVEL)
                .build();

        assertThat(carro.getModelo()).isEqualTo("Civic");
        assertThat(carro.getPlaca()).isEqualTo("ABC1234");
        assertThat(carro.getCategoria()).isEqualTo("sedan");
        assertThat(carro.getValorDaDiaria()).isEqualByComparingTo("300.00");
        assertThat(carro.getStatusDisponibilidade()).isEqualTo(Carro.StatusCarro.DISPONIVEL);
    }
}
