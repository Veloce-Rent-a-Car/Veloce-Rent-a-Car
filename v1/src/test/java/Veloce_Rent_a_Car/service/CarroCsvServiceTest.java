package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import Veloce_Rent_a_Car.service.CarroCsvService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarroCsvServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private CarroCsvService carroCsvService;

    @TempDir
    Path tempDir;

    @Test
    void exportarParaCsv_sucesso() throws IOException {
        Carro carro1 = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        carro1.setId(1L);
        Carro carro2 = new Carro("Corolla", "XYZ7890", "sedan", new BigDecimal("280.00"), Carro.StatusCarro.ALUGADO);
        carro2.setId(2L);

        when(carroRepository.findAll()).thenReturn(List.of(carro1, carro2));

        Path arquivo = tempDir.resolve("carros.csv");
        carroCsvService.exportarParaCsv(arquivo);

        String conteudo = Files.readString(arquivo);
        assertThat(conteudo).contains("id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade");
        assertThat(conteudo).contains("1,Civic,ABC1234,sedan,300.00,DISPONIVEL");
        assertThat(conteudo).contains("2,Corolla,XYZ7890,sedan,280.00,ALUGADO");
    }

    @Test
    void exportarParaCsv_listaVazia() throws IOException {
        when(carroRepository.findAll()).thenReturn(Collections.emptyList());

        Path arquivo = tempDir.resolve("carros-vazio.csv");
        carroCsvService.exportarParaCsv(arquivo);

        String conteudo = Files.readString(arquivo);
        assertThat(conteudo).contains("id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade");
        assertThat(conteudo.lines().count()).isEqualTo(1); // apenas cabeçalho
    }

    @Test
    void importarDeCsv_sucesso() throws IOException {
        Carro existente = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        when(carroRepository.findAll()).thenReturn(List.of(existente));
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        String csv = "id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade\n" +
                     "1,Corolla,XYZ7890,sedan,280.00,DISPONIVEL\n" +
                     "2,Yaris,DEF5678,hatch,220.00,DISPONIVEL";
        Path arquivo = tempDir.resolve("import.csv");
        Files.writeString(arquivo, csv);

        int importados = carroCsvService.importarDeCsv(arquivo);

        assertThat(importados).isEqualTo(2);
        verify(carroRepository, times(2)).save(any(Carro.class));
    }

    @Test
    void importarDeCsv_placaDuplicada_pula() throws IOException {
        Carro existente = new Carro("Civic", "ABC1234", "sedan", new BigDecimal("300.00"), Carro.StatusCarro.DISPONIVEL);
        when(carroRepository.findAll()).thenReturn(List.of(existente));
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        String csv = "id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade\n" +
                     "1,Corolla,ABC1234,sedan,280.00,DISPONIVEL\n" + // placa duplicada
                     "2,Yaris,DEF5678,hatch,220.00,DISPONIVEL";
        Path arquivo = tempDir.resolve("import-dup.csv");
        Files.writeString(arquivo, csv);

        int importados = carroCsvService.importarDeCsv(arquivo);

        assertThat(importados).isEqualTo(1); // apenas o Yaris
        verify(carroRepository, times(1)).save(any(Carro.class));
    }

    @Test
    void importarDeCsv_linhaInvalida_pula() throws IOException {
        when(carroRepository.findAll()).thenReturn(Collections.emptyList());
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        String csv = "id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade\n" +
                     "1,Corolla,XYZ7890,sedan,280.00,DISPONIVEL\n" +
                     "linha,invalida,sem,colunas\n" + // linha inválida
                     "2,Yaris,DEF5678,hatch,220.00,DISPONIVEL";
        Path arquivo = tempDir.resolve("import-invalid.csv");
        Files.writeString(arquivo, csv);

        int importados = carroCsvService.importarDeCsv(arquivo);

        assertThat(importados).isEqualTo(2); // pula a linha inválida
    }

    @Test
    void importarDeCsv_valorInvalido_pula() throws IOException {
        when(carroRepository.findAll()).thenReturn(Collections.emptyList());
        when(carroRepository.save(any(Carro.class))).thenAnswer(inv -> inv.getArgument(0));

        String csv = "id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade\n" +
                     "1,Corolla,XYZ7890,sedan,abc,DISPONIVEL\n" + // valor inválido
                     "2,Yaris,DEF5678,hatch,220.00,DISPONIVEL";
        Path arquivo = tempDir.resolve("import-bad-value.csv");
        Files.writeString(arquivo, csv);

        int importados = carroCsvService.importarDeCsv(arquivo);

        assertThat(importados).isEqualTo(1); // pula a linha com valor inválido
    }

    @Test
    void importarDeCsv_arquivoNaoExiste_lancaIOException() {
        Path arquivoInexistente = tempDir.resolve("nao-existe.csv");

        assertThatThrownBy(() -> carroCsvService.importarDeCsv(arquivoInexistente))
                .isInstanceOf(IOException.class);
    }
}