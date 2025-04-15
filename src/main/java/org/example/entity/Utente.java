package org.example.entity;

import java.util.Objects;

public class Utente {
    private Long id;
    private String email;
    private String hashPassword; // Memorizza SEMPRE l'hash, mai la password in chiaro
    private String ruolo;

    public Utente() {}

    public Utente(Long id, String email, String hashPassword, String ruolo) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.ruolo = ruolo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return Objects.equals(id, utente.id) && Objects.equals(email, utente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Utente{" + "id=" + id + ", email='" + email + '\'' + ", ruolo='" + ruolo + '\'' + '}';
    }
}