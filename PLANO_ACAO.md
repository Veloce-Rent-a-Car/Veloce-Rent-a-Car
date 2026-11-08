# Plano de Ação — Pontos Críticos Veloce Rent-a-Car

## Visão Geral
Ordem de execução baseada no veredito do tribunal: **1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10-14**

---

## 1. Fix RentService.create() — **CRÍTICO**

**Problema:** `RentModel` tem `@ManyToOne Carro car` mas `create(NewRentDTO)` não busca nem vincula o carro. DTO tem `carName` + `carModelType` não utilizados.

**Arquivos a alterar:**
- `v1/src/main/java/Veloce_Rent_a_Car/services/RentService.java`
- `v1/src/main/java/Veloce_Rent_a_Car/repositories/CarroRepository.java` (novo método)
- `v1/src/main/java/Veloce_Rent_a_Car/dto/NewRentDTO.java` (opcional: validar campos)

**Passos:**
1. Adicionar `@Autowired CarroRepository` no `RentService`
2. No `create()`:
   - Buscar `Carro` por `modelo` + `categoria` (ou criar método `findByModeloAndCategoriaAndStatusDisponibilidade`)
   - Validar se `statusDisponibilidade == DISPONIVEL`
   - Setar `rent.setCar(carro)`
   - Mudar `carro.setStatusDisponibilidade(ALUGADO)`
   - `carroRepository.save(carro)`
   - `rentRepository.save(rent)`
3. Tratar exceção: carro não encontrado ou indisponível → `EntityNotFoundException` ou `IllegalStateException`

**Teste:** Criar `@SpringBootTest` + `MockMvc` POST `/api/rentals` verificando se carro vinculado e status alterado.

---

## 2. Implement SecurityConfig (JWT) — **CRÍTICO**

**Problema:** Spring Security no classpath mas sem configuração. Endpoints abertos. Senha aleatória no startup.

**Dependências já no pom:** `spring-boot-starter-security`, `spring-boot-starter-webmvc`

**Arquivos a criar:**
- `v1/src/main/java/Veloce_Rent_a_Car/config/SecurityConfig.java`
- `v1/src/main/java/Veloce_Rent_a_Car/security/JwtTokenProvider.java`
- `v1/src/main/java/Veloce_Rent_a_Car/security/JwtAuthenticationFilter.java`
- `v1/src/main/java/Veloce_Rent_a_Car/security/JwtAuthenticationEntryPoint.java`
- `v1/src/main/java/Veloce_Rent_a_Car/security/CustomUserDetailsService.java` (ou usar `Client` como principal)

**Passos:**
1. `SecurityConfig`:
   - `@EnableWebSecurity` + `@Configuration`
   - `SecurityFilterChain` bean: stateless, CORS, permitAll para `/swagger-ui/**`, `/v3/api-docs/**`, `/api/clients` (POST), `/payments` (POST?) — definir regras
   - `PasswordEncoder` bean (BCrypt)
   - `AuthenticationManager` bean
   - Add `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`
2. `JwtTokenProvider`: generate/validate token, claims (subject=email/cpf, roles), expiration
3. `JwtAuthenticationFilter`: extract Bearer token, validate, set `SecurityContextHolder`
4. `JwtAuthenticationEntryPoint`: return 401 JSON padronizado
5. Remover senha aleatória: definir `spring.security.user.password` em `application.properties` ou desabilitar auto-config
6. Atualizar `OpenApiConfig`: descomentar `SecurityScheme` JWT quando pronto

**Teste:** Integration test com `@SpringBootTest` + `MockMvc` + `@WithMockUser` ou token real.

---

## 3. Fix CarroService.atualizar() — **ALTO**

**Problema:** `atualizar()` chama `setPlaca()` mas `placa` é `@Column(unique=true)`. Pode violar unicidade.

**Arquivo:** `v1/src/main/java/Veloce_Rent_a_Car/services/CarroService.java`

**Solução simples (recomendada):**
```java
// Remover linha:
// carroExistente.setPlaca(carroAtualizado.getPlaca());
// Placa é imutável após criação
```

**Alternativa (se precisar permitir mudança):**
```java
if (!carroExistente.getPlaca().equals(carroAtualizado.getPlaca())) {
    if (carroRepository.existsByPlaca(carroAtualizado.getPlaca())) {
        throw new IllegalArgumentException("Placa já cadastrada");
    }
    carroExistente.setPlaca(carroAtualizado.getPlaca());
}
```

**Teste:** Unit test `@ExtendWith(MockitoExtension)` verificando que placa não muda ou lança exceção.

---

## 4. Fix DatabaseInitializer — **ALTO**

**Problema:** `CREATE DATABASE IF NOT EXISTS` não existe no PostgreSQL.

**Arquivo:** `v1/src/main/java/Veloce_Rent_a_Car/config/DatabaseInitializer.java`

**Solução:**
```java
@Override
public void run(String... args) throws Exception {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
        // PostgreSQL: bloco DO anônimo para CREATE DATABASE condicional
        String sql = "DO $$ BEGIN " +
                     "IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'carrental') THEN " +
                     "CREATE DATABASE carrental; " +
                     "END IF; END $$;";
        stmt.execute(sql);
        System.out.println("Banco de dados 'carrental' verificado/criado.");
    } catch (Exception e) {
        System.err.println("Erro ao criar banco: " + e.getMessage());
    }
}
```

**Nota:** Requer conexão no `postgres` DB (default), não no `carrental`. O `application.properties` usa `carrental` — pode falhar na primeira execução. Alternativa: Flyway/Liquibase.

---

## 5. GlobalExceptionHandler — **MÉDIO**

**Arquivo novo:** `v1/src/main/java/Veloce_Rent_a_Car/exception/GlobalExceptionHandler.java`

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, WebRequest req) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, WebRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex, WebRequest req) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest req) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", req);
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String msg, WebRequest req) {
        ErrorResponse err = new ErrorResponse(
            Instant.now(), status.value(), status.getReasonPhrase(), msg,
            req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(err);
    }

    record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {}
}
```

**Depois:** Remover try/catch dos controllers (`RentController`, `PaymentController`, `ClientController`, `CarroController`) — deixar exceções subirem.

---

## 6. Testes — **MÉDIO**

**Estrutura sugerida:**
```
v1/src/test/java/Veloce_Rent_a_Car/
├── repository/
│   ├── ClientRepositoryTest.java (@DataJpaTest)
│   ├── CarroRepositoryTest.java
│   ├── RentRepositoryTest.java
│   └── PaymentRepositoryTest.java
├── service/
│   ├── ClientServiceTest.java (@ExtendWith(MockitoExtension))
│   ├── CarroServiceTest.java
│   ├── RentServiceTest.java
│   └── PaymentServiceTest.java
└── controller/
    ├── ClientControllerTest.java (@SpringBootTest + @AutoConfigureMockMvc)
    ├── CarroControllerTest.java
    ├── RentControllerTest.java
    └── PaymentControllerTest.java
```

**Cobertura alvo:** >70% nos módulos core (Rent, Payment, Carro, Client).

**JaCoCo no pom.xml:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

---

## 7. UI Java (Thymeleaf + HTMX) — **MÉDIO**

**Dependências a adicionar no pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

**Estrutura templates:**
```
src/main/resources/templates/
├── layout.html (fragment header/footer)
├── index.html (dashboard)
├── clients/
│   ├── list.html
│   ├── form.html (create/edit)
│   └── fragments.html
├── carros/
│   ├── list.html
│   ├── form.html
│   └── fragments.html
├── rentals/
│   ├── list.html
│   ├── form.html
│   └── fragments.html
└── payments/
    ├── list.html
    └── form.html
```

**Controller MVC (novo pacote `web`):**
- `ClientWebController` — GET `/ui/clients`, POST `/ui/clients`, etc.
- Retorna `String` (template name), popula `Model`

**HTMX:** Incluir via CDN no `layout.html`:
```html
<script src="https://unpkg.com/htmx.org@1.9.10"></script>
```
Usar `hx-get`, `hx-post`, `hx-target`, `hx-swap` para interações sem reload.

---

## 8-9. Docs — **BAIXO**
- Atualizar `API.md`: Carro → ✅ Completo
- `OpenApiConfig`: remover `@SecurityScheme` comentado

---

## 10-14. CI/CD — **MELHORIAS**

**`.github/workflows/ci.yml` atualizado:**

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 2 * * 1'  # weekly dependency check

jobs:
  build:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:17
        env:
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: carrental
        ports: [ 5432:5432 ]
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
          cache-key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          cache-restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build & Test with Maven
        run: |
          cd v1
          chmod +x mvnw
          ./mvnw clean verify -DskipTests=false
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/carrental
          SPRING_DATASOURCE_USERNAME: postgres
          SPRING_DATASOURCE_PASSWORD: postgres

      - name: Upload JaCoCo Coverage Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: v1/target/site/jacoco/
          retention-days: 7

      - name: Comment Coverage on PR
        if: github.event_name == 'pull_request'
        uses: madrapps/jacoco-report@v1.6
        with:
          paths: v1/target/site/jacoco/jacoco.xml
          token: ${{ secrets.GITHUB_TOKEN }}
          title: "📊 Code Coverage Report"
          min-coverage-overall: 70
          min-coverage-changed-files: 50

  dependency-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run OWASP Dependency Check
        run: |
          cd v1
          chmod +x mvnw
          ./mvnw org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7
        continue-on-error: true  # não bloquear PR, só alertar

  docker:
    needs: build
    if: github.event_name == 'push' && startsWith(github.ref, 'refs/tags/')
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      - name: Login to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: ./v1
          push: true
          tags: ghcr.io/${{ github.repository }}/veloce-rent-a-car:${{ github.ref_name }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

**Dockerfile (novo em `v1/Dockerfile`):**
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Execução Sugerida

| Sprint | Itens | Entregável |
|--------|-------|------------|
| 1 | 1, 3, 4 | Aluguel funcional + Carro seguro + DB init correto |
| 2 | 2, 5 | Auth JWT + Error handling padronizado |
| 3 | 6, 7 | Testes >70% + UI Thymeleaf/HTMX básica |
| 4 | 8, 9, 10-14 | Docs + CI/CD robusto + Docker |

---

**Próximo passo:** Quer que eu implemente o **item 1** (Fix RentService.create()) agora? É o ponto de partida que desbloqueia o fluxo principal.