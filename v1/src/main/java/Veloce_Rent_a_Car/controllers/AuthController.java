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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authFacade.autenticar(request);
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        }
    }

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
