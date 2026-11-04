package Veloce_Rent_a_Car.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Veloce_Rent_a_Car.models.RentModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<RentModel, Long> {
    Optional<RentModel> findByClientId(Long clientId);
    List<RentModel> findByCarId(Long carId);
    List<RentModel> findByStatus(RentModel.RentStatus status);
}
