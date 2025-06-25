package org.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import it.nextre.nextcart.dto.CategoriaDTO;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class CategoriaResourceTest {

    @Inject
    ProdottoRepository prodottoRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        prodottoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    private Long creaCategoriaPerTest(String nome) {
        Categoria cat = new Categoria(null, nome, null);
        categoriaRepository.persist(cat);
        return cat.getId();
    }



    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    @Transactional
    void testElimina_conflitto() {
        Categoria cat = new Categoria(null, "In Uso", null);
        categoriaRepository.persist(cat);
        Prodotto p = new Prodotto(null, "Latte", null, BigDecimal.ONE, null, cat, "l");
        prodottoRepository.persist(p);

        given()
                .pathParam("id", cat.getId())
                .when().delete("/api/v1/categorie/{id}")
                .then()
                .statusCode(409);
    }


    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaTutte() {
        creaCategoriaPerTest("Frutta");
        given()
                .when().get("/api/v1/categorie")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].nome", is("Frutta"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerId_successo() {
        Long id = creaCategoriaPerTest("Verdura");
        given()
                .pathParam("id", id)
                .when().get("/api/v1/categorie/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Verdura"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testTrovaPerId_nonTrovata() {
        given()
                .pathParam("id", 9999L)
                .when().get("/api/v1/categorie/{id}")
                .then()
                .statusCode(404)
                .body("error", containsString("non trovata con ID: 9999"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_successo() {
        CategoriaDTO dto = new CategoriaDTO(null, "Latticini", "/img.png");
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/v1/categorie")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("nome", is("Latticini"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_conflitto() {
        creaCategoriaPerTest("Dolci");
        CategoriaDTO dto = new CategoriaDTO(null, "Dolci", null);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/v1/categorie")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_conId() {
        CategoriaDTO dto = new CategoriaDTO(123L, "Test", null);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/v1/categorie")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testCrea_validazioneFallita() {
        CategoriaDTO dto = new CategoriaDTO(null, "", null); // nome vuoto
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/v1/categorie")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testAggiorna_successo() {
        Long id = creaCategoriaPerTest("Bevande");
        CategoriaDTO dto = new CategoriaDTO(id, "Bevande Fredde", null);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .pathParam("id", id)
                .when().put("/api/v1/categorie/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Bevande Fredde"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testAggiorna_idMismatch() {
        Long id = creaCategoriaPerTest("Test");
        CategoriaDTO dto = new CategoriaDTO(99L, "Nuovo Nome", null);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .pathParam("id", id)
                .when().put("/api/v1/categorie/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testAggiorna_nonTrovata() {
        CategoriaDTO dto = new CategoriaDTO(999L, "Test", null);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .pathParam("id", 999L)
                .when().put("/api/v1/categorie/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "testuser", roles = "admin")
    void testElimina_successo() {
        Long id = creaCategoriaPerTest("Da eliminare");
        given()
                .pathParam("id", id)
                .when().delete("/api/v1/categorie/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void testEndpoint_nonAutorizzato() {
        given().when().get("/api/v1/categorie").then().statusCode(401);
    }
}