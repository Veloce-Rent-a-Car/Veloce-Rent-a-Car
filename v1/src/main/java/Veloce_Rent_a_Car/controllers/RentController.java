package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.repositories.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rent")
public class RentController {

    @Autowired
    private RentRepository rentRepository;

}
