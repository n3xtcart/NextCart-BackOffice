package org.example.entity;

import java.util.Objects;

public class Prodotto {
    private Long id;
    private String nome;
    private String descrizione;
    private int quantita;
    private String percorsoImmagine;
    private Long idCategoria; // Foreign key verso Categoria

    public Prodotto() {}

    public Prodotto(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, Long idCategoria) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.idCategoria = idCategoria;
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
    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prodotto prodotto = (Prodotto) o;
        return Objects.equals(id, prodotto.id) && Objects.equals(nome, prodotto.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Prodotto{" + "id=" + id + ", nome='" + nome + '\'' + ", idCategoria=" + idCategoria + ", quantita=" + quantita +'}';
    }
}
