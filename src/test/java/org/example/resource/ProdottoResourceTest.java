package org.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import it.nextre.nextcart.dto.CategoriaDTO;
import it.nextre.nextcart.dto.ProdottoDTO;
import jakarta.inject.Inject; // Aggiungi
import jakarta.transaction.Transactional;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.repository.CategoriaRepository; // Aggiungi
import org.example.repository.ProdottoRepository; // Aggiungi
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional; // Aggiungi

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ProdottoResourceTest {

    private Long categoriaId;

    @Inject
    ProdottoRepository prodottoRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        prodottoRepository.deleteAll();
        categoriaRepository.deleteAll();
        Categoria cat = new Categoria(null, "Frutta Test", null);
        categoriaRepository.persist(cat);
        this.categoriaId = cat.getId();
    }

    private Long creaProdottoPerTest(String nome, Long catId) {

        Optional<Categoria> catOpt = categoriaRepository.findByIdOptional(catId);
        assertTrue(catOpt.isPresent());
        Categoria cat = catOpt.get();

        Prodotto p = new Prodotto(null, nome, null, BigDecimal.ONE, null, cat, "pz");
        prodottoRepository.persist(p);
        return p.getId();
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_successo() {
        CategoriaDTO catDto = new CategoriaDTO(categoriaId, "Frutta Test", null);

        ProdottoDTO dto = new ProdottoDTO();
        dto.setNome("Mela");
        dto.setQuantita(BigDecimal.TEN);
        dto.setTipologia("pz");
        dto.setPercorsoImmagine("/img.png");
        dto.setCategoriaDTO(catDto);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/prodotti")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", is("Mela"));
    }



    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testAggiorna_successo() {
        Long prodId = creaProdottoPerTest("Uva", categoriaId);
        CategoriaDTO catDto = new CategoriaDTO(categoriaId, null, null);


        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(prodId);
        dto.setNome("Uva Nera");
        dto.setQuantita(BigDecimal.ONE);
        dto.setTipologia("kg");
        dto.setCategoriaDTO(catDto);

        given()
                .pathParam("id", prodId)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/prodotti/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Uva Nera"));
    }


    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_conId() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(123L);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/prodotti")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaTutti() {
        creaProdottoPerTest("Mela", categoriaId);
        given()
                .when()
                .get("/api/v1/prodotti")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerId_successo() {
        Long prodId = creaProdottoPerTest("Pera", categoriaId);
        given()
                .pathParam("id", prodId)
                .when()
                .get("/api/v1/prodotti/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Pera"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerId_nonTrovato() {
        given()
                .pathParam("id", 9999L)
                .when()
                .get("/api/v1/prodotti/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerCategoria() {
        creaProdottoPerTest("Banana", categoriaId);
        given()
                .pathParam("categoriaId", categoriaId)
                .when()
                .get("/api/v1/prodotti/categoria/{categoriaId}")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].nome", is("Banana"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerCategoria_idNonValido() {
        given()
                .pathParam("categoriaId", 0L)
                .when()
                .get("/api/v1/prodotti/categoria/{categoriaId}")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testAggiorna_idMismatch() {
        Long prodId = creaProdottoPerTest("Test", categoriaId);
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(99L);
        given()
                .pathParam("id", prodId)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/prodotti/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testElimina_successo() {
        Long prodId = creaProdottoPerTest("Arancia", categoriaId);
        given()
                .pathParam("id", prodId)
                .when()
                .delete("/api/v1/prodotti/{id}")
                .then()
                .statusCode(204);
    }
}