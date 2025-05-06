package org.example.dao.impl;

import org.example.dao.CategoriaDao;
import org.example.entity.Categoria;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO per l'entità Categoria tramite JDBC.
 *
 * <p>
 * Questa classe utilizza il pattern Singleton per garantire una singola istanza
 * e fornisce metodi per eseguire operazioni di accesso al database come salvataggio,
 * modifica, eliminazione e ricerca di categorie.
 * </p>
 */
public class JdbcCategoriaDao implements CategoriaDao {

    private static volatile JdbcCategoriaDao instance;
    private final DatabaseConnectionManager connectionManager;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private JdbcCategoriaDao() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Ritorna l'istanza singleton di JdbcCategoriaDao.
     *
     * @return l'istanza di JdbcCategoriaDao
     */
    public static JdbcCategoriaDao getInstance() {
        if (instance == null) {
            synchronized (JdbcCategoriaDao.class) {
                if (instance == null) {
                    instance = new JdbcCategoriaDao();
                }
            }
        }
        return instance;
    }

    /**
     * Salva o aggiorna una categoria nel database.
     *
     * <p>
     * Se la categoria non ha un ID, viene eseguito un inserimento.
     * Se l'ID è presente, viene eseguito un aggiornamento. Viene inoltre verificata
     * l'unicità del nome della categoria.
     * </p>
     *
     * @param categoria la categoria da salvare o aggiornare
     * @return la categoria salvata o aggiornata
     * @throws IllegalArgumentException se la categoria è nulla
     * @throws EccezioneAccessoDati se si verificano errori nell'accesso al database
     */
    @Override
    public Categoria salva(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria non può essere nulla");
        }
        if (categoria.getId() == null) { // Nuova categoria
            if (trovaPerNome(categoria.getNome()).isPresent()) {
                throw new EccezioneAccessoDati("Categoria con nome '" + categoria.getNome() + "' già esistente.");
            }
        } else { // Categoria esistente
            Optional<Categoria> esistenteOpt = trovaPerId(categoria.getId());
            if (esistenteOpt.isPresent()) {
                Categoria esistente = esistenteOpt.get();
                if (!esistente.getNome().equalsIgnoreCase(categoria.getNome())) {
                    if (trovaPerNome(categoria.getNome()).filter(c -> !c.getId().equals(categoria.getId())).isPresent()) {
                        throw new EccezioneAccessoDati("Impossibile aggiornare il nome della categoria a '"
                                + categoria.getNome() + "', è già usato da un'altra categoria.");
                    }
                }
            } else {
                throw new EccezioneAccessoDati("Impossibile aggiornare categoria non esistente con id: " + categoria.getId());
            }
        }

        String sql;
        if (categoria.getId() == null) { // INSERT
            sql = "INSERT INTO categorie (nome, percorso_immagine) VALUES (?, ?)";
        } else { // UPDATE
            sql = "UPDATE categorie SET nome = ?, percorso_immagine = ? WHERE id = ?";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, categoria.getNome());
            pstmt.setString(2, categoria.getPercorsoImmagine());

            if (categoria.getId() != null) {
                pstmt.setLong(3, categoria.getId());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new EccezioneAccessoDati("Salvataggio categoria fallito, nessuna riga modificata.");
            }

            if (categoria.getId() == null) { // Se nuovo, ottiene l'ID generato
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getLong(1));
                    } else {
                        throw new EccezioneAccessoDati("Salvataggio categoria fallito, nessun ID generato.");
                    }
                }
            }
            return categoria;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new EccezioneAccessoDati("Categoria con nome '" + categoria.getNome()
                        + "' già esistente (violazione vincolo DB).", e);
            }
            throw new EccezioneAccessoDati("Errore durante il salvataggio della categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Cerca una categoria per ID.
     *
     * @param id l'ID della categoria da cercare
     * @return un Optional contenente la categoria se trovata, altrimenti Optional.empty()
     * @throws EccezioneAccessoDati se si verificano errori durante l'accesso al database
     */
    @Override
    public Optional<Categoria> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, nome, percorso_immagine FROM categorie WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCategoria(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca della categoria per ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Cerca una categoria per nome.
     *
     * @param nome il nome della categoria da cercare
     * @return un Optional contenente la categoria se trovata, altrimenti Optional.empty()
     * @throws EccezioneAccessoDati se si verificano errori durante l'accesso al database
     */
    @Override
    public Optional<Categoria> trovaPerNome(String nome) {
        if (nome == null) return Optional.empty();
        String sql = "SELECT id, nome, percorso_immagine FROM categorie WHERE LOWER(nome) = LOWER(?)";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCategoria(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca della categoria per nome: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Recupera tutte le categorie presenti nel database.
     *
     * @return una lista di tutte le categorie
     * @throws EccezioneAccessoDati se si verificano errori durante l'accesso al database
     */
    @Override
    public List<Categoria> trovaTutte() {
        List<Categoria> categorie = new ArrayList<>();
        String sql = "SELECT id, nome, percorso_immagine FROM categorie ORDER BY nome ASC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorie.add(mapRowToCategoria(rs));
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante il recupero di tutte le categorie: " + e.getMessage(), e);
        }
        return categorie;
    }

    /**
     * Modifica una categoria esistente.
     *
     * @param categoria la categoria da modificare (deve avere un ID non nullo)
     * @return la categoria modificata
     * @throws IllegalArgumentException se la categoria o il suo ID sono nulli
     * @throws EccezioneAccessoDati se la categoria non viene trovata o si verificano errori nel database
     */
    @Override
    public Categoria modifica(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Categoria o ID della categoria non possono essere nulli per la modifica.");
        }
        if (trovaPerId(categoria.getId()).isEmpty()) {
            throw new EccezioneAccessoDati("Categoria non trovata per ID: " + categoria.getId() + " per la modifica.");
        }
        Optional<Categoria> esistenteOpt = trovaPerId(categoria.getId());
        if (esistenteOpt.isPresent()) {
            Categoria esistente = esistenteOpt.get();
            if (!esistente.getNome().equalsIgnoreCase(categoria.getNome())) {
                if (trovaPerNome(categoria.getNome()).filter(c -> !c.getId().equals(categoria.getId())).isPresent()) {
                    throw new EccezioneAccessoDati("Impossibile modificare il nome della categoria a '"
                            + categoria.getNome() + "', è già usato da un'altra categoria.");
                }
            }
        }
        String sql = "UPDATE categorie SET nome = ?, percorso_immagine = ? WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoria.getNome());
            pstmt.setString(2, categoria.getPercorsoImmagine());
            pstmt.setLong(3, categoria.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new EccezioneAccessoDati("Modifica categoria fallita, nessuna riga modificata per ID: " + categoria.getId());
            }
            return categoria;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new EccezioneAccessoDati("Impossibile modificare il nome della categoria a '"
                        + categoria.getNome() + "', è già usato da un'altra categoria (violazione vincolo DB).", e);
            }
            throw new EccezioneAccessoDati("Errore durante la modifica della categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una categoria dal database in base al suo ID.
     *
     * @param id l'ID della categoria da eliminare
     * @throws IllegalArgumentException se l'ID è nullo o la categoria non esiste
     * @throws EccezioneAccessoDati se si verificano errori durante l'eliminazione
     */
    @Override
    public void elimina(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID categoria non può essere nullo per l'eliminazione.");
        }
        if (trovaPerId(id).isEmpty()) {
            throw new IllegalArgumentException("Categoria non trovata per ID: " + id + " per l'eliminazione.");
        }
        String sql = "DELETE FROM categorie WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Tentativo di eliminare categoria con ID " + id
                        + " non ha modificato righe, potrebbe essere già stata eliminata.");
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante l'eliminazione della categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Mappa una riga del ResultSet a un oggetto Categoria.
     *
     * @param rs il ResultSet contenente i dati della categoria
     * @return un oggetto Categoria popolato con i dati del ResultSet
     * @throws SQLException se si verifica un errore durante la lettura dei dati
     */
    private Categoria mapRowToCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("percorso_immagine")
        );
    }
}