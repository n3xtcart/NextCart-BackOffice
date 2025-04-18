package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.dao.CategoriaDao;
import org.example.dto.CategoriaDTO;
import org.example.entity.Categoria;
import org.example.service.ServizioCategoria;

/**
 * Implementazione dell'interfaccia ServizioCategoria.
 * Fornisce metodi per la gestione delle categorie, includendo operazioni di salvataggio,
 * ricerca, modifica ed eliminazione delle categorie.
 */
public class ServizioCategoriaImpl implements ServizioCategoria {

	private CategoriaDao categoriaDao;

	/**
	 * Costruttore per ServizioCategoriaImpl.
	 *
	 * @param categoriaDao il DAO utilizzato per l'accesso ai dati della categoria.
	 */
	public ServizioCategoriaImpl(CategoriaDao categoriaDao) {
		this.categoriaDao = categoriaDao;
	}

	/**
	 * Salva una nuova categoria.
	 *
	 * @param categoriaDTO il DTO contenente i dati della categoria da salvare.
	 * @return il DTO della categoria salvata.
	 */
	@Override
	public CategoriaDTO salva(CategoriaDTO categoriaDTO) {
		Categoria categoria = new Categoria();
		categoria.setNome(categoriaDTO.getNome());
		categoria.setPercorsoImmagine(categoriaDTO.getPercorsoImmagine());
		Categoria categoriaSalvata = categoriaDao.salva(categoria);
		return new CategoriaDTO(categoriaSalvata.getId(), categoriaSalvata.getNome(), categoriaSalvata.getPercorsoImmagine());
	}

	/**
	 * Cerca una categoria per ID.
	 *
	 * @param id l'ID della categoria da trovare.
	 * @return un Optional contenente il DTO della categoria se trovata, altrimenti vuoto.
	 */
	@Override
	public Optional<CategoriaDTO> trovaPerId(Long id) {
		Optional<Categoria> categoria = categoriaDao.trovaPerId(id);
		return categoria.map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()));
	}

	/**
	 * Cerca una categoria per nome.
	 *
	 * @param nome il nome della categoria da cercare.
	 * @return un Optional contenente il DTO della categoria se trovata, altrimenti vuoto.
	 */
	@Override
	public Optional<CategoriaDTO> trovaPerNome(String nome) {
		Optional<Categoria> categoria = categoriaDao.trovaPerNome(nome);
		return categoria.map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()));
	}

	/**
	 * Recupera tutte le categorie.
	 *
	 * @return una lista di DTO di tutte le categorie presenti.
	 */
	@Override
	public List<CategoriaDTO> trovaTutte() {
		List<Categoria> categorie = categoriaDao.trovaTutte();
		return categorie.stream()
				.map(c -> new CategoriaDTO(c.getId(), c.getNome(), c.getPercorsoImmagine()))
				.collect(Collectors.toList());
	}

	/**
	 * Modifica una categoria esistente.
	 *
	 * @param categoriaDTO il DTO contenente i dati aggiornati della categoria.
	 * @return il DTO della categoria modificata.
	 */
	@Override
	public CategoriaDTO modifica(CategoriaDTO categoriaDTO) {
		Categoria categoria = new Categoria();
		categoria.setId(categoriaDTO.getId());
		categoria.setNome(categoriaDTO.getNome());
		categoria.setPercorsoImmagine(categoriaDTO.getPercorsoImmagine());

		Categoria categoriaModificata = categoriaDao.salva(categoria);
		return new CategoriaDTO(categoriaModificata.getId(), categoriaModificata.getNome(), categoriaModificata.getPercorsoImmagine());
	}

	/**
	 * Elimina una categoria dato il suo ID.
	 *
	 * @param id l'ID della categoria da eliminare.
	 */
	@Override
	public void elimina(Long id) {
		categoriaDao.elimina(id);
	}
}