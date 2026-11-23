
package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Oferece operações de consulta e manutenção do cadastro de carros e de sua disponibilidade.
 */
@Service
public class CarroService {

    @Autowired
    private CarroRepository carroRepository;

    /**
     * Recupera todos os carros cadastrados, independentemente de seu status.
     *
     * @return lista com todos os carros persistidos
     */
    public List<Carro> listarTodos() {
        return carroRepository.findAll();
    }

    /**
     * Filtra o cadastro pelos carros atualmente disponíveis para aluguel.
     *
     * @return lista de carros com status {@code DISPONIVEL}
     */
    public List<Carro> listarCarrosDisponiveis() {
        return carroRepository.findByStatusDisponibilidade(
                Carro.StatusCarro.DISPONIVEL
        );
    }

    /**
     * Localiza um carro pelo seu identificador.
     *
     * @param id identificador do carro
     * @return carro correspondente ao identificador
     * @throws RecursoNaoEncontradoException se o carro não existir
     */
    public Carro buscarPorId(Long id) {
        return carroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Carro não encontrado com o ID: " + id
                ));
    }

    /**
     * Persiste os dados do carro informado por meio do repositório.
     *
     * @param carro carro que será salvo
     * @return instância persistida pelo repositório
     */
    public Carro salvar(Carro carro) {
        return carroRepository.save(carro);
    }

    /**
     * Atualiza modelo, categoria, valor da diária e disponibilidade do carro existente,
     * preservando sua placa, que é imutável.
     *
     * @param id identificador do carro que será atualizado
     * @param carroAtualizado objeto com os novos dados editáveis
     * @return carro existente após a persistência das alterações
     * @throws RecursoNaoEncontradoException se o carro não existir
     */
    public Carro atualizar(Long id, Carro carroAtualizado) {
        Carro carroExistente = buscarPorId(id);

        carroExistente.setModelo(carroAtualizado.getModelo());

        // A placa é imutável.
        carroExistente.setCategoria(carroAtualizado.getCategoria());
        carroExistente.setValorDaDiaria(carroAtualizado.getValorDaDiaria());
        carroExistente.setStatusDisponibilidade(
                carroAtualizado.getStatusDisponibilidade()
        );

        return carroRepository.save(carroExistente);
    }

    /**
     * Remove do cadastro o carro identificado, após confirmar que ele existe.
     *
     * @param id identificador do carro que será removido
     * @throws RecursoNaoEncontradoException se o carro não existir
     */
    public void deletar(Long id) {
        Carro carro = buscarPorId(id);
        carroRepository.delete(carro);
    }
}
