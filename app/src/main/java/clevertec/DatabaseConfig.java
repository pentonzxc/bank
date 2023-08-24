package clevertec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    public static Connection getConnecion() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(
                            "jdbc:postgresql://localhost:5432/postgres",
                            "postgres",
                            "postgres");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
