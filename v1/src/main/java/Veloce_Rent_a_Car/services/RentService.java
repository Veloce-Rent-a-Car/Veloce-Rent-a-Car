package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<RentModel> findByClientCpf(String cpf) {
        return rentRepository.findByClientCpf(cpf)
                .map(List::of)
                .orElse(List.of());
    }

    public List<RentModel> findByClientId(Long clientId) {
        return rentRepository.findByClientId(clientId);
    }

    public RentModel create(NewRentDTO dto) {
        Client client = clientRepository.findByCpf(dto.getClientCpf())
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado com CPF: " + dto.getClientCpf()));

        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCarName(dto.getCarName());
        rent.setCarModelType(dto.getCarModelType());
        rent.setRentValue(dto.getRentValue());
        rent.setRentDate(LocalDateTime.now());
        return rentRepository.save(rent);
    }

    public RentModel returnCar(Long id) {
        RentModel rent = findById(id);
        if (rent.getReturnDate() != null) {
            throw new IllegalStateException("Este aluguel ja foi finalizado.");
        }
        rent.setReturnDate(LocalDateTime.now());
        return rentRepository.save(rent);
    }

    public void delete(Long id) {
        RentModel rent = findById(id);
        rentRepository.delete(rent);
    }
}
