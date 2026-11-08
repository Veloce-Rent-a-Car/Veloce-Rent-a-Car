package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
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
class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodos_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/carros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void criar_returnsCreatedCarro() throws Exception {
        Carro carro = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);

        mockMvc.perform(post("/api/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.modelo", is("Civic")))
                .andExpect(jsonPath("$.placa", is("ABC1234")))
                .andExpect(jsonPath("$.statusDisponibilidade", is("DISPONIVEL")));
    }

    @Test
    void criar_whenPlacaExists_returnsBadRequest() throws Exception {
        Carro carro1 = new Carro("Civic", "AAA1111", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        carroRepository.save(carro1);

        Carro carro2 = new Carro("Corolla", "AAA1111", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL);

        mockMvc.perform(post("/api/carros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carro2)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void listarDisponiveis_returnsOnlyDisponiveis() throws Exception {
        Carro disp1 = carroRepository.save(new Carro("Civic", "BBB2222", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));
        Carro disp2 = carroRepository.save(new Carro("Corolla", "CCC3333", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL));
        carroRepository.save(new Carro("Hilux", "DDD4444", "SUV", new BigDecimal("500.00"), Carro.StatusCarro.ALUGADO));

        mockMvc.perform(get("/api/carros/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].statusDisponibilidade", is("DISPONIVEL")))
                .andExpect(jsonPath("$[1].statusDisponibilidade", is("DISPONIVEL")));
    }

    @Test
    void buscarPorId_returnsCarro() throws Exception {
        Carro carro = carroRepository.save(new Carro("Onix", "EEE5555", "hatch", new BigDecimal("180.00"), Carro.StatusCarro.DISPONIVEL));

        mockMvc.perform(get("/api/carros/{id}", carro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(carro.getId().intValue())))
                .andExpect(jsonPath("$.modelo", is("Onix")));
    }

    @Test
    void buscarPorId_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/carros/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_updatesCarro() throws Exception {
        Carro carro = carroRepository.save(new Carro("Civic", "FFF6666", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));

        Carro updated = new Carro("Civic Turbo", "FFF6666", "sedan", new BigDecimal("350.00"), Carro.StatusCarro.ALUGADO);

        mockMvc.perform(put("/api/carros/{id}", carro.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo", is("Civic Turbo")))
                .andExpect(jsonPath("$.valorDaDiaria", is(350.00)))
                .andExpect(jsonPath("$.statusDisponibilidade", is("ALUGADO")));
    }

    @Test
    void deletar_removesCarro() throws Exception {
        Carro carro = carroRepository.save(new Carro("Civic", "GGG7777", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));

        mockMvc.perform(delete("/api/carros/{id}", carro.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/carros/{id}", carro.getId()))
                .andExpect(status().isNotFound());
    }
}