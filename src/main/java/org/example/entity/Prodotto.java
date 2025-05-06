package org.example.entity;

import java.util.Objects;

/**
 * Classe entità che rappresenta un prodotto.
 * Questa classe modella i dati di un prodotto all'interno dell'applicazione.
 */
public class Prodotto {
    // Identificatore univoco del prodotto.
    private Long id;
    // Nome del prodotto.
    private String nome;
    // Descrizione del prodotto.
    private String descrizione;
    // Quantità disponibile del prodotto.
    private int quantita;
    // Percorso dell'immagine associata al prodotto.
    private String percorsoImmagine;
    // Categoria a cui appartiene il prodotto (oggetto Categoria invece dell'id).
    private Categoria categoria;
    // Tipologia del prodotto (es. "BIOLOGICO", "CONVENZIONALE", "SENZA GLUTINE").
    private String tipologia; // NUOVO CAMPO

    /**
     * Costruttore di default.
     * Inizializza un oggetto Prodotto senza attributi.
     */
    public Prodotto() {}

    /**
     * Costruttore che inizializza un prodotto con i parametri specificati.
     *
     * @param id               Identificatore univoco del prodotto.
     * @param nome             Nome del prodotto.
     * @param descrizione      Descrizione del prodotto.
     * @param quantita         Quantità disponibile del prodotto.
     * @param percorsoImmagine Percorso dell'immagine associata al prodotto.
     * @param categoria        Categoria a cui appartiene il prodotto.
     * @param tipologia        Tipologia del prodotto.
     */
    public Prodotto(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, Categoria categoria, String tipologia) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoria = categoria;
        this.tipologia = tipologia; // NUOVO CAMPO
    }

    // Getters and Setters

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

    public String getTipologia() { return tipologia; } // NUOVO GETTER
    public void setTipologia(String tipologia) { this.tipologia = tipologia; } // NUOVO SETTER

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
                ", descrizione='" + descrizione + '\'' +
                ", quantita=" + quantita +
                ", percorsoImmagine='" + percorsoImmagine + '\'' +
                ", categoria=" + (categoria != null ? categoria.getNome() : "null") +
                ", tipologia='" + tipologia + '\'' + // NUOVO CAMPO
                '}';
    }
}