package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void findByCpf_whenExists_returnsClient() {
        Client client = new Client("João Silva", "12345678901", "11999999999", "joao@email.com", "São Paulo");
        Client saved = clientRepository.save(client);

        Optional<Client> found = clientRepository.findByCpf("12345678901");

        assertThat(found).isPresent();
        assertThat(found.get().getNome()).isEqualTo("João Silva");
        assertThat(found.get().getCpf()).isEqualTo("12345678901");
    }

    @Test
    void findByCpf_whenNotExists_returnsEmpty() {
        Optional<Client> found = clientRepository.findByCpf("99999999999");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByCpf_whenExists_returnsTrue() {
        Client client = new Client("Maria Santos", "11122233344", "11888888888", "maria@email.com", "Rio de Janeiro");
        clientRepository.save(client);

        boolean exists = clientRepository.existsByCpf("11122233344");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByCpf_whenNotExists_returnsFalse() {
        boolean exists = clientRepository.existsByCpf("00000000000");

        assertThat(exists).isFalse();
    }

    @Test
    void save_andFindById_works() {
        Client client = new Client("Pedro Oliveira", "55566677788", "11777777777", "pedro@email.com", "Belo Horizonte");
        Client saved = clientRepository.save(client);

        Optional<Client> found = clientRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNome()).isEqualTo("Pedro Oliveira");
    }
}