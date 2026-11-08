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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import Veloce_Rent_a_Car.v1.V1Application;

import java.math.BigDecimal;

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

    private Client createClient(String nome, String cpf) {
        return clientRepository.save(new Client(nome, cpf, "11999999999", "email@test.com", "Cidade"));
    }

    private Carro createCarro(String modelo, String placa) {
        return carroRepository.save(new Carro(modelo, placa, "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));
    }

    private RentModel createRent(Client client, Carro carro) {
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(java.time.LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        return rentRepository.save(rent);
    }

    @Test
    void getAll_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create_whenValid_createsPayment() throws Exception {
        Client client = createClient("João", "11111111111");
        Carro carro = createCarro("Civic", "AAA1111");
        RentModel rent = createRent(client, carro);

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(rent.getId());
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rent.id", is(rent.getId().intValue())))
                .andExpect(jsonPath("$.valorPago", is(300.00)))
                .andExpect(jsonPath("$.metodoPagamento", is("PIX")))
                .andExpect(jsonPath("$.status", is("APROVADO")))
                .andExpect(jsonPath("$.dataPagamento").exists());
    }

    @Test
    void create_whenRentNotExists_returnsBadRequest() throws Exception {
        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(999L);
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenRentAlreadyPaid_returnsBadRequest() throws Exception {
        Client client = createClient("Pedro", "22222222222");
        Carro carro = createCarro("Corolla", "BBB2222");
        RentModel rent = createRent(client, carro);

        Payment existingPayment = new Payment();
        existingPayment.setRent(rent);
        existingPayment.setValorPago(new BigDecimal("300.00"));
        existingPayment.setDataPagamento(java.time.LocalDateTime.now());
        existingPayment.setMetodoPagamento(MetodoPagamento.PIX);
        existingPayment.setStatus(StatusPagamento.APROVADO);
        paymentRepository.save(existingPayment);

        NewPaymentDTO dto = new NewPaymentDTO();
        dto.setRentId(rent.getId());
        dto.setValorPago(new BigDecimal("300.00"));
        dto.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenValorDifferentFromRentValue_returnsBadRequest() throws Exception {
        Client client = createClient("Ana", "33333333333");
        Carro carro = createCarro("Civic", "CCC3333");
        RentModel rent = createRent(client, carro);

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
        Carro carro = createCarro("Onix", "DDD4444");
        RentModel rent = createRent(client, carro);

        Payment payment = new Payment();
        payment.setRent(rent);
        payment.setValorPago(new BigDecimal("300.00"));
        payment.setDataPagamento(java.time.LocalDateTime.now());
        payment.setMetodoPagamento(MetodoPagamento.PIX);
        payment.setStatus(StatusPagamento.APROVADO);
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(get("/payments/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
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
        Carro carro = createCarro("Civic", "EEE5555");
        RentModel rent = createRent(client, carro);

        Payment p1 = new Payment();
        p1.setRent(rent);
        p1.setValorPago(new BigDecimal("300.00"));
        p1.setDataPagamento(java.time.LocalDateTime.now());
        p1.setMetodoPagamento(MetodoPagamento.PIX);
        p1.setStatus(StatusPagamento.APROVADO);
        paymentRepository.save(p1);

        Payment p2 = new Payment();
        p2.setRent(rent);
        p2.setValorPago(new BigDecimal("300.00"));
        p2.setDataPagamento(java.time.LocalDateTime.now());
        p2.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
        p2.setStatus(StatusPagamento.APROVADO);
        paymentRepository.save(p2);

        mockMvc.perform(get("/payments/rent/{rentId}", rent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}