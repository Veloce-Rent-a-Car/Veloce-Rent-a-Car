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

@RestController
@RequestMapping("/api/carros/csv")
public class CarroCsvController {

    @Autowired
    private CarroCsvService carroCsvService;

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