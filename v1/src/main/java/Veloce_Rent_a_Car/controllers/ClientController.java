package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.services.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Get all clients
    @GetMapping
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    // Get client by ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clientService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get client by CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Client> getByCpf(@PathVariable String cpf) {
        return clientService.findByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new client
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        try {
            Client saved = clientService.create(client);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing client
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable Long id,
            @RequestBody Client updatedClient) {

        try {
            Client updated = clientService.update(id, updatedClient);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        try {
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
