package org.example;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import org.example.dao.CategoriaDao;
import org.example.dao.impl.InMemoryCategoriaDao;
import org.example.dto.CategoriaDTO;
import org.example.entity.Categoria;
import org.example.exception.EccezioneAccessoDati;
import org.example.service.ServizioCategoria;
import org.example.service.impl.ServizioCategoriaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Classe di test per il servizio ServizioCategoria.
 * Verifica il funzionamento delle operazioni di salvataggio, ricerca, modifica ed eliminazione sulle categorie.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TestServizioCategoria {

	// Istanza del servizio per gestire le categorie.
	ServizioCategoria servizioCategoria;

	// DAO per la gestione delle categorie.
	CategoriaDao categoriaDao;

	// Dati di esempio utilizzati nei test.
	private Categoria catFrutta;
	private Categoria catVerdura;

	/**
	 * Metodo di inizializzazione eseguito prima di ogni test.
	 * Configura il DAO e il servizio, quindi salva alcune categorie di esempio.
	 */
	@BeforeEach
	void setUp() {
		categoriaDao = new InMemoryCategoriaDao();
		servizioCategoria = new ServizioCategoriaImpl(categoriaDao);
		catFrutta = categoriaDao.salva(new Categoria(null, "Frutta Test", "/img/frutta.png"));
		catVerdura = categoriaDao.salva(new Categoria(null, "Verdura Test", "/img/verdura.png"));
	}

	/**
	 * Verifica la corretta istanziazione del ServizioCategoriaImpl.
	 */
	@Test
	@DisplayName("Costruttore ServizioCategoriaImpl")
	void testCostruttore() {
		assertNotNull(new ServizioCategoriaImpl(new InMemoryCategoriaDao()));
	}

	/**
	 * Test per il salvataggio di una nuova categoria.
	 * Controlla che l'ID venga assegnato e che i dati salvati corrispondano a quelli inseriti.
	 */
	@Test
	@DisplayName("Salva CategoriaDTO Nuova con Successo")
	void salva_nuovaCategoria_successo() {
		CategoriaDTO input = new CategoriaDTO(null, "Salumi Test", "/img/salumi.png");
		CategoriaDTO result = servizioCategoria.salva(input);
		assertNotNull(result.getId(), "L'ID dovrebbe essere assegnato");
		assertEquals(input.getNome(), result.getNome());
		assertEquals(input.getPercorsoImmagine(), result.getPercorsoImmagine());
		assertTrue(categoriaDao.trovaPerId(result.getId()).isPresent(), "La categoria salvata dovrebbe essere trovabile nel DAO");
	}

	/**
	 * Test per la gestione dei nomi duplicati.
	 * Verifica il lancio di EccezioneAccessoDati quando si tenta di salvare una categoria con nome già esistente.
	 */
	@Test
	@DisplayName("Salva CategoriaDTO con Nome Duplicato Lancia EccezioneAccessoDati")
	void salva_nomeDuplicato_lanciaEccezione() {
		CategoriaDTO input = new CategoriaDTO(null, catFrutta.getNome(), "/img/frutta_dupe.png");
		EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
			servizioCategoria.salva(input);
		}, "Salvare un nome duplicato dovrebbe lanciare EccezioneAccessoDati");
		assertTrue(ex.getMessage().contains("già esistente"), "Il messaggio d'errore dovrebbe indicare il nome duplicato");
	}

	/**
	 * Test per la ricerca di una categoria esistente tramite ID.
	 * Verifica che il metodo trovi la categoria corretta.
	 */
	@Test
	@DisplayName("TrovaPerId Categoria Esistente")
	void trovaPerId_esistente_successo() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerId(catFrutta.getId());
		assertTrue(result.isPresent(), "Dovrebbe trovare la categoria esistente");
		CategoriaDTO dto = result.get();
		assertEquals(catFrutta.getId(), dto.getId());
		assertEquals(catFrutta.getNome(), dto.getNome());
		assertEquals(catFrutta.getPercorsoImmagine(), dto.getPercorsoImmagine());
	}

	/**
	 * Test per la ricerca tramite ID di una categoria inesistente.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerId Categoria Non Esistente")
	void trovaPerId_nonEsistente_optionalVuoto() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerId(999L);
		assertTrue(result.isEmpty(), "Non dovrebbe trovare una categoria con ID inesistente");
	}

	/**
	 * Test per la ricerca mandato un ID null.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerId con ID Null")
	void trovaPerId_nullId_optionalVuoto() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerId(null);
		assertTrue(result.isEmpty(), "La ricerca con ID null dovrebbe restituire Optional vuoto");
	}

	/**
	 * Test per la ricerca di una categoria esistente tramite nome.
	 * Verifica che il metodo trovi la categoria corretta in base al nome.
	 */
	@Test
	@DisplayName("TrovaPerNome Categoria Esistente")
	void trovaPerNome_esistente_successo() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerNome(catVerdura.getNome());
		assertTrue(result.isPresent(), "Dovrebbe trovare la categoria esistente per nome");
		CategoriaDTO dto = result.get();
		assertEquals(catVerdura.getId(), dto.getId());
		assertEquals(catVerdura.getNome(), dto.getNome());
		assertEquals(catVerdura.getPercorsoImmagine(), dto.getPercorsoImmagine());
	}

	/**
	 * Test per la ricerca di una categoria inesistente tramite nome.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerNome Categoria Non Esistente")
	void trovaPerNome_nonEsistente_optionalVuoto() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerNome("NomeCheNonEsiste");
		assertTrue(result.isEmpty(), "Non dovrebbe trovare una categoria con nome inesistente");
	}

	/**
	 * Test per la ricerca tramite nome con input null.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerNome con Nome Null")
	void trovaPerNome_nullNome_optionalVuoto() {
		Optional<CategoriaDTO> result = servizioCategoria.trovaPerNome(null);
		assertTrue(result.isEmpty(), "La ricerca con nome null dovrebbe restituire Optional vuoto");
	}

	/**
	 * Test per il recupero di tutte le categorie.
	 * Controlla che il numero delle categorie ritornate corrisponda a quello previsto.
	 */
	@Test
	@DisplayName("TrovaTutte Restituisce Tutte le Categorie")
	void trovaTutte_conCategorie_successo() {
		List<CategoriaDTO> result = servizioCategoria.trovaTutte();
		assertEquals(2, result.size(), "Dovrebbe restituire le 2 categorie inserite nel setup");
		assertTrue(result.stream().anyMatch(c -> c.getId().equals(catFrutta.getId()) && c.getNome().equals(catFrutta.getNome())));
		assertTrue(result.stream().anyMatch(c -> c.getId().equals(catVerdura.getId()) && c.getNome().equals(catVerdura.getNome())));
	}

	/**
	 * Test per il caso in cui il DAO risulti vuoto.
	 * Dopo aver eliminato tutte le categorie, verifica che venga restituita una lista vuota.
	 */
	@Test
	@DisplayName("TrovaTutte Restituisce Lista Vuota se DAO è Vuoto")
	void trovaTutte_daoVuoto_listaVuota() {
		categoriaDao.elimina(catFrutta.getId());
		categoriaDao.elimina(catVerdura.getId());
		List<CategoriaDTO> result = servizioCategoria.trovaTutte();
		assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota se il DAO non contiene categorie");
	}

	/**
	 * Test per la modifica di una categoria esistente.
	 * Verifica che le modifiche (nome e percorso immagine) siano correttamente applicate.
	 */
	@Test
	@DisplayName("Modifica CategoriaDTO Esistente con Successo")
	void modifica_esistente_successo() {
		CategoriaDTO modificaInput = new CategoriaDTO(catFrutta.getId(), "Frutta Fresca Modificata", "/img/frutta_mod.png");
		CategoriaDTO result = servizioCategoria.modifica(modificaInput);
		assertEquals(modificaInput.getId(), result.getId());
		assertEquals(modificaInput.getNome(), result.getNome());
		assertEquals(modificaInput.getPercorsoImmagine(), result.getPercorsoImmagine());
		Categoria inDao = categoriaDao.trovaPerId(catFrutta.getId()).orElseThrow(() -> new AssertionError("Categoria non trovata dopo modifica"));
		assertEquals(modificaInput.getNome(), inDao.getNome());
		assertEquals(modificaInput.getPercorsoImmagine(), inDao.getPercorsoImmagine());
	}

	/**
	 * Test per la modifica di una categoria inesistente.
	 * Verifica che il metodo lanci EccezioneAccessoDati in caso di aggiornamento su ID non esistente.
	 */
	@Test
	@DisplayName("Modifica CategoriaDTO Non Esistente Lancia EccezioneAccessoDati (da DAO.salva)")
	void modifica_nonEsistente_lanciaEccezione() {
		CategoriaDTO modificaInput = new CategoriaDTO(999L, "Fantasma", "/img/fantasma.png");
		EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
			servizioCategoria.modifica(modificaInput);
		}, "Modificare un ID non esistente dovrebbe lanciare EccezioneAccessoDati");
		assertTrue(ex.getMessage().contains("Impossibile aggiornare categoria non esistente"), "Il messaggio dovrebbe indicare l'impossibilità di aggiornare");
	}

	/**
	 * Test per la modifica di una categoria cambiando il nome in uno già usato da un'altra categoria.
	 * Verifica che venga lanciata EccezioneAccessoDati in caso di nome duplicato.
	 */
	@Test
	@DisplayName("Modifica CategoriaDTO Cambiando Nome in uno Duplicato Lancia EccezioneAccessoDati (da DAO.salva)")
	void modifica_cambioNomeDuplicato_lanciaEccezione() {
		CategoriaDTO modificaInput = new CategoriaDTO(catFrutta.getId(), catVerdura.getNome(), catFrutta.getPercorsoImmagine());
		EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
			servizioCategoria.modifica(modificaInput);
		}, "Modificare il nome rendendolo duplicato dovrebbe lanciare EccezioneAccessoDati");
		assertTrue(ex.getMessage().contains("è già usato da un'altra categoria"), "Il messaggio dovrebbe indicare il conflitto sul nome");
	}

	/**
	 * Test per la modifica di una categoria con ID null.
	 * Verifica che venga lanciata EccezioneAccessoDati in presenza di ID null, in particolare per nome duplicato.
	 */
	@Test
	@DisplayName("Modifica CategoriaDTO con ID Null Lancia Eccezione (da DAO.salva per nome duplicato)")
	void modifica_nullId_lanciaEccezioneNomeDuplicato() {
		CategoriaDTO modificaInput = new CategoriaDTO(null, catFrutta.getNome(), "/img/qualsiasi.png");
		EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
			servizioCategoria.modifica(modificaInput);
		}, "Modifica con ID null e nome duplicato dovrebbe lanciare EccezioneAccessoDati");
		assertTrue(ex.getMessage().contains("già esistente"));
	}

	/**
	 * Test per la gestione del salvataggio con input null.
	 * Verifica che DAO.salva(null) lanci una IllegalArgumentException.
	 */
	@Test
	@DisplayName("Modifica CategoriaDTO con DTO Null Lancia IllegalArgumentException (da DAO.salva)")
	void modifica_nullDto_lanciaEccezione() {
		assertThrows(IllegalArgumentException.class, () -> categoriaDao.salva(null), "DAO.salva(null) dovrebbe lanciare IllegalArgumentException");
	}

	/**
	 * Test per l'eliminazione di una categoria esistente.
	 * Controlla che la categoria venga effettivamente rimossa dal DAO.
	 */
	@Test
	@DisplayName("Elimina Categoria Esistente con Successo")
	void elimina_esistente_successo() {
		Long idDaEliminare = catVerdura.getId();
		assertTrue(categoriaDao.trovaPerId(idDaEliminare).isPresent(), "La categoria dovrebbe esistere prima dell'eliminazione");
		assertDoesNotThrow(() -> servizioCategoria.elimina(idDaEliminare), "L'eliminazione non dovrebbe lanciare eccezioni per ID esistente");
		assertFalse(categoriaDao.trovaPerId(idDaEliminare).isPresent(), "La categoria non dovrebbe più essere trovabile dopo l'eliminazione");
	}

	/**
	 * Test per l'eliminazione di una categoria inesistente.
	 * Verifica che venga lanciata una IllegalArgumentException indicando che la categoria non è stata trovata.
	 */
	@Test
	@DisplayName("Elimina Categoria Non Esistente Lancia IllegalArgumentException (da DAO.elimina)")
	void elimina_nonEsistente_lanciaEccezione() {
		Long idNonEsistente = 999L;
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			servizioCategoria.elimina(idNonEsistente);
		}, "Eliminare un ID non esistente dovrebbe lanciare IllegalArgumentException");
		assertTrue(ex.getMessage().contains("Categoria non trovata per ID"), "Il messaggio dovrebbe indicare l'ID non trovato");
	}

	/**
	 * Test per l'eliminazione di una categoria con ID null.
	 * Verifica che l'eliminazione con ID null lanci una IllegalArgumentException.
	 */
	@Test
	@DisplayName("Elimina con ID Null Lancia IllegalArgumentException (da DAO.elimina)")
	void elimina_nullId_lanciaEccezione() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
			servizioCategoria.elimina(null);
		}, "Eliminare con ID null dovrebbe lanciare IllegalArgumentException");
		assertTrue(ex.getMessage().contains("Categoria non trovata per ID"), "Il messaggio dovrebbe indicare ID null non trovato");
	}
}