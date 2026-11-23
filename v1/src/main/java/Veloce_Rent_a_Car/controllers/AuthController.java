package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.LoginRequest;
import Veloce_Rent_a_Car.dto.RegisterRequest;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.services.AuthFacade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Expõe os endpoints de autenticação por CPF e senha e de cadastro de clientes
 * sob o caminho base {@code /api/auth}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    /**
     * Cria o controlador com a fachada responsável pelas operações de autenticação e registro.
     *
     * @param authFacade fachada de autenticação e cadastro de clientes
     */
    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    /**
     * Autentica as credenciais recebidas e devolve o token gerado pela fachada.
     * Credenciais inválidas são convertidas em uma resposta de não autorizado.
     *
     * @param request CPF e senha enviados para autenticação
     * @return resposta HTTP 200 com token e tipo {@code Bearer}, ou HTTP 401 com mensagem de credenciais inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authFacade.autenticar(request);
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        }
    }

    /**
     * Registra um cliente com dados previamente validados e devolve seu identificador.
     * Uma tentativa de registro em estado inválido resulta em resposta de requisição inválida.
     *
     * @param request dados validados para o cadastro do cliente
     * @return resposta HTTP 200 com mensagem de sucesso e ID do cliente, ou HTTP 400 com o erro informado pela fachada
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Client saved = authFacade.registrar(request);
            return ResponseEntity.ok(Map.of("message", "Cliente cadastrado com sucesso", "id", saved.getId()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}