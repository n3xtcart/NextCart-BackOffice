package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "categoria", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nome", name = "uq_categoria_nome")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
@ToString
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome della categoria non può essere vuoto")
    @Size(max = 255, message = "Il nome della categoria non può superare i 255 caratteri")
    @Column(nullable = false, length = 255)
    private String nome;

    @Size(max = 512, message = "Il percorso immagine non può superare i 512 caratteri")
    @Column(name = "percorso_immagine", length = 512)
    private String percorsoImmagine;
}