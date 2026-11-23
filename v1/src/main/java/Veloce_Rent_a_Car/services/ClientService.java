package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.exception.RegraDeNegocioException;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Gerencia o cadastro de clientes, incluindo consultas, unicidade de CPF, codificação de senha
 * e atualização dos dados pessoais editáveis.
 */
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Recupera todos os clientes persistidos.
     *
     * @return lista com todos os clientes cadastrados
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Localiza um cliente pelo seu identificador.
     *
     * @param id identificador do cliente
     * @return cliente correspondente ao identificador
     * @throws RecursoNaoEncontradoException se o cliente não existir
     */
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente nao encontrado com id: " + id));
    }

    /**
     * Pesquisa um cliente pelo CPF sem lançar exceção quando ele não estiver cadastrado.
     *
     * @param cpf CPF do cliente procurado
     * @return cliente encontrado ou um {@link Optional} vazio
     */
    public Optional<Client> findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf);
    }

    /**
     * Cadastra um cliente após verificar que seu CPF ainda não existe e codificar sua senha.
     *
     * @param client cliente com os dados de cadastro e a senha ainda não codificada
     * @return cliente persistido com a senha codificada
     * @throws RegraDeNegocioException se já existir cliente com o mesmo CPF
     */
    public Client create(Client client) {
        if (clientRepository.existsByCpf(client.getCpf())) {
            throw new RegraDeNegocioException(
                    "Ja existe um cliente com este CPF: " + client.getCpf());
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    /**
     * Atualiza nome, telefone, e-mail e endereço de um cliente existente, sem alterar CPF ou senha.
     *
     * @param id identificador do cliente que será atualizado
     * @param updatedClient objeto contendo os novos dados pessoais
     * @return cliente persistido após a atualização
     * @throws RecursoNaoEncontradoException se o cliente não existir
     */
    public Client update(Long id, Client updatedClient) {
        Client client = findById(id);

        client.setName(updatedClient.getName());
        client.setPhone(updatedClient.getPhone());
        client.setEmail(updatedClient.getEmail());
        client.setAddress(updatedClient.getAddress());

        return clientRepository.save(client);
    }

    /**
     * Remove o cliente identificado, após confirmar que ele existe.
     *
     * @param id identificador do cliente que será removido
     * @throws RecursoNaoEncontradoException se o cliente não existir
     */
    public void delete(Long id) {
        Client client = findById(id);
        clientRepository.delete(client);
    }
}
