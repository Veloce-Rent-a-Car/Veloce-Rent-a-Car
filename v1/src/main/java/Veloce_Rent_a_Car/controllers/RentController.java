
package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.services.RentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Expõe os endpoints REST de consulta, cadastro, atualização de status e relatórios
 * de aluguéis sob o caminho base {@code /api/rentals}.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentController {

    @Autowired
    private RentService rentService;

    /**
     * Recupera todos os aluguéis registrados por meio do serviço de aluguéis.
     *
     * @return resposta HTTP 200 contendo a lista de aluguéis encontrada
     */
    @GetMapping
    public ResponseEntity<List<RentModel>> getAll() {
        return ResponseEntity.ok(rentService.findAll());
    }

    /**
     * Recupera o aluguel correspondente ao identificador informado.
     *
     * @param id identificador do aluguel
     * @return resposta HTTP 200 contendo o aluguel retornado pelo serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentModel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rentService.findById(id));
    }

    /**
     * Lista os aluguéis associados ao cliente indicado.
     *
     * @param clientId identificador do cliente
     * @return resposta HTTP 200 contendo os aluguéis do cliente
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<RentModel>> getByClient(
            @PathVariable Long clientId) {

        List<RentModel> rents = rentService.findByClient(clientId);

        return ResponseEntity.ok(rents);
    }

    /**
     * Monta o relatório de aluguéis agrupados por cliente.
     *
     * @return resposta HTTP 200 com um mapa de cada cliente para seus aluguéis
     */
    @GetMapping("/report/by-client")
    public ResponseEntity<Map<Client, List<RentModel>>> relatorioPorCliente() {
        return ResponseEntity.ok(rentService.relatorioPorCliente());
    }

    /**
     * Monta o relatório de aluguéis agrupados por carro.
     *
     * @return resposta HTTP 200 com um mapa de cada carro para seus aluguéis
     */
    @GetMapping("/report/by-car")
    public ResponseEntity<Map<Carro, List<RentModel>>> relatorioPorCarro() {
        return ResponseEntity.ok(rentService.relatorioPorCarro());
    }

    /**
     * Cria um aluguel a partir dos dados de seleção do cliente, carro e valor recebidos.
     *
     * @param dto dados necessários para criar o aluguel
     * @return resposta HTTP 200 contendo o aluguel criado
     */
    @PostMapping
    public ResponseEntity<RentModel> create(@RequestBody NewRentDTO dto) {
        RentModel rent = rentService.create(dto);
        return ResponseEntity.ok(rent);
    }

    /**
     * Atualiza o status de um aluguel, aceitando o nome do status sem diferenciar
     * letras maiúsculas de minúsculas.
     *
     * @param id identificador do aluguel
     * @param status nome do novo status do aluguel
     * @return resposta HTTP 200 contendo o aluguel com o status atualizado
     * @throws IllegalArgumentException se o status informado não corresponder a um valor de {@link RentModel.RentStatus}
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RentModel> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        RentModel.RentStatus rentStatus =
                RentModel.RentStatus.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
                rentService.updateStatus(id, rentStatus)
        );
    }
}