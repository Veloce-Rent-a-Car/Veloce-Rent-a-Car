package Veloce_Rent_a_Car.controllers;

import Veloce_Rent_a_Car.service.CarroCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Expõe endpoints para exportar o cadastro de carros em CSV e importar carros
 * de um arquivo CSV enviado pelo cliente, sob {@code /api/carros/csv}.
 */
@RestController
@RequestMapping("/api/carros/csv")
public class CarroCsvController {

    @Autowired
    private CarroCsvService carroCsvService;

    /**
     * Exporta os carros para um arquivo CSV temporário e o disponibiliza como anexo
     * denominado {@code carros.csv}, com conteúdo em UTF-8.
     *
     * @return resposta HTTP 200 contendo o recurso CSV para download
     * @throws IOException se ocorrer falha ao criar, preencher ou abrir o arquivo temporário
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> exportarCsv() throws IOException {
        Path tempFile = Files.createTempFile("carros-", ".csv");
        tempFile.toFile().deleteOnExit();

        carroCsvService.exportarParaCsv(tempFile);

        Resource resource = new InputStreamResource(Files.newInputStream(tempFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"carros.csv\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(resource);
    }

    /**
     * Importa carros do arquivo CSV enviado, copiando-o antes para um arquivo temporário.
     * Um upload vazio é rejeitado sem acionar o serviço de importação.
     *
     * @param file arquivo CSV recebido no parâmetro multipart {@code file}
     * @return resposta HTTP 400 para arquivo vazio ou HTTP 200 com a quantidade de carros importados
     * @throws IOException se ocorrer falha ao ler o upload ou manipular o arquivo temporário
     */
    @PostMapping("/import")
    public ResponseEntity<String> importarCsv(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo vazio");
        }

        // Não usar nome do arquivo original - segurança
        Path tempFile = Files.createTempFile("import-", ".csv");
        tempFile.toFile().deleteOnExit();
        Files.copy(file.getInputStream(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        int importados = carroCsvService.importarDeCsv(tempFile);

        return ResponseEntity.ok(String.format("%d carros importados com sucesso", importados));
    }
}