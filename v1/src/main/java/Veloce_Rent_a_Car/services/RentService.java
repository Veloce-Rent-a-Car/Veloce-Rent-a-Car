
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
import java.util.Map;
import java.util.stream.Collectors;

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
        Client client = findClientByCpf(dto.getClientCpf());
        Carro carro = findAvailableCarro(dto.getCarName(), dto.getCarModelType());

        RentModel rent = buildRentModel(client, carro, dto);
        updateCarroStatus(carro, Carro.StatusCarro.ALUGADO);

        return rentRepository.save(rent);
    }

    public Map<Client, List<RentModel>> relatorioPorCliente() {
        List<RentModel> todos = rentRepository.findAll();
        return todos.stream()
                .collect(Collectors.groupingBy(RentModel::getClient));
    }

    public Map<Carro, List<RentModel>> relatorioPorCarro() {
        List<RentModel> todos = rentRepository.findAll();
        return todos.stream()
                .collect(Collectors.groupingBy(RentModel::getCar));
    }

    private Client findClientByCpf(String cpf) {
        return clientRepository.findByCpf(cpf)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente nao encontrado com CPF: " + cpf
                ));
    }

    private Carro findAvailableCarro(String modelo, String categoria) {
        return carroRepository.findByModeloAndCategoriaAndStatusDisponibilidade(
                        modelo, categoria, Carro.StatusCarro.DISPONIVEL
                )
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Carro nao encontrado ou indisponivel: " + modelo + " " + categoria
                ));
    }

    private RentModel buildRentModel(Client client, Carro carro, NewRentDTO dto) {
        RentModel rent = new RentModel();
        rent.setClient(client);
        rent.setCar(carro);
        rent.setRentValue(dto.getRentValue());
        rent.setStartDate(LocalDateTime.now());
        rent.setStatus(RentModel.RentStatus.PENDING);
        return rent;
    }

    private void updateCarroStatus(Carro carro, Carro.StatusCarro novoStatus) {
        carro.setStatusDisponibilidade(novoStatus);
        carroRepository.save(carro);
    }

    public RentModel updateStatus(Long id, RentModel.RentStatus status) {
        RentModel rent = findById(id);
        rent.setStatus(status);
        return rentRepository.save(rent);
    }
}
