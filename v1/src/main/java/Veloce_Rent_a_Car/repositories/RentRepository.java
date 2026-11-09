package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.RentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<RentModel, Long> {

    List<RentModel> findByClientId(Long clientId);

    List<RentModel> findByCarId(Long carId);

    List<RentModel> findByStatus(RentModel.RentStatus status);
}
