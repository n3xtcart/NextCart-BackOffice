package org.example.dto;

public class ProdottoDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private int quantita;
    private String percorsoImmagine;
    private CategoriaDTO categoriaDTO; 

    public ProdottoDTO(Long id, String nome, String descrizione, int quantita, String percorsoImmagine, CategoriaDTO categoriaDTO) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoriaDTO = categoriaDTO;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public int getQuantita() { return quantita; }
    public String getPercorsoImmagine() { return percorsoImmagine; }
    public CategoriaDTO getCategoriaDTO() { return categoriaDTO; }

}
