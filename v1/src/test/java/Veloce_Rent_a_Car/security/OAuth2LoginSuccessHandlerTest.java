package Veloce_Rent_a_Car.security;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private OAuth2LoginSuccessHandler successHandler;

    @Test
    void onAuthenticationSuccess_emailValido_retornaJwt() throws Exception {
        // Arrange
        String email = "usuario@gmail.com";
        String token = "eyJhbG...oken";

        Client client = new Client();
        client.setEmail(email);

        Map<String, Object> attributes = Map.of("email", email, "email_verified", true);
        OAuth2User oauth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(jwtTokenProvider.generateTokenFromEmail(email)).thenReturn(token);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString()).isEqualTo("{\"token\":\"eyJhbG...oken\",\"type\":\"Bearer\"}");
        verify(jwtTokenProvider).generateTokenFromEmail(email);
        verify(clientRepository).findByEmail(email);
    }

    @Test
    void onAuthenticationSuccess_emailNulo_retorna400() throws Exception {
        // Arrange
        Map<String, Object> attributes = Map.of("sub", "12345"); // sem email
        OAuth2User oauth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrorMessage()).isEqualTo("Email não fornecido pelo provedor OAuth2");
        verify(jwtTokenProvider, never()).generateTokenFromEmail(anyString());
        verify(clientRepository, never()).findByEmail(anyString());
    }

    @Test
    void onAuthenticationSuccess_emailVerifiedFalse_retorna403() throws Exception {
        // Arrange - email_verified = false deve rejeitar com 403
        String email = "usuario@gmail.com";

        Map<String, Object> attributes = Map.of("email", email, "email_verified", false);
        OAuth2User oauth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getErrorMessage()).isEqualTo("Email não verificado pelo provedor OAuth2");
        verify(jwtTokenProvider, never()).generateTokenFromEmail(anyString());
        verify(clientRepository, never()).findByEmail(anyString());
    }
}