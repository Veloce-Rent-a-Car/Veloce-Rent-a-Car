package Veloce_Rent_a_Car.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Inicializador executado fora do perfil de teste para verificar e, se necessário,
 * criar o banco PostgreSQL {@code carrental} durante a inicialização da aplicação.
 */
@Component
@Profile("!test")
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    /**
     * Cria o inicializador com a fonte de conexões usada para acessar o PostgreSQL.
     *
     * @param dataSource fonte de conexões configurada para a aplicação
     */
    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executa um bloco PostgreSQL que cria o banco {@code carrental} somente quando
     * ele ainda não existe. Falhas de conexão ou de SQL são registradas no erro padrão
     * e não são propagadas por esta implementação.
     *
     * @param args argumentos recebidos na inicialização, não utilizados neste método
     */
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
}