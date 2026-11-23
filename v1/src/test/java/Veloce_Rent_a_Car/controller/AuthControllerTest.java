package Veloce_Rent_a_Car.controller;

import Veloce_Rent_a_Car.controllers.AuthController;
import Veloce_Rent_a_Car.dto.LoginRequest;
import Veloce_Rent_a_Car.dto.RegisterRequest;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.services.AuthFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthFacade authFacade;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_whenAuthenticationSucceeds_preservesResponseFormat() {
        LoginRequest request = new LoginRequest();
        when(authFacade.autenticar(request)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Map.of("token", "jwt-token", "type", "Bearer"));
    }

    @Test
    void login_whenCredentialsAreInvalid_returnsUnauthorizedError() {
        LoginRequest request = new LoginRequest();
        when(authFacade.autenticar(request)).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Credenciais inválidas"));
    }

    @Test
    void login_whenUnexpectedErrorOccurs_doesNotMaskItAsInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        when(authFacade.autenticar(request)).thenThrow(new IllegalStateException("Falha de infraestrutura"));

        assertThatThrownBy(() -> authController.login(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Falha de infraestrutura");
    }

    @Test
    void register_whenRegistrationSucceeds_preservesResponseFormat() {
        RegisterRequest request = new RegisterRequest();
        Client saved = new Client();
        saved.setId(42L);
        when(authFacade.registrar(request)).thenReturn(saved);

        ResponseEntity<?> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Map.of(
                "message", "Cliente cadastrado com sucesso",
                "id", 42L
        ));
    }
}
