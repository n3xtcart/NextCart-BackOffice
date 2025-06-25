package org.example;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.service.ServizioAutenticazione;

@ApplicationScoped
public class StartupService {


    @Inject
    ServizioAutenticazione servizioAutenticazione;

    @ConfigProperty(name = "quarkus.profile")
    String profile;

    @ConfigProperty(name = "app.admin.default.email", defaultValue = "admin@example.com")
    String adminEmail;

    @ConfigProperty(name = "app.admin.default.password", defaultValue = "password")
    String adminPassword;

    /**
     * Metodo eseguito all'avvio dell'applicazione.
     * Crea un utente admin di default se il profilo è 'dev'.
     */
    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if ("dev".equalsIgnoreCase(profile)) {
            System.out.println("**********************************************************************");
            System.out.println("MODALITÀ SVILUPPO: Controllo/Creazione utente admin di default...");
            System.out.println("**********************************************************************");
            try {
                servizioAutenticazione.inizializzaUtenteAdminSeAssente(adminEmail, adminPassword, "admin");
            } catch (Exception e) {
                System.err.println("!!! ATTENZIONE !!! Errore durante la creazione dell'utente admin di default: "
                        + e.getMessage());
            }
        } else {
            System.out.println("Avvio in profilo: " + profile + ". Creazione utente admin di default saltata.");
        }
    }
}