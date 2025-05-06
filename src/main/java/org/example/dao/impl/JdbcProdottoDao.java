package org.example.dao.impl;

import org.example.dao.ProdottoDao;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione del DAO per l'entità Prodotto tramite JDBC.
 *
 * <p>
 * Questa classe utilizza il pattern Singleton ed esegue operazioni
 * di accesso al database (salvataggio, modifica, eliminazione e ricerca di prodotti).
 * </p>
 */
public class JdbcProdottoDao implements ProdottoDao {

    private static volatile JdbcProdottoDao instance;
    private final DatabaseConnectionManager connectionManager;
    private final JdbcCategoriaDao categoriaDao; // Per recuperare i dettagli della Categoria

    /**
     * Costruttore privato per implementare il pattern Singleton.
     */
    private JdbcProdottoDao() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
        this.categoriaDao = JdbcCategoriaDao.getInstance(); // Recupera l'istanza di CategoriaDao
    }

    /**
     * Ritorna l'istanza singleton di JdbcProdottoDao.
     *
     * @return l'istanza di JdbcProdottoDao
     */
    public static JdbcProdottoDao getInstance() {
        if (instance == null) {
            synchronized (JdbcProdottoDao.class) {
                if (instance == null) {
                    instance = new JdbcProdottoDao();
                }
            }
        }
        return instance;
    }

    /**
     * Salva o aggiorna un prodotto nel database.
     *
     * <p>
     * Se il prodotto non possiede un ID, viene eseguito un inserimento.
     * Altrimenti, viene eseguito un aggiornamento; la validità della categoria associata viene verificata.
     * </p>
     *
     * @param prodotto il prodotto da salvare o aggiornare
     * @return il prodotto salvato o aggiornato
     * @throws IllegalArgumentException se il prodotto è nullo o la categoria è invalida
     * @throws EccezioneAccessoDati se si verificano errori nell'accesso al database
     */
    @Override
    public Prodotto salva(Prodotto prodotto) {
        if (prodotto == null) {
            throw new IllegalArgumentException("Il prodotto non può essere nullo");
        }
        if (prodotto.getCategoria() != null && prodotto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("L'ID della categoria associata al prodotto non può essere nullo se la categoria è presente.");
        }

        String sql;
        if (prodotto.getId() == null) { // INSERT
            sql = "INSERT INTO prodotti (nome, descrizione, quantita, percorso_immagine, id_categoria, tipologia) VALUES (?, ?, ?, ?, ?, ?)";
        } else { // UPDATE
            if (trovaPerId(prodotto.getId()).isEmpty()) {
                throw new EccezioneAccessoDati("Impossibile aggiornare prodotto non esistente con id: " + prodotto.getId());
            }
            sql = "UPDATE prodotti SET nome = ?, descrizione = ?, quantita = ?, percorso_immagine = ?, id_categoria = ?, tipologia = ? WHERE id = ?";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, prodotto.getNome());
            pstmt.setString(2, prodotto.getDescrizione());
            pstmt.setInt(3, prodotto.getQuantita());
            pstmt.setString(4, prodotto.getPercorsoImmagine());
            if (prodotto.getCategoria() != null) {
                pstmt.setLong(5, prodotto.getCategoria().getId());
            } else {
                pstmt.setNull(5, Types.BIGINT);
            }
            pstmt.setString(6, prodotto.getTipologia());

            if (prodotto.getId() != null) { // UPDATE
                pstmt.setLong(7, prodotto.getId());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new EccezioneAccessoDati("Salvataggio prodotto fallito, nessuna riga modificata.");
            }

            if (prodotto.getId() == null) { // Se nuovo, ottiene l'ID generato
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        prodotto.setId(generatedKeys.getLong(1));
                    } else {
                        throw new EccezioneAccessoDati("Salvataggio prodotto fallito, nessun ID generato.");
                    }
                }
            }
            return prodotto;
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante il salvataggio del prodotto: " + e.getMessage(), e);
        }
    }

    /**
     * Cerca un prodotto per ID.
     *
     * @param id l'ID del prodotto da cercare
     * @return un Optional contenente il prodotto se trovato, altrimenti Optional.empty()
     * @throws EccezioneAccessoDati se si verificano errori nell'accesso al database
     */
    @Override
    public Optional<Prodotto> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT p.id, p.nome, p.descrizione, p.quantita, p.percorso_immagine, p.tipologia, p.id_categoria, " +
                "c.nome AS cat_nome, c.percorso_immagine AS cat_percorso_immagine " +
                "FROM prodotti p LEFT JOIN categorie c ON p.id_categoria = c.id WHERE p.id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProdotto(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca del prodotto per ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Recupera tutti i prodotti presenti nel database.
     *
     * @return una lista di prodotti
     * @throws EccezioneAccessoDati se si verificano errori nell'accesso al database
     */
    @Override
    public List<Prodotto> trovaTutti() {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT p.id, p.nome, p.descrizione, p.quantita, p.percorso_immagine, p.tipologia, p.id_categoria, " +
                "c.nome AS cat_nome, c.percorso_immagine AS cat_percorso_immagine " +
                "FROM prodotti p LEFT JOIN categorie c ON p.id_categoria = c.id ORDER BY p.nome ASC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                prodotti.add(mapRowToProdotto(rs));
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante il recupero di tutti i prodotti: " + e.getMessage(), e);
        }
        return prodotti;
    }

    /**
     * Cerca i prodotti per ID della categoria.
     *
     * @param idCategoria l'ID della categoria
     * @return una lista di prodotti appartenenti alla categoria specificata
     * @throws EccezioneAccessoDati se si verificano errori durante la ricerca
     */
    @Override
    public List<Prodotto> trovaPerIdCategoria(Long idCategoria) {
        if (idCategoria == null) return List.of();
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT p.id, p.nome, p.descrizione, p.quantita, p.percorso_immagine, p.tipologia, p.id_categoria, " +
                "c.nome AS cat_nome, c.percorso_immagine AS cat_percorso_immagine " +
                "FROM prodotti p INNER JOIN categorie c ON p.id_categoria = c.id WHERE p.id_categoria = ? ORDER BY p.nome ASC";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, idCategoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(mapRowToProdotto(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca dei prodotti per ID categoria: " + e.getMessage(), e);
        }
        return prodotti;
    }

    /**
     * Modifica un prodotto esistente.
     *
     * @param prodotto il prodotto da modificare (deve avere un ID non nullo)
     * @return il prodotto modificato
     * @throws IllegalArgumentException se il prodotto o il suo ID sono nulli
     * @throws EccezioneAccessoDati se il prodotto non viene trovato o si verifica un errore nel database
     */
    @Override
    public Prodotto modifica(Prodotto prodotto) {
        if (prodotto == null || prodotto.getId() == null) {
            throw new IllegalArgumentException("Il prodotto e il suo ID non possono essere nulli per la modifica.");
        }
        if (trovaPerId(prodotto.getId()).isEmpty()) {
            throw new EccezioneAccessoDati("Prodotto non trovato per ID: " + prodotto.getId() + " per la modifica.");
        }
        if (prodotto.getCategoria() != null && prodotto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("L'ID della categoria associata al prodotto non può essere nullo se la categoria è presente.");
        }
        return salva(prodotto);
    }

    /**
     * Elimina un prodotto dal database in base al suo ID.
     *
     * @param id l'ID del prodotto
     * @throws IllegalArgumentException se l'ID è nullo o il prodotto non esiste
     * @throws EccezioneAccessoDati se si verificano errori durante l'eliminazione
     */
    @Override
    public void elimina(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID del prodotto non può essere nullo per l'eliminazione.");
        }
        if (trovaPerId(id).isEmpty()) {
            throw new IllegalArgumentException("Prodotto non trovato per ID: " + id + " per l'eliminazione.");
        }
        String sql = "DELETE FROM prodotti WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Tentativo di eliminare il prodotto con ID " + id + " non ha modificato righe.");
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante l'eliminazione del prodotto: " + e.getMessage(), e);
        }
    }

    /**
     * Mappa i dati di una riga del ResultSet in un oggetto Prodotto.
     *
     * @param rs il ResultSet contenente i dati del prodotto
     * @return il prodotto popolato
     * @throws SQLException se si verifica un errore nella lettura dei dati
     */
    private Prodotto mapRowToProdotto(ResultSet rs) throws SQLException {
        Prodotto prodotto = new Prodotto();
        prodotto.setId(rs.getLong("id"));
        prodotto.setNome(rs.getString("nome"));
        prodotto.setDescrizione(rs.getString("descrizione"));
        prodotto.setQuantita(rs.getInt("quantita"));
        prodotto.setPercorsoImmagine(rs.getString("percorso_immagine"));
        prodotto.setTipologia(rs.getString("tipologia"));

        long idCategoria = rs.getLong("id_categoria");
        if (!rs.wasNull()) {
            Categoria categoria = new Categoria();
            categoria.setId(idCategoria);
            categoria.setNome(rs.getString("cat_nome"));
            categoria.setPercorsoImmagine(rs.getString("cat_percorso_immagine"));
            prodotto.setCategoria(categoria);
        } else {
            prodotto.setCategoria(null);
        }
        return prodotto;
    }
}