package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tipologia", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nome", name = "uq_tipologia_nome")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "nome"})
@ToString
public class Tipologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome della tipologia non può essere vuoto")
    @Size(max = 100, message = "Il nome della tipologia non può superare i 100 caratteri")
    @Column(nullable = false, length = 100)
    private String nome;
}