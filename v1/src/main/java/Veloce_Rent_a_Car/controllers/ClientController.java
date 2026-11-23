package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expõe as operações REST de consulta, cadastro, atualização e remoção de clientes
 * sob o caminho base {@code /api/clients}.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    // Get all clients
    /**
     * Recupera todos os clientes registrados.
     *
     * @return resposta HTTP 200 contendo a lista de clientes
     */
    @GetMapping
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    // Get client by ID
    /**
     * Recupera o cliente correspondente ao identificador informado.
     *
     * @param id identificador do cliente
     * @return resposta HTTP 200 contendo o cliente retornado pelo serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    // Get client by CPF
    /**
     * Pesquisa um cliente pelo CPF e informa quando nenhum registro é encontrado.
     *
     * @param cpf CPF do cliente procurado
     * @return resposta HTTP 200 com o cliente encontrado ou HTTP 404 sem conteúdo
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Client> getByCpf(@PathVariable String cpf) {
        return clientService.findByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new client
    /**
     * Cadastra o cliente enviado no corpo da requisição.
     *
     * @param client dados do cliente a ser criado
     * @return resposta HTTP 200 contendo o cliente persistido
     */
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client saved = clientService.create(client);
        return ResponseEntity.ok(saved);
    }

    // Update an existing client
    /**
     * Atualiza o cliente identificado com os dados recebidos na requisição.
     *
     * @param id identificador do cliente a ser atualizado
     * @param updatedClient dados que substituirão as informações atuais do cliente
     * @return resposta HTTP 200 contendo o cliente atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable Long id,
            @RequestBody Client updatedClient) {

        Client updated = clientService.update(id, updatedClient);
        return ResponseEntity.ok(updated);
    }

    // Delete a client
    /**
     * Remove o cliente correspondente ao identificador informado.
     *
     * @param id identificador do cliente a ser removido
     * @return resposta HTTP 204 sem conteúdo após a remoção
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}