package Veloce_Rent_a_Car.repositories;

import Veloce_Rent_a_Car.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para persistência e consulta de clientes.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Busca um cliente pelo CPF.
     *
     * @param cpf CPF do cliente
     * @return cliente correspondente, se existir
     */
    Optional<Client> findByCpf(String cpf);

    /**
     * Busca um cliente pelo endereço de e-mail.
     *
     * @param email endereço de e-mail do cliente
     * @return cliente correspondente, se existir
     */
    Optional<Client> findByEmail(String email);

    /**
     * Verifica se existe um cliente com o CPF informado.
     *
     * @param cpf CPF do cliente
     * @return {@code true} se o CPF estiver cadastrado; caso contrário, {@code false}
     */
    boolean existsByCpf(String cpf);
}
