package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Client> findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf);
    }

    public Client create(Client client) {
        if (clientRepository.existsByCpf(client.getCpf())) {
            throw new IllegalStateException("Ja existe um cliente com este CPF: " + client.getCpf());
        }
        return clientRepository.save(client);
    }
}
