package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BcryptPasswordEncodingTest {

    @Mock
    private ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        // Injeta o PasswordEncoder manualmente via reflexão
        var field = org.springframework.util.ReflectionUtils.findField(ClientService.class, "passwordEncoder");
        org.springframework.util.ReflectionUtils.makeAccessible(field);
        org.springframework.util.ReflectionUtils.setField(field, clientService, passwordEncoder);
    }

    @Test
    void create_senhaDeveSerCodificadaComBCrypt() {
        // Arrange
        Client client = new Client("João", "11111111111", "11999999999", "joao@email.com", "SP");
        client.setPassword("minhaSenha123");

        when(clientRepository.existsByCpf("11111111111")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Act
        Client saved = clientService.create(client);

        // Assert
        assertThat(saved.getPassword()).isNotEqualTo("minhaSenha123");
        assertThat(passwordEncoder.matches("minhaSenha123", saved.getPassword())).isTrue();
    }

    @Test
    void create_duasChamadasProduzemHashesDiferentes() {
        // Arrange
        Client client1 = new Client("João", "11111111111", "11999999999", "joao@email.com", "SP");
        client1.setPassword("mesmaSenha");

        Client client2 = new Client("Maria", "22222222222", "11888888888", "maria@email.com", "RJ");
        client2.setPassword("mesmaSenha");

        when(clientRepository.existsByCpf("11111111111")).thenReturn(false);
        when(clientRepository.existsByCpf("22222222222")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(c.getCpf().equals("11111111111") ? 1L : 2L);
            return c;
        });

        // Act
        Client saved1 = clientService.create(client1);
        Client saved2 = clientService.create(client2);

        // Assert - BCrypt gera salt diferente a cada vez
        assertThat(saved1.getPassword()).isNotEqualTo(saved2.getPassword());
        assertThat(passwordEncoder.matches("mesmaSenha", saved1.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("mesmaSenha", saved2.getPassword())).isTrue();
    }

    @Test
    void passwordEncoder_diretamente_funciona() {
        // Teste direto do PasswordEncoder
        String raw = "senhaSecreta";
        String encoded = passwordEncoder.encode(raw);

        assertThat(encoded).startsWith("$2a$"); // prefixo BCrypt
        assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
        assertThat(passwordEncoder.matches("errada", encoded)).isFalse();
    }
}