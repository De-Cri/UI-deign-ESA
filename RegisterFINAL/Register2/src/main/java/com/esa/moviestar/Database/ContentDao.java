package com.esa.moviestar.Database;

import com.esa.moviestar.model.Content;
import com.esa.moviestar.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

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


    public List<List<Content>> getHomePageContents(Utente user) {
        List<Integer> gusti = user.getGustiComeLista();
        String top_genres = IntStream.range(0, gusti.size()).boxed().sorted(Comparator.comparing(gusti::get)).map(Object::toString).limit(3).collect(java.util.stream.Collectors.joining(","));
        List<Integer>list=  IntStream.range(0, gusti.size()).boxed().sorted(Comparator.comparing(gusti::get).reversed()).limit(3).toList();
        String bottom_genres =  list.stream().map(Object::toString).collect(java.util.stream.Collectors.joining(","));
        String query =
                // Popular content from top genres
                "SELECT * FROM(SELECT C.*, 0 AS Ordinamento " +
                        "FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                        "WHERE CG.ID_Genere IN (" + top_genres + ") " +
                        "ORDER BY C.Click DESC LIMIT 5)" +

                        " UNION ALL " +
                        // Random top genres content
                        "SELECT * FROM(SELECT C.*, 1 AS Ordinamento FROM Contenuto C " +
                        "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                        "WHERE CG.ID_Genere IN (" + top_genres + ") " +
                        "ORDER BY RANDOM() LIMIT 10)" +

                        " UNION ALL " +
                        // New releases
                        "SELECT * FROM(SELECT C.*, 2 AS Ordinamento FROM Contenuto C ORDER BY Data_di_pubblicazione DESC LIMIT 8)" +

                        " UNION ALL " +
                        // Favorite tag but not watched
                        "SELECT * FROM(SELECT C.*, 3 AS Ordinamento FROM Contenuto C " +
                        "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                        "LEFT JOIN Cronologia CR ON C.ID_Contenuto = CR.ID_Contenuto AND CR.ID_Utente = " + user.getID() + " " +
                        "WHERE CG.ID_Genere = ( " +
                        "SELECT ID_Genere FROM Contenuti_Generi WHERE ID_Contenuto IN ( " +
                        "SELECT ID_Contenuto FROM Contenuti_Generi WHERE ID_Genere = " + list.getFirst() + " LIMIT 1 ) LIMIT 1" +
                        ") AND CR.ID_Contenuto IS NULL AND C.N_Episodi = 0 ORDER BY RANDOM() LIMIT 8)" +

                        " UNION ALL " +
                        // Similar to last watched
                        "SELECT * FROM(SELECT C2.*, 4 AS Ordinamento FROM Contenuti_Generi CG1 " +
                        "JOIN Contenuti_Generi CG2 ON CG1.ID_Genere = CG2.ID_Genere AND CG1.ID_Contenuto != CG2.ID_Contenuto " +
                        "JOIN Contenuto C2 ON CG2.ID_Contenuto = C2.ID_Contenuto " +
                        "WHERE CG1.ID_Contenuto = ( SELECT CR.ID_Contenuto FROM Cronologia CR WHERE CR.ID_Utente = " + user.getID() + " " +
                        "ORDER BY CR.DataVisione DESC LIMIT 1 ) ORDER BY RANDOM() LIMIT 7)" +
                        " UNION ALL " +

                        // Recently watched
                        "SELECT * FROM(SELECT C.*, 5 AS Ordinamento FROM Cronologia CR JOIN Contenuto C ON CR.ID_Contenuto = C.ID_Contenuto " +
                        "WHERE CR.ID_Utente = " + user.getID() + " ORDER BY CR.DataVisione DESC LIMIT 7)" +

                        " UNION ALL " +

                        // Recommended series
                        "SELECT * FROM(SELECT C.*, 6 AS Ordinamento FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                        "WHERE CG.ID_Genere IN (" + top_genres + ") AND C.N_Episodi > 0 ORDER BY RANDOM() LIMIT 7)" +

                        " UNION ALL " +
                        // User favorites
                        "SELECT * FROM(SELECT C.*, 7 AS Ordinamento FROM Preferiti P JOIN Contenuto C ON P.ID_Contenuto = C.ID_Contenuto " +
                        "WHERE P.ID_Utente = " + user.getID() + " ORDER BY RANDOM() LIMIT 7)" +

                        " UNION ALL " +
                        // Other categories (bottom genres)
                        "SELECT * FROM(SELECT C.*, 8 AS Ordinamento FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                        "WHERE CG.ID_Genere IN (" + bottom_genres + ") ORDER BY RANDOM() LIMIT 7)";

        return getPagesContents(user,query);
    }

    public List<List<Content>> getFilterPageContents(Utente user, boolean isFilm) {
        List<Integer> gusti = user.getGustiComeLista();
        String top_genres = IntStream.range(0, gusti.size()).boxed().sorted(Comparator.comparing(gusti::get)).map(Object::toString).limit(3).collect(java.util.stream.Collectors.joining(","));
        String query;
        if (isFilm) {
            // Popular content from top genres
            query = "SELECT * FROM ( SELECT C.*, 0 AS Ordinamento  FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto  " +
                    "WHERE CG.ID_Genere IN ("+top_genres+") AND C.Stagioni = 0 AND C.N_Episodi = 0 ORDER BY Data_di_pubblicazione DESC LIMIT 10 ) " +
                    "UNION ALL  " +
                    "SELECT * FROM ( SELECT C.*, 1 AS Ordinamento  FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto  " +
                    "WHERE CG.ID_Genere IN ("+top_genres+")  AND C.Stagioni = 0 AND C.N_Episodi = 0 ORDER BY RANDOM() LIMIT 7 ) " +
                    "UNION ALL " +
                    "SELECT * FROM ( SELECT C.*, 2 AS Ordinamento FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto  " +
                    "    LEFT JOIN Cronologia CR ON C.ID_Contenuto = CR.ID_Contenuto AND CR.ID_Utente = "+ user.getID() +" WHERE CG.ID_Genere = ( SELECT ID_Genere  " +
                    "        FROM Contenuti_Generi  " +
                    "        WHERE ID_Contenuto IN ( SELECT ID_Contenuto FROM Contenuti_Generi " +
                    "            WHERE ID_Genere IN ("+ top_genres +
                    ") LIMIT 1 ) LIMIT 1 ) AND CR.ID_Contenuto IS NULL  AND C.Stagioni = 0 AND C.N_Episodi = 0 ORDER BY RANDOM() LIMIT 8 ) " +
                    "UNION ALL  " +
                    "SELECT * FROM ( " +
                    "    SELECT C.*, 3 AS Ordinamento FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto  " +
                    "    WHERE C.Stagioni = 0 AND C.N_Episodi = 0 ORDER BY RANDOM() LIMIT 8 ) " +
                    " UNION ALL " +
                    "SELECT * FROM ( SELECT C.*, 4 AS Ordinamento " +
                    "    FROM Contenuto C JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "    WHERE C.Stagioni = 0 AND C.N_Episodi = 0 ORDER BY RANDOM() LIMIT 8" +
                    ");";
        } else {
             query = "SELECT * FROM (" +
                    "SELECT C.*, 0 AS Ordinamento " +
                    "FROM Contenuto C " +
                    "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "WHERE CG.ID_Genere IN (" + top_genres + ") " +
                    "AND (C.Stagioni > 0 OR C.N_Episodi > 0) " +
                    "ORDER BY Data_di_pubblicazione DESC " +
                    "LIMIT 10 " +
                    ") " +
                    "UNION ALL " +
                    "SELECT * FROM (" +
                    "SELECT C.*, 1 AS Ordinamento " +
                    "FROM Contenuto C " +
                    "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "WHERE CG.ID_Genere IN (" + top_genres + ") " +
                    "AND (C.Stagioni > 0 OR C.N_Episodi > 0) " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 7 " +
                    ") " +
                    "UNION ALL " +
                    "SELECT * FROM (" +
                    "SELECT C.*, 2 AS Ordinamento " +
                    "FROM Contenuto C " +
                    "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "LEFT JOIN Cronologia CR ON C.ID_Contenuto = CR.ID_Contenuto AND CR.ID_Utente = " +user.getID()+
                    " WHERE CG.ID_Genere = (" +
                    "SELECT ID_Genere " +
                    "FROM Contenuti_Generi " +
                    "WHERE ID_Contenuto IN (" +
                    "SELECT ID_Contenuto " +
                    "FROM Contenuti_Generi " +
                    "WHERE ID_Genere IN (" + top_genres + ") " +
                    "LIMIT 1 " +
                    ") " +
                    "LIMIT 1 " +
                    ") " +
                    "AND CR.ID_Contenuto IS NULL " +
                    "AND (C.Stagioni > 0 OR C.N_Episodi > 0) " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 8 " +
                    ") " +
                    "UNION ALL " +
                    "SELECT * FROM (" +
                    "SELECT C.*, 4 AS Ordinamento " +
                    "FROM Contenuto C " +
                    "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "WHERE C.Stagioni > 0 OR C.N_Episodi > 0 " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 8 " +
                    ") " +
                    "UNION ALL " +
                    "SELECT * FROM (" +
                    "SELECT C.*, 4 AS Ordinamento " +
                    "FROM Contenuto C " +
                    "JOIN Contenuti_Generi CG ON C.ID_Contenuto = CG.ID_Contenuto " +
                    "WHERE C.Stagioni > 0 OR C.N_Episodi > 0 " +
                    "ORDER BY RANDOM() " +
                    "LIMIT 10 " +
                    ");";
        }

        return getPagesContents(user, query);
    }

    private List<List<Content>> getPagesContents(Utente user, String query) {
        List<List<Content>> list = new Vector<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                while(rs.getInt("Ordinamento")>=list.size()){
                    list.add(new Vector<>());
                }
                list.get(rs.getInt("Ordinamento")).add(createContent(rs));
            }
        } catch (Exception e) {
            System.err.println("ContentDao: Error to create content list in take_home_contents \n Error:" + e.getMessage());
        }
        return list;
    }

    public  Content getFiLmDetails(int id) {
        String query = "SELECT C.* FROM Contenuto C WHERE ID_Contenuto = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            Content content = createContent(rs);
            while(rs.next()){
                content.addCategory(rs.getInt("ID_Genere"));
            }
            return content;
        } catch (SQLException e) {
            System.err.println("ContentDao: failed to load content: "+id + "\n Error:"+e.getMessage());
        }
        return  null;
    }
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

    public List<Content> getWatched(int idUser,int limit) {
        return getList(idUser,idUser,"SELECT C.* FROM Contenuto C JOIN Cronologia Cr ON C.ID_Contenuto = Cr.ID_Contenuto WHERE Cr.ID_Utente = ? LIMIT ?;");
    }

    public List<Content> getFavourites(int idUser,int limit) {
        return getList(idUser,idUser,"SELECT C.* FROM Contenuto C JOIN Preferiti Pr ON C.ID_Contenuto = Pr.ID_Contenuto WHERE Pr.ID_Utente = ? LIMIT ?;");
    }

    private List<Content> getList(int idUser,int limit,String query){
        List<Content> resultContents = new ArrayList<>();
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setInt(1, idUser);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultContents.add(createContent(rs));
            }
        }catch(SQLException e){
            System.err.println("ContentDao: error to take execute query: "+query+ "\n Error:"+e.getMessage());
        }
        return resultContents;
    }

    private Content createContent(ResultSet rs) throws SQLException {
        Content content = new Content();
        content.setId(rs.getInt("ID_Contenuto"));
        content.setTitle(rs.getString("Titolo"));
        content.setPlot(rs.getString("Trama"));
        content.setImageUrl(rs.getString("Link_immagine"));
        content.setVideoUrl(rs.getString("Link_film"));
        content.setDuration(rs.getDouble("Durata"));
        content.setYear(rs.getInt("Anno"));
        content.setRating(rs.getDouble("Valutazione"));
        content.setClicks(rs.getInt("Click"));
        content.setCountry(rs.getString("Nazione"));
        content.setReleaseDate(rs.getString("Data_di_pubblicazione"));
        content.seasonDivided(rs.getInt("Stagioni") > 0);
        content.setSeasonCount(rs.getInt("Stagioni"));
        content.Series(rs.getInt("N_Episodi")>0);
        content.setEpisodeCount(rs.getInt("N_Episodi"));
        return content;
    }


}
