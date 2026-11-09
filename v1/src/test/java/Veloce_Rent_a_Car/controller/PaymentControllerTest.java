
package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.dto.NewPaymentDTO;
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

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = V1Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAll_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void create_whenValid_createsPayment() throws Exception {
        Client client = createClient("João", "11111111111");
        Carro car = createCarro("Civic", "AAA1111");
        RentModel rent = createRent(client, car);

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(rent.getId());
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath(
                        "$.rent.id",
                        is(rent.getId().intValue())
                ))
                .andExpect(jsonPath("$.valorPago", is(300.00)))
                .andExpect(jsonPath("$.metodoPagamento", is("PIX")))
                .andExpect(jsonPath("$.status", is("APROVADO")))
                .andExpect(jsonPath("$.dataPagamento").exists());
    }

    @Test
    @WithMockUser
    void create_whenRentNotExists_returnsNotFound() throws Exception {
        NewPaymentDTO dto = new NewPaymentDTO();

        dto.setRentId(999L);
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_whenRentAlreadyPaid_returnsConflict() throws Exception {
        Client client = createClient("Pedro", "22222222222");
        Carro car = createCarro("Corolla", "BBB2222");
        RentModel rent = createRent(client, car);

        createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        NewPaymentDTO dto = new NewPaymentDTO();

        dto.setRentId(rent.getId());
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void create_whenValorDifferentFromRentValue_returnsBadRequest()
            throws Exception {

        Client client = createClient("Ana", "33333333333");
        Carro car = createCarro("Civic", "CCC3333");
        RentModel rent = createRent(client, car);

        NewPaymentDTO dto = new NewPaymentDTO();

        dto.setRentId(rent.getId());
        dto.setValorPago(new BigDecimal("250.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsPayment() throws Exception {
        Client client = createClient("Carlos", "44444444444");
        Carro car = createCarro("Onix", "DDD4444");
        RentModel rent = createRent(client, car);

        Payment saved = createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        mockMvc.perform(get("/payments/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.id",
                        is(saved.getId().intValue())
                ))
                .andExpect(jsonPath("$.valorPago", is(300.00)))
                .andExpect(jsonPath("$.metodoPagamento", is("PIX")));
    }

    @Test
    void getById_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/payments/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByRentId_returnsPaymentsForRent() throws Exception {
        Client client = createClient("Maria", "55555555555");
        Carro car = createCarro("Civic", "EEE5555");
        RentModel rent = createRent(client, car);

        createPayment(
                rent,
                MetodoPagamento.PIX,
                StatusPagamento.APROVADO
        );

        createPayment(
                rent,
                MetodoPagamento.CARTAO_CREDITO,
                StatusPagamento.APROVADO
        );

        mockMvc.perform(get("/payments/rent/{rentId}", rent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
