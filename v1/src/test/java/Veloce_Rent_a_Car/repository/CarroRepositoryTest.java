package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CarroRepositoryTest {

    @Autowired
    private CarroRepository carroRepository;

    @Test
    void findByPlaca_whenExists_returnsCarro() {
        Carro carro = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        carroRepository.save(carro);

        Optional<Carro> found = carroRepository.findByPlaca("ABC1234");

        assertThat(found).isPresent();
        assertThat(found.get().getModelo()).isEqualTo("Civic");
        assertThat(found.get().getPlaca()).isEqualTo("ABC1234");
    }

    @Test
    void findByPlaca_whenNotExists_returnsEmpty() {
        Optional<Carro> found = carroRepository.findByPlaca("ZZZ9999");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByPlaca_whenExists_returnsTrue() {
        Carro carro = new Carro("Corolla", "DEF5678", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL);
        carroRepository.save(carro);

        boolean exists = carroRepository.existsByPlaca("DEF5678");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByPlaca_whenNotExists_returnsFalse() {
        boolean exists = carroRepository.existsByPlaca("XYZ0000");

        assertThat(exists).isFalse();
    }

    @Test
    void findByStatusDisponibilidade_returnsOnlyMatchingStatus() {
        Carro disponivel1 = carroRepository.save(new Carro("Civic", "AAA1111", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL));
        Carro disponivel2 = carroRepository.save(new Carro("Corolla", "BBB2222", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL));
        carroRepository.save(new Carro("Hilux", "CCC3333", "SUV", new BigDecimal("500.00"), Carro.StatusCarro.ALUGADO));
        carroRepository.save(new Carro("Fiat Uno", "DDD4444", "economy", new BigDecimal("100.00"), Carro.StatusCarro.MANUTENCAO));

        List<Carro> disponiveis = carroRepository.findByStatusDisponibilidade(Carro.StatusCarro.DISPONIVEL);
        List<Carro> alugados = carroRepository.findByStatusDisponibilidade(Carro.StatusCarro.ALUGADO);
        List<Carro> manutencoes = carroRepository.findByStatusDisponibilidade(Carro.StatusCarro.MANUTENCAO);

        assertThat(disponiveis).hasSize(2);
        assertThat(alugados).hasSize(1);
        assertThat(manutencoes).hasSize(1);
    }

    @Test
    void save_andFindById_works() {
        Carro carro = new Carro("Onix", "EEE5555", "hatch", new BigDecimal("180.00"), Carro.StatusCarro.DISPONIVEL);
        Carro saved = carroRepository.save(carro);

        Optional<Carro> found = carroRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getModelo()).isEqualTo("Onix");
    }
}