package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.dao.CategoriaDao;
import org.example.dao.ProdottoDao;
import org.example.dto.CategoriaDTO;
import org.example.dto.ProdottoDTO;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.service.ServizioProdotto;


public class ServizioProdottoImpl implements ServizioProdotto{
	CategoriaDao categoriaDao;
	ProdottoDao prodottoDao;
	
	public ServizioProdottoImpl(CategoriaDao categoriaDao, ProdottoDao prodottoDao) {
		this.categoriaDao = categoriaDao;
		this.prodottoDao = prodottoDao;
	}

	@Override
	public ProdottoDTO salva(ProdottoDTO prodottoDTO) {
	    Categoria categoria = categoriaDao.trovaPerId(prodottoDTO.getCategoriaDTO().getId())
	            .orElseThrow(() -> new IllegalArgumentException("Categoria non trovata per ID: " + prodottoDTO.getCategoriaDTO().getId()));

	    Prodotto prodotto = new Prodotto();
	    prodotto.setNome(prodottoDTO.getNome());
	    prodotto.setDescrizione(prodottoDTO.getDescrizione());
	    prodotto.setQuantita(prodottoDTO.getQuantita());
	    prodotto.setPercorsoImmagine(prodottoDTO.getPercorsoImmagine());
	    prodotto.setCategoria(categoria);

	    Prodotto prodottoSalvato = prodottoDao.salva(prodotto);

	    return new ProdottoDTO(
	            prodottoSalvato.getId(),
	            prodottoSalvato.getNome(),
	            prodottoSalvato.getDescrizione(),
	            prodottoSalvato.getQuantita(),
	            prodottoSalvato.getPercorsoImmagine(),
	            prodottoDTO.getCategoriaDTO()
	    );
	}

	@Override
	public Optional<ProdottoDTO> trovaPerId(Long id) {
	    Optional<Prodotto> prodotto = prodottoDao.trovaPerId(id);
	    if (prodotto.isPresent()) {
	        Prodotto p = prodotto.get();
	        Categoria categoria = p.getCategoria();

	        CategoriaDTO categoriaDTO = new CategoriaDTO(
	                categoria.getId(),
	                categoria.getNome(),
	                categoria.getPercorsoImmagine()
	        );

	        return Optional.of(new ProdottoDTO(
	                p.getId(),
	                p.getNome(),
	                p.getDescrizione(),
	                p.getQuantita(),
	                p.getPercorsoImmagine(),
	                categoriaDTO
	        ));
	    }
	    return Optional.empty();
	}

	@Override
	public List<ProdottoDTO> trovaTutti() {
	    List<Prodotto> prodotti = prodottoDao.trovaTutti();
	    return prodotti.stream().map(p -> {
	        Categoria categoria = p.getCategoria();
	        CategoriaDTO categoriaDTO = new CategoriaDTO(
	                categoria.getId(),
	                categoria.getNome(),
	                categoria.getPercorsoImmagine()
	        );

	        return new ProdottoDTO(
	                p.getId(),
	                p.getNome(),
	                p.getDescrizione(),
	                p.getQuantita(),
	                p.getPercorsoImmagine(),
	                categoriaDTO
	        );
	    }).collect(Collectors.toList());
	}

	@Override
	public List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria) {
	    List<Prodotto> prodotti = prodottoDao.trovaPerIdCategoria(idCategoria);
	    return prodotti.stream().map(p -> {
	        Categoria categoria = p.getCategoria();
	        CategoriaDTO categoriaDTO = new CategoriaDTO(
	                categoria.getId(),
	                categoria.getNome(),
	                categoria.getPercorsoImmagine()
	        );

	        return new ProdottoDTO(
	                p.getId(),
	                p.getNome(),
	                p.getDescrizione(),
	                p.getQuantita(),
	                p.getPercorsoImmagine(),
	                categoriaDTO
	        );
	    }).collect(Collectors.toList());
	}

	@Override
	public ProdottoDTO modifica(ProdottoDTO prodottoDTO) {
	    Optional<Prodotto> prodottoEsistente = prodottoDao.trovaPerId(prodottoDTO.getId());
	    if (prodottoEsistente.isEmpty()) {
	        throw new IllegalArgumentException("Prodotto non trovato per ID: " + prodottoDTO.getId());
	    }

	    Categoria categoria = categoriaDao.trovaPerId(prodottoDTO.getCategoriaDTO().getId())
	            .orElseThrow(() -> new IllegalArgumentException("Categoria non trovata per ID: " + prodottoDTO.getCategoriaDTO().getId()));

	    Prodotto prodotto = prodottoEsistente.get();
	    prodotto.setNome(prodottoDTO.getNome());
	    prodotto.setDescrizione(prodottoDTO.getDescrizione());
	    prodotto.setQuantita(prodottoDTO.getQuantita());
	    prodotto.setPercorsoImmagine(prodottoDTO.getPercorsoImmagine());
	    prodotto.setCategoria(categoria);

	    Prodotto prodottoModificato = prodottoDao.salva(prodotto);

	    return new ProdottoDTO(
	            prodottoModificato.getId(),
	            prodottoModificato.getNome(),
	            prodottoModificato.getDescrizione(),
	            prodottoModificato.getQuantita(),
	            prodottoModificato.getPercorsoImmagine(),
	            prodottoDTO.getCategoriaDTO()
	    );
	}

	@Override
	public void elimina(Long id) {
	    Optional<Prodotto> prodotto = prodottoDao.trovaPerId(id);
	    if (prodotto.isPresent()) {
	        prodottoDao.elimina(id);
	    } else {
	        throw new IllegalArgumentException("Prodotto non trovato per ID: " + id);
	    }
	}
}