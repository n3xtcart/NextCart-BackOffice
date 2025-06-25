package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import org.example.dto.RichiestaLoginDTO;
import org.example.dto.UtenteDTO;
import org.example.entity.Utente;
import org.example.exception.EccezioneAutenticazione;
import org.example.repository.UtenteRepository;
import org.example.service.impl.ServizioAutenticazioneImpl;
import org.example.util.CodificatorePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@QuarkusTest
@DisplayName("ServizioAutenticazioneImpl Tests")
class ServizioAutenticazioneImplTest {


    @Inject
    ServizioAutenticazioneImpl servizioAutenticazione;


    @InjectMock
    UtenteRepository utenteRepository;

    private Utente utenteDiTest;
    private final String passwordInChiaro = "password123";

    @BeforeEach
    void setUp() {

        utenteDiTest = new Utente();
        utenteDiTest.setId(1L);
        utenteDiTest.setEmail("test@example.com");
        utenteDiTest.setRuolo("admin");
        utenteDiTest.setHashPassword(CodificatorePassword.calcolaHashPassword(passwordInChiaro));


        Mockito.reset(utenteRepository);
    }

    @Test
    @DisplayName("login should succeed with valid credentials")
    void login_Successo() throws EccezioneAutenticazione {
        RichiestaLoginDTO richiesta = new RichiestaLoginDTO("test@example.com", passwordInChiaro);

        Mockito.when(utenteRepository.findByEmail("test@example.com".toLowerCase())).thenReturn(Optional.of(utenteDiTest));

        UtenteDTO result = servizioAutenticazione.login(richiesta);

        assertNotNull(result);
        assertEquals(utenteDiTest.getEmail(), result.getEmail());
        assertEquals(utenteDiTest.getRuolo(), result.getRuolo());
    }

    @Test
    @DisplayName("login should fail for non-existent user")
    void login_UtenteNonTrovato_LanciaEccezione() {
        RichiestaLoginDTO richiesta = new RichiestaLoginDTO("notfound@example.com", "password");
        Mockito.when(utenteRepository.findByEmail("notfound@example.com".toLowerCase())).thenReturn(Optional.empty());

        EccezioneAutenticazione ex = assertThrows(EccezioneAutenticazione.class, () -> {
            servizioAutenticazione.login(richiesta);
        });
        assertTrue(ex.getMessage().contains("Utente non trovato"));
    }

    @Test
    @DisplayName("login should fail for incorrect password")
    void login_PasswordErrata_LanciaEccezione() {
        RichiestaLoginDTO richiesta = new RichiestaLoginDTO("test@example.com", "wrongpassword");
        Mockito.when(utenteRepository.findByEmail("test@example.com".toLowerCase())).thenReturn(Optional.of(utenteDiTest));

        EccezioneAutenticazione ex = assertThrows(EccezioneAutenticazione.class, () -> {
            servizioAutenticazione.login(richiesta);
        });
        assertTrue(ex.getMessage().contains("Password errata"));
    }



    @Test
    @DisplayName("inizializzaUtenteAdminSeAssente should create admin if not present")
    void inizializzaAdmin_QuandoNonEsiste() {
        String email = "newadmin@example.com";

        Mockito.when(utenteRepository.findByEmail(email)).thenReturn(Optional.empty());


        servizioAutenticazione.inizializzaUtenteAdminSeAssente(email, "newpass", "admin");


        Mockito.verify(utenteRepository, times(1)).persist(any(Utente.class));
    }

    @Test
    @DisplayName("inizializzaUtenteAdminSeAssente should do nothing if admin already exists")
    void inizializzaAdmin_QuandoGiaEsiste() {
        String email = "existing@example.com";
        Utente existingUser = new Utente(1L, email, "hash", "admin");

        Mockito.when(utenteRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));


        servizioAutenticazione.inizializzaUtenteAdminSeAssente(email, "password", "admin");


        Mockito.verify(utenteRepository, never()).persist(any(Utente.class));
    }
}