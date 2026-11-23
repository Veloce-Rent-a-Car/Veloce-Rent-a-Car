package Veloce_Rent_a_Car.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Registra os metadados gerais da documentação OpenAPI da API Veloce Rent a Car,
 * identificada como {@code VRC_API}, versão {@code 0.1} beta.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "VRC_API", version = "0.1", description = "Veloce Rent a Car (VRC) - beta")
)
public class OpenApiConfig {
}