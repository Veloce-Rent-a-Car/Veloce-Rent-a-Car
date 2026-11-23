package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expõe as operações REST de consulta, cadastro, atualização e remoção de carros
 * sob o caminho base {@code /api/carros}.
 */
@RestController
@RequestMapping("/api/carros")
public class CarroController {

    @Autowired
    private CarroService carroService;

    /**
     * Lista todos os carros cadastrados.
     *
     * @return resposta HTTP 200 contendo a lista completa de carros
     */
    @GetMapping
    public ResponseEntity<List<Carro>> listarTodos() {
        List<Carro> carros = carroService.listarTodos();
        return ResponseEntity.ok(carros);
    }

    // Novo endpoint para listar apenas os carros disponíveis
    /**
     * Lista somente os carros que o serviço identifica como disponíveis.
     *
     * @return resposta HTTP 200 contendo os carros disponíveis
     */
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Carro>> listarDisponiveis() {
        List<Carro> carrosDisponiveis = carroService.listarCarrosDisponiveis();
        return ResponseEntity.ok(carrosDisponiveis);
    }

    /**
     * Recupera o carro correspondente ao identificador informado.
     *
     * @param id identificador do carro
     * @return resposta HTTP 200 contendo o carro retornado pelo serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<Carro> buscarPorId(@PathVariable Long id) {
        Carro carro = carroService.buscarPorId(id);
        return ResponseEntity.ok(carro);
    }

    /**
     * Cadastra o carro enviado no corpo da requisição.
     *
     * @param carro dados do carro a ser salvo
     * @return resposta HTTP 201 contendo o carro cadastrado
     */
    @PostMapping
    public ResponseEntity<Carro> criar(@RequestBody Carro carro) {
        Carro novoCarro = carroService.salvar(carro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCarro);
    }

    /**
     * Atualiza o carro identificado com os dados recebidos na requisição.
     *
     * @param id identificador do carro a ser atualizado
     * @param carro dados atualizados do carro
     * @return resposta HTTP 200 contendo o carro atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizar(@PathVariable Long id, @RequestBody Carro carro) {
        Carro carroAtualizado = carroService.atualizar(id, carro);
        return ResponseEntity.ok(carroAtualizado);
    }

    /**
     * Remove o carro correspondente ao identificador informado.
     *
     * @param id identificador do carro a ser removido
     * @return resposta HTTP 204 sem conteúdo após a remoção
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        carroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}