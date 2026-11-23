package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.RentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para persistência e consulta de aluguéis.
 */
@Repository
public interface RentRepository extends JpaRepository<RentModel, Long> {

    /**
     * Lista os aluguéis de um cliente.
     *
     * @param clientId identificador do cliente
     * @return aluguéis do cliente, ou uma lista vazia se não houver resultados
     */
    List<RentModel> findByClientId(Long clientId);

    /**
     * Lista os aluguéis de um carro.
     *
     * @param carId identificador do carro
     * @return aluguéis do carro, ou uma lista vazia se não houver resultados
     */
    List<RentModel> findByCarId(Long carId);

    /**
     * Lista os aluguéis com a situação informada.
     *
     * @param status situação desejada dos aluguéis
     * @return aluguéis correspondentes, ou uma lista vazia se não houver resultados
     */
    List<RentModel> findByStatus(RentModel.RentStatus status);
}
