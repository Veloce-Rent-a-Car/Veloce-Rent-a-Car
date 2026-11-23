package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.dto.LoginRequest;
import Veloce_Rent_a_Car.dto.RegisterRequest;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.security.JwtTokenProvider;
import Veloce_Rent_a_Car.services.AuthFacade;
import Veloce_Rent_a_Car.services.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AuthFacade authFacade;

    @Test
    void autenticar_whenAuthenticationSucceeds_returnsJwt() {
        LoginRequest request = new LoginRequest();
        request.setCpf("12345678901");
        request.setPassword("secret");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("jwt-token");

        String token = authFacade.autenticar(request);

        assertThat(token).isEqualTo("jwt-token");
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getPrincipal()).isEqualTo("12345678901");
        assertThat(captor.getValue().getCredentials()).isEqualTo("secret");
    }

    @Test
    void autenticar_whenCredentialsAreInvalid_propagatesAuthenticationException() {
        LoginRequest request = new LoginRequest();
        request.setCpf("12345678901");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> authFacade.autenticar(request))
                .isInstanceOf(BadCredentialsException.class);
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void registrar_mapsRequestAndReturnsSavedClient() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("João");
        request.setCpf("12345678901");
        request.setTelefone("11999999999");
        request.setEmail("joao@email.com");
        request.setEndereco("Recife");
        request.setPassword("secret");
        when(clientService.create(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(42L);
            return client;
        });

        Client saved = authFacade.registrar(request);

        assertThat(saved.getId()).isEqualTo(42L);
        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).create(captor.capture());
        Client client = captor.getValue();
        assertThat(client.getName()).isEqualTo("João");
        assertThat(client.getCpf()).isEqualTo("12345678901");
        assertThat(client.getPhone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
        assertThat(client.getAddress()).isEqualTo("Recife");
        assertThat(client.getPassword()).isEqualTo("secret");
    }
}
