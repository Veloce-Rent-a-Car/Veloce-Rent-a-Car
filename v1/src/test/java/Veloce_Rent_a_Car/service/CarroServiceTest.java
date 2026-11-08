package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.services.CarroService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private CarroService carroService;

    @Test
    void listarTodos_returnsAllCarros() {
        Carro carro1 = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        Carro carro2 = new Carro("Corolla", "DEF5678", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL);
        when(carroRepository.findAll()).thenReturn(List.of(carro1, carro2));

        List<Carro> result = carroService.listarTodos();

        assertThat(result).hasSize(2);
        verify(carroRepository).findAll();
    }

    @Test
    void listarCarrosDisponiveis_returnsOnlyDisponiveis() {
        Carro carro1 = new Carro("Civic", "AAA1111", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        Carro carro2 = new Carro("Corolla", "BBB2222", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.DISPONIVEL);
        when(carroRepository.findByStatusDisponibilidade(Carro.StatusCarro.DISPONIVEL)).thenReturn(List.of(carro1, carro2));

        List<Carro> result = carroService.listarCarrosDisponiveis();

        assertThat(result).hasSize(2);
        verify(carroRepository).findByStatusDisponibilidade(Carro.StatusCarro.DISPONIVEL);
    }

    @Test
    void buscarPorId_whenExists_returnsCarro() {
        Carro carro = new Carro("Civic", "CCC3333", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        carro.setId(1L);
        when(carroRepository.findById(1L)).thenReturn(Optional.of(carro));

        Carro result = carroService.buscarPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getModelo()).isEqualTo("Civic");
    }

    @Test
    void buscarPorId_whenNotExists_throwsRuntimeException() {
        when(carroRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carroService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Carro não encontrado com o ID: 999");
    }

    @Test
    void salvar_savesAndReturnsCarro() {
        Carro carro = new Carro("Onix", "DDD4444", "hatch", new BigDecimal("180.00"), Carro.StatusCarro.DISPONIVEL);
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> {
            Carro c = inv.getArgument(0);
            c.setId(20L);
            return c;
        });

        Carro result = carroService.salvar(carro);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        verify(carroRepository).save(carro);
    }

    @Test
    void atualizar_updatesFieldsExceptPlaca() {
        Carro existing = new Carro("Civic", "EEE5555", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        existing.setId(1L);

        Carro updated = new Carro("Civic Turbo", "EEE5555", "sedan", new BigDecimal("350.00"), Carro.StatusCarro.ALUGADO);

        when(carroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        Carro result = carroService.atualizar(1L, updated);

        assertThat(result.getModelo()).isEqualTo("Civic Turbo");
        assertThat(result.getPlaca()).isEqualTo("EEE5555"); // unchanged
        assertThat(result.getValorDaDiaria()).isEqualByComparingTo(new BigDecimal("350.00"));
        assertThat(result.getStatusDisponibilidade()).isEqualTo(Carro.StatusCarro.ALUGADO);
        verify(carroRepository).save(existing);
    }

    @Test
    void deletar_deletesCarro() {
        Carro carro = new Carro("Civic", "FFF6666", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        carro.setId(1L);
        when(carroRepository.findById(1L)).thenReturn(Optional.of(carro));

        carroService.deletar(1L);

        verify(carroRepository).delete(carro);
    }
}