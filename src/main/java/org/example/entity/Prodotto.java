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
     */
    public Prodotto(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoria = categoria;
    }

    /**
     * Restituisce l'identificatore del prodotto.
     *
     * @return l'id del prodotto.
     */
    public Long getId() { return id; }

    /**
     * Imposta l'identificatore del prodotto.
     *
     * @param id Identificatore da impostare.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Restituisce il nome del prodotto.
     *
     * @return il nome del prodotto.
     */
    public String getNome() { return nome; }

    /**
     * Imposta il nome del prodotto.
     *
     * @param nome Nome da impostare.
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Restituisce la descrizione del prodotto.
     *
     * @return la descrizione del prodotto.
     */
    public String getDescrizione() { return descrizione; }

    /**
     * Imposta la descrizione del prodotto.
     *
     * @param descrizione Descrizione da impostare.
     */
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    /**
     * Restituisce la quantità disponibile del prodotto.
     *
     * @return la quantità del prodotto.
     */
    public int getQuantita() { return quantita; }

    /**
     * Imposta la quantità disponibile del prodotto.
     *
     * @param quantita Quantità da impostare.
     */
    public void setQuantita(int quantita) { this.quantita = quantita; }

    /**
     * Restituisce il percorso dell'immagine associata al prodotto.
     *
     * @return il percorso dell'immagine.
     */
    public String getPercorsoImmagine() { return percorsoImmagine; }

    /**
     * Imposta il percorso dell'immagine associata al prodotto.
     *
     * @param percorsoImmagine Percorso da impostare.
     */
    public void setPercorsoImmagine(String percorsoImmagine) { this.percorsoImmagine = percorsoImmagine; }

    /**
     * Restituisce la categoria a cui appartiene il prodotto.
     *
     * @return la categoria del prodotto.
     */
    public Categoria getCategoria() { return categoria; }

    /**
     * Imposta la categoria a cui appartiene il prodotto.
     *
     * @param categoria Categoria da impostare.
     */
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    /**
     * Compara questo prodotto con un altro oggetto per verificare l'uguaglianza.
     * Due prodotti sono considerati uguali se hanno lo stesso id e nome.
     *
     * @param o L'oggetto da confrontare.
     * @return true se i prodotti sono uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prodotto prodotto = (Prodotto) o;
        return Objects.equals(id, prodotto.id) &&
                Objects.equals(nome, prodotto.nome);
    }

    /**
     * Genera un hash code per il prodotto basato su id e nome.
     *
     * @return Il codice hash del prodotto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    /**
     * Restituisce una rappresentazione in forma di stringa del prodotto.
     *
     * @return Una stringa contenente id, nome, categoria e quantità del prodotto.
     */
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