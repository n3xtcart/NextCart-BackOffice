package org.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.RichiestaLoginDTO;
import org.example.entity.Utente;
import org.example.repository.UtenteRepository;
import org.example.util.CodificatorePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class AuthResourceTest {

    @Inject
    UtenteRepository utenteRepository;

    @BeforeEach
    @Transactional
    void setUp() {

        utenteRepository.deleteAll();
        Utente admin = new Utente();
        admin.setEmail("admin@test.com");
        admin.setHashPassword(CodificatorePassword.calcolaHashPassword("password"));
        admin.setRuolo("admin");
        utenteRepository.persist(admin);

        Utente user = new Utente();
        user.setEmail("user@test.com");
        user.setHashPassword(CodificatorePassword.calcolaHashPassword("password"));
        user.setRuolo("user");
        utenteRepository.persist(user);
    }


    @Test
    void login_successo() {
        RichiestaLoginDTO credenziali = new RichiestaLoginDTO("admin@test.com", "password");
        given()
                .contentType("application/json")
                .body(credenziali)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("user.email", is("admin@test.com"))
                .body("user.ruolo", is("admin"));
    }

    @Test
    void login_utenteNonAdmin() {
        RichiestaLoginDTO credenziali = new RichiestaLoginDTO("user@test.com", "password");
        given()
                .contentType("application/json")
                .body(credenziali)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("error", is("Accesso negato. L'utente non ha i privilegi di amministratore."));
    }

    @Test
    void login_passwordErrata() {
        RichiestaLoginDTO credenziali = new RichiestaLoginDTO("admin@test.com", "wrongpass");
        given()
                .contentType("application/json")
                .body(credenziali)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("error", is("Credenziali non valide. (Password errata)"));
    }

    @Test
    void login_utenteInesistente() {
        RichiestaLoginDTO credenziali = new RichiestaLoginDTO("fake@test.com", "password");
        given()
                .contentType("application/json")
                .body(credenziali)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("error", is("Credenziali non valide. (Utente non trovato)"));
    }

    @Test
    void login_validazioneFallita() {
        RichiestaLoginDTO credenziali = new RichiestaLoginDTO("", " ");
        given()
                .contentType("application/json")
                .body(credenziali)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("error", containsString("Errore di validazione"));
    }

    @Test
    @TestSecurity(user = "admin@test.com", roles = "admin")
    void me_successo() {
        given()
                .when().get("/api/v1/auth/me")
                .then()
                .statusCode(200)
                .body("email", is("admin@test.com"))
                .body("ruolo", is("admin"));
    }

    @Test
    void me_nonAutorizzato() {
        given()
                .when().get("/api/v1/auth/me")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "user@test.com", roles = "user")
    void me_nonAdmin() {
        given()
                .when().get("/api/v1/auth/me")
                .then()
                .statusCode(403);
    }

    @Test
    void testOptionsEndpoints() {
        given().when().options("/api/v1/auth/login").then().statusCode(200);
        given().when().options("/api/v1/auth").then().statusCode(200);
    }
}