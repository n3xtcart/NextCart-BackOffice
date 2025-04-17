package test;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.example.service.ServizioCategoria;
import org.example.service.ServizioProdotto;
import org.example.service.impl.ServizioProdottoImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestServizioProdotto {
	
	ServizioProdotto servizioProdotto;
	ProdottoDao prodottoDao;
	CategoriaDao categoriaDao;
	ServizioCategoria servizioCtaegoria;
	
	
	@BeforeAll
	void setUp() {
		
		prodottoDao = new InMemoryProdottoDao();
		categoriaDao = new InMemoryCategoriaDao();
		
		servizioProdotto = new ServizioProdottoImpl(categoriaDao, prodottoDao);
		Categoria cat = new Categoria(1L, "carne", "/percorsoImmagine");
		Prodotto prod = new Prodotto(null,  "vitello",  " ",  1000,  "/percorsoImmagine",  cat);

		prodottoDao.salva(prod);
		categoriaDao.salva(cat);
	}
	
	
	
	// TEST SALVA 
	
	@Test
	public void  salvaProdottoDTO() {
		CategoriaDTO categoriaDTO = new CategoriaDTO(1L, "carne", "/percorsoImmagine");
        ProdottoDTO prodottoDTO = new ProdottoDTO(
                null,
                "pollo",
                "fresco",
                500,
                "/immaginePollo.jpg",
                categoriaDTO
        );

        ProdottoDTO salvato = servizioProdotto.salva(prodottoDTO);

        assertNotNull(salvato.getId());
        assertEquals("pollo", salvato.getNome());
        assertEquals("fresco", salvato.getDescrizione());
        assertEquals(500, salvato.getQuantita());
        assertEquals("/immaginePollo.jpg", salvato.getPercorsoImmagine());
        assertEquals(1L, salvato.getCategoriaDTO().getId());
        assertEquals("carne", salvato.getCategoriaDTO().getNome());
		
	}
	
	@Test
	public void salvaProdottoDTOfail() {
		CategoriaDTO categoriaDTO = new CategoriaDTO(99L, "inesistente", "/nessunPercorso");
        ProdottoDTO prodottoDTO = new ProdottoDTO(
                null,
                "agnello",
                "tenero",
                300,
                "/immagineAgnello.jpg",
                categoriaDTO
        );

        IllegalArgumentException eccezione = assertThrows(
                IllegalArgumentException.class,
                () -> servizioProdotto.salva(prodottoDTO)
        );

        assertTrue(eccezione.getMessage().contains("Categoria non trovata per ID: 99"));
	}
	
	
	
	
	
	// TEST TROVA PER ID
	
	@Test
	public void trovaPerId() {
		Long idProdotto = 1L;  
	    Categoria categoria = new Categoria(1L, "carne", "/percorsoImmagine");
	    categoriaDao.salva(categoria);
	    
	    Prodotto prodotto = new Prodotto(idProdotto, "vitello", "tenero", 1000, "/percorsoImmagine", categoria);
	    prodottoDao.salva(prodotto);

	    Optional<ProdottoDTO> prodottoDTO = servizioProdotto.trovaPerId(idProdotto);

	    assertTrue(prodottoDTO.isPresent(), "Il prodotto dovrebbe essere presente");
	    ProdottoDTO prodottoRestituito = prodottoDTO.get();
	    assertEquals(idProdotto, prodottoRestituito.getId(), "Gli ID devono essere uguali");
	    assertEquals("vitello", prodottoRestituito.getNome(), "I nomi devono essere uguali");
	    assertEquals("carne", prodottoRestituito.getCategoriaDTO().getNome(), "Le categorie devono corrispondere");
		
	}
	
	@Test
	public void trovaPerIdFail() {
		Long idProdottoInesistente = 999L;  

	    Optional<ProdottoDTO> prodottoDTO = servizioProdotto.trovaPerId(idProdottoInesistente);

	    assertFalse(prodottoDTO.isPresent(), "Il prodotto non dovrebbe essere presente");
	}
	
	
	
	
	
	
	// TEST TROVA TUTTI
	
	@Test
	public void trovaTutti() {
		Categoria categoria = new Categoria(1L, "Carne", "/percorsoImmagine");
	    categoriaDao.salva(categoria);
	    
	    Prodotto prodotto2 = new Prodotto(null, "manzo", "robusto", 1000, "/percorsoImmagine", categoria);
	    prodottoDao.salva(prodotto2);

	    List<ProdottoDTO> prodottiDTO = servizioProdotto.trovaTutti();

	    assertEquals(3, prodottiDTO.size(), "La lista dei prodotti dovrebbe contenere 2 elementi");
	    assertEquals("vitello", prodottiDTO.get(1).getNome(), "Il primo prodotto dovrebbe essere Vitello");
	    assertEquals("manzo", prodottiDTO.get(0).getNome(), "Il secondo prodotto dovrebbe essere Manzo");
		
	}
	
	@Test
	public void trovaTuttiFail() {
	    prodottoDao.trovaTutti().forEach(p -> prodottoDao.elimina(p.getId())); 

	    List<ProdottoDTO> prodottiDTO = servizioProdotto.trovaTutti();

	    assertTrue(prodottiDTO.isEmpty(), "La lista dei prodotti dovrebbe essere vuota");
	}
	
	
	
	
	
	// TEST TROVA PER ID CATEGORIA
	
	@Test 
	public void trovaPerIdCategoria() {
		Categoria categoria = new Categoria(1L, "Carne", "/percorsoImmagine");
	    categoriaDao.salva(categoria);
	    
	    Prodotto prodotto2 = new Prodotto(null, "manzo", "robusto", 1000, "/percorsoImmagine", categoria);
	    prodottoDao.salva(prodotto2);

	    List<ProdottoDTO> prodottiDTO = servizioProdotto.trovaTutti();

	    assertEquals(2, prodottiDTO.size(), "La lista dei prodotti dovrebbe contenere 2 elementi");
	    assertEquals("vitello", prodottiDTO.get(0).getNome(), "Il primo prodotto dovrebbe essere Vitello");
	    assertEquals("manzo", prodottiDTO.get(1).getNome(), "Il secondo prodotto dovrebbe essere Manzo");
		
	}
	
	@Test 
	public void trovaPerIdCategoriaFail() {
		List<ProdottoDTO> prodottiDTO = servizioProdotto.trovaPerIdCategoria(99L);

	    assertTrue(prodottiDTO.isEmpty(), "La lista dei prodotti dovrebbe essere vuota per una categoria inesistente");
		
	}
	
	
	
	
	
	// TEST MODIFICA 
	@Test
	public void modifica() {
		Categoria categoria = new Categoria(1L, "Carne", "/carne.jpg");
	    categoriaDao.salva(categoria);

	    Prodotto prodotto = new Prodotto(null, "Vitello", "Taglio fresco", 1000, "/vitello.jpg", categoria);
	    prodotto = prodottoDao.salva(prodotto);

	    CategoriaDTO categoriaDTO = new CategoriaDTO(1L, "Carne", "/carne.jpg");
	    ProdottoDTO prodottoDTO = new ProdottoDTO(
	            prodotto.getId(), "Vitello Modificato", "Taglio premium", 800, "/nuovo.jpg", categoriaDTO);

	    ProdottoDTO modificato = servizioProdotto.modifica(prodottoDTO);

	    assertEquals("Vitello Modificato", modificato.getNome());
	    assertEquals("Taglio premium", modificato.getDescrizione());
	    assertEquals(800, modificato.getQuantita());
	    assertEquals("/nuovo.jpg", modificato.getPercorsoImmagine());
		
	}
	
	@Test
	public void modificaFail() {
		Categoria categoria = new Categoria(1L, "Carne", "/carne.jpg");
	    categoriaDao.salva(categoria);

	    CategoriaDTO categoriaDTO = new CategoriaDTO(1L, "Carne", "/carne.jpg");
	    ProdottoDTO prodottoDTO = new ProdottoDTO(
	            999L, "Fantasma", "Non esiste", 10, "/fantasma.jpg", categoriaDTO);

	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
	        servizioProdotto.modifica(prodottoDTO);
	    });

	    assertEquals("Prodotto non trovato per ID: 999", ex.getMessage());
		
	}
	
	
	
	
	

	// TEST ELIMINA
	
	@Test
	public void elimina() {
		Categoria categoria = new Categoria(1L, "Carne", "/carne.jpg");
	    categoriaDao.salva(categoria);

	    Prodotto prodotto = new Prodotto(null, "Pollo", "Fresco", 500, "/pollo.jpg", categoria);
	    prodotto = prodottoDao.salva(prodotto);

	    Long idProdotto = prodotto.getId();

	    assertDoesNotThrow(() -> servizioProdotto.elimina(idProdotto));
	    assertTrue(prodottoDao.trovaPerId(prodotto.getId()).isEmpty());
		
	}
	
	@Test
	public void eliminaFail() {
		long idInesistente = 999L;

	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
	        servizioProdotto.elimina(idInesistente);
	    });

	    assertEquals("Prodotto non trovato per ID: 999", ex.getMessage());
		
	}
}
