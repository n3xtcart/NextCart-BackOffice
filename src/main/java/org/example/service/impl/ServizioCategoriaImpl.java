package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.dao.CategoriaDao;
import org.example.dao.ProdottoDao;
import org.example.dto.CategoriaDTO;
import org.example.entity.Categoria;
import org.example.service.ServizioCategoria;


public class ServizioCategoriaImpl implements ServizioCategoria{
	
	CategoriaDao categoriaDao;

	
	public ServizioCategoriaImpl(CategoriaDao categoriaDao) {
		this.categoriaDao = categoriaDao;

	}

	@Override
	public CategoriaDTO salva(CategoriaDTO categoriaDTO) {
		 Categoria categoria = new Categoria();
	        categoria.setNome(categoriaDTO.getNome());
	        categoria.setPercorsoImmagine(categoriaDTO.getPercorsoImmagine());
	        Categoria categoriaSalvata = categoriaDao.salva(categoria);
	        return new CategoriaDTO(categoriaSalvata.getId(), categoriaSalvata.getNome(), categoriaSalvata.getPercorsoImmagine());
	}

	@Override
	public Optional<CategoriaDTO> trovaPerId(Long id) {
		Optional<Categoria> categoria = categoriaDao.trovaPerId(id);
        return categoria.map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()));
    }


	@Override
	public Optional<CategoriaDTO> trovaPerNome(String nome) {
		Optional<Categoria> categoria = categoriaDao.trovaPerNome(nome);
        return categoria.map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()));
    }


	@Override
	public List<CategoriaDTO> trovaTutte() {
        List<Categoria> categorie = categoriaDao.trovaTutte();
        return categorie.stream()
                        .map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()))
                        .collect(Collectors.toList());
	}

	@Override
	public CategoriaDTO modifica(CategoriaDTO categoriaDTO) {
		Categoria categoria = new Categoria();
        categoria.setId(categoriaDTO.getId());
        categoria.setNome(categoriaDTO.getNome());
        categoria.setPercorsoImmagine(categoriaDTO.getPercorsoImmagine());
        
        Categoria categoriaModificata = categoriaDao.salva(categoria);
        return new CategoriaDTO(categoriaModificata.getId(), categoriaModificata.getNome(), categoriaModificata.getPercorsoImmagine());
	}

	@Override
	public void elimina(Long id) {
        categoriaDao.elimina(id);
		
	}

}
