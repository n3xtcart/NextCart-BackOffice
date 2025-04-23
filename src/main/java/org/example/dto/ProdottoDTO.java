package org.example.dto;

/**
 * Data Transfer Object (DTO) for representing a product.
 * This class is used to transfer product-related data between different layers of the application.
 */
public class ProdottoDTO {
    // Unique identifier of the product.
    private Long id;
    // Name of the product.
    private String nome;
    // Description of the product.
    private String descrizione;
    // Quantity of the product available.
    private int quantita;
    // Path to the image associated with the product.
    private String percorsoImmagine;
    // Category associated with the product.
    private CategoriaDTO categoriaDTO;

    /**
     * Constructor to initialize a ProdottoDTO with the specified parameters.
     *
     * @param id               Unique identifier of the product.
     * @param nome             Name of the product.
     * @param descrizione      Description of the product.
     * @param quantita         Quantity of the product available.
     * @param percorsoImmagine Path to the image associated with the product.
     * @param categoriaDTO     Category associated with the product.
     */
    public ProdottoDTO(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, CategoriaDTO categoriaDTO) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoriaDTO = categoriaDTO;
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return the product ID.
     */
    public Long getId() { return id; }

    /**
     * Returns the name of the product.
     *
     * @return the product name.
     */
    public String getNome() { return nome; }

    /**
     * Returns the description of the product.
     *
     * @return the product description.
     */
    public String getDescrizione() { return descrizione; }

    /**
     * Returns the quantity of the product available.
     *
     * @return the product quantity.
     */
    public int getQuantita() { return quantita; }

    /**
     * Returns the path to the image associated with the product.
     *
     * @return the image path.
     */
    public String getPercorsoImmagine() { return percorsoImmagine; }

    /**
     * Returns the category associated with the product.
     *
     * @return the product category.
     */
    public CategoriaDTO getCategoriaDTO() { return categoriaDTO; }
}