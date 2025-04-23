package org.example.dto;

/**
 * Data Transfer Object (DTO) per rappresentare un utente.
 * Questa classe viene utilizzata per trasferire i dati relativi ad un utente
 * tra i vari livelli dell'applicazione.
 */
public class UtenteDTO {
    // Identificatore univoco dell'utente.
    private Long id;
    // Indirizzo email dell'utente.
    private String email;
    // Ruolo dell'utente.
    private String ruolo;

    /**
     * Costruttore per inizializzare un oggetto UtenteDTO con i parametri specificati.
     *
     * @param id    Identificatore univoco dell'utente.
     * @param email Indirizzo email dell'utente.
     * @param ruolo Ruolo dell'utente.
     */
    public UtenteDTO(Long id, String email, String ruolo) {
        this.id = id;
        this.email = email;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce l'identificatore univoco dell'utente.
     *
     * @return l'identificatore dell'utente.
     */
    public Long getId() {
        return id;
    }

    /**
     * Restituisce l'indirizzo email dell'utente.
     *
     * @return l'indirizzo email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return il ruolo.
     */
    public String getRuolo() {
        return ruolo;
    }
}