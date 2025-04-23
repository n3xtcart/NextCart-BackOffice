package org.example.entity;

import java.util.Objects;

/**
 * Classe entità che rappresenta una categoria.
 * Questa classe modella i dati di una categoria all'interno dell'applicazione.
 */
public class Categoria {
    // Identificatore univoco della categoria.
    private Long id;
    // Nome della categoria.
    private String nome;
    // Percorso dell'immagine associata alla categoria.
    private String percorsoImmagine;

    /**
     * Costruttore di default.
     * Inizializza un oggetto Categoria senza attributi.
     */
    public Categoria() {}

    /**
     * Costruttore che inizializza una categoria con i parametri specificati.
     *
     * @param id               Identificatore univoco della categoria.
     * @param nome             Nome della categoria.
     * @param percorsoImmagine Percorso dell'immagine associata alla categoria.
     */
    public Categoria(Long id, String nome, String percorsoImmagine) {
        this.id = id;
        this.nome = nome;
        this.percorsoImmagine = percorsoImmagine;
    }

    /**
     * Restituisce l'identificatore della categoria.
     *
     * @return l'id della categoria.
     */
    public Long getId() {
        return id;
    }

    /**
     * Imposta l'identificatore della categoria.
     *
     * @param id Identificatore da impostare.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Restituisce il nome della categoria.
     *
     * @return il nome della categoria.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome della categoria.
     *
     * @param nome Nome da impostare.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il percorso dell'immagine associata alla categoria.
     *
     * @return il percorso dell'immagine.
     */
    public String getPercorsoImmagine() {
        return percorsoImmagine;
    }

    /**
     * Imposta il percorso dell'immagine associata alla categoria.
     *
     * @param percorsoImmagine Percorso da impostare.
     */
    public void setPercorsoImmagine(String percorsoImmagine) {
        this.percorsoImmagine = percorsoImmagine;
    }

    /**
     * Compara questa categoria con un altro oggetto per verificare l'uguaglianza.
     * Due categorie sono considerate uguali se hanno lo stesso id e nome.
     *
     * @param o L'oggetto da confrontare.
     * @return true se le categorie sono uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id) &&
                Objects.equals(nome, categoria.nome);
    }

    /**
     * Genera un hash code per la categoria basato su id e nome.
     *
     * @return Il codice hash della categoria.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    /**
     * Restituisce una rappresentazione in forma di stringa della categoria.
     *
     * @return Una stringa contenente id, nome e percorsoImmagine della categoria.
     */
    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", percorsoImmagine='" + percorsoImmagine + '\'' +
                '}';
    }
}