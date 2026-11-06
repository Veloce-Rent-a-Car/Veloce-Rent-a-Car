package Veloce_Rent_a_Car.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Veloce_Rent_a_Car.models.Carro;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {
    Optional<Carro> findByPlaca(String placa);

    boolean existsByPlaca(String placa);

    List<Carro> findByStatusDisponibilidade(Carro.StatusCarro statusDisponibilidade);
}
