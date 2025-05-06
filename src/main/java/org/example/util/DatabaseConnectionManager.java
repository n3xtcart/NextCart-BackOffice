package org.example.util;

import org.example.exception.EccezioneAccessoDati;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionManager {
    private static final String DB_URL = "jdbc:h2:mem:gestionale_db;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static volatile DatabaseConnectionManager instance;

    private DatabaseConnectionManager() {
        try {
            // Load the H2 driver
            Class.forName("org.h2.Driver");
            // Initialize schema on first connection
            initializeSchema();
        } catch (ClassNotFoundException e) {
            throw new EccezioneAccessoDati("Driver H2 non trovato", e);
        }
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void initializeSchema() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Categorie Table
            stmt.execute("CREATE TABLE IF NOT EXISTS categorie (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(255) NOT NULL UNIQUE, " +
                    "percorso_immagine VARCHAR(255)" +
                    ")");

            // Create Prodotti Table
            stmt.execute("CREATE TABLE IF NOT EXISTS prodotti (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(255) NOT NULL, " +
                    "descrizione TEXT, " +
                    "quantita INT, " +
                    "percorso_immagine VARCHAR(255), " +
                    "id_categoria BIGINT, " +
                    "tipologia VARCHAR(100), " + // NUOVO CAMPO
                    "FOREIGN KEY (id_categoria) REFERENCES categorie(id) ON DELETE SET NULL" +
                    ")");

            // Create Utenti Table
            stmt.execute("CREATE TABLE IF NOT EXISTS utenti (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(255) NOT NULL UNIQUE, " +
                    "hash_password VARCHAR(255) NOT NULL, " +
                    "ruolo VARCHAR(50)" +
                    ")");

            System.out.println("Database schema initialized successfully or already exists.");

        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante l'inizializzazione dello schema del database", e);
        }
    }
}