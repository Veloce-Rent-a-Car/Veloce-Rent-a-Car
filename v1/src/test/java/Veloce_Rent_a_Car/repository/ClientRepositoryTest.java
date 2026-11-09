
package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.v1.V1Application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = V1Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client createClient(
            String name,
            String cpf,
            String phone,
            String email,
            String city) {

        Client client = new Client(
                name,
                cpf,
                phone,
                email,
                city
        );

        client.setPassword("test-password-hash");

        return clientRepository.saveAndFlush(client);
    }

    @Test
    void findByCpf_whenExists_returnsClient() {
        Client saved = createClient(
                "João Silva",
                "12345678901",
                "11999999999",
                "joao@email.com",
                "São Paulo"
        );

        Optional<Client> found =
                clientRepository.findByCpf("12345678901");

        assertThat(found).isPresent();
        assertThat(found.get().getName())
                .isEqualTo("João Silva");
        assertThat(found.get().getCpf())
                .isEqualTo("12345678901");
        assertThat(found.get().getId())
                .isEqualTo(saved.getId());
    }

    @Test
    void findByCpf_whenNotExists_returnsEmpty() {
        Optional<Client> found =
                clientRepository.findByCpf("99999999999");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByCpf_whenExists_returnsTrue() {
        createClient(
                "Maria Santos",
                "11122233344",
                "11888888888",
                "maria@email.com",
                "Rio de Janeiro"
        );

        boolean exists =
                clientRepository.existsByCpf("11122233344");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByCpf_whenNotExists_returnsFalse() {
        boolean exists =
                clientRepository.existsByCpf("00000000000");

        assertThat(exists).isFalse();
    }

    @Test
    void save_andFindById_works() {
        Client saved = createClient(
                "Pedro Oliveira",
                "55566677788",
                "11777777777",
                "pedro@email.com",
                "Belo Horizonte"
        );

        Optional<Client> found =
                clientRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName())
                .isEqualTo("Pedro Oliveira");
    }
}
