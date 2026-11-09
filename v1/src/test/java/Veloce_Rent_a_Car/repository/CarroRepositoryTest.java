
package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.v1.V1Application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = V1Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CarroRepositoryTest {

    @Autowired
    private CarroRepository carroRepository;

    private Carro createCarro(
            String model,
            String plate,
            String category,
            String dailyRate,
            Carro.StatusCarro status) {

        Carro car = new Carro(
                model,
                plate,
                category,
                new BigDecimal(dailyRate),
                status
        );

        return carroRepository.saveAndFlush(car);
    }

    @Test
    void findByPlaca_whenExists_returnsCarro() {
        Carro saved = createCarro(
                "Civic",
                "ABC1234",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        Optional<Carro> found =
                carroRepository.findByPlaca("ABC1234");

        assertThat(found).isPresent();
        assertThat(found.get().getModelo())
                .isEqualTo("Civic");
        assertThat(found.get().getPlaca())
                .isEqualTo("ABC1234");
        assertThat(found.get().getId())
                .isEqualTo(saved.getId());
    }

    @Test
    void findByPlaca_whenNotExists_returnsEmpty() {
        Optional<Carro> found =
                carroRepository.findByPlaca("ZZZ9999");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByPlaca_whenExists_returnsTrue() {
        createCarro(
                "Corolla",
                "DEF5678",
                "sedan",
                "280.00",
                Carro.StatusCarro.DISPONIVEL
        );

        boolean exists =
                carroRepository.existsByPlaca("DEF5678");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByPlaca_whenNotExists_returnsFalse() {
        boolean exists =
                carroRepository.existsByPlaca("XYZ0000");

        assertThat(exists).isFalse();
    }

    @Test
    void findByStatusDisponibilidade_returnsOnlyMatchingStatus() {
        createCarro(
                "Civic",
                "AAA1111",
                "sedan",
                "300.00",
                Carro.StatusCarro.DISPONIVEL
        );

        createCarro(
                "Corolla",
                "BBB2222",
                "sedan",
                "280.00",
                Carro.StatusCarro.DISPONIVEL
        );

        createCarro(
                "Hilux",
                "CCC3333",
                "SUV",
                "500.00",
                Carro.StatusCarro.ALUGADO
        );

        createCarro(
                "Fiat Uno",
                "DDD4444",
                "economy",
                "100.00",
                Carro.StatusCarro.MANUTENCAO
        );

        List<Carro> available =
                carroRepository.findByStatusDisponibilidade(
                        Carro.StatusCarro.DISPONIVEL
                );

        List<Carro> rented =
                carroRepository.findByStatusDisponibilidade(
                        Carro.StatusCarro.ALUGADO
                );

        List<Carro> maintenance =
                carroRepository.findByStatusDisponibilidade(
                        Carro.StatusCarro.MANUTENCAO
                );

        assertThat(available).hasSize(2);
        assertThat(rented).hasSize(1);
        assertThat(maintenance).hasSize(1);

        assertThat(available)
                .allMatch(car ->
                        car.getStatusDisponibilidade()
                                == Carro.StatusCarro.DISPONIVEL
                );

        assertThat(rented)
                .allMatch(car ->
                        car.getStatusDisponibilidade()
                                == Carro.StatusCarro.ALUGADO
                );

        assertThat(maintenance)
                .allMatch(car ->
                        car.getStatusDisponibilidade()
                                == Carro.StatusCarro.MANUTENCAO
                );
    }

    @Test
    void save_andFindById_works() {
        Carro saved = createCarro(
                "Onix",
                "EEE5555",
                "hatch",
                "180.00",
                Carro.StatusCarro.DISPONIVEL
        );

        Optional<Carro> found =
                carroRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getModelo())
                .isEqualTo("Onix");
        assertThat(found.get().getId())
                .isEqualTo(saved.getId());
    }
}
