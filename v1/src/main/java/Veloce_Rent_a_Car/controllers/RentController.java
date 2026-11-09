
package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewRentDTO;
import Veloce_Rent_a_Car.models.RentModel;
import Veloce_Rent_a_Car.services.RentService;

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
        return ResponseEntity.ok(rentService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<RentModel>> getByClient(
            @PathVariable Long clientId) {

        List<RentModel> rents = rentService.findByClient(clientId);

        return ResponseEntity.ok(rents);
    }

    @PostMapping
    public ResponseEntity<RentModel> create(@RequestBody NewRentDTO dto) {
        RentModel rent = rentService.create(dto);
        return ResponseEntity.ok(rent);
    }

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
