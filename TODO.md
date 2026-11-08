# TODO — Veloce Rent-a-Car

## 🔴 Crítico (bloqueia core)
- [x] **1. Fix RentService.create()** — Vincular Carro ao aluguel, validar disponibilidade, atualizar status Carro → ALUGADO
- [ ] **2. Implement SecurityConfig (JWT)** — SecurityFilterChain, JwtFilter, TokenProvider, proteger endpoints, remover senha aleatória

## 🟠 Alto (bugs latentes / setup)
- [x] **3. Fix CarroService.atualizar()** — Impedir alteração de `placa` (unique constraint)
- [x] **4. Fix DatabaseInitializer** — PostgreSQL: `DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname='carrental') THEN CREATE DATABASE carrental; END IF; END $$;`

## 🟡 Médio (qualidade / DX)
- [x] **5. GlobalExceptionHandler** — @ControllerAdvice, ErrorResponse padronizado, limpar try/catch dos controllers
- [ ] **6. Testes de integração/unidade** — @DataJpaTest (repos), @SpringBootTest+MockMvc (controllers), Mockito (services) — meta >70% core
- [ ] **7. UI Java (Thymeleaf + HTMX)** — Server-side rendering: templates CRUD Client/Carro/Aluguel/Pagamento, fragments layout, HTMX para interações sem full reload, servidos por Spring MVC

## 🟢 Baixo (docs / polimento)
- [x] **8. Atualizar API.md** — Módulo Carro: ⚙️ Parcial → ✅ Completo
- [x] **9. OpenApiConfig** — Remover SecurityScheme JWT comentado até implementar auth real

## 🔧 CI/CD (melhorias pipeline)
- [x] **10. Cache Maven wrapper** — Já usa `cache: maven` mas adicionar `key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}` e `restore-keys` para cache cruzado
- [x] **11. TestReport / Coverage** — Adicionar `./mvnw jacoco:report` + upload artifact + comment PR com coverage (codecov/gh-action)
- [ ] **12. Lint/Checkstyle** — Adicionar `checkstyle:check` + `spotbugs:check` no verify
- [x] **13. Dependency check** — OWASP dependency-check-maven no schedule (weekly) ou PR
- [x] **14. Build Docker image** — Multi-stage Dockerfile + push para GHCR em tag/release