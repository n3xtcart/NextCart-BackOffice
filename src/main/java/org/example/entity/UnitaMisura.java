package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "unita_misura", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nome", name = "uq_unita_misura_nome")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
@ToString
public class UnitaMisura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome dell'unità di misura non può essere vuoto")
    @Size(max = 100, message = "Il nome dell'unità di misura non può superare i 100 caratteri")
    @Column(nullable = false, length = 100)
    private String nome;
}