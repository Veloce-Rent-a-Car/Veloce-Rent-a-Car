package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarroCsvService {

    @Autowired
    private CarroRepository carroRepository;

    public void exportarParaCsv(Path caminhoArquivo) throws IOException {
        List<Carro> carros = carroRepository.findAll();

        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo)) {
            writer.write("id,modelo,placa,categoria,valorDaDiaria,statusDisponibilidade");
            writer.newLine();

            for (Carro carro : carros) {
                writer.write(String.join(",",
                        String.valueOf(carro.getId()),
                        escapeCsv(carro.getModelo()),
                        escapeCsv(carro.getPlaca()),
                        escapeCsv(carro.getCategoria()),
                        carro.getValorDaDiaria().toPlainString(),
                        carro.getStatusDisponibilidade().name()
                ));
                writer.newLine();
            }
        }
    }

    public int importarDeCsv(Path caminhoArquivo) throws IOException {
        Set<String> placasExistentes = carroRepository.findAll().stream()
                .map(Carro::getPlaca)
                .collect(Collectors.toSet());

        int importados = 0;

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo)) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue; // pula cabeçalho
                }

                String[] campos = parseCsvLine(linha);
                if (campos.length < 5) {
                    continue; // linha inválida
                }

                String placa = campos[2].trim();
                if (placasExistentes.contains(placa)) {
                    continue; // pula duplicado
                }

                try {
                    Carro carro = new Carro();
                    carro.setModelo(campos[1].trim());
                    carro.setPlaca(placa);
                    carro.setCategoria(campos[3].trim());
                    carro.setValorDaDiaria(new BigDecimal(campos[4].trim()));
                    carro.setStatusDisponibilidade(Carro.StatusCarro.valueOf(campos[5].trim()));

                    carroRepository.save(carro);
                    placasExistentes.add(placa);
                    importados++;
                } catch (Exception e) {
                    // ignora linhas com dados inválidos
                }
            }
        }

        return importados;
    }

    private String escapeCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String[] parseCsvLine(String line) {
        // Simple CSV parser handling quoted fields
        List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}