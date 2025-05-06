package org.example.dto;

/**
 * Data Transfer Object (DTO) for representing a product.
 * This class is used to transfer product-related data between different layers of the application.
 */
public class ProdottoDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private int quantita;
    private String percorsoImmagine;
    private CategoriaDTO categoriaDTO;
    private String tipologia;

    /**
     * Constructor to initialize a ProdottoDTO with the specified parameters.
     *
     * @param id               Unique identifier of the product.
     * @param nome             Name of the product.
     * @param descrizione      Description of the product.
     * @param quantita         Quantity of the product available.
     * @param percorsoImmagine Path to the image associated with the product.
     * @param categoriaDTO     Category associated with the product.
     * @param tipologia        Type of the product.
     */
    public ProdottoDTO(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, CategoriaDTO categoriaDTO, String tipologia) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoriaDTO = categoriaDTO;
        this.tipologia = tipologia;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public int getQuantita() { return quantita; }
    public String getPercorsoImmagine() { return percorsoImmagine; }
    public CategoriaDTO getCategoriaDTO() { return categoriaDTO; }
    public String getTipologia() { return tipologia; }
}