package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class RentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Client createClient(String nome, String cpf) {
        return clientRepository.save(new Client(nome, cpf, "11999999999", "email@test.com", "Cidade"));
    }

    private Carro createCarro(String modelo, String placa) {
        return carroRepository.save(new Carro(modelo, placa, "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));
    }

    @Test
    void getAll_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create_whenClientExists_createsRent() throws Exception {
        Client client = createClient("João", "11111111111");

        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf("11111111111");
        dto.setRentValue(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client.id", is(client.getId().intValue())))
                .andExpect(jsonPath("$.rentValue", is(300.00)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void create_whenClientNotExists_returnsBadRequest() throws Exception {
        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf("99999999999");
        dto.setRentValue(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsRent() throws Exception {
        Client client = createClient("Pedro", "22222222222");
        Carro carro = createCarro("Civic", "AAA1111");
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(java.time.LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        RentModel saved = rentRepository.save(rent);

        mockMvc.perform(get("/api/rentals/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.client.id", is(client.getId().intValue())));
    }

    @Test
    void getById_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/rentals/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByClient_returnsRentsForClient() throws Exception {
        Client client1 = createClient("Cliente 1", "33333333333");
        Client client2 = createClient("Cliente 2", "44444444444");
        Carro carro = createCarro("Civic", "BBB2222");

        RentModel rent1 = new RentModel();
        rent1.setClient(client1);
        rent1.setCar(carro);
        rent1.setRentValue(new BigDecimal("300.00"));
        rent1.setStartDate(java.time.LocalDateTime.now());
        rent1.setStatus(RentModel.RentStatus.PENDING);
        rentRepository.save(rent1);

        RentModel rent2 = new RentModel();
        rent2.setClient(client1);
        rent2.setCar(carro);
        rent2.setRentValue(new BigDecimal("300.00"));
        rent2.setStartDate(java.time.LocalDateTime.now());
        rent2.setStatus(RentModel.RentStatus.CONFIRMED);
        rentRepository.save(rent2);

        RentModel rent3 = new RentModel();
        rent3.setClient(client2);
        rent3.setCar(carro);
        rent3.setRentValue(new BigDecimal("300.00"));
        rent3.setStartDate(java.time.LocalDateTime.now());
        rent3.setStatus(RentModel.RentStatus.ACTIVE);
        rentRepository.save(rent3);

        mockMvc.perform(get("/api/rentals/client/{clientId}", client1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateStatus_updatesRentStatus() throws Exception {
        Client client = createClient("Ana", "55555555555");
        Carro carro = createCarro("Corolla", "CCC3333");
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(java.time.LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        RentModel saved = rentRepository.save(rent);

        mockMvc.perform(patch("/api/rentals/{id}/status", saved.getId())
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        mockMvc.perform(patch("/api/rentals/{id}/status", saved.getId())
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void updateStatus_whenInvalidStatus_returnsBadRequest() throws Exception {
        Client client = createClient("Teste", "66666666666");
        Carro carro = createCarro("Civic", "DDD4444");
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(java.time.LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        RentModel saved = rentRepository.save(rent);

        mockMvc.perform(patch("/api/rentals/{id}/status", saved.getId())
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/rentals/{id}/status", 999L)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isNotFound());
    }
}