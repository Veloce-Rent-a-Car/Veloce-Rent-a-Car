package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import Veloce_Rent_a_Car.services.RentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentServiceTest {

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
        when(rentRepository.findAll()).thenReturn(List.of(rent1, rent2));

        List<RentModel> result = rentService.findAll();

        assertThat(result).hasSize(2);
        verify(rentRepository).findAll();
    }

    @Test
    void findById_whenExists_returnsRent() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));

        RentModel result = rentService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotExists_throwsEntityNotFoundException() {
        when(rentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Aluguel nao encontrado com id: 999");
    }

    @Test
    void findByClient_returnsRentsForClient() {
        RentModel rent = new RentModel();
        when(rentRepository.findByClientId(1L)).thenReturn(Optional.of(rent));

        Optional<RentModel> result = rentService.findByClient(1L);

        assertThat(result).isPresent();
        verify(rentRepository).findByClientId(1L);
    }

    @Test
    void create_whenClientExists_createsRent() {
        Client client = new Client("João", "11111111111", "11999999999", "joao@email.com", "SP");
        client.setId(1L);

        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf("11111111111");
        dto.setRentValue(new BigDecimal("300.00"));

        when(clientRepository.findByCpf("11111111111")).thenReturn(Optional.of(client));
        when(rentRepository.save(any(RentModel.class))).thenAnswer(inv -> {
            RentModel r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        RentModel result = rentService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getClient().getId()).isEqualTo(1L);
        assertThat(result.getRentValue()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(result.getStatus()).isEqualTo(RentModel.RentStatus.PENDING);
        assertThat(result.getStartDate()).isNotNull();
        verify(rentRepository).save(any(RentModel.class));
    }

    @Test
    void create_whenClientNotExists_throwsEntityNotFoundException() {
        NewRentDTO dto = new NewRentDTO();
        dto.setClientCpf("99999999999");
        dto.setRentValue(new BigDecimal("300.00"));

        when(clientRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente nao encontrado com CPF: 99999999999");

        verify(rentRepository, never()).save(any());
    }

    @Test
    void updateStatus_updatesAndReturnsRent() {
        RentModel rent = new RentModel();
        rent.setId(1L);
        rent.setStatus(RentModel.RentStatus.PENDING);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
        when(rentRepository.save(any(RentModel.class))).thenAnswer(inv -> inv.getArgument(0));

        RentModel result = rentService.updateStatus(1L, RentModel.RentStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(RentModel.RentStatus.CONFIRMED);
        verify(rentRepository).save(rent);
    }
}