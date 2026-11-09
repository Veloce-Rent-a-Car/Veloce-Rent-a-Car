package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente nao encontrado com id: " + id));
    }

    public Optional<Client> findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf);
    }

    public Client create(Client client) {
        if (clientRepository.existsByCpf(client.getCpf())) {
            throw new IllegalStateException(
                    "Ja existe um cliente com este CPF: " + client.getCpf());
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public Client update(Long id, Client updatedClient) {
        Client client = findById(id);

        client.setName(updatedClient.getName());
        client.setPhone(updatedClient.getPhone());
        client.setEmail(updatedClient.getEmail());
        client.setAddress(updatedClient.getAddress());

        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);
        clientRepository.delete(client);
    }
}
