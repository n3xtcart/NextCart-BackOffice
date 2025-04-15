package org.example.dto;

public class UtenteDTO {
    private Long id;
    private String email;
    private String ruolo;

    public UtenteDTO(Long id, String email, String ruolo) {
        this.id = id;
        this.email = email;
        this.ruolo = ruolo;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRuolo() { return ruolo; }
}