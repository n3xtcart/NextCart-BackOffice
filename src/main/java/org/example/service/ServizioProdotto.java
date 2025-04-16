package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dto.ProdottoDTO;

public interface ServizioProdotto {
	
    ProdottoDTO salva(ProdottoDTO prodottoDTO);
    Optional<ProdottoDTO> trovaPerId(Long id);
    List<ProdottoDTO> trovaTutti();
    List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria);
    ProdottoDTO modifica(ProdottoDTO prodottoDTO);
    void elimina(Long id);
}
