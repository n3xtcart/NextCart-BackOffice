package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.example.dao.UtenteDao;
import org.example.dao.impl.InMemoryUtenteDao;
import org.example.entity.Utente;
import org.example.exception.EccezioneAccessoDati;
import org.example.util.CodificatorePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Classe di test per l'implementazione InMemoryUtenteDao.
 * Verifica le operazioni di salvataggio, ricerca e aggiornamento degli utenti tramite il DAO.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Test InMemoryUtenteDao (Basato su Istanza)")
class TestInMemoryUtenteDao {

    // Istanza del DAO per la gestione degli utenti
    UtenteDao utenteDao;

    // Costanti per i dati di test degli utenti
    private static final String ADMIN_EMAIL_TEST = "admin.test@example.com";
    private static final String ADMIN_PASS_TEST = "password123";
    private static final String USER_EMAIL_TEST = "user.test@example.com";
    private static final String USER_PASS_TEST = "password456";

    // Variabili per gli utenti di test
    private Utente adminUser;
    private Utente regularUser;

    /**
     * Metodo di inizializzazione eseguito prima di ogni test.
     * Configura il DAO e salva gli utenti admin e utente regolare con password hashate.
     */
    @BeforeEach
    void setUp() {
        utenteDao = new InMemoryUtenteDao();

        // Calcola hash per la password dell'amministratore e salva l'utente
        String hashAdmin = CodificatorePassword.calcolaHashPassword(ADMIN_PASS_TEST);
        adminUser = utenteDao.salva(new Utente(null, ADMIN_EMAIL_TEST, hashAdmin, "ADMIN"));

        // Calcola hash per la password dell'utente regolare e salva l'utente
        String hashUser = CodificatorePassword.calcolaHashPassword(USER_PASS_TEST);
        regularUser = utenteDao.salva(new Utente(null, USER_EMAIL_TEST, hashUser, "USER"));
    }

    /**
     * Test per verificare la ricerca di un utente admin esistente tramite email.
     * Si attende il risultato corretto e una corrispondenza degli ID.
     */
    @Test
    @DisplayName("TrovaPerEmail: Utente Admin Esistente")
    void trovaPerEmail_esistenteAdmin_successo() {
        Optional<Utente> result = utenteDao.trovaPerEmail(ADMIN_EMAIL_TEST);
        assertTrue(result.isPresent(), "L'utente admin deve essere presente");
        assertEquals(ADMIN_EMAIL_TEST, result.get().getEmail(), "L'email dell'utente deve corrispondere");
        assertEquals(adminUser.getId(), result.get().getId(), "L'ID dell'utente admin deve corrispondere");
    }

    /**
     * Test per verificare la ricerca di un utente regolare esistente tramite email.
     */
    @Test
    @DisplayName("TrovaPerEmail: Utente User Esistente")
    void trovaPerEmail_esistenteUser_successo() {
        Optional<Utente> result = utenteDao.trovaPerEmail(USER_EMAIL_TEST);
        assertTrue(result.isPresent(), "L'utente regolare deve essere presente");
        assertEquals(USER_EMAIL_TEST, result.get().getEmail(), "L'email dell'utente deve corrispondere");
        assertEquals(regularUser.getId(), result.get().getId(), "L'ID dell'utente deve corrispondere");
    }

    /**
     * Test per la ricerca tramite email in maniera case-insensitive.
     * Verifica che la ricerca restituisca l'utente corretto anche passando l'email in maiuscolo.
     */
    @Test
    @DisplayName("TrovaPerEmail: Case Insensitive")
    void trovaPerEmail_caseInsensitive_successo() {
        Optional<Utente> result = utenteDao.trovaPerEmail(ADMIN_EMAIL_TEST.toUpperCase());
        assertTrue(result.isPresent(), "La ricerca non deve essere case-sensitive");
        assertEquals(adminUser.getId(), result.get().getId(), "L'ID dell'utente admin deve corrispondere");
    }

    /**
     * Test per la ricerca tramite email che non esiste.
     * Verifica che venga restituito un Optional vuoto.
     */
    @Test
    @DisplayName("TrovaPerEmail: Non Esistente")
    void trovaPerEmail_nonEsistente_optionalVuoto() {
        Optional<Utente> result = utenteDao.trovaPerEmail("inexistente@example.com");
        assertTrue(result.isEmpty(), "La ricerca deve restituire Optional vuoto per email inesistente");
    }

    /**
     * Test per la ricerca tramite email null.
     * Verifica che venga restituito un Optional vuoto in caso di email nulla.
     */
    @Test
    @DisplayName("TrovaPerEmail: Email Null")
    void trovaPerEmail_nullEmail_optionalVuoto() {
        Optional<Utente> result = utenteDao.trovaPerEmail(null);
        assertTrue(result.isEmpty(), "La ricerca con email null deve restituire Optional vuoto");
    }

    /**
     * Test per la ricerca di un utente admin esistente tramite ID.
     */
    @Test
    @DisplayName("TrovaPerId: Utente Admin Esistente")
    void trovaPerId_esistenteAdmin_successo() {
        Optional<Utente> result = utenteDao.trovaPerId(adminUser.getId());
        assertTrue(result.isPresent(), "L'utente admin deve essere trovato per ID");
        assertEquals(adminUser.getId(), result.get().getId(), "L'ID deve corrispondere all'utente admin");
        assertEquals(adminUser.getEmail(), result.get().getEmail(), "L'email deve corrispondere");
    }

    /**
     * Test per la ricerca di un utente regolare esistente tramite ID.
     */
    @Test
    @DisplayName("TrovaPerId: Utente User Esistente")
    void trovaPerId_esistenteUser_successo() {
        Optional<Utente> result = utenteDao.trovaPerId(regularUser.getId());
        assertTrue(result.isPresent(), "L'utente regolare deve essere trovato per ID");
        assertEquals(regularUser.getId(), result.get().getId(), "L'ID deve corrispondere all'utente regolare");
        assertEquals(regularUser.getEmail(), result.get().getEmail(), "L'email deve corrispondere");
    }

    /**
     * Test per la ricerca tramite ID inesistente.
     * Si attende che venga restituito un Optional vuoto.
     */
    @Test
    @DisplayName("TrovaPerId: Non Esistente")
    void trovaPerId_nonEsistente_optionalVuoto() {
        Optional<Utente> result = utenteDao.trovaPerId(9999L);
        assertTrue(result.isEmpty(), "La ricerca per ID inesistente deve restituire Optional vuoto");
    }

    /**
     * Test per la ricerca tramite ID null.
     */
    @Test
    @DisplayName("TrovaPerId: ID Null")
    void trovaPerId_nullId_optionalVuoto() {
        Optional<Utente> result = utenteDao.trovaPerId(null);
        assertTrue(result.isEmpty(), "La ricerca per ID null deve restituire Optional vuoto");
    }

    /**
     * Test per il salvataggio di un nuovo utente.
     * Verifica che un utente nuovo venga salvato e che i suoi dati siano correttamente registrati.
     */
    @Test
    @DisplayName("Salva: Nuovo Utente con Successo")
    void salva_nuovoUtente_successo() {
        // Il codice per il test di salvataggio di un nuovo utente andrà qui.
    }

    /**
     * Test per il salvataggio di un nuovo utente con email duplicata (Admin).
     * Verifica che venga lanciata l'EccezioneAccessoDati in caso di duplicazione dell'email.
     */
    @Test
    @DisplayName("Salva: Nuovo Utente con Email Duplicata (Admin) Lancia EccezioneAccessoDati")
    void salva_nuovoUtenteEmailDuplicataAdmin_lanciaEccezione() {
        // Il codice per il test di email duplicata per admin andrà qui.
    }

    /**
     * Test per il salvataggio di un nuovo utente con email duplicata (User).
     */
    @Test
    @DisplayName("Salva: Nuovo Utente con Email Duplicata (User) Lancia EccezioneAccessoDati")
    void salva_nuovoUtenteEmailDuplicataUser_lanciaEccezione() {
        // Il codice per il test di email duplicata per user andrà qui.
    }

    /**
     * Test per il salvataggio quando si passa un utente null.
     * Verifica che venga lanciata una IllegalArgumentException.
     */
    @Test
    @DisplayName("Salva: Utente Null Lancia IllegalArgumentException")
    void salva_utenteNull_lanciaEccezione() {
        // Il codice per il test del caso utente null andrà qui.
    }

    /**
     * Test per l'aggiornamento del ruolo di un utente esistente (Admin).
     */
    @Test
    @DisplayName("Salva: Aggiorna Ruolo Utente Esistente (Admin)")
    void salva_aggiornaUtenteRuolo_successo() {
        // Il codice per il test di aggiornamento ruolo andrà qui.
    }

    /**
     * Test per l'aggiornamento dell'email di un utente esistente (User) a una nuova.
     */
    @Test
    @DisplayName("Salva: Aggiorna Email Utente Esistente (User) a una Nuova")
    void salva_aggiornaUtenteNuovaEmail_successo() {
        // Il codice per il test di aggiornamento email andrà qui.
    }

    /**
     * Test per il tentativo di aggiornamento dell'email di un utente (User) impostandola uguale a quella dell'Admin,
     * che deve lanciare un'EccezioneAccessoDati.
     */
    @Test
    @DisplayName("Salva: Aggiorna Email Utente (User) a quella dell'Admin Lancia EccezioneAccessoDati")
    void salva_aggiornaUtenteEmailDuplicataAdmin_lanciaEccezione() {
        // Il codice per il test di aggiornamento email duplicata andrà qui.
    }

    /**
     * Test per l'aggiornamento di un utente non esistente.
     * Verifica che venga lanciata un'EccezioneAccessoDati.
     */
    @Test
    @DisplayName("Salva: Aggiorna Utente Non Esistente Lancia EccezioneAccessoDati")
    void salva_aggiornaUtenteNonEsistente_lanciaEccezione() {
        // Il codice per il test di aggiornamento di utente non esistente andrà qui.
    }

    /**
     * Test per l'aggiornamento dell'utente senza cambiare l'email (caso limite per il controllo dei duplicati).
     * Verifica che l'utente venga aggiornato correttamente.
     */
    @Test
    @DisplayName("Salva: Aggiorna Utente senza cambiare Email (caso limite controllo duplicati)")
    void salva_aggiornaSenzaCambioEmail_successo() {
        Utente userConModifiche = new Utente(regularUser.getId(), USER_EMAIL_TEST, regularUser.getHashPassword(), "USER_MODIFIED");
        assertDoesNotThrow(() -> utenteDao.salva(userConModifiche), "L'aggiornamento dell'utente non deve lanciare eccezioni");
        Utente inDao = utenteDao.trovaPerId(regularUser.getId()).orElseThrow();
        assertEquals("USER_MODIFIED", inDao.getRuolo(), "Il ruolo aggiornato deve essere 'USER_MODIFIED'");
        assertEquals(USER_EMAIL_TEST, inDao.getEmail(), "L'email deve rimanere invariata");
    }
}