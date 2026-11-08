# TODO — Veloce Rent-a-Car

## 🔴 Crítico (bloqueia core)
- [ ] **1. Fix RentService.create()** — Vincular Carro ao aluguel, validar disponibilidade, atualizar status Carro → ALUGADO
- [ ] **2. Implement SecurityConfig (JWT)** — SecurityFilterChain, JwtFilter, TokenProvider, proteger endpoints, remover senha aleatória

## 🟠 Alto (bugs latentes / setup)
- [ ] **3. Fix CarroService.atualizar()** — Impedir alteração de `placa` (unique constraint)
- [ ] **4. Fix DatabaseInitializer** — PostgreSQL: `DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname='carrental') THEN CREATE DATABASE carrental; END IF; END $$;`

## 🟡 Médio (qualidade / DX)
- [ ] **5. GlobalExceptionHandler** — @ControllerAdvice, ErrorResponse padronizado, limpar try/catch dos controllers
- [ ] **6. Testes de integração/unidade** — @DataJpaTest (repos), @SpringBootTest+MockMvc (controllers), Mockito (services) — meta >70% core

## 🟢 Baixo (docs / polimento)
- [ ] **7. Atualizar API.md** — Módulo Carro: ⚙️ Parcial → ✅ Completo
- [ ] **8. OpenApiConfig** — Remover SecurityScheme JWT comentado até implementar auth real