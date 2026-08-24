package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.services.RentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rent")
public class RentController {

    @Autowired
    private RentService rentService;

    @GetMapping
    public ResponseEntity<List<RentModel>> getAll() {
        return ResponseEntity.ok(rentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentModel> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/client/{cpf}")
    public ResponseEntity<List<RentModel>> getByClientCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(rentService.findByClientCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<RentModel> create(@RequestBody NewRentDTO dto) {
        try {
            RentModel created = rentService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<RentModel> returnCar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentService.returnCar(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            rentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
