package Veloce_Rent_a_Car.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
