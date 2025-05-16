package com.esa.moviestar.Database;

import com.esa.moviestar.model.Account;
import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDao {
    private static Connection connection;


    public ContentDao(){
        try{
            connection = DataBaseManager.getConnection();
        }
        catch (SQLException e) {
            System.err.println("Errore di connessione al database: " + e.getMessage());
        }
    }

    public List<Content> take_all_contents() {
        List<Content> listaContenuti = new ArrayList<>();
        Map<Integer, List<Integer>> tagsMap = new HashMap<>();

        // Prima query per ottenere tutti i generi associati ai contenuti
        String queryGeneri = "SELECT ID_Contenuto, ID_Genere FROM Contenuti_Generi;";
        try (PreparedStatement stmtGeneri = connection.prepareStatement(queryGeneri)) {
            ResultSet rsGeneri = stmtGeneri.executeQuery();

            while (rsGeneri.next()) {
                int idContenuto = rsGeneri.getInt("ID_Contenuto");
                int idGenere = rsGeneri.getInt("ID_Genere");

                if (!tagsMap.containsKey(idContenuto)) {
                    tagsMap.put(idContenuto, new ArrayList<>());
                }
                tagsMap.get(idContenuto).add(idGenere);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei generi: " + e.getMessage(), e);
        }

        String queryContenuti = "SELECT c.ID_Contenuto, c.Titolo, c.Trama, c.Link_immagine, c.Link_film, " +
                "c.Durata, c.Anno, c.Valutazione, c.Click, c.Nazione, c.Data_di_pubblicazione, c.Stagioni, c.N_Episodi " +
                "FROM Contenuto c";

        try (PreparedStatement stmt = connection.prepareStatement(queryContenuti)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Content contenuto = new Content();

                // Popola l'oggetto Content con tutti i campi dal ResultSet
                contenuto.setId(rs.getInt("ID_Contenuto"));
                contenuto.setTitle(rs.getString("Titolo"));
                contenuto.setPlot(rs.getString("Trama"));
                contenuto.setImageUrl(rs.getString("Link_immagine"));
                contenuto.setVideoUrl(rs.getString("Link_film"));
                contenuto.setDuration(rs.getDouble("Durata"));
                contenuto.setYear(rs.getInt("Anno"));
                contenuto.setRating(rs.getDouble("Valutazione"));
                contenuto.setClicks(rs.getInt("Click"));
                contenuto.setCountry(rs.getString("Nazione"));
                contenuto.setReleaseDate(rs.getString("Data_di_pubblicazione"));
                contenuto.seasonDivided(rs.getInt("Stagioni") > 0);
                contenuto.setSeasonCount(rs.getInt("Stagioni"));
                contenuto.Series(rs.getInt("N_Episodi")>0);
                contenuto.setEpisodeCount(rs.getInt("N_Episodi"));
                contenuto.setCategories(tagsMap.get(rs.getInt("ID_Contenuto")));
                // Aggiungi l'oggetto completo alla lista
                listaContenuti.add(contenuto);
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return listaContenuti;
    }

//    public  Content take_film(int id) throws SQLException {
//        String query = "SELECT * FROM account WHERE id = ?;";
//
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, email);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return new Account(
//                        rs.getString("email"),
//                        rs.getString("password")
//                );
//            } else {
//                return null;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new SQLException("Errore nel cercare l'utente", e);
//        }
//    }
    public List<Content> take_film_tvseries(String title, Utente u) {
        String gustiU = u.getGusti();
        List<Integer> weights = new ArrayList<>();
        Map<Integer, Integer> genreWeights = new HashMap<>();

        // Estrai i pesi dai gusti dell'utente
        for (int i = 0; i < gustiU.length(); i += 2) {
            if (i + 2 <= gustiU.length()) {
                try {
                    // Assumo che ogni genere abbia un ID corrispondente alla posizione / 2
                    int genreId = i / 2 + 1; // Generi numerati da 1
                    int weight = Integer.parseInt(gustiU.substring(i, i + 2), 16);
                    genreWeights.put(genreId, weight);
                    weights.add(weight);
                } catch (NumberFormatException e) {
                    // Skip invalid hex values
                }
            }
        }

        // Trova i 5 generi con i pesi più alti
        List<Map.Entry<Integer, Integer>> sortedGenres = new ArrayList<>(genreWeights.entrySet());
        sortedGenres.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // Ordina per peso decrescente

        List<Integer> topGenreIds = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedGenres.size()); i++) {
            topGenreIds.add(sortedGenres.get(i).getKey());
        }

        // Se non ci sono generi, ritorna una lista vuota
        if (topGenreIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Prepara la lista finale dei contenuti
        List<Content> resultContents = new ArrayList<>();
        Map<Integer, List<Integer>> tagsMap = new HashMap<>();

        try {
            // Prepara la mappa di generi per ciascun contenuto
            String queryGeneri = "SELECT ID_Contenuto, ID_Genere FROM Contenuti_Generi;";
            try (PreparedStatement stmtGeneri = connection.prepareStatement(queryGeneri)) {
                ResultSet rsGeneri = stmtGeneri.executeQuery();

                while (rsGeneri.next()) {
                    int idContenuto = rsGeneri.getInt("ID_Contenuto");
                    int idGenere = rsGeneri.getInt("ID_Genere");

                    if (!tagsMap.containsKey(idContenuto)) {
                        tagsMap.put(idContenuto, new ArrayList<>());
                    }
                    tagsMap.get(idContenuto).add(idGenere);
                }
            }

            // Per ciascuno dei top 5 generi, ottieni i contenuti
            for (Integer genreId : topGenreIds) {
                String queryContenuti =
                        "SELECT c.ID_Contenuto, c.Titolo, c.Trama, c.Link_immagine, c.Link_film, " +
                                "c.Durata, c.Anno, c.Valutazione, c.Click, c.Nazione, c.Data_di_pubblicazione, c.Stagioni, c.N_Episodi " +
                                "FROM Contenuto c " +
                                "JOIN Contenuti_Generi cg ON c.ID_Contenuto = cg.ID_Contenuto " +
                                "WHERE c.Titolo LIKE ? AND cg.ID_Genere = ? " +
                                "ORDER BY c.Valutazione DESC";

                try (PreparedStatement stmt = connection.prepareStatement(queryContenuti)) {
                    stmt.setString(1, "%" + title + "%");
                    stmt.setInt(2, genreId);
                    ResultSet rs = stmt.executeQuery();

                    // Processa i risultati
                    while (rs.next()) {
                        // Evita duplicati
                        int contentId = rs.getInt("ID_Contenuto");
                        boolean isDuplicate = resultContents.stream().anyMatch(c -> c.getId() == contentId);
                        if (isDuplicate) continue;

                        Content contenuto = new Content();
                        contenuto.setId(contentId);
                        contenuto.setTitle(rs.getString("Titolo"));
                        contenuto.setPlot(rs.getString("Trama"));
                        contenuto.setImageUrl(rs.getString("Link_immagine"));
                        contenuto.setVideoUrl(rs.getString("Link_film"));
                        contenuto.setDuration(rs.getDouble("Durata"));
                        contenuto.setYear(rs.getInt("Anno"));
                        contenuto.setRating(rs.getDouble("Valutazione"));
                        contenuto.setClicks(rs.getInt("Click"));
                        contenuto.setCountry(rs.getString("Nazione"));
                        contenuto.setReleaseDate(rs.getString("Data_di_pubblicazione"));
                        contenuto.seasonDivided(rs.getInt("Stagioni") > 0);
                        contenuto.setSeasonCount(rs.getInt("Stagioni"));
                        contenuto.Series(rs.getInt("N_Episodi") > 0);
                        contenuto.setEpisodeCount(rs.getInt("N_Episodi"));

                        contenuto.setCategories(tagsMap.getOrDefault(contentId, new ArrayList<>()));

                        resultContents.add(contenuto);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei contenuti: " + e.getMessage(), e);
        }

        return resultContents;
    }

}
