package org.example.entity;

import java.util.Objects;

/**
 * Classe entità che rappresenta un utente.
 * Questa classe modella i dati di un utente all'interno dell'applicazione.
 */
public class Utente {
    // Identificatore univoco dell'utente.
    private Long id;
    // Email dell'utente.
    private String email;
    // Hash della password dell'utente. Memorizza SEMPRE l'hash, mai la password in chiaro.
    private String hashPassword;
    // Ruolo dell'utente (es. ADMIN, USER, etc.).
    private String ruolo;

    /**
     * Costruttore di default.
     * Inizializza un oggetto Utente senza attributi.
     */
    public Utente() {}

    /**
     * Costruttore che inizializza un utente con i parametri specificati.
     *
     * @param id            Identificatore univoco dell'utente.
     * @param email         Email dell'utente.
     * @param hashPassword  Hash della password dell'utente.
     * @param ruolo         Ruolo dell'utente.
     */
    public Utente(Long id, String email, String hashPassword, String ruolo) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce l'identificatore dell'utente.
     *
     * @return l'id dell'utente.
     */
    public Long getId() { return id; }

    /**
     * Imposta l'identificatore dell'utente.
     *
     * @param id Identificatore da impostare.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Restituisce l'email dell'utente.
     *
     * @return l'email dell'utente.
     */
    public String getEmail() { return email; }

    /**
     * Imposta l'email dell'utente.
     *
     * @param email Email da impostare.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Restituisce l'hash della password dell'utente.
     *
     * @return l'hash della password.
     */
    public String getHashPassword() { return hashPassword; }

    /**
     * Imposta l'hash della password dell'utente.
     *
     * @param hashPassword Hash della password da impostare.
     */
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }

    /**
     * Restituisce il ruolo dell'utente.
     *
     * @return il ruolo dell'utente.
     */
    public String getRuolo() { return ruolo; }

    /**
     * Imposta il ruolo dell'utente.
     *
     * @param ruolo Ruolo da impostare.
     */
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    /**
     * Compara questo utente con un altro oggetto per verificare l'uguaglianza.
     * Due utenti sono considerati uguali se hanno lo stesso id e email.
     *
     * @param o L'oggetto da confrontare.
     * @return true se gli utenti sono uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return Objects.equals(id, utente.id) && Objects.equals(email, utente.email);
    }

    /**
     * Genera un hash code per l'utente basato su id e email.
     *
     * @return Il codice hash dell'utente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    /**
     * Restituisce una rappresentazione in forma di stringa dell'utente.
     *
     * @return Una stringa contenente id, email e ruolo dell'utente.
     */
    @Override
    public String toString() {
        return "Utente{" + "id=" + id + ", email='" + email + '\'' + ", ruolo='" + ruolo + '\'' + '}';
    }
}