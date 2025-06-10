package org.example;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.service.impl.ServizioAutenticazioneImpl;

@ApplicationScoped
public class StartupService {

    @Inject
    ServizioAutenticazioneImpl servizioAutenticazione;

    @ConfigProperty(name = "quarkus.profile")
    String profile;

    @ConfigProperty(name = "app.admin.default.email", defaultValue = "admin@example.com")
    String adminEmail;

    @ConfigProperty(name = "app.admin.default.password", defaultValue = "password")
    String adminPassword;

    /**
     * Metodo che viene eseguito all'avvio dell'applicazione Quarkus.
     * Osserva l'evento {@link StartupEvent}.
     * Se il profilo è 'dev', tenta di creare un utente amministratore di default
     * se non esiste già.
     *
     * @param ev L'evento di avvio (non usato direttamente nel corpo del metodo ma necessario per l'observer).
     */
    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if ("dev".equalsIgnoreCase(profile)) {
            System.out.println("**********************************************************************");
            System.out.println("MODALITÀ SVILUPPO: Controllo/Creazione utente admin di default...");
            System.out.println("**********************************************************************");
            try {

                servizioAutenticazione.registraAdminDiDefault(adminEmail, adminPassword, "admin");

            } catch (Exception e) {

                System.err.println("!!! ATTENZIONE !!! Errore critico durante il tentativo di creazione dell'utente admin di default: "
                        + e.getMessage());

            }
        } else {
            System.out.println("Avvio in profilo: " + profile + ". Creazione utente admin di default saltata.");
        }
    }
}