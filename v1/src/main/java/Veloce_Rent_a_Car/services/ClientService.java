package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.ClientDTO;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado com id: " + id));
    }

    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado com CPF: " + cpf));
    }

    public Client create(ClientDTO dto) {
        if (clientRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF ja cadastrado: " + dto.getCpf());
        }
        Client client = new Client();
        client.setNome(dto.getNome());
        client.setCpf(dto.getCpf());
        client.setTelefone(dto.getTelefone());
        client.setEmail(dto.getEmail());
        client.setEndereco(dto.getEndereco());
        return clientRepository.save(client);
    }

    public Client update(Long id, ClientDTO dto) {
        Client client = findById(id);
        client.setNome(dto.getNome());
        client.setTelefone(dto.getTelefone());
        client.setEmail(dto.getEmail());
        client.setEndereco(dto.getEndereco());
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);
        clientRepository.delete(client);
    }
}
