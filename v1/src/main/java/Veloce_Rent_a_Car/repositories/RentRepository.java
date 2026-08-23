package Veloce_Rent_a_Car.repositories;


import Veloce_Rent_a_Car.models.RentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<RentModel, Long> {
    Optional<RentModel> findByCpf(String cpf);
}
