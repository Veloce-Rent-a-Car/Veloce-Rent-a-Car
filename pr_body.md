## Resumo
Adiciona `TODO.md` com lista de tarefas priorizada (crítico → baixo), `PLANO_ACAO.md` com plano detalhado para corrigir os pontos críticos, e melhora o CI/CD.

## Mudanças
### Documentação
- **TODO.md** (atualizado): 14 tarefas organizadas por prioridade
  - 🔴 Crítico (2): Fix RentService.create(), Implement SecurityConfig JWT
  - 🟠 Alto (2): Fix CarroService.atualizar() placa unique, Fix DatabaseInitializer PostgreSQL
  - 🟡 Médio (3): GlobalExceptionHandler, Testes integração/unidade, **UI Java (Thymeleaf + HTMX)**
  - 🟢 Baixo (2): Atualizar API.md Carro status, Limpar OpenApiConfig JWT comentado
  - 🔧 CI/CD (5): Cache Maven otimizado, JaCoCo coverage + PR comment, Checkstyle/SpotBugs, OWASP Dependency Check (weekly), Docker multi-stage + GHCR push
- **PLANO_ACAO.md** (novo): Plano detalhado passo-a-passo para cada item crítico/alto/médio com arquivos a alterar, código de exemplo e ordem de execução sugerida

### CI/CD (`.github/workflows/ci.yml`)
- Cache Maven com key/restore-keys baseada no hash do pom.xml
- **JaCoCo coverage**: upload artifact + comment automático no PR com `madrapps/jacoco-report` (meta 70% overall, 50% changed files)
- **Dependency Check job**: OWASP dependency-check-maven rodando semanalmente (schedule) e em PRs (non-blocking)
- **Docker job**: multi-stage build + push para GHCR em tags (`refs/tags/*`)
- Schedule trigger para dependency check semanal

### Build (`v1/pom.xml`)
- Adicionado plugin `jacoco-maven-plugin:0.8.12` com `prepare-agent` + `report` no phase `verify`

## Origem
Priorização definida via processo de tribunal (acusação/defesa/júri/juiz) sobre o código atual.