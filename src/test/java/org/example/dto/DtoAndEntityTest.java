package org.example.dto;

import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.entity.Utente;
import org.example.exception.mapper.ErrorMessage;
import org.example.resource.AuthResource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class DtoAndEntityTest {

    @Test
    void testRichiestaLoginDTO() {
        RichiestaLoginDTO dto1 = new RichiestaLoginDTO("test@test.com", "pass");
        RichiestaLoginDTO dto2 = new RichiestaLoginDTO();
        dto2.setEmail("test@test.com");
        dto2.setPassword("pass");

        assertEquals("test@test.com", dto1.getEmail());
        assertEquals("pass", dto1.getPassword());
        assertEquals(dto1.getEmail(), dto2.getEmail());
    }

    @Test
    void testUtenteDTO() {
        UtenteDTO dto1 = new UtenteDTO(1L, "user@test.com", "admin");
        UtenteDTO dto2 = new UtenteDTO();
        dto2.setId(1L);
        dto2.setEmail("user@test.com");
        dto2.setRuolo("admin");

        assertEquals(1L, dto1.getId());
        assertEquals("user@test.com", dto1.getEmail());
        assertEquals("admin", dto1.getRuolo());
        assertEquals(dto1.getId(), dto2.getId());
    }

    @Test
    void testErrorMessage() {
        ErrorMessage msg1 = new ErrorMessage("Error 1");
        ErrorMessage msg2 = new ErrorMessage();
        msg2.setError("Error 1");

        assertEquals("Error 1", msg1.getError());
        assertEquals(msg1.getError(), msg2.getError());
    }

    @Test
    void testCategoriaEntity() {
        Categoria c1 = new Categoria(1L, "Frutta", "/img/frutta.png");
        Categoria c2 = new Categoria(1L, "Frutta", "/img/frutta.png");
        Categoria c3 = new Categoria(2L, "Verdura", "/img/verdura.png");
        Categoria c4 = new Categoria();
        c4.setId(1L);
        c4.setNome("Frutta");
        c4.setPercorsoImmagine("/img/frutta.png");

        assertEquals(1L, c1.getId());
        assertEquals("Frutta", c1.getNome());
        assertEquals("/img/frutta.png", c1.getPercorsoImmagine());

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, new Object());
        assertTrue(c1.toString().contains("Frutta"));
    }

    @Test
    void testProdottoEntity() {
        Categoria cat = new Categoria(1L, "Frutta", null);
        Prodotto p1 = new Prodotto(1L, "Mela", "desc", BigDecimal.TEN, "/img/mela.png", cat, "kg");
        Prodotto p2 = new Prodotto(1L, "Mela", "desc", BigDecimal.TEN, "/img/mela.png", cat, "kg");
        Prodotto p3 = new Prodotto(2L, "Pera", "desc2", BigDecimal.ONE, "/img/pera.png", cat, "kg");
        Prodotto p4 = new Prodotto();
        p4.setId(1L);
        p4.setNome("Mela");
        p4.setDescrizione("desc");
        p4.setQuantita(BigDecimal.TEN);
        p4.setPercorsoImmagine("/img/mela.png");
        p4.setCategoria(cat);
        p4.setTipologia("kg");

        assertEquals(1L, p1.getId());
        assertEquals("Mela", p1.getNome());
        assertEquals("desc", p1.getDescrizione());

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());
        assertTrue(p1.toString().contains("Mela"));
    }

    @Test
    void testUtenteEntity() {
        Utente u1 = new Utente(1L, "user@test.com", "hash", "admin");
        Utente u2 = new Utente(1L, "user@test.com", "hash", "admin");
        Utente u3 = new Utente(2L, "user2@test.com", "hash2", "user");
        Utente u4 = new Utente();
        u4.setId(1L);
        u4.setEmail("user@test.com");
        u4.setHashPassword("hash");
        u4.setRuolo("admin");

        assertEquals(1L, u1.getId());
        assertEquals("user@test.com", u1.getEmail());
        assertEquals("hash", u1.getHashPassword());
        assertEquals("admin", u1.getRuolo());

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1, u3);
        assertNotEquals(u1, null);
        assertNotEquals(u1, new Object());
        assertTrue(u1.toString().contains("user@test.com"));
    }

    @Test
    void testLoginResponse() {
        UtenteDTO user = new UtenteDTO(1L, "user@test.com", "admin");
        AuthResource.LoginResponse response = new AuthResource.LoginResponse("token123", user);
        assertEquals("token123", response.token);
        assertEquals(user, response.user);
    }
}