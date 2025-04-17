package org.example.entity;

import java.util.Objects;

import java.util.Objects;

public class Prodotto {
    private Long id;
    private String nome;
    private String descrizione;
    private int quantita;
    private String percorsoImmagine;
    private Categoria categoria; // Oggetto Categoria invece dell'id

    public Prodotto() {}

    public Prodotto(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public String getPercorsoImmagine() { return percorsoImmagine; }
    public void setPercorsoImmagine(String percorsoImmagine) { this.percorsoImmagine = percorsoImmagine; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prodotto prodotto = (Prodotto) o;
        return Objects.equals(id, prodotto.id) &&
               Objects.equals(nome, prodotto.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Prodotto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria=" + (categoria != null ? categoria.getNome() : "null") +
                ", quantita=" + quantita +
                '}';
    }
}
