package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prodotto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
@ToString(exclude = {"categoria", "tipologia", "unitaMisura"})
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome del prodotto non può essere vuoto")
    @Size(max = 255, message = "Il nome del prodotto non può superare i 255 caratteri")
    @Column(nullable = false, length = 255)
    private String nome;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @NotNull(message = "La quantità non può essere nulla")
    @DecimalMin(value = "0.0", inclusive = true, message = "La quantità non può essere negativa")
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantita;

    @Size(max = 512, message = "Il percorso immagine non può superare i 512 caratteri")
    @Column(name = "percorso_immagine", length = 512)
    private String percorsoImmagine;

    @NotNull(message = "La categoria è obbligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_prodotto_categoria"))
    private Categoria categoria;

    @NotNull(message = "La tipologia è obbligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipologia_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_prodotto_tipologia"))
    private Tipologia tipologia;

    @NotNull(message = "L'unità di misura è obbligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unita_misura_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_prodotto_unita"))
    private UnitaMisura unitaMisura;
}