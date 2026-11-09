
package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = V1Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Client createTestClient(
            String name,
            String cpf,
            String phone,
            String email,
            String address) {

        Client client = new Client(
                name, cpf, phone, email, address
        );

        client.setPassword("test-password-hash");
        return client;
    }

    @Test
    void getAll_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void createClient_returnsCreatedClient() throws Exception {
        Client client = createTestClient(
                "João Silva",
                "12345678901",
                "11999999999",
                "joao@email.com",
                "São Paulo"
        );

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("João Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.email", is("joao@email.com")));
    }

    @Test
    @WithMockUser
    void createClient_whenCpfExists_returnsBadRequest() throws Exception {
        Client client1 = createTestClient(
                "João",
                "11111111111",
                "11999999999",
                "joao@email.com",
                "SP"
        );

        clientRepository.saveAndFlush(client1);

        Client client2 = createTestClient(
                "Maria",
                "11111111111",
                "11888888888",
                "maria@email.com",
                "RJ"
        );

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsClient() throws Exception {
        Client client = clientRepository.saveAndFlush(
                createTestClient(
                        "Pedro",
                        "22222222222",
                        "11777777777",
                        "pedro@email.com",
                        "MG"
                )
        );

        mockMvc.perform(get("/api/clients/{id}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(client.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Pedro")));
    }

    @Test
    void getById_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/clients/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByCpf_returnsClient() throws Exception {
        Client client = clientRepository.saveAndFlush(
                createTestClient(
                        "Ana",
                        "33333333333",
                        "11666666666",
                        "ana@email.com",
                        "RS"
                )
        );

        mockMvc.perform(get("/api/clients/cpf/{cpf}", "33333333333"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(client.getId().intValue())))
                .andExpect(jsonPath("$.cpf", is("33333333333")));
    }
}
