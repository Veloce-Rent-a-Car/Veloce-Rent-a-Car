
package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = V1Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Carro createCarro(
            String model,
            String plate,
            String category,
            String dailyRate,
            Carro.StatusCarro status) {

        Carro car = new Carro(
                model,
                plate,
                category,
                new BigDecimal(dailyRate),
                status
        );

        return carroRepository.saveAndFlush(car);
    }

    @Test
    void listarTodos_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/carros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void criar_returnsCreatedCarro() throws Exception {
        Carro car = new Carro(
                "Civic",
                "ABC1234",
                "sedan",
                new BigDecimal("300.00"),
                Carro.StatusCarro.DISPONIVEL
        );

        mockMvc.perform(post("/api/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.modelo", is("Civic")))
                .andExpect(jsonPath("$.placa", is("ABC1234")))
                .andExpect(jsonPath(
                        "$.statusDisponibilidade",
                        is("DISPONIVEL")
                ));
    }

    @Test
    @WithMockUser
    void criar_whenPlacaExists_returnsConflict() throws Exception {
        createCarro(
                "Civic",
                "AAA1111",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        Carro duplicate = new Carro(
                "Corolla",
                "AAA1111",
                "sedan",
                new BigDecimal("280.00"),
                Carro.StatusCarro.DISPONIVEL
        );

        mockMvc.perform(post("/api/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void listarDisponiveis_returnsOnlyDisponiveis() throws Exception {
        createCarro(
                "Civic",
                "BBB2222",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        createCarro(
                "Corolla",
                "CCC3333",
                "sedan",
                "280.00",
                Carro.StatusCarro.DISPONIVEL
        );

        createCarro(
                "Hilux",
                "DDD4444",
                "SUV",
                "500.00",
                Carro.StatusCarro.ALUGADO
        );

        mockMvc.perform(get("/api/carros/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath(
                        "$[0].statusDisponibilidade",
                        is("DISPONIVEL")
                ))
                .andExpect(jsonPath(
                        "$[1].statusDisponibilidade",
                        is("DISPONIVEL")
                ));
    }

    @Test
    void buscarPorId_returnsCarro() throws Exception {
        Carro car = createCarro(
                "Onix",
                "EEE5555",
                "hatch",
                "180.00",
                Carro.StatusCarro.DISPONIVEL
        );

        mockMvc.perform(get("/api/carros/{id}", car.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.id",
                        is(car.getId().intValue())
                ))
                .andExpect(jsonPath("$.modelo", is("Onix")));
    }

    @Test
    void buscarPorId_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/carros/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void atualizar_updatesCarro() throws Exception {
        Carro car = createCarro(
                "Civic",
                "FFF6666",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        Carro updated = new Carro(
                "Civic Turbo",
                "FFF6666",
                "sedan",
                new BigDecimal("350.00"),
                Carro.StatusCarro.ALUGADO
        );

        mockMvc.perform(put("/api/carros/{id}", car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo", is("Civic Turbo")))
                .andExpect(jsonPath("$.valorDaDiaria", is(350.00)))
                .andExpect(jsonPath(
                        "$.statusDisponibilidade",
                        is("ALUGADO")
                ));
    }

    @Test
    @WithMockUser
    void deletar_removesCarro() throws Exception {
        Carro car = createCarro(
                "Civic",
                "GGG7777",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        mockMvc.perform(delete("/api/carros/{id}", car.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carros/{id}", car.getId()))
                .andExpect(status().isNotFound());
    }
}
