package com.esa.moviestar.Database;

import com.esa.moviestar.model.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDao {
    private Connection connection;

    public UtenteDao() {
        try {
            this.connection = DataBaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inserimento di un nuovo utente
    public void inserisciUtente(Utente utente) throws SQLException {
        String query = "INSERT INTO Utente (Nome, Gusti, Email, ImmagineProfilo) VALUES (?, ?, ?, ?);";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getGusti());
            stmt.setString(3, utente.getEmail());
            stmt.setInt(4, utente.getImmagineProfilo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Errore nell'inserimento dell'utente", e);
        }
    }

    // Rimozione utente tramite codice
    public void rimuoviUtente(int codUtente) throws SQLException {
        String sql = "DELETE FROM Utente WHERE CodUtente = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, codUtente);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nessun utente trovato con codice utente = " + codUtente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Errore nell'eliminazione dell'utente", e);
        }
    }

    // Ricerca utente tramite codice
    public Utente cercaUtente(int codUtente) throws SQLException {
        String query = "SELECT * FROM Utente WHERE CodUtente = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utente(
                        rs.getString("Nome"),
                        rs.getInt("ImmagineProfilo"),
                        rs.getString("Gusti"),
                        rs.getString("Email")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Errore nel cercare l'utente", e);
        }
    }

    public int contaProfiliPerEmail(String email) throws  SQLException{
        String query = "SELECT COUNT(*) FROM Utente WHERE Email = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Utente recuperoUtente(String email,int codUtente) throws SQLException {
        String query = "SELECT * FROM Utente WHERE Email = ? AND CodUtente = ?;";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, email);
            stmt.setInt(2,codUtente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utente(
                        rs.getString("Nome"),
                        rs.getInt("ImmagineProfilo"),
                        rs.getString("Gusti"),
                        rs.getString("Email")
                );
            }else {return null;}

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public int recuperoCodiceUtente(String email) throws SQLException {
        String query = "SELECT CodUtente FROM Utente WHERE Email = ?;";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("CodUtente");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Metodo che recupera tutti gli utenti
    public List<Utente> recuperaTuttiGliUtenti() throws SQLException {
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT * FROM Utente;";  // Recupera tutti gli utenti

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Crea un oggetto Utente per ogni record nel risultato della query
                Utente utente = new Utente(
                        rs.getString("Nome"),
                        rs.getInt("ImmagineProfilo"),
                        rs.getString("Gusti"),
                        rs.getString("Email")
                );
                utenti.add(utente);  // Aggiungi l'utente alla lista
            }
            System.out.println("Numero di utenti recuperati: " + utenti.size());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Errore nel recupero degli utenti", e);
        }
        return utenti;  // Restituisci la lista di utenti
    }
}
