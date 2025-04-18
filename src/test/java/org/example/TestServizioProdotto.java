package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.example.dao.CategoriaDao;
import org.example.dao.ProdottoDao;
import org.example.dao.impl.InMemoryCategoriaDao;
import org.example.dao.impl.InMemoryProdottoDao;
import org.example.dto.CategoriaDTO;
import org.example.dto.ProdottoDTO;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneAccessoDati;
import org.example.service.ServizioProdotto;
import org.example.service.impl.ServizioProdottoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Classe di test per il servizio ServizioProdotto.
 * Contiene i test per le operazioni di salvataggio, modifica, ricerca ed eliminazione dei prodotti.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TestServizioProdotto {

	// Istanza del servizio per i prodotti
	ServizioProdotto servizioProdotto;

	// DAO per la gestione dei prodotti e delle categorie
	ProdottoDao prodottoDao;
	CategoriaDao categoriaDao;

	// Dati di esempio: categorie e prodotti
	private Categoria categoriaCarne;
	private Categoria categoriaFrutta;
	private Prodotto prodottoVitello;
	private Prodotto prodottoPollo;
	private Prodotto prodottoMela;

	/**
	 * Metodo di inizializzazione eseguito prima di ogni test.
	 * Configura i DAO, il servizio e inserisce i dati di esempio.
	 */
	@BeforeEach
	void setUp() {
		categoriaDao = new InMemoryCategoriaDao();
		prodottoDao = new InMemoryProdottoDao();
		servizioProdotto = new ServizioProdottoImpl(categoriaDao, prodottoDao);
		categoriaCarne = categoriaDao.salva(new Categoria(null, "Carne Test", "/img/carne.png"));
		categoriaFrutta = categoriaDao.salva(new Categoria(null, "Frutta Test", "/img/frutta.png"));
		prodottoVitello = prodottoDao.salva(new Prodotto(null, "Vitello Test", "Desc Vitello", 100, "/img/vitello.png", categoriaCarne));
		prodottoPollo = prodottoDao.salva(new Prodotto(null, "Pollo Test", "Desc Pollo", 200, "/img/pollo.png", categoriaCarne));
		prodottoMela = prodottoDao.salva(new Prodotto(null, "Mela Test", "Desc Mela", 50, "/img/mela.png", categoriaFrutta));
	}

	/**
	 * Test per il salvataggio di un nuovo prodotto con una categoria valida.
	 * Verifica che il prodotto venga salvato correttamente e che i dati siano consistenti.
	 */
	@Test
	@DisplayName("Salva: Nuovo prodotto con categoria valida")
	public void salvaProdottoDTO_Successo_NuovoProdotto() {
		CategoriaDTO categoriaDTO = new CategoriaDTO(categoriaCarne.getId(), categoriaCarne.getNome(), categoriaCarne.getPercorsoImmagine());
		ProdottoDTO prodottoInputDTO = new ProdottoDTO(null, "Agnello Test", "Desc Agnello", 150, "/img/agnello.png", categoriaDTO);
		ProdottoDTO salvatoDTO = servizioProdotto.salva(prodottoInputDTO);

		// Verifica che l'ID sia stato assegnato e che i dati corrispondano
		assertNotNull(salvatoDTO.getId(), "L'ID dovrebbe essere assegnato al salvataggio");
		assertEquals(prodottoInputDTO.getNome(), salvatoDTO.getNome());
		assertEquals(prodottoInputDTO.getDescrizione(), salvatoDTO.getDescrizione());
		assertEquals(prodottoInputDTO.getQuantita(), salvatoDTO.getQuantita());
		assertEquals(prodottoInputDTO.getPercorsoImmagine(), salvatoDTO.getPercorsoImmagine());
		assertNotNull(salvatoDTO.getCategoriaDTO(), "Il DTO della categoria dovrebbe essere presente nel risultato");
		assertEquals(categoriaCarne.getId(), salvatoDTO.getCategoriaDTO().getId());
		assertTrue(prodottoDao.trovaPerId(salvatoDTO.getId()).isPresent(), "Il prodotto dovrebbe essere trovabile nel DAO dopo il salvataggio");
	}

	/**
	 * Test per il salvataggio di un prodotto specificando una categoria inesistente.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Salva: Fallisce se la categoria specificata non esiste")
	public void salvaProdottoDTO_Fail_CategoriaNonTrovata() {
		Long idCategoriaInesistente = 999L;
		CategoriaDTO categoriaInesistenteDTO = new CategoriaDTO(idCategoriaInesistente, "Inesistente", "/none.png");
		ProdottoDTO prodottoInputDTO = new ProdottoDTO(null, "Prodotto Fantasma", "Desc Fantasma", 10, "/img/fantasma.png", categoriaInesistenteDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.salva(prodottoInputDTO), "Dovrebbe lanciare eccezione quando l'ID della categoria non esiste");
		assertTrue(exception.getMessage().contains("Categoria non trovata per ID: " + idCategoriaInesistente), "Il messaggio dell'eccezione dovrebbe indicare l'ID della categoria mancante");
	}

	/**
	 * Test per il salvataggio di un prodotto con CategoriaDTO null.
	 * Si attende che venga lanciato un NullPointerException.
	 */
	@Test
	@DisplayName("Salva: Fallisce (NPE) se CategoriaDTO è null nel ProdottoDTO")
	public void salvaProdottoDTO_Fail_CategoriaDTONull() {
		ProdottoDTO prodottoInputDTO = new ProdottoDTO(null, "Prodotto Senza Categoria", "Desc", 10, "/img/invalid.png", null);
		assertThrows(NullPointerException.class, () -> servizioProdotto.salva(prodottoInputDTO), "Dovrebbe lanciare NullPointerException se CategoriaDTO è null");
	}

	/**
	 * Test per il salvataggio di un prodotto con CategoriaDTO avente ID null.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Salva: Fallisce se ID Categoria è null nel CategoriaDTO")
	public void salvaProdottoDTO_Fail_CategoriaDTOIdNull() {
		CategoriaDTO categoriaSenzaIdDTO = new CategoriaDTO(null, "Categoria Senza ID", "/no_id.png");
		ProdottoDTO prodottoInputDTO = new ProdottoDTO(null, "Prodotto Cat Senza ID", "Desc", 20, "/img/cat_no_id.png", categoriaSenzaIdDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.salva(prodottoInputDTO), "Dovrebbe lanciare eccezione se l'ID della CategoriaDTO è null");
		assertTrue(exception.getMessage().contains("Categoria non trovata per ID: null"), "Il messaggio dell'eccezione dovrebbe indicare che l'ID della categoria null è stato cercato");
	}

	/**
	 * Test per la ricerca di un prodotto esistente assegnato alla categoria 'Carne'.
	 * Verifica che il prodotto copiato abbia dati coerenti con quelli attesi.
	 */
	@Test
	@DisplayName("TrovaPerId: Prodotto esistente (Categoria Carne)")
	public void trovaPerId_Successo_ProdottoEsistente_Cat1() {
		Optional<ProdottoDTO> optionalProdottoDTO = servizioProdotto.trovaPerId(prodottoVitello.getId());
		assertTrue(optionalProdottoDTO.isPresent(), "Il prodotto dovrebbe essere trovato");
		ProdottoDTO dto = optionalProdottoDTO.get();
		assertEquals(prodottoVitello.getId(), dto.getId());
		assertEquals(prodottoVitello.getNome(), dto.getNome());
		assertEquals(categoriaCarne.getId(), dto.getCategoriaDTO().getId(), "Dovrebbe appartenere alla categoria Carne");
	}

	/**
	 * Test per la ricerca di un prodotto esistente assegnato alla categoria 'Frutta'.
	 * Verifica che il prodotto copiato sia coerente con i dati di input.
	 */
	@Test
	@DisplayName("TrovaPerId: Prodotto esistente (Categoria Frutta)")
	public void trovaPerId_Successo_ProdottoEsistente_Cat2() {
		Optional<ProdottoDTO> optionalProdottoDTO = servizioProdotto.trovaPerId(prodottoMela.getId());
		assertTrue(optionalProdottoDTO.isPresent(), "Il prodotto dovrebbe essere trovato");
		ProdottoDTO dto = optionalProdottoDTO.get();
		assertEquals(prodottoMela.getId(), dto.getId());
		assertEquals(prodottoMela.getNome(), dto.getNome());
		assertEquals(categoriaFrutta.getId(), dto.getCategoriaDTO().getId(), "Dovrebbe appartenere alla categoria Frutta");
	}

	/**
	 * Test per la ricerca di un prodotto con ID inesistente.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerId: Fallisce se ID non esiste")
	public void trovaPerId_Fail_IdNonEsistente() {
		Optional<ProdottoDTO> optionalProdottoDTO = servizioProdotto.trovaPerId(999L);
		assertFalse(optionalProdottoDTO.isPresent(), "Il prodotto non dovrebbe essere trovato per ID inesistente");
	}

	/**
	 * Test per la ricerca di un prodotto con ID null.
	 * Verifica che il risultato sia un Optional vuoto.
	 */
	@Test
	@DisplayName("TrovaPerId: Fallisce se ID è null")
	public void trovaPerId_Fail_IdNull() {
		Optional<ProdottoDTO> optionalProdottoDTO = servizioProdotto.trovaPerId(null);
		assertFalse(optionalProdottoDTO.isPresent(), "Il prodotto non dovrebbe essere trovato per ID null");
	}

	/**
	 * Test per il recupero di tutti i prodotti presenti.
	 * Controlla che il numero di prodotti restituiti corrisponda a quello atteso.
	 */
	@Test
	@DisplayName("TrovaTutti: Restituisce tutti i prodotti")
	public void trovaTutti_Successo_ConProdotti() {
		List<ProdottoDTO> tuttiProdotti = servizioProdotto.trovaTutti();
		assertEquals(3, tuttiProdotti.size(), "Dovrebbe restituire tutti i 3 prodotti dal setup");
		assertTrue(tuttiProdotti.stream().anyMatch(p -> p.getId().equals(prodottoVitello.getId()) && p.getCategoriaDTO().getId().equals(categoriaCarne.getId())));
		assertTrue(tuttiProdotti.stream().anyMatch(p -> p.getId().equals(prodottoMela.getId()) && p.getCategoriaDTO().getId().equals(categoriaFrutta.getId())));
	}

	/**
	 * Test per il recupero di tutti i prodotti quando il DAO non contiene alcun prodotto.
	 * Verifica che venga restituita una lista vuota.
	 */
	@Test
	@DisplayName("TrovaTutti: Restituisce lista vuota se non ci sono prodotti")
	public void trovaTutti_Successo_NessunProdotto() {
		prodottoDao.elimina(prodottoVitello.getId());
		prodottoDao.elimina(prodottoPollo.getId());
		prodottoDao.elimina(prodottoMela.getId());
		List<ProdottoDTO> tuttiProdotti = servizioProdotto.trovaTutti();
		assertTrue(tuttiProdotti.isEmpty(), "Dovrebbe restituire una lista vuota quando il DAO è vuoto");
	}

	/**
	 * Test per il recupero di tutti i prodotti appartenenti a una categoria esistente.
	 * Verifica che vengano trovati esclusivamente i prodotti della categoria specificata.
	 */
	@Test
	@DisplayName("TrovaPerIdCategoria: Categoria con prodotti esistenti")
	public void trovaPerIdCategoria_Successo_CategoriaConProdotti() {
		List<ProdottoDTO> prodottiCategoria = servizioProdotto.trovaPerIdCategoria(categoriaCarne.getId());
		assertEquals(2, prodottiCategoria.size(), "Dovrebbe trovare 2 prodotti in 'Carne Test'");
		assertTrue(prodottiCategoria.stream().allMatch(p -> p.getCategoriaDTO().getId().equals(categoriaCarne.getId())));
		assertTrue(prodottiCategoria.stream().anyMatch(p -> p.getId().equals(prodottoVitello.getId())));
		assertTrue(prodottiCategoria.stream().anyMatch(p -> p.getId().equals(prodottoPollo.getId())));
	}

	/**
	 * Test per il recupero dei prodotti di una categoria che non contiene nessun prodotto.
	 * Verifica che venga restituita una lista vuota.
	 */
	@Test
	@DisplayName("TrovaPerIdCategoria: Categoria senza prodotti")
	public void trovaPerIdCategoria_Successo_CategoriaSenzaProdotti() {
		Categoria categoriaVuota = categoriaDao.salva(new Categoria(null, "Categoria Vuota", "/empty.png"));
		List<ProdottoDTO> prodottiCategoria = servizioProdotto.trovaPerIdCategoria(categoriaVuota.getId());
		assertTrue(prodottiCategoria.isEmpty(), "Dovrebbe restituire una lista vuota per la categoria senza prodotti");
	}

	/**
	 * Test per il recupero dei prodotti utilizzando un ID categoria inesistente.
	 * Attende il ritorno di una lista vuota.
	 */
	@Test
	@DisplayName("TrovaPerIdCategoria: Fallisce se ID Categoria non esiste")
	public void trovaPerIdCategoria_Fail_IdCategoriaNonEsistente() {
		List<ProdottoDTO> prodottiCategoria = servizioProdotto.trovaPerIdCategoria(999L);
		assertTrue(prodottiCategoria.isEmpty(), "Dovrebbe restituire una lista vuota per un ID categoria inesistente");
	}

	/**
	 * Test per il recupero dei prodotti utilizzando un ID categoria null.
	 * Attende il ritorno di una lista vuota.
	 */
	@Test
	@DisplayName("TrovaPerIdCategoria: Fallisce se ID Categoria è null")
	public void trovaPerIdCategoria_Fail_IdCategoriaNull() {
		List<ProdottoDTO> prodottiCategoria = servizioProdotto.trovaPerIdCategoria(null);
		assertTrue(prodottiCategoria.isEmpty(), "Dovrebbe restituire una lista vuota per ID categoria null");
	}

	/**
	 * Test per la modifica di un prodotto aggiornandone nome, quantità e categoria.
	 * Verifica che le modifiche siano riflesse in entrambi i DTO e nel DAO.
	 */
	@Test
	@DisplayName("Modifica: Successo cambiando nome, quantità e categoria")
	public void modificaProdottoDTO_Successo_CampiMultipli() {
		Long idDaModificare = prodottoMela.getId();
		String nuovoNome = "Mela Modificata";
		int nuovaQuantita = 75;
		CategoriaDTO nuovaCategoriaDTO = new CategoriaDTO(categoriaCarne.getId(), categoriaCarne.getNome(), categoriaCarne.getPercorsoImmagine());
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idDaModificare, nuovoNome, prodottoMela.getDescrizione(), nuovaQuantita, prodottoMela.getPercorsoImmagine(), nuovaCategoriaDTO);

		ProdottoDTO modificatoDTO = servizioProdotto.modifica(dtoDaModificare);
		assertEquals(idDaModificare, modificatoDTO.getId());
		assertEquals(nuovoNome, modificatoDTO.getNome());
		assertEquals(nuovaQuantita, modificatoDTO.getQuantita());
		assertEquals(nuovaCategoriaDTO.getId(), modificatoDTO.getCategoriaDTO().getId());

		Prodotto prodottoInDAO = prodottoDao.trovaPerId(idDaModificare).orElseThrow();
		assertEquals(nuovoNome, prodottoInDAO.getNome());
		assertEquals(nuovaQuantita, prodottoInDAO.getQuantita());
		assertEquals(categoriaCarne.getId(), prodottoInDAO.getCategoria().getId());
	}

	/**
	 * Test per la modifica di un prodotto aggiornando solo i campi non relativi alla categoria.
	 * Verifica che la categoria rimanga invariata.
	 */
	@Test
	@DisplayName("Modifica: Successo cambiando solo campi non-categoria")
	public void modificaProdottoDTO_Successo_SoloCampiNonCategoria() {
		Long idDaModificare = prodottoPollo.getId();
		String nuovoNome = "Pollo Arrosto";
		int nuovaQuantita = 180;
		String nuovaDesc = "Pronto da cuocere";
		CategoriaDTO categoriaOriginaleDTO = new CategoriaDTO(categoriaCarne.getId(), categoriaCarne.getNome(), categoriaCarne.getPercorsoImmagine());
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idDaModificare, nuovoNome, nuovaDesc, nuovaQuantita, prodottoPollo.getPercorsoImmagine(), categoriaOriginaleDTO);

		ProdottoDTO modificatoDTO = servizioProdotto.modifica(dtoDaModificare);
		assertEquals(idDaModificare, modificatoDTO.getId());
		assertEquals(nuovoNome, modificatoDTO.getNome());
		assertEquals(nuovaQuantita, modificatoDTO.getQuantita());
		assertEquals(categoriaCarne.getId(), modificatoDTO.getCategoriaDTO().getId(), "La categoria dovrebbe rimanere invariata");

		Prodotto prodottoInDAO = prodottoDao.trovaPerId(idDaModificare).orElseThrow();
		assertEquals(nuovoNome, prodottoInDAO.getNome());
		assertEquals(nuovaQuantita, prodottoInDAO.getQuantita());
		assertEquals(categoriaCarne.getId(), prodottoInDAO.getCategoria().getId());
	}

	/**
	 * Test per la modifica di un prodotto aggiornandone la sola categoria.
	 * Verifica che il cambio di categoria sia effettivamente applicato.
	 */
	@Test
	@DisplayName("Modifica: Successo cambiando solo la categoria")
	public void modificaProdottoDTO_Successo_SoloCategoria() {
		Long idDaModificare = prodottoVitello.getId();
		CategoriaDTO nuovaCategoriaDTO = new CategoriaDTO(categoriaFrutta.getId(), categoriaFrutta.getNome(), categoriaFrutta.getPercorsoImmagine());
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idDaModificare, prodottoVitello.getNome(), prodottoVitello.getDescrizione(), prodottoVitello.getQuantita(), prodottoVitello.getPercorsoImmagine(), nuovaCategoriaDTO);

		ProdottoDTO modificatoDTO = servizioProdotto.modifica(dtoDaModificare);
		assertEquals(idDaModificare, modificatoDTO.getId());
		assertEquals(prodottoVitello.getNome(), modificatoDTO.getNome());
		assertEquals(categoriaFrutta.getId(), modificatoDTO.getCategoriaDTO().getId(), "La categoria dovrebbe essere cambiata in Frutta");

		Prodotto prodottoInDAO = prodottoDao.trovaPerId(idDaModificare).orElseThrow();
		assertEquals(prodottoVitello.getNome(), prodottoInDAO.getNome());
		assertEquals(categoriaFrutta.getId(), prodottoInDAO.getCategoria().getId());
	}

	/**
	 * Test per la modifica di un prodotto con ID inesistente.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Modifica: Fallisce se Prodotto ID non esiste")
	public void modificaProdottoDTO_Fail_ProdottoNonTrovato() {
		Long idInesistente = 999L;
		CategoriaDTO categoriaDTO = new CategoriaDTO(categoriaCarne.getId(), categoriaCarne.getNome(), categoriaCarne.getPercorsoImmagine());
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idInesistente, "Fantasma Mod", "Desc", 10, "/img/fantasma.png", categoriaDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dtoDaModificare));
		assertTrue(exception.getMessage().contains("Prodotto non trovato per ID: " + idInesistente), "Il messaggio dell'eccezione dovrebbe indicare l'ID del prodotto mancante");
	}

	/**
	 * Test per la modifica di un prodotto con ID null.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Modifica: Fallisce se Prodotto ID è null")
	public void modificaProdottoDTO_Fail_ProdottoIdNull() {
		CategoriaDTO categoriaDTO = new CategoriaDTO(categoriaCarne.getId(), categoriaCarne.getNome(), categoriaCarne.getPercorsoImmagine());
		ProdottoDTO dtoDaModificare = new ProdottoDTO(null, "Nome Invalido", "Desc", 10, "/img/invalid.png", categoriaDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dtoDaModificare));
		assertTrue(exception.getMessage().contains("Prodotto non trovato per ID: null"), "Il messaggio dell'eccezione dovrebbe indicare che il prodotto non è stato trovato per ID: null");
	}

	/**
	 * Test per la modifica di un prodotto con una nuova categoria inesistente.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Modifica: Fallisce se la nuova Categoria ID non esiste")
	public void modificaProdottoDTO_Fail_CategoriaNonTrovata() {
		Long idProdottoEsistente = prodottoPollo.getId();
		Long idCategoriaInesistente = 999L;
		CategoriaDTO categoriaInesistenteDTO = new CategoriaDTO(idCategoriaInesistente, "Inesistente", "/none.png");
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idProdottoEsistente, prodottoPollo.getNome(), prodottoPollo.getDescrizione(), prodottoPollo.getQuantita(), prodottoPollo.getPercorsoImmagine(), categoriaInesistenteDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dtoDaModificare));
		assertTrue(exception.getMessage().contains("Categoria non trovata per ID: " + idCategoriaInesistente), "Il messaggio dell'eccezione dovrebbe indicare che la categoria non è stata trovata per l'ID: " + idCategoriaInesistente);
	}

	/**
	 * Test per la modifica di un prodotto con CategoriaDTO null.
	 * Attende il lancio di un NullPointerException.
	 */
	@Test
	@DisplayName("Modifica: Fallisce (NPE) se CategoriaDTO è null nel ProdottoDTO")
	public void modificaProdottoDTO_Fail_CategoriaDTONull() {
		Long idProdottoEsistente = prodottoPollo.getId();
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idProdottoEsistente, "Nome", "Desc", 10, "/img/img.png", null);
		assertThrows(NullPointerException.class, () -> servizioProdotto.modifica(dtoDaModificare));
	}

	/**
	 * Test per la modifica di un prodotto con CategoriaDTO avente ID null.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Modifica: Fallisce se ID Categoria è null nel CategoriaDTO")
	public void modificaProdottoDTO_Fail_CategoriaDTOIdNull() {
		Long idProdottoEsistente = prodottoPollo.getId();
		CategoriaDTO categoriaSenzaIdDTO = new CategoriaDTO(null, "Senza ID", "/no_id.png");
		ProdottoDTO dtoDaModificare = new ProdottoDTO(idProdottoEsistente, "Nome", "Desc", 10, "/img/img.png", categoriaSenzaIdDTO);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dtoDaModificare));
		assertTrue(exception.getMessage().contains("Categoria non trovata per ID: null"), "Il messaggio dell'eccezione dovrebbe indicare che la categoria non è stata trovata per ID: null");
	}

	/**
	 * Test per l'eliminazione di un prodotto esistente.
	 * Verifica che il prodotto venga effettivamente rimosso dal DAO.
	 */
	@Test
	@DisplayName("Elimina: Successo per prodotto esistente")
	public void elimina_Successo_ProdottoEsistente() {
		Long idDaEliminare = prodottoMela.getId();
		assertTrue(prodottoDao.trovaPerId(idDaEliminare).isPresent(), "Il prodotto dovrebbe esistere prima della cancellazione");
		assertDoesNotThrow(() -> servizioProdotto.elimina(idDaEliminare), "La cancellazione non dovrebbe lanciare eccezioni per un ID esistente");
		assertFalse(prodottoDao.trovaPerId(idDaEliminare).isPresent(), "Il prodotto dovrebbe essere eliminato dal DAO");
	}

	/**
	 * Test per l'eliminazione di un prodotto con un ID inesistente.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Elimina: Fallisce se ID non esiste")
	public void elimina_Fail_IdNonEsistente() {
		Long idInesistente = 999L;
		assertFalse(prodottoDao.trovaPerId(idInesistente).isPresent(), "Il prodotto non dovrebbe esistere prima del tentativo di eliminazione");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.elimina(idInesistente), "Dovrebbe lanciare eccezione quando si tenta di eliminare un ID inesistente");
		assertTrue(exception.getMessage().contains("Prodotto non trovato per ID: " + idInesistente), "Il messaggio dell'eccezione dovrebbe indicare che il prodotto non è stato trovato per ID: " + idInesistente);
	}

	/**
	 * Test per l'eliminazione di un prodotto con ID null.
	 * Attende il lancio di un'eccezione IllegalArgumentException.
	 */
	@Test
	@DisplayName("Elimina: Fallisce se ID è null")
	public void elimina_Fail_IdNull() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> servizioProdotto.elimina(null), "Dovrebbe lanciare eccezione quando si tenta di eliminare un ID null");
		assertTrue(exception.getMessage().contains("Prodotto non trovato per ID: null"), "Il messaggio dell'eccezione dovrebbe indicare che il prodotto non è stato trovato per ID: null");
	}
}