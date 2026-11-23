package Veloce_Rent_a_Car.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Veloce_Rent_a_Car.models.Carro;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para persistência e consulta de carros.
 */
@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {
    /**
     * Busca um carro pela placa.
     *
     * @param placa placa do carro
     * @return carro correspondente, se existir
     */
    Optional<Carro> findByPlaca(String placa);

    /**
     * Verifica se existe um carro com a placa informada.
     *
     * @param placa placa do carro
     * @return {@code true} se a placa estiver cadastrada; caso contrário, {@code false}
     */
    boolean existsByPlaca(String placa);

    /**
     * Lista os carros com a disponibilidade informada.
     *
     * @param statusDisponibilidade situação de disponibilidade desejada
     * @return carros correspondentes, ou uma lista vazia se não houver resultados
     */
    List<Carro> findByStatusDisponibilidade(Carro.StatusCarro statusDisponibilidade);

    /**
     * Busca um carro pela combinação de modelo, categoria e disponibilidade.
     *
     * @param modelo modelo do carro
     * @param categoria categoria do carro
     * @param status situação de disponibilidade desejada
     * @return carro correspondente, se existir
     */
    Optional<Carro> findByModeloAndCategoriaAndStatusDisponibilidade(String modelo, String categoria, Carro.StatusCarro status);
}
