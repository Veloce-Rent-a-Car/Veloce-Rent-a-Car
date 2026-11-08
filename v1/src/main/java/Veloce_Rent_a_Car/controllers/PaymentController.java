package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewPaymentDTO;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/rent/{rentId}")
    public ResponseEntity<List<Payment>> getByRentId(@PathVariable Long rentId) {
        return ResponseEntity.ok(paymentService.findByRentId(rentId));
    }

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody NewPaymentDTO dto) {
        Payment created = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}