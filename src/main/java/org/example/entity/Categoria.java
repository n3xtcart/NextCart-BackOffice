package org.example.entity;

import java.util.Objects;

public class Categoria {
    private Long id;
    private String nome;
    private String percorsoImmagine;

    public Categoria() {}

    public Categoria(Long id, String nome, String percorsoImmagine) {
        this.id = id;
        this.nome = nome;
        this.percorsoImmagine = percorsoImmagine;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPercorsoImmagine() { return percorsoImmagine; }
    public void setPercorsoImmagine(String percorsoImmagine) { this.percorsoImmagine = percorsoImmagine; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id) && Objects.equals(nome, categoria.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Categoria{" + "id=" + id + ", nome='" + nome + '\'' + ", percorsoImmagine='" + percorsoImmagine + '\'' +'}';
    }
}
