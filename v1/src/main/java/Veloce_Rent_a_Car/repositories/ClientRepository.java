package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
