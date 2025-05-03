package com.esa.moviestar.Database;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

    private static final String DB_NAME = "DatabaseProjectUID.db";  //nome del file che contiente il database

    //Metodo vero e proprio di connessione
    public static Connection getConnection() throws SQLException {
        try {
            URL dbUrl = DataBaseManager.class.getResource("/" + DB_NAME);  //cerca il file nel classpath grazie a getResource , poi usiamo /+DB_NAME perche nella parte prima dello / si trova tutto il path e poi nella parte dopo il nome del file del database
            if (dbUrl == null) { //se il percorso è nullo
                throw new RuntimeException("Database non trovato nel classpath."); //manda questo errore
            }

            String jdbcUrl = "jdbc:sqlite:" + Paths.get(dbUrl.toURI()).toString();

            /*Converte l'URL del database in un percorso di file che può essere usato da SQLite.

            (dbUrl.toURI()) converte l'URL in un oggetto URI, che è una rappresentazione generica di un percorso.

            (Paths.get(...)) converte l'oggetto URI in un oggetto Path che rappresenta un file nel filesystem.

            (toString()) converte il Path in una stringa che può essere usata dal driver JDBC.

            Il risultato finale è una stringa che rappresenta il percorso assoluto del file del database, */

            return DriverManager.getConnection(jdbcUrl); //la vera e propria connessione avviene qui

        } catch (URISyntaxException e) { //errore nella conversione del percorso file da url a uri
            throw new RuntimeException("Errore nella conversione del percorso DB.", e);
        }
    }

    // Metodo per controllare che il database sia effettivamente connesso con una stampa su terminale
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connesso al database con successo!");
            }
        } catch (SQLException e) {
            System.err.println("Connessione fallita: " + e.getMessage());
        }
    }
}
