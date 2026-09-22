package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.services.RentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
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

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getByClient(@PathVariable Long clientId) {
        return rentService.findByClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RentModel> create(@RequestBody NewRentDTO dto) {
        try {
            RentModel rent = rentService.create(dto);
            return ResponseEntity.ok(rent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RentModel> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            RentModel.RentStatus rentStatus = RentModel.RentStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(rentService.updateStatus(id, rentStatus));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
