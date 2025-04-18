package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.dto.CategoriaDTO;
import org.example.dto.ProdottoDTO;
import org.example.dto.RichiestaLoginDTO;
import org.example.dto.UtenteDTO;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.entity.Utente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per verificare il funzionamento dei metodi comuni delle classi Entity e DTO.
 * I test verificano il comportamento dei costruttori, dei getter, dei setter,
 * dei metodi equals, hashCode e toString.
 */
@DisplayName("Test Metodi Comuni di Entity e DTO")
class TestEntitiesAndDTOs {

    /**
     * Verifica il corretto funzionamento della classe Categoria.
     * Testa sia il costruttore di default che quello parametrico, oltre ai metodi setter,
     * getter, equals, hashCode e toString.
     */
    @Test
    @DisplayName("Test Categoria Entity (Costruttori, Getter, Setter, Equals, HashCode, ToString)")
    void testCategoriaEntity() {

        // Test del costruttore di default e dei metodi setter
        Categoria cEmpty = new Categoria();
        assertNull(cEmpty.getId(), "ID deve essere null per istanza non inizializzata");
        assertNull(cEmpty.getNome(), "Nome deve essere null per istanza non inizializzata");
        assertNull(cEmpty.getPercorsoImmagine(), "Percorso immagine deve essere null per istanza non inizializzata");
        cEmpty.setId(10L);
        cEmpty.setNome("Nome Cat 10");
        cEmpty.setPercorsoImmagine("/img/cat10.png");
        assertEquals(10L, cEmpty.getId(), "L'ID deve essere 10");
        assertEquals("Nome Cat 10", cEmpty.getNome(), "Il nome deve essere 'Nome Cat 10'");
        assertEquals("/img/cat10.png", cEmpty.getPercorsoImmagine(), "Il percorso immagine deve essere '/img/cat10.png'");

        // Test del costruttore parametrico
        Categoria cFull = new Categoria(1L, "Nome Cat 1", "/img/cat1.png");
        assertEquals(1L, cFull.getId(), "L'ID deve essere 1");
        assertEquals("Nome Cat 1", cFull.getNome(), "Il nome deve essere 'Nome Cat 1'");
        assertEquals("/img/cat1.png", cFull.getPercorsoImmagine(), "Il percorso immagine deve essere '/img/cat1.png'");

        // Test dei metodi equals e hashCode
        Categoria c1 = new Categoria(1L, "Nome Cat 1", "/img/cat1.png");
        Categoria c1Dup = new Categoria(1L, "Nome Cat 1", "/img/cat1_alt.png");
        Categoria c2 = new Categoria(2L, "Nome Cat 1", "/img/cat1.png");
        Categoria c3 = new Categoria(1L, "Nome Cat 2", "/img/cat1.png");
        Categoria cNull = null;
        Object otherObj = new Object();

        assertTrue(c1.equals(c1), "Un oggetto deve essere uguale a se stesso");
        assertTrue(c1.equals(c1Dup), "Oggetti con stesso ID e nome sono considerati uguali");
        assertFalse(c1.equals(c2), "Oggetti con ID diversi non devono essere uguali");
        assertFalse(c1.equals(c3), "Oggetti con nomi diversi non devono essere uguali");
        assertFalse(c1.equals(cNull), "L'equals con null deve restituire false");
        assertFalse(c1.equals(otherObj), "L'equals con un oggetto di tipo differente deve restituire false");

        assertEquals(c1.hashCode(), c1Dup.hashCode(), "HashCode deve essere uguale per oggetti uguali");

        // Test del metodo toString
        String toString = c1.toString();
        assertNotNull(toString, "Il metodo toString non deve restituire null");
        assertTrue(toString.contains("id=1"), "La stringa toString deve contenere l'ID");
        assertTrue(toString.contains("nome='Nome Cat 1'"), "La stringa toString deve contenere il nome");
        assertTrue(toString.contains("percorsoImmagine='/img/cat1.png'"), "La stringa toString deve contenere il percorso immagine");
    }

    /**
     * Verifica il corretto funzionamento della classe Prodotto.
     * Testa sia il costruttore di default che quello parametrico, oltre ai metodi setter,
     * getter, equals, hashCode e toString.
     */
    @Test
    @DisplayName("Test Prodotto Entity (Costruttori, Getter, Setter, Equals, HashCode, ToString)")
    void testProdottoEntity() {
        Categoria cat = new Categoria(5L, "Categoria Prod", "/catprod.png");

        // Test del costruttore di default e dei metodi setter
        Prodotto pEmpty = new Prodotto();
        assertNull(pEmpty.getId(), "ID deve essere null per istanza non inizializzata");
        assertNull(pEmpty.getNome(), "Nome deve essere null per istanza non inizializzata");
        assertNull(pEmpty.getDescrizione(), "Descrizione deve essere null per istanza non inizializzata");
        assertEquals(0, pEmpty.getQuantita(), "Quantità iniziale deve essere 0");
        assertNull(pEmpty.getPercorsoImmagine(), "Percorso immagine deve essere null per istanza non inizializzata");
        assertNull(pEmpty.getCategoria(), "Categoria deve essere null per istanza non inizializzata");
        pEmpty.setId(20L);
        pEmpty.setNome("Nome Prod 20");
        pEmpty.setDescrizione("Desc Prod 20");
        pEmpty.setQuantita(150);
        pEmpty.setPercorsoImmagine("/img/prod20.png");
        pEmpty.setCategoria(cat);
        assertEquals(20L, pEmpty.getId(), "L'ID deve essere 20");
        assertEquals("Nome Prod 20", pEmpty.getNome(), "Il nome deve essere 'Nome Prod 20'");
        assertEquals("Desc Prod 20", pEmpty.getDescrizione(), "La descrizione deve essere 'Desc Prod 20'");
        assertEquals(150, pEmpty.getQuantita(), "La quantità deve essere 150");
        assertEquals("/img/prod20.png", pEmpty.getPercorsoImmagine(), "Il percorso immagine deve essere '/img/prod20.png'");
        assertEquals(cat, pEmpty.getCategoria(), "La categoria deve corrispondere all'oggetto 'cat'");

        // Test del costruttore parametrico
        Prodotto pFull = new Prodotto(2L, "Nome Prod 2", "Desc Prod 2", 50, "/img/prod2.png", cat);
        assertEquals(2L, pFull.getId(), "L'ID deve essere 2");
        assertEquals("Nome Prod 2", pFull.getNome(), "Il nome deve essere 'Nome Prod 2'");
        assertEquals(cat, pFull.getCategoria(), "La categoria deve corrispondere all'oggetto 'cat'");

        // Test dei metodi equals e hashCode
        Prodotto p1 = new Prodotto(2L, "Nome Prod 2", "Desc 1", 10, "/img1", cat);
        Prodotto p1Dup = new Prodotto(2L, "Nome Prod 2", "Desc 2", 20, "/img2", null);
        Prodotto p2 = new Prodotto(3L, "Nome Prod 2", "Desc 1", 10, "/img1", cat);
        Prodotto p3 = new Prodotto(2L, "Nome Prod 3", "Desc 1", 10, "/img1", cat);
        Prodotto pNull = null;
        Object otherObj = new Object();

        assertTrue(p1.equals(p1), "Un oggetto deve essere uguale a se stesso");
        assertTrue(p1.equals(p1Dup), "Oggetti con stesso ID e nome devono essere uguali");
        assertFalse(p1.equals(p2), "Oggetti con ID differenti non devono essere uguali");
        assertFalse(p1.equals(p3), "Oggetti con nome differente non devono essere uguali");
        assertFalse(p1.equals(pNull), "Il confronto con null deve restituire false");
        assertFalse(p1.equals(otherObj), "Il confronto con un oggetto di tipo differente deve restituire false");

        assertEquals(p1.hashCode(), p1Dup.hashCode(), "HashCode deve essere uguale per oggetti uguali");

        // Test del metodo toString
        String toString = p1.toString();
        assertNotNull(toString, "Il metodo toString non deve restituire null");
        assertTrue(toString.contains("id=2"), "La stringa toString deve contenere l'ID");
        assertTrue(toString.contains("nome='Nome Prod 2'"), "La stringa toString deve contenere il nome");
        assertTrue(toString.contains("categoria=" + cat.getNome()), "La stringa toString deve contenere il nome della categoria");
        assertTrue(toString.contains("quantita=10"), "La stringa toString deve contenere la quantità");

        p1.setCategoria(null);
        String toStringNullCat = p1.toString();
        assertTrue(toStringNullCat.contains("categoria=null"), "La stringa toString deve gestire correttamente una categoria null");
    }

    /**
     * Verifica il corretto funzionamento della classe Utente.
     * Testa sia il costruttore di default che quello parametrico, oltre ai metodi setter,
     * getter, equals, hashCode e toString.
     */
    @Test
    @DisplayName("Test Utente Entity (Costruttori, Getter, Setter, Equals, HashCode, ToString)")
    void testUtenteEntity() {

        // Test del costruttore di default e dei metodi setter
        Utente uEmpty = new Utente();
        assertNull(uEmpty.getId(), "ID deve essere null per istanza non inizializzata");
        assertNull(uEmpty.getEmail(), "Email deve essere null per istanza non inizializzata");
        assertNull(uEmpty.getHashPassword(), "HashPassword deve essere null per istanza non inizializzata");
        assertNull(uEmpty.getRuolo(), "Ruolo deve essere null per istanza non inizializzata");
        uEmpty.setId(30L);
        uEmpty.setEmail("utente30@example.com");
        uEmpty.setHashPassword("hash30");
        uEmpty.setRuolo("RUOLO30");
        assertEquals(30L, uEmpty.getId(), "L'ID deve essere 30");
        assertEquals("utente30@example.com", uEmpty.getEmail(), "L'email deve essere 'utente30@example.com'");
        assertEquals("hash30", uEmpty.getHashPassword(), "L'hash della password deve essere 'hash30'");
        assertEquals("RUOLO30", uEmpty.getRuolo(), "Il ruolo deve essere 'RUOLO30'");

        // Test del costruttore parametrico
        Utente uFull = new Utente(3L, "utente3@example.com", "hash3", "RUOLO3");
        assertEquals(3L, uFull.getId(), "L'ID deve essere 3");
        assertEquals("utente3@example.com", uFull.getEmail(), "L'email deve essere 'utente3@example.com'");
        assertEquals("hash3", uFull.getHashPassword(), "L'hash della password deve essere 'hash3'");
        assertEquals("RUOLO3", uFull.getRuolo(), "Il ruolo deve essere 'RUOLO3'");

        // Test dei metodi equals e hashCode
        Utente u1 = new Utente(3L, "utente3@example.com", "hash1", "RUOLO1");
        Utente u1Dup = new Utente(3L, "utente3@example.com", "hash2", "RUOLO2");
        Utente u2 = new Utente(4L, "utente3@example.com", "hash1", "RUOLO1");
        Utente u3 = new Utente(3L, "utente4@example.com", "hash1", "RUOLO1");
        Utente uNull = null;
        Object otherObj = new Object();

        assertTrue(u1.equals(u1), "Un oggetto deve essere uguale a se stesso");
        assertTrue(u1.equals(u1Dup), "Oggetti con stesso ID e email devono essere uguali");
        assertFalse(u1.equals(u2), "Oggetti con ID differenti non devono essere uguali");
        assertFalse(u1.equals(u3), "Oggetti con email differenti non devono essere uguali");
        assertFalse(u1.equals(uNull), "Il confronto con null deve restituire false");
        assertFalse(u1.equals(otherObj), "Il confronto con un oggetto di tipo differente deve restituire false");

        assertEquals(u1.hashCode(), u1Dup.hashCode(), "HashCode deve essere uguale per oggetti uguali");

        // Test del metodo toString
        String toString = u1.toString();
        assertNotNull(toString, "Il metodo toString non deve restituire null");
        assertTrue(toString.contains("id=3"), "La stringa toString deve contenere l'ID");
        assertTrue(toString.contains("email='utente3@example.com'"), "La stringa toString deve contenere l'email");
        assertTrue(toString.contains("ruolo='RUOLO1'"), "La stringa toString deve contenere il ruolo");
        assertFalse(toString.contains(u1.getHashPassword()), "La stringa toString non deve contenere l'hash della password");
    }

    /**
     * Verifica il corretto funzionamento della classe CategoriaDTO.
     * Testa il costruttore e i metodi getter.
     */
    @Test
    @DisplayName("Test CategoriaDTO (Costruttore, Getter)")
    void testCategoriaDTO() {
        CategoriaDTO dto = new CategoriaDTO(1L, "Nome DTO", "/dto.png");
        assertEquals(1L, dto.getId(), "L'ID deve essere 1");
        assertEquals("Nome DTO", dto.getNome(), "Il nome deve essere 'Nome DTO'");
        assertEquals("/dto.png", dto.getPercorsoImmagine(), "Il percorso immagine deve essere '/dto.png'");
    }

    /**
     * Verifica il corretto funzionamento della classe ProdottoDTO.
     * Testa il costruttore e i metodi getter.
     */
    @Test
    @DisplayName("Test ProdottoDTO (Costruttore, Getter)")
    void testProdottoDTO() {
        CategoriaDTO catDto = new CategoriaDTO(5L, "Cat DTO", "/cat.png");
        ProdottoDTO dto = new ProdottoDTO(10L, "Prod DTO", "Desc DTO", 50, "/prod.png", catDto);
        assertEquals(10L, dto.getId(), "L'ID deve essere 10");
        assertEquals("Prod DTO", dto.getNome(), "Il nome deve essere 'Prod DTO'");
        assertEquals("Desc DTO", dto.getDescrizione(), "La descrizione deve essere 'Desc DTO'");
        assertEquals(50, dto.getQuantita(), "La quantità deve essere 50");
        assertEquals("/prod.png", dto.getPercorsoImmagine(), "Il percorso immagine deve essere '/prod.png'");
        assertNotNull(dto.getCategoriaDTO(), "CategoriaDTO non deve essere null");
        assertEquals(5L, dto.getCategoriaDTO().getId(), "L'ID della categoria deve essere 5");
        assertEquals("Cat DTO", dto.getCategoriaDTO().getNome(), "Il nome della categoria deve essere 'Cat DTO'");
        assertEquals("/cat.png", dto.getCategoriaDTO().getPercorsoImmagine(), "Il percorso immagine della categoria deve essere '/cat.png'");
    }

    /**
     * Verifica il corretto funzionamento della classe UtenteDTO.
     * Testa il costruttore e i metodi getter.
     */
    @Test
    @DisplayName("Test UtenteDTO (Costruttore, Getter)")
    void testUtenteDTO() {
        UtenteDTO dto = new UtenteDTO(15L, "utente.dto@example.com", "ADMIN");
        assertEquals(15L, dto.getId(), "L'ID deve essere 15");
        assertEquals("utente.dto@example.com", dto.getEmail(), "L'email deve essere 'utente.dto@example.com'");
        assertEquals("ADMIN", dto.getRuolo(), "Il ruolo deve essere 'ADMIN'");
    }

    /**
     * Verifica il corretto funzionamento della classe RichiestaLoginDTO.
     * Testa il costruttore e i metodi getter.
     */
    @Test
    @DisplayName("Test RichiestaLoginDTO (Costruttore, Getter)")
    void testRichiestaLoginDTO() {
        RichiestaLoginDTO dto = new RichiestaLoginDTO("login@test.com", "secretPass");
        assertEquals("login@test.com", dto.getEmail(), "L'email deve essere 'login@test.com'");
        assertEquals("secretPass", dto.getPassword(), "La password deve essere 'secretPass'");
    }
}