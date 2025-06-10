package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProdottoDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private BigDecimal quantita;
    private String percorsoImmagine;

    private Long categoriaId;
    private String categoriaNome;

    private Long tipologiaId;
    private String tipologiaNome;

    private Long unitaMisuraId;
    private String unitaMisuraNome;


    public ProdottoDTO(Long id, String nome, String descrizione, BigDecimal quantita, String percorsoImmagine,
                       Long categoriaId, Long tipologiaId, Long unitaMisuraId) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.quantita = quantita;
        this.percorsoImmagine = percorsoImmagine;
        this.categoriaId = categoriaId;
        this.tipologiaId = tipologiaId;
        this.unitaMisuraId = unitaMisuraId;
    }

    public ProdottoDTO(Long id, String nome, String descrizione, BigDecimal quantita, String percorsoImmagine,
                       Long categoriaId, String categoriaNome,
                       Long tipologiaId, String tipologiaNome,
                       Long unitaMisuraId, String unitaMisuraNome) {
        this(id, nome, descrizione, quantita, percorsoImmagine, categoriaId, tipologiaId, unitaMisuraId);
        this.categoriaNome = categoriaNome;
        this.tipologiaNome = tipologiaNome;
        this.unitaMisuraNome = unitaMisuraNome;
    }
}