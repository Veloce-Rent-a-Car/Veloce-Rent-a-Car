package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.exception.RegraDeNegocioException;
import Veloce_Rent_a_Car.exception.ValidacaoException;
import Veloce_Rent_a_Car.v1.V1Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = V1Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(ObjectMapper.class)
class GlobalExceptionHandlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recursoNaoEncontrado_returns404WithJson() throws Exception {
        // GET /api/clients/999 não existe
        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Cliente nao encontrado com id: 999")))
                .andExpect(jsonPath("$.path", is("/api/clients/999")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void regraDeNegocio_returns409WithJson() throws Exception {
        // Criar cliente e tentar criar outro com mesmo CPF
        String cpf = "11111111111";
        String clientJson = String.format("""
                {"nome":"João","cpf":"%s","telefone":"11999999999","email":"joao@email.com","endereco":"SP","password":"123456"}
                """, cpf);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isOk());

        // Segunda tentativa - deve retornar 409 Conflict
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Ja existe um cliente com este CPF: " + cpf)))
                .andExpect(jsonPath("$.path", is("/api/auth/register")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void validacaoException_returns400WithJson() throws Exception {
        // Criar aluguel sem cliente válido - deve retornar 400 via ValidacaoException
        // Mas como não temos endpoint que lança ValidacaoException diretamente,
        // testamos via MethodArgumentNotValidException no POST /api/clients com dados inválidos
        String clientJsonInvalido = """
                {"nome":"","cpf":"123","telefone":"","email":"invalido","endereco":"","password":""}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientJsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path", is("/api/auth/register")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}