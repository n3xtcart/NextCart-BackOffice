package org.example.dto;

public class CategoriaDTO {
    private Long id;
    private String nome;
    private String percorsoImmagine;

    public CategoriaDTO(Long id, String nome, String percorsoImmagine) {
        this.id = id;
        this.nome = nome;
        this.percorsoImmagine = percorsoImmagine;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getPercorsoImmagine() { return percorsoImmagine; }
}