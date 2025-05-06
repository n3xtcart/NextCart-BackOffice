package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.dao.CategoriaDao;
import org.example.dao.ProdottoDao;
// Assuming you will use JDBC DAOs, you might inject them directly
// or keep injecting interfaces and wire them up elsewhere.
// For this example, I'll assume the interfaces are injected.
// import org.example.dao.impl.JdbcCategoriaDao;
// import org.example.dao.impl.JdbcProdottoDao;
import org.example.dto.CategoriaDTO;
import org.example.dto.ProdottoDTO;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneRisorsaNonTrovata; // Assuming you want to use this
import org.example.service.ServizioProdotto;

public class ServizioProdottoImpl implements ServizioProdotto {

	private final CategoriaDao categoriaDao;
	private final ProdottoDao prodottoDao;

	/**
	 * Costruttore per ServizioProdottoImpl.
	 *
	 * @param categoriaDao il DAO utilizzato per l'accesso ai dati delle categorie.
	 * @param prodottoDao  il DAO utilizzato per l'accesso ai dati dei prodotti.
	 */
	public ServizioProdottoImpl(CategoriaDao categoriaDao, ProdottoDao prodottoDao) {
		this.categoriaDao = categoriaDao;
		this.prodottoDao = prodottoDao;
	}

	private ProdottoDTO convertiAProdottoDTO(Prodotto prodotto) {
		if (prodotto == null) return null;
		CategoriaDTO categoriaDTO = null;
		if (prodotto.getCategoria() != null) {
			categoriaDTO = new CategoriaDTO(
					prodotto.getCategoria().getId(),
					prodotto.getCategoria().getNome(),
					prodotto.getCategoria().getPercorsoImmagine()
			);
		}
		return new ProdottoDTO(
				prodotto.getId(),
				prodotto.getNome(),
				prodotto.getDescrizione(),
				prodotto.getQuantita(),
				prodotto.getPercorsoImmagine(),
				categoriaDTO,
				prodotto.getTipologia() // NUOVO CAMPO
		);
	}

	private Prodotto convertiAProdottoEntity(ProdottoDTO prodottoDTO, Categoria categoria) {
		if (prodottoDTO == null) return null;
		Prodotto prodotto = new Prodotto();
		prodotto.setId(prodottoDTO.getId()); // Might be null for new products
		prodotto.setNome(prodottoDTO.getNome());
		prodotto.setDescrizione(prodottoDTO.getDescrizione());
		prodotto.setQuantita(prodottoDTO.getQuantita());
		prodotto.setPercorsoImmagine(prodottoDTO.getPercorsoImmagine());
		prodotto.setCategoria(categoria); // Categoria entity passed separately
		prodotto.setTipologia(prodottoDTO.getTipologia()); // NUOVO CAMPO
		return prodotto;
	}


	@Override
	public ProdottoDTO salva(ProdottoDTO prodottoDTO) {
		if (prodottoDTO == null) throw new IllegalArgumentException("ProdottoDTO non può essere nullo.");
		if (prodottoDTO.getCategoriaDTO() == null || prodottoDTO.getCategoriaDTO().getId() == null) {
			throw new IllegalArgumentException("CategoriaDTO o ID della categoria nel ProdottoDTO non possono essere nulli.");
		}

		Categoria categoria = categoriaDao.trovaPerId(prodottoDTO.getCategoriaDTO().getId())
				.orElseThrow(() -> new IllegalArgumentException("Categoria non trovata per ID: " + prodottoDTO.getCategoriaDTO().getId()));

		Prodotto prodottoDaSalvare = convertiAProdottoEntity(prodottoDTO, categoria);
		// ID will be null for new product, so DAO will assign it.
		// For updates, ID will be present.

		Prodotto prodottoSalvato = prodottoDao.salva(prodottoDaSalvare);
		return convertiAProdottoDTO(prodottoSalvato);
	}

	@Override
	public Optional<ProdottoDTO> trovaPerId(Long id) {
		return prodottoDao.trovaPerId(id).map(this::convertiAProdottoDTO);
	}

	@Override
	public List<ProdottoDTO> trovaTutti() {
		return prodottoDao.trovaTutti().stream()
				.map(this::convertiAProdottoDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria) {
		if (idCategoria == null) return List.of();
		return prodottoDao.trovaPerIdCategoria(idCategoria).stream()
				.map(this::convertiAProdottoDTO)
				.collect(Collectors.toList());
	}

	@Override
	public ProdottoDTO modifica(ProdottoDTO prodottoDTO) {
		if (prodottoDTO == null || prodottoDTO.getId() == null) {
			throw new IllegalArgumentException("ProdottoDTO o ID del prodotto non possono essere nulli per la modifica.");
		}
		if (prodottoDTO.getCategoriaDTO() == null || prodottoDTO.getCategoriaDTO().getId() == null) {
			throw new IllegalArgumentException("CategoriaDTO o ID della categoria nel ProdottoDTO non possono essere nulli per la modifica.");
		}

		// Check if product exists
		prodottoDao.trovaPerId(prodottoDTO.getId())
				.orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato per ID: " + prodottoDTO.getId() + " per la modifica."));


		Categoria categoria = categoriaDao.trovaPerId(prodottoDTO.getCategoriaDTO().getId())
				.orElseThrow(() -> new IllegalArgumentException("Categoria non trovata per ID: " + prodottoDTO.getCategoriaDTO().getId()));

		Prodotto prodottoDaModificare = convertiAProdottoEntity(prodottoDTO, categoria);

		// The DAO's 'modifica' or 'salva' (with ID) will handle the update.
		Prodotto prodottoModificato = prodottoDao.modifica(prodottoDaModificare);
		// Or: Prodotto prodottoModificato = prodottoDao.salva(prodottoDaModificare);
		// Depending on how you want to structure DAO methods. JdbcProdottoDao.modifica calls salva.

		return convertiAProdottoDTO(prodottoModificato);
	}

	@Override
	public void elimina(Long id) {
		if (id == null) throw new IllegalArgumentException("ID prodotto non può essere nullo.");
		// Check if product exists before attempting deletion to provide a clear message
		prodottoDao.trovaPerId(id)
				.orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato per ID: " + id + " per l'eliminazione."));
		prodottoDao.elimina(id);
	}
}