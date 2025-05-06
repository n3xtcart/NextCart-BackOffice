package org.example.dao.impl;

import org.example.dao.UtenteDao;
import org.example.entity.Utente;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.Optional;

/**
 * Implementazione del DAO per l'entità Utente tramite JDBC.
 *
 * <p>
 * Questa classe utilizza il pattern Singleton ed effettua operazioni di accesso
 * al database come ricerca, salvataggio e aggiornamento degli utenti.
 * </p>
 */
public class JdbcUtenteDao implements UtenteDao {

    private static volatile JdbcUtenteDao instance;
    private final DatabaseConnectionManager connectionManager;

    /**
     * Costruttore privato per il pattern Singleton.
     */
    private JdbcUtenteDao() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Restituisce l'istanza singleton di JdbcUtenteDao.
     *
     * @return l'istanza di JdbcUtenteDao
     */
    public static JdbcUtenteDao getInstance() {
        if (instance == null) {
            synchronized (JdbcUtenteDao.class) {
                if (instance == null) {
                    instance = new JdbcUtenteDao();
                }
            }
        }
        return instance;
    }

    /**
     * Cerca un utente a partire dall'email.
     *
     * @param email L'email dell'utente da cercare
     * @return un {@code Optional} contenente l'utente, se trovato
     * @throws EccezioneAccessoDati in caso di errori durante l'accesso al database
     */
    @Override
    public Optional<Utente> trovaPerEmail(String email) {
        if (email == null) return Optional.empty();
        String sql = "SELECT id, email, hash_password, ruolo FROM utenti WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUtente(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca dell'utente per email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Cerca un utente a partire dall'ID.
     *
     * @param id L'ID dell'utente da cercare
     * @return un {@code Optional} contenente l'utente, se trovato
     * @throws EccezioneAccessoDati in caso di errori durante l'accesso al database
     */
    @Override
    public Optional<Utente> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, email, hash_password, ruolo FROM utenti WHERE id = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUtente(rs));
                }
            }
        } catch (SQLException e) {
            throw new EccezioneAccessoDati("Errore durante la ricerca dell'utente per ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Salva o aggiorna un utente nel database.
     *
     * <p>
     * Se l'utente non ha un ID, viene eseguito un inserimento; altrimenti, viene eseguito un aggiornamento.
     * Viene inoltre verificata l'unicità dell'email.
     * </p>
     *
     * @param utente L'utente da salvare o aggiornare
     * @return l'utente salvato o aggiornato
     * @throws IllegalArgumentException se l'utente è nullo
     * @throws EccezioneAccessoDati in caso di errori durante l'accesso al database o violazioni di vincolo
     */
    @Override
    public Utente salva(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere nullo");
        }

        if (utente.getId() == null) {
            if (trovaPerEmail(utente.getEmail()).isPresent()) {
                throw new EccezioneAccessoDati("Email già esistente: " + utente.getEmail());
            }
        } else {
            Optional<Utente> utenteEsistenteOpt = trovaPerId(utente.getId());
            if (utenteEsistenteOpt.isPresent()) {
                Utente utenteEsistente = utenteEsistenteOpt.get();
                if (!utente.getEmail().equalsIgnoreCase(utenteEsistente.getEmail())) {
                    if (trovaPerEmail(utente.getEmail()).filter(u -> !u.getId().equals(utente.getId())).isPresent()) {
                        throw new EccezioneAccessoDati("Impossibile aggiornare email a " + utente.getEmail() + ", è già usata da un altro utente.");
                    }
                }
            } else {
                throw new EccezioneAccessoDati("Impossibile aggiornare utente non esistente con id: " + utente.getId());
            }
        }

        String sql;
        if (utente.getId() == null) {
            sql = "INSERT INTO utenti (email, hash_password, ruolo) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE utenti SET email = ?, hash_password = ?, ruolo = ? WHERE id = ?";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, utente.getEmail());
            pstmt.setString(2, utente.getHashPassword());
            pstmt.setString(3, utente.getRuolo());

            if (utente.getId() != null) {
                pstmt.setLong(4, utente.getId());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new EccezioneAccessoDati("Salvataggio utente fallito, nessuna riga modificata.");
            }

            if (utente.getId() == null) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utente.setId(generatedKeys.getLong(1));
                    } else {
                        throw new EccezioneAccessoDati("Salvataggio utente fallito, nessun ID generato.");
                    }
                }
            }
            return utente;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new EccezioneAccessoDati("Email '" + utente.getEmail() + "' già esistente (violazione vincolo DB).", e);
            }
            throw new EccezioneAccessoDati("Errore durante il salvataggio dell'utente: " + e.getMessage(), e);
        }
    }

    /**
     * Mappa una riga del ResultSet a un oggetto Utente.
     *
     * @param rs Il ResultSet contenente i dati dell'utente
     * @return un oggetto Utente popolato con i dati del ResultSet
     * @throws SQLException se si verifica un errore durante la lettura dei dati
     */
    private Utente mapRowToUtente(ResultSet rs) throws SQLException {
        return new Utente(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("hash_password"),
                rs.getString("ruolo")
        );
    }
}