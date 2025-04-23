package org.example.dto;

/**
 * Data Transfer Object (DTO) per rappresentare una categoria.
 * Questa classe viene utilizzata per trasferire i dati relativi a una categoria
 * tra i vari livelli dell'applicazione.
 */
public class CategoriaDTO {
    // Identificatore univoco della categoria.
    private Long id;
    // Nome della categoria.
    private String nome;
    // Percorso dell'immagine associata alla categoria.
    private String percorsoImmagine;

    /**
     * Costruttore che inizializza una CategoriaDTO con i parametri specificati.
     *
     * @param id               Identificatore univoco della categoria.
     * @param nome             Nome della categoria.
     * @param percorsoImmagine Percorso dell'immagine associata alla categoria.
     */
    public CategoriaDTO(Long id, String nome, String percorsoImmagine) {
        this.id = id;
        this.nome = nome;
        this.percorsoImmagine = percorsoImmagine;
    }

    /**
     * Restituisce l'identificatore della categoria.
     *
     * @return l'id della categoria.
     */
    public Long getId() { return id; }

    /**
     * Restituisce il nome della categoria.
     *
     * @return il nome della categoria.
     */
    public String getNome() { return nome; }

    /**
     * Restituisce il percorso dell'immagine associata alla categoria.
     *
     * @return il percorso dell'immagine.
     */
    public String getPercorsoImmagine() { return percorsoImmagine; }
}