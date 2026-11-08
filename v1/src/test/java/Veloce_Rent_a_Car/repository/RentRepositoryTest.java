package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.RentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RentRepositoryTest {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    private Client createClient(String nome, String cpf) {
        Client client = new Client(nome, cpf, "11999999999", "email@test.com", "Cidade");
        return clientRepository.save(client);
    }

    private Carro createCarro(String modelo, String placa) {
        Carro carro = new Carro(modelo, placa, "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        return carroRepository.save(carro);
    }

    private RentModel createRent(Client client, Carro carro, RentModel.RentStatus status) {
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(new BigDecimal("300.00"));
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(status);
        return rentRepository.save(rent);
    }

    @Test
    void findByClientId_returnsRentsForClient() {
        Client client1 = createClient("Cliente 1", "11111111111");
        Client client2 = createClient("Cliente 2", "22222222222");
        Carro carro = createCarro("Civic", "ABC1234");

        createRent(client1, carro, RentModel.RentStatus.PENDING);
        createRent(client1, carro, RentModel.RentStatus.CONFIRMED);
        createRent(client2, carro, RentModel.RentStatus.ACTIVE);

        Optional<RentModel> rentClient1 = rentRepository.findByClientId(client1.getId());
        Optional<RentModel> rentClient2 = rentRepository.findByClientId(client2.getId());

        assertThat(rentClient1).isPresent();
        assertThat(rentClient2).isPresent();
    }

    @Test
    void findByCarId_returnsRentsForCar() {
        Client client = createClient("Cliente", "33333333333");
        Carro carro1 = createCarro("Civic", "AAA1111");
        Carro carro2 = createCarro("Corolla", "BBB2222");

        createRent(client, carro1, RentModel.RentStatus.PENDING);
        createRent(client, carro1, RentModel.RentStatus.ACTIVE);
        createRent(client, carro2, RentModel.RentStatus.FINISHED);

        List<RentModel> rentsCarro1 = rentRepository.findByCarId(carro1.getId());
        List<RentModel> rentsCarro2 = rentRepository.findByCarId(carro2.getId());

        assertThat(rentsCarro1).hasSize(2);
        assertThat(rentsCarro2).hasSize(1);
    }

    @Test
    void findByStatus_returnsRentsWithMatchingStatus() {
        Client client = createClient("Cliente", "44444444444");
        Carro carro = createCarro("Civic", "CCC3333");

        createRent(client, carro, RentModel.RentStatus.PENDING);
        createRent(client, carro, RentModel.RentStatus.PENDING);
        createRent(client, carro, RentModel.RentStatus.CONFIRMED);
        createRent(client, carro, RentModel.RentStatus.FINISHED);

        List<RentModel> pending = rentRepository.findByStatus(RentModel.RentStatus.PENDING);
        List<RentModel> confirmed = rentRepository.findByStatus(RentModel.RentStatus.CONFIRMED);
        List<RentModel> finished = rentRepository.findByStatus(RentModel.RentStatus.FINISHED);

        assertThat(pending).hasSize(2);
        assertThat(confirmed).hasSize(1);
        assertThat(finished).hasSize(1);
    }

    @Test
    void save_andFindById_works() {
        Client client = createClient("Cliente", "55555555555");
        Carro carro = createCarro("Civic", "DDD4444");
        RentModel rent = createRent(client, carro, RentModel.RentStatus.PENDING);

        Optional<RentModel> found = rentRepository.findById(rent.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getClient().getId()).isEqualTo(client.getId());
        assertThat(found.get().getCar().getId()).isEqualTo(carro.getId());
        assertThat(found.get().getStatus()).isEqualTo(RentModel.RentStatus.PENDING);
    }
}