package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.services.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carros")
public class CarroController {

    @Autowired
    private CarroService carroService;

    @GetMapping
    public ResponseEntity<List<Carro>> listarTodos() {
        List<Carro> carros = carroService.listarTodos();
        return ResponseEntity.ok(carros);
    }

    // Novo endpoint para listar apenas os carros disponíveis
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Carro>> listarDisponiveis() {
        List<Carro> carrosDisponiveis = carroService.listarCarrosDisponiveis();
        return ResponseEntity.ok(carrosDisponiveis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carro> buscarPorId(@PathVariable Long id) {
        Carro carro = carroService.buscarPorId(id);
        return ResponseEntity.ok(carro);
    }

    @PostMapping
    public ResponseEntity<Carro> criar(@RequestBody Carro carro) {
        Carro novoCarro = carroService.salvar(carro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCarro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carro> atualizar(@PathVariable Long id, @RequestBody Carro carro) {
        Carro carroAtualizado = carroService.atualizar(id, carro);
        return ResponseEntity.ok(carroAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        carroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
