package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<RentModel> findAll() {
        return rentRepository.findAll();
    }

    public RentModel findById(Long id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluguel nao encontrado com id: " + id));
    }

    public Optional<RentModel> findByClient(Long clientId) {
        return rentRepository.findByClientId(clientId);
    }

    public RentModel create(NewRentDTO dto) {
        Client client = clientRepository.findByCpf(dto.getClientCpf())
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado com CPF: " + dto.getClientCpf()));

        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setRentValue(dto.getRentValue());
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);

        return rentRepository.save(rent);
    }

    public RentModel updateStatus(Long id, RentModel.RentStatus status) {
        RentModel rent = findById(id);
        rent.setStatus(status);
        return rentRepository.save(rent);
    }
}
