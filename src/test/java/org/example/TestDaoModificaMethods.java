package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.example.dao.CategoriaDao;
import org.example.dao.ProdottoDao;
import org.example.dao.impl.InMemoryCategoriaDao;
import org.example.dao.impl.InMemoryProdottoDao;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneAccessoDati;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

/**
 * Classe di test per verificare il funzionamento dei metodi di modifica nei DAO.
 * Contiene test per le implementazioni in memoria di CategoriaDao e ProdottoDao.
 */
@DisplayName("Test Metodi DAO.modifica()")
class TestDaoModificaMethods {

    /**
     * Classe annidata per testare i metodi di modifica di InMemoryCategoriaDao.
     */
    @Nested
    @DisplayName("Test InMemoryCategoriaDao.modifica")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    class CategoriaModificaTests {
        CategoriaDao categoriaDao;
        Categoria catFrutta;
        Categoria catVerdura;

        /**
         * Inizializza il DAO e crea alcune categorie di esempio prima di ogni test.
         */
        @BeforeEach
        void setUp() {
            categoriaDao = new InMemoryCategoriaDao();
            catFrutta = categoriaDao.salva(new Categoria(null, "Frutta", "/frutta.png"));
            catVerdura = categoriaDao.salva(new Categoria(null, "Verdura", "/verdura.png"));
        }

        /**
         * Verifica che la modifica di una categoria esistente aggiorni correttamente i campi.
         */
        @Test
        @DisplayName("Modifica Categoria Esistente con Successo (Aggiorna Campi)")
        void modifica_successoAggiornamento() {
            Categoria modifiche = new Categoria(catFrutta.getId(), "Frutta Modificata", "/frutta_mod.png");

            Categoria risultato = categoriaDao.modifica(modifiche);

            assertSame(catFrutta, risultato, "Dovrebbe restituire la stessa istanza modificata");
            assertEquals("Frutta Modificata", risultato.getNome());
            assertEquals("/frutta_mod.png", risultato.getPercorsoImmagine());

            assertEquals("Frutta Modificata", catFrutta.getNome());
            assertEquals("/frutta_mod.png", catFrutta.getPercorsoImmagine());

            Categoria trovata = categoriaDao.trovaPerId(catFrutta.getId()).orElseThrow();
            assertEquals("Frutta Modificata", trovata.getNome());
            assertEquals("/frutta_mod.png", trovata.getPercorsoImmagine());
            assertSame(catFrutta, trovata);
        }

        /**
         * Verifica che la modifica di una categoria con un nome duplicato lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica Categoria Cambiando Nome in uno Duplicato Lancia Eccezione")
        void modifica_nomeDuplicato_lanciaEccezione() {
            Categoria modifiche = new Categoria(catFrutta.getId(), catVerdura.getNome(), catFrutta.getPercorsoImmagine());

            EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
                categoriaDao.modifica(modifiche);
            });
            assertTrue(ex.getMessage().contains("è già usato da un'altra categoria"));

            assertEquals("Frutta", catFrutta.getNome());
        }

        /**
         * Verifica che la modifica di una categoria non esistente lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica Categoria Non Esistente Lancia IllegalArgumentException")
        void modifica_nonTrovata_lanciaEccezione() {
            Categoria modifiche = new Categoria(999L, "Fantasma", "/fantasma.png");
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                categoriaDao.modifica(modifiche);
            });
            assertTrue(ex.getMessage().contains("Categoria non trovata per ID: 999"));
        }

        /**
         * Verifica che la modifica con input null o ID null lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica con Input Null o ID Null Lancia IllegalArgumentException")
        void modifica_inputNull_lanciaEccezione() {
            IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
                categoriaDao.modifica(null);
            });
            assertTrue(ex1.getMessage().contains("Categoria o ID della categoria non possono essere nulli"));

            Categoria senzaId = new Categoria(null, "Senza ID", "/no_id.png");
            IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
                categoriaDao.modifica(senzaId);
            });
            assertTrue(ex2.getMessage().contains("Categoria o ID della categoria non possono essere nulli"));
        }

        /**
         * Verifica che la modifica senza cambiare il nome non lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica senza cambiare nome non lancia eccezione per nome")
        void modifica_senzaCambioNome_successo() {

            Categoria modifiche = new Categoria(catFrutta.getId(), catFrutta.getNome(), "/frutta_nuovo_path.png");
            assertDoesNotThrow(() -> categoriaDao.modifica(modifiche));
            assertEquals("/frutta_nuovo_path.png", catFrutta.getPercorsoImmagine());
        }
    }

    /**
     * Classe annidata per testare i metodi di modifica di InMemoryProdottoDao.
     */
    @Nested
    @DisplayName("Test InMemoryProdottoDao.modifica")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    class ProdottoModificaTests {
        ProdottoDao prodottoDao;
        CategoriaDao categoriaDao;
        Categoria cat1;
        Categoria cat2;
        Prodotto prod1;
        Prodotto prod2;

        /**
         * Inizializza i DAO e crea alcune categorie e prodotti di esempio prima di ogni test.
         */
        @BeforeEach
        void setUp() {
            categoriaDao = new InMemoryCategoriaDao();
            prodottoDao = new InMemoryProdottoDao();

            cat1 = categoriaDao.salva(new Categoria(null, "Cat 1", "/c1.png"));
            cat2 = categoriaDao.salva(new Categoria(null, "Cat 2", "/c2.png"));
            prod1 = prodottoDao.salva(new Prodotto(null, "Prod 1", "Desc 1", 10, "/p1.png", cat1));
            prod2 = prodottoDao.salva(new Prodotto(null, "Prod 2", "Desc 2", 20, "/p2.png", cat1));
        }

        /**
         * Verifica che la modifica di un prodotto esistente aggiorni correttamente i campi e la categoria.
         */
        @Test
        @DisplayName("Modifica Prodotto Esistente con Successo (Aggiorna Campi e Categoria)")
        void modifica_successoAggiornamentoCompleto() {
            Prodotto modifiche = new Prodotto(prod1.getId(), "Prod 1 Modificato", "Desc 1 Mod", 15, "/p1_mod.png", cat2);

            Prodotto risultato = prodottoDao.modifica(modifiche);

            assertSame(prod1, risultato, "Dovrebbe restituire la stessa istanza modificata");
            assertEquals("Prod 1 Modificato", risultato.getNome());
            assertEquals("Desc 1 Mod", risultato.getDescrizione());
            assertEquals(15, risultato.getQuantita());
            assertEquals("/p1_mod.png", risultato.getPercorsoImmagine());
            assertEquals(cat2.getId(), risultato.getCategoria().getId(), "La categoria dovrebbe essere aggiornata a Cat 2");

            assertEquals("Prod 1 Modificato", prod1.getNome());
            assertEquals(cat2, prod1.getCategoria());

            Prodotto trovato = prodottoDao.trovaPerId(prod1.getId()).orElseThrow();
            assertEquals("Prod 1 Modificato", trovato.getNome());
            assertEquals(cat2.getId(), trovato.getCategoria().getId());
            assertSame(prod1, trovato);
        }

        /**
         * Verifica che la modifica di un prodotto non esistente lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica Prodotto Non Esistente Lancia IllegalArgumentException")
        void modifica_nonTrovato_lanciaEccezione() {
            Prodotto modifiche = new Prodotto(999L, "Fantasma", "Desc F", 5, "/f.png", cat1);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                prodottoDao.modifica(modifiche);
            });
            assertTrue(ex.getMessage().contains("Prodotto non trovato per ID: 999"));
        }

        /**
         * Verifica che la modifica con input null o ID null lanci un'eccezione.
         */
        @Test
        @DisplayName("Modifica con Input Null o ID Null Lancia IllegalArgumentException")
        void modifica_inputNull_lanciaEccezione() {
            IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
                prodottoDao.modifica(null);
            });
            assertTrue(ex1.getMessage().contains("Prodotto o ID del prodotto non possono essere nulli"));

            Prodotto senzaId = new Prodotto(null, "Senza ID", "Desc", 1, "/no.png", cat1);
            IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
                prodottoDao.modifica(senzaId);
            });
            assertTrue(ex2.getMessage().contains("Prodotto o ID del prodotto non possono essere nulli"));
        }

        /**
         * Verifica che i prodotti con categoria null siano ignorati durante la ricerca per ID categoria.
         */
        @Test
        @DisplayName("TrovaPerIdCategoria ignora prodotti con categoria null")
        void trovaPerIdCategoria_ignoraProdottiCategoriaNull() {
            prod1.setCategoria(null);

            List<Prodotto> trovatiCat1 = prodottoDao.trovaPerIdCategoria(cat1.getId());

            assertEquals(1, trovatiCat1.size());
            assertEquals(prod2.getId(), trovatiCat1.get(0).getId());
        }

        /**
         * Verifica che la ricerca per ID categoria null restituisca una lista vuota.
         */
        @Test
        @DisplayName("TrovaPerIdCategoria con ID Null restituisce lista vuota")
        void trovaPerIdCategoria_idNull_listaVuota() {
            List<Prodotto> risultato = prodottoDao.trovaPerIdCategoria(null);
            assertNotNull(risultato);
            assertTrue(risultato.isEmpty());
        }

        /**
         * Verifica che il salvataggio di un prodotto non esistente lanci un'eccezione.
         */
        @Test
        @DisplayName("DAO Salva: Aggiornamento Prodotto Non Esistente Lancia Eccezione")
        void daoSalva_updateNonEsistente_lanciaEccezione() {
            Prodotto fantasmaUpdate = new Prodotto(999L, "Update Fantasma", "D", 1, "/f.png", cat1);
            EccezioneAccessoDati ex = assertThrows(EccezioneAccessoDati.class, () -> {
                prodottoDao.salva(fantasmaUpdate);
            });
            assertTrue(ex.getMessage().contains("Impossibile aggiornare prodotto non esistente"));
        }

        /**
         * Verifica che l'eliminazione di un prodotto non esistente lanci un'eccezione.
         */
        @Test
        @DisplayName("DAO Elimina Prodotto Non Esistente Lancia Eccezione")
        void daoElimina_nonEsistente_lanciaEccezione() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                prodottoDao.elimina(999L);
            });
            assertTrue(ex.getMessage().contains("Prodotto non trovato per ID: 999"));
        }

        /**
         * Verifica che l'eliminazione di un prodotto con ID null lanci un'eccezione.
         */
        @Test
        @DisplayName("DAO Elimina Prodotto ID Null Lancia Eccezione")
        void daoElimina_idNull_lanciaEccezione() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                prodottoDao.elimina(null);
            });
            assertTrue(ex.getMessage().contains("Prodotto non trovato per ID: null"));
        }
    }
}