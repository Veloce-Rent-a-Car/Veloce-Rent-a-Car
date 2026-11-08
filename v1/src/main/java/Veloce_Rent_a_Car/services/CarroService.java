package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarroService {

    @Autowired
    private CarroRepository carroRepository;

    public List<Carro> listarTodos() {
        return carroRepository.findAll();
    }

    public List<Carro> listarCarrosDisponiveis() {
        return carroRepository.findByStatusDisponibilidade(Carro.StatusCarro.DISPONIVEL);
    }

    public Carro buscarPorId(Long id) {
        return carroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carro não encontrado com o ID: " + id));
    }

    public Carro salvar(Carro carro) {
        return carroRepository.save(carro);
    }

    public Carro atualizar(Long id, Carro carroAtualizado) {
        Carro carroExistente = buscarPorId(id);

        carroExistente.setModelo(carroAtualizado.getModelo());
        // placa é imutável (unique constraint)
        carroExistente.setCategoria(carroAtualizado.getCategoria());
        carroExistente.setValorDaDiaria(carroAtualizado.getValorDaDiaria());
        carroExistente.setStatusDisponibilidade(carroAtualizado.getStatusDisponibilidade());

        return carroRepository.save(carroExistente);
    }

    public void deletar(Long id) {
        Carro carro = buscarPorId(id);
        carroRepository.delete(carro);
    }
}