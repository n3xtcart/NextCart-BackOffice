package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.ProdottoDTO;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.entity.Tipologia;
import org.example.entity.UnitaMisura;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;
import org.example.repository.TipologiaRepository;
import org.example.repository.UnitaMisuraRepository;
import org.example.service.ServizioProdotto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServizioProdottoImpl implements ServizioProdotto {

    @Inject
    ProdottoRepository prodottoRepository;
    @Inject
    CategoriaRepository categoriaRepository;
    @Inject
    TipologiaRepository tipologiaRepository;
    @Inject
    UnitaMisuraRepository unitaMisuraRepository;

    private ProdottoDTO toDTO(Prodotto p) {
        if (p == null) {
            return null;
        }

        return new ProdottoDTO(
                p.getId(),
                p.getNome(),
                p.getDescrizione(),
                p.getQuantita(),
                p.getPercorsoImmagine(),
                p.getCategoria() != null ? p.getCategoria().getId() : null,
                p.getCategoria() != null ? p.getCategoria().getNome() : null,
                p.getTipologia() != null ? p.getTipologia().getId() : null,
                p.getTipologia() != null ? p.getTipologia().getNome() : null,
                p.getUnitaMisura() != null ? p.getUnitaMisura().getId() : null,
                p.getUnitaMisura() != null ? p.getUnitaMisura().getNome() : null
        );
    }

    private Prodotto toEntityForSave(ProdottoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ProdottoDTO non può essere nullo per la conversione in entità.");
        }
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("ID Categoria è obbligatorio nel ProdottoDTO.");
        }
        if (dto.getTipologiaId() == null) {
            throw new IllegalArgumentException("ID Tipologia è obbligatorio nel ProdottoDTO.");
        }
        if (dto.getUnitaMisuraId() == null) {
            throw new IllegalArgumentException("ID Unità di Misura è obbligatorio nel ProdottoDTO.");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome del prodotto è obbligatorio.");
        }
        if (dto.getQuantita() == null) {
            throw new IllegalArgumentException("Quantità del prodotto è obbligatoria.");
        }


        Categoria categoria = categoriaRepository.findByIdOptional(dto.getCategoriaId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Categoria non trovata con ID: " + dto.getCategoriaId()));
        Tipologia tipologia = tipologiaRepository.findByIdOptional(dto.getTipologiaId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Tipologia non trovata con ID: " + dto.getTipologiaId()));
        UnitaMisura unitaMisura = unitaMisuraRepository.findByIdOptional(dto.getUnitaMisuraId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Unità di Misura non trovata con ID: " + dto.getUnitaMisuraId()));

        Prodotto prodotto = new Prodotto();

        prodotto.setNome(dto.getNome());
        prodotto.setDescrizione(dto.getDescrizione());
        prodotto.setQuantita(dto.getQuantita());
        prodotto.setPercorsoImmagine(dto.getPercorsoImmagine());
        prodotto.setCategoria(categoria);
        prodotto.setTipologia(tipologia);
        prodotto.setUnitaMisura(unitaMisura);
        return prodotto;
    }

    private void updateEntityFromDTO(ProdottoDTO dto, Prodotto entity) {
        if (dto == null || entity == null) {
            throw new IllegalArgumentException("DTO o Entità non possono essere nulli per l'aggiornamento.");
        }
        if (dto.getCategoriaId() == null || dto.getTipologiaId() == null || dto.getUnitaMisuraId() == null) {
            throw new IllegalArgumentException("ID di Categoria, Tipologia e Unità di Misura sono obbligatori per l'aggiornamento del prodotto.");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome del prodotto è obbligatorio.");
        }
        if (dto.getQuantita() == null) {
            throw new IllegalArgumentException("Quantità del prodotto è obbligatoria.");
        }

        Categoria categoria = categoriaRepository.findByIdOptional(dto.getCategoriaId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Categoria non trovata con ID: " + dto.getCategoriaId() + " durante l'aggiornamento del prodotto."));
        Tipologia tipologia = tipologiaRepository.findByIdOptional(dto.getTipologiaId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Tipologia non trovata con ID: " + dto.getTipologiaId() + " durante l'aggiornamento del prodotto."));
        UnitaMisura unitaMisura = unitaMisuraRepository.findByIdOptional(dto.getUnitaMisuraId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Unità di Misura non trovata con ID: " + dto.getUnitaMisuraId() + " durante l'aggiornamento del prodotto."));

        entity.setNome(dto.getNome());
        entity.setDescrizione(dto.getDescrizione());
        entity.setQuantita(dto.getQuantita());
        entity.setPercorsoImmagine(dto.getPercorsoImmagine());
        entity.setCategoria(categoria);
        entity.setTipologia(tipologia);
        entity.setUnitaMisura(unitaMisura);
    }



    @Override
    @Transactional
    public ProdottoDTO salva(ProdottoDTO prodottoDTO) throws EccezioneRisorsaNonTrovata {
        Prodotto prodotto = toEntityForSave(prodottoDTO);

        prodottoRepository.persist(prodotto);
        return toDTO(prodotto);
    }

    @Override
    public Optional<ProdottoDTO> trovaPerId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID del prodotto non può essere nullo.");
        }

        return prodottoRepository.findByIdOptional(id).map(this::toDTO);
    }

    @Override
    public List<ProdottoDTO> trovaTutti() {
        return prodottoRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria) {
        if (idCategoria == null) {
            throw new IllegalArgumentException("L'ID della categoria non può essere nullo per la ricerca prodotti.");
        }
        return prodottoRepository.findByCategoriaId(idCategoria).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProdottoDTO modifica(ProdottoDTO prodottoDTO) throws EccezioneRisorsaNonTrovata {
        if (prodottoDTO == null || prodottoDTO.getId() == null) {
            throw new IllegalArgumentException("ProdottoDTO e il suo ID non possono essere nulli per la modifica.");
        }

        Prodotto prodottoEsistente = prodottoRepository.findByIdOptional(prodottoDTO.getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Prodotto non trovato con ID: " + prodottoDTO.getId()));

        updateEntityFromDTO(prodottoDTO, prodottoEsistente);
        prodottoRepository.persist(prodottoEsistente);
        return toDTO(prodottoEsistente);
    }

    @Override
    @Transactional
    public void elimina(Long id) throws EccezioneRisorsaNonTrovata {
        if (id == null) {
            throw new IllegalArgumentException("L'ID del prodotto non può essere nullo per l'eliminazione.");
        }
        boolean deleted = prodottoRepository.deleteById(id);
        if (!deleted) {
            throw new EccezioneRisorsaNonTrovata("Prodotto non trovato con ID: " + id + " per l'eliminazione.");
        }
    }
}