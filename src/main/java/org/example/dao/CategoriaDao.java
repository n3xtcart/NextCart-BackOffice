package org.example.dao;

import org.example.entity.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaDao {
    Categoria salva(Categoria categoria);
    Optional<Categoria> trovaPerId(Long id);
    Optional<Categoria> trovaPerNome(String nome);
    List<Categoria> trovaTutte();
    Categoria modifica(Categoria categoria);
    void elimina(Long id);  
}
