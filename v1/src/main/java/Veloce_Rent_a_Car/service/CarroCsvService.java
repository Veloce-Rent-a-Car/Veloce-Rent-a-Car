package Veloce_Rent_a_Car.service;

import Veloce_Rent_a_Car.models.Carro;
import Veloce_Rent_a_Car.repositories.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarroCsvService {

    @Autowired
    private CarroRepository carroRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public void exportarParaCsv(Path caminhoArquivo) throws IOException {
        List<Carro> carros = carroRepository.findAll();

        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardCharsets.UTF_8)) {
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
        // Limite de tamanho do arquivo
        long fileSize = Files.size(caminhoArquivo);
        if (fileSize > MAX_FILE_SIZE) {
            throw new IOException("Arquivo excede o tamanho máximo de " + (MAX_FILE_SIZE / 1024 / 1024) + " MB");
        }

        Set<String> placasExistentes = carroRepository.findAll().stream()
                .map(Carro::getPlaca)
                .collect(Collectors.toSet());

        int importados = 0;

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)) {
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
                    carro.setModelo(unescapeCsv(campos[1].trim()));
                    carro.setPlaca(placa);
                    carro.setCategoria(unescapeCsv(campos[3].trim()));
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
        String escaped = valor;
        // Escape de aspas e vírgulas
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = "\"" + escaped.replace("\"", "\"\"") + "\"";
        }
        // Proteção contra CSV Injection: prefixar com ' se começar com = + - @
        if (escaped.startsWith("=") || escaped.startsWith("+") || escaped.startsWith("-") || escaped.startsWith("@")) {
            escaped = "'" + escaped;
        }
        return escaped;
    }

    private String unescapeCsv(String valor) {
        if (valor == null) return "";
        String unescaped = valor.trim();
        // Remove aspas externas se existirem
        if (unescaped.startsWith("\"") && unescaped.endsWith("\"") && unescaped.length() >= 2) {
            unescaped = unescaped.substring(1, unescaped.length() - 1);
            unescaped = unescaped.replace("\"\"", "\"");
        }
        // Remove prefixo ' de proteção contra CSV injection
        if (unescaped.startsWith("'")) {
            unescaped = unescaped.substring(1);
        }
        return unescaped;
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