
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

/**
 * Coordena consultas, criação e atualização de aluguéis, além de agrupar relatórios por cliente
 * e por carro e controlar a disponibilidade do veículo alugado.
 */
@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarroRepository carroRepository;

    /**
     * Recupera todos os aluguéis persistidos.
     *
     * @return lista com todos os aluguéis cadastrados
     */
    public List<RentModel> findAll() {
        return rentRepository.findAll();
    }

    /**
     * Localiza um aluguel pelo seu identificador.
     *
     * @param id identificador do aluguel
     * @return aluguel correspondente ao identificador
     * @throws RecursoNaoEncontradoException se o aluguel não existir
     */
    public RentModel findById(Long id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluguel nao encontrado com id: " + id
                ));
    }

    /**
     * Recupera os aluguéis vinculados ao identificador de um cliente.
     *
     * @param clientId identificador do cliente
     * @return lista de aluguéis associados ao cliente
     */
    public List<RentModel> findByClient(Long clientId) {
        return rentRepository.findByClientId(clientId);
    }

    /**
     * Cria um aluguel pendente para o CPF informado e para um carro disponível com o modelo e a
     * categoria solicitados. Define o início no horário atual, marca o carro como alugado e
     * executa as alterações em uma transação.
     *
     * @param dto dados do cliente, carro e valor do novo aluguel
     * @return aluguel persistido com status {@code PENDING}
     * @throws RecursoNaoEncontradoException se o cliente não existir ou não houver carro disponível compatível
     */
    @Transactional
    public RentModel create(NewRentDTO dto) {
        Client client = findClientByCpf(dto.getClientCpf());
        Carro carro = findAvailableCarro(dto.getCarName(), dto.getCarModelType());

        RentModel rent = buildRentModel(client, carro, dto);
        updateCarroStatus(carro, Carro.StatusCarro.ALUGADO);

        return rentRepository.save(rent);
    }

    /**
     * Agrupa todos os aluguéis cadastrados pelo cliente associado a cada registro.
     *
     * @return mapa de cada cliente para sua lista de aluguéis
     */
    public Map<Client, List<RentModel>> relatorioPorCliente() {
        List<RentModel> todos = rentRepository.findAll();
        return todos.stream()
                .collect(Collectors.groupingBy(RentModel::getClient));
    }

    /**
     * Agrupa todos os aluguéis cadastrados pelo carro associado a cada registro.
     *
     * @return mapa de cada carro para sua lista de aluguéis
     */
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
        return RentModel.builder()
                .client(client)
                .car(carro)
                .rentValue(dto.getRentValue())
                .startDate(LocalDateTime.now())
                .status(RentModel.RentStatus.PENDING)
                .build();
    }

    private void updateCarroStatus(Carro carro, Carro.StatusCarro novoStatus) {
        carro.setStatusDisponibilidade(novoStatus);
        carroRepository.save(carro);
    }

    /**
     * Atualiza somente o status de um aluguel existente e persiste a alteração.
     *
     * @param id identificador do aluguel
     * @param status novo status do aluguel
     * @return aluguel persistido com o status atualizado
     * @throws RecursoNaoEncontradoException se o aluguel não existir
     */
    public RentModel updateStatus(Long id, RentModel.RentStatus status) {
        RentModel rent = findById(id);
        rent.setStatus(status);
        return rentRepository.save(rent);
    }
}
