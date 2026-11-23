package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.dto.NewPaymentDTO;
import Veloce_Rent_a_Car.models.Payment;
import Veloce_Rent_a_Car.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expõe endpoints de consulta e registro de pagamentos sob o caminho base
 * {@code /payments}, incluindo a busca dos pagamentos vinculados a um aluguel.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Recupera todos os pagamentos registrados.
     *
     * @return resposta HTTP 200 contendo a lista de pagamentos
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    /**
     * Recupera o pagamento correspondente ao identificador informado.
     *
     * @param id identificador do pagamento
     * @return resposta HTTP 200 contendo o pagamento retornado pelo serviço
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    /**
     * Lista os pagamentos associados ao aluguel indicado.
     *
     * @param rentId identificador do aluguel
     * @return resposta HTTP 200 contendo os pagamentos do aluguel
     */
    @GetMapping("/rent/{rentId}")
    public ResponseEntity<List<Payment>> getByRentId(@PathVariable Long rentId) {
        return ResponseEntity.ok(paymentService.findByRentId(rentId));
    }

    /**
     * Registra um pagamento a partir do aluguel, valor e método informados.
     *
     * @param dto dados necessários para criar o pagamento
     * @return resposta HTTP 201 contendo o pagamento criado
     */
    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody NewPaymentDTO dto) {
        Payment created = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}