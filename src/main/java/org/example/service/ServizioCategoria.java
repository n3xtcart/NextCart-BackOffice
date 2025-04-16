package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dto.CategoriaDTO;

public interface ServizioCategoria {
	
    CategoriaDTO salva(CategoriaDTO categoriaDTO);
    Optional<CategoriaDTO> trovaPerId(Long id);
    Optional<CategoriaDTO> trovaPerNome(String nome);
    List<CategoriaDTO> trovaTutte();
    CategoriaDTO modifica(CategoriaDTO categoriaDTO);
    void elimina(Long id);
}
