package com.evmarket.trade;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootTest
@ActiveProfiles("test")
public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=EV_Battery_Trading;encrypt=false;trustServerCertificate=true";
        String username = "sa";
        String password = "12345";
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ Database connection successful!");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Version: " + connection.getMetaData().getDriverVersion());
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection test failed", e);
        }
    }
}


