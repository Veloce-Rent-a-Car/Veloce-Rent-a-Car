
package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
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

    private RentModel createRent(
            Client client,
            Carro car,
            RentModel.RentStatus status) {

        RentModel rent = new RentModel();

        rent.setClient(client);
        rent.setCar(car);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(status);

        return rentRepository.saveAndFlush(rent);
    }

    @Test
    void getAll_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void create_whenClientExists_createsRent() throws Exception {
        Client client = createClient("João", "11111111111");

        createCarro("Civic", "AAA1111");

        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf(client.getCpf());
        dto.setCarName("Civic");
        dto.setCarModelType("sedan");
        dto.setRentValue(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath(
                        "$.client.id",
                        is(client.getId().intValue())
                ))
                .andExpect(jsonPath("$.rentValue", is(300.00)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @WithMockUser
    void create_whenClientNotExists_returnsBadRequest() throws Exception {
        NewRentDTO dto = new NewRentDTO();

        dto.setClientCpf("99999999999");
        dto.setCarName("Civic");
        dto.setCarModelType("sedan");
        dto.setRentValue(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_returnsRent() throws Exception {
        Client client = createClient("Pedro", "22222222222");
        Carro car = createCarro("Civic", "AAA1111");

        RentModel saved = createRent(
                client,
                car,
                RentModel.RentStatus.PENDING
        );

        mockMvc.perform(get("/api/rentals/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.id",
                        is(saved.getId().intValue())
                ))
                .andExpect(jsonPath(
                        "$.client.id",
                        is(client.getId().intValue())
                ));
    }

    @Test
    void getById_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/rentals/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByClient_returnsRentsForClient() throws Exception {
        Client client1 = createClient(
                "Cliente 1",
                "33333333333"
        );

        Client client2 = createClient(
                "Cliente 2",
                "44444444444"
        );

        Carro car = createCarro("Civic", "BBB2222");

        createRent(
                client1,
                car,
                RentModel.RentStatus.PENDING
        );

        createRent(
                client1,
                car,
                RentModel.RentStatus.CONFIRMED
        );

        createRent(
                client2,
                car,
                RentModel.RentStatus.ACTIVE
        );

        mockMvc.perform(get(
                        "/api/rentals/client/{clientId}",
                        client1.getId()
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void updateStatus_updatesRentStatus() throws Exception {
        Client client = createClient("Ana", "55555555555");
        Carro car = createCarro("Corolla", "CCC3333");

        RentModel saved = createRent(
                client,
                car,
                RentModel.RentStatus.PENDING
        );

        mockMvc.perform(patch(
                        "/api/rentals/{id}/status",
                        saved.getId()
                ).param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.status",
                        is("CONFIRMED")
                ));

        mockMvc.perform(patch(
                        "/api/rentals/{id}/status",
                        saved.getId()
                ).param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.status",
                        is("ACTIVE")
                ));
    }

    @Test
    @WithMockUser
    void updateStatus_whenInvalidStatus_returnsBadRequest()
            throws Exception {

        Client client = createClient("Teste", "66666666666");
        Carro car = createCarro("Civic", "DDD4444");

        RentModel saved = createRent(
                client,
                car,
                RentModel.RentStatus.PENDING
        );

        mockMvc.perform(patch(
                        "/api/rentals/{id}/status",
                        saved.getId()
                ).param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateStatus_whenNotFound_returnsNotFound()
            throws Exception {

        mockMvc.perform(patch(
                        "/api/rentals/{id}/status",
                        999L
                ).param("status", "CONFIRMED"))
                .andExpect(status().isNotFound());
    }
}
