package Veloce_Rent_a_Car.repository;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import Veloce_Rent_a_Car.services.RentService;

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
class RentRepositoryTest {

    @Mock
    private RentRepository rentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private RentService rentService;

    @Test
    void findAll_returnsAllRents() {
        RentModel rent1 = new RentModel();
        RentModel rent2 = new RentModel();

        when(rentRepository.findAll())
                .thenReturn(List.of(rent1, rent2));

        List<RentModel> result = rentService.findAll();

        assertThat(result).hasSize(2);

        verify(rentRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsRent() {
        RentModel rent = new RentModel();
        rent.setId(1L);

        when(rentRepository.findById(1L))
                .thenReturn(Optional.of(rent));

        RentModel result = rentService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotExists_throwsRecursoNaoEncontradoException() {
        when(rentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentService.findById(999L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(
                        "Aluguel nao encontrado com id: 999"
                );
    }

    @Test
    void findByClient_returnsRentsForClient() {
        RentModel rent1 = new RentModel();
        rent1.setId(1L);

        RentModel rent2 = new RentModel();
        rent2.setId(2L);

        when(rentRepository.findByClientId(1L))
                .thenReturn(List.of(rent1, rent2));

        List<RentModel> result = rentService.findByClient(1L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(RentModel::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(rentRepository).findByClientId(1L);
    }

    @Test
    void create_whenClientExists_createsRent() {
        Client client = new Client(
                "João",
                "11111111111",
                "11999999999",
                "joao@email.com",
                "SP"
        );

        client.setId(1L);

        Carro car = new Carro(
                "Civic",
                "ABC1234",
                "sedan",
                new BigDecimal("300.00"),
                Carro.StatusCarro.DISPONIVEL
        );

        car.setId(2L);

        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf("11111111111");
        dto.setCarName("Civic");
        dto.setCarModelType("sedan");
        dto.setRentValue(new BigDecimal("300.00"));

        when(clientRepository.findByCpf("11111111111"))
                .thenReturn(Optional.of(client));

        when(carroRepository
                .findByModeloAndCategoriaAndStatusDisponibilidade(
                        "Civic",
                        "sedan",
                        Carro.StatusCarro.DISPONIVEL
                ))
                .thenReturn(Optional.of(car));

        when(rentRepository.save(any(RentModel.class)))
                .thenAnswer(invocation -> {
                    RentModel rent = invocation.getArgument(0);
                    rent.setId(10L);
                    return rent;
                });

        RentModel result = rentService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);

        assertThat(result.getClient().getId())
                .isEqualTo(1L);

        assertThat(result.getCar().getId())
                .isEqualTo(2L);

        assertThat(result.getRentValue())
                .isEqualByComparingTo(
                        new BigDecimal("300.00")
                );

        assertThat(result.getStatus())
                .isEqualTo(RentModel.RentStatus.PENDING);

        assertThat(result.getStartDate()).isNotNull();

        assertThat(car.getStatusDisponibilidade())
                .isEqualTo(Carro.StatusCarro.ALUGADO);

        verify(carroRepository).save(car);
        verify(rentRepository).save(any(RentModel.class));
    }

    @Test
    void create_whenClientNotExists_throwsRecursoNaoEncontradoException() {
        NewRentDTO dto = new NewRentDTO();

        dto.setClientCpf("99999999999");
        dto.setRentValue(new BigDecimal("300.00"));

        when(clientRepository.findByCpf("99999999999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentService.create(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(
                        "Cliente nao encontrado com CPF: 99999999999"
                );

        verify(rentRepository, never()).save(any());
        verify(carroRepository, never()).save(any());
    }

    @Test
    void updateStatus_updatesAndReturnsRent() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setStatus(RentModel.RentStatus.PENDING);

        when(rentRepository.findById(1L))
                .thenReturn(Optional.of(rent));

        when(rentRepository.save(any(RentModel.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        RentModel result = rentService.updateStatus(
                1L,
                RentModel.RentStatus.CONFIRMED
        );

        assertThat(result.getStatus())
                .isEqualTo(RentModel.RentStatus.CONFIRMED);

        verify(rentRepository).save(rent);
    }
}
