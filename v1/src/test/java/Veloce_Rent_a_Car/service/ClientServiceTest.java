package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.services.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void findAll_returnsAllClients() {
        Client client1 = new Client("João", "11111111111", "11999999999", "joao@email.com", "SP");
        Client client2 = new Client("Maria", "22222222222", "11888888888", "maria@email.com", "RJ");
        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        List<Client> result = clientService.findAll();

        assertThat(result).hasSize(2);
        verify(clientRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsClient() {
        Client client = new Client("Pedro", "33333333333", "11777777777", "pedro@email.com", "MG");
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client result = clientService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Pedro");
    }

    @Test
    void findById_whenNotExists_throwsEntityNotFoundException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente nao encontrado com id: 999");
    }

    @Test
    void findByCpf_whenExists_returnsClient() {
        Client client = new Client("Ana", "44444444444", "11666666666", "ana@email.com", "RS");
        when(clientRepository.findByCpf("44444444444")).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findByCpf("44444444444");

        assertThat(result).isPresent();
        assertThat(result.get().getNome()).isEqualTo("Ana");
    }

    @Test
    void findByCpf_whenNotExists_returnsEmpty() {
        when(clientRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        Optional<Client> result = clientService.findByCpf("00000000000");

        assertThat(result).isEmpty();
    }

    @Test
    void create_whenCpfNotExists_savesAndReturnsClient() {
        Client client = new Client("Carlos", "55555555555", "11555555555", "carlos@email.com", "PR");
        when(clientRepository.existsByCpf("55555555555")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        Client result = clientService.create(client);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(clientRepository).save(client);
    }

    @Test
    void create_whenCpfExists_throwsIllegalStateException() {
        Client client = new Client("Carlos", "55555555555", "11555555555", "carlos@email.com", "PR");
        when(clientRepository.existsByCpf("55555555555")).thenReturn(true);

        assertThatThrownBy(() -> clientService.create(client))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ja existe um cliente com este CPF");

        verify(clientRepository, never()).save(any());
    }
}