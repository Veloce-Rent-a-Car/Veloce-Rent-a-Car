
package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.repositories.RentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    public List<RentModel> findAll() {
        return rentRepository.findAll();
    }

    public RentModel findById(Long id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluguel nao encontrado com id: " + id
                ));
    }

    public List<RentModel> findByClient(Long clientId) {
        return rentRepository.findByClientId(clientId);
    }

    @Transactional
    public RentModel create(NewRentDTO dto) {
        Client client = clientRepository.findByCpf(dto.getClientCpf())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente nao encontrado com CPF: " + dto.getClientCpf()
                ));

        Carro carro = carroRepository.findByModeloAndCategoriaAndStatusDisponibilidade(
                        dto.getCarName(),
                        dto.getCarModelType(),
                        Carro.StatusCarro.DISPONIVEL
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Carro nao encontrado ou indisponivel: "
                                + dto.getCarName() + " " + dto.getCarModelType()
                ));

        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(dto.getRentValue());
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);

        carro.setStatusDisponibilidade(Carro.StatusCarro.ALUGADO);
        carroRepository.save(carro);

        return rentRepository.save(rent);
    }

    public RentModel updateStatus(Long id, RentModel.RentStatus status) {
        RentModel rent = findById(id);
        rent.setStatus(status);

        return rentRepository.save(rent);
    }
}
