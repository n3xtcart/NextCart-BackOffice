package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.TipologiaDTO;
import org.example.entity.Tipologia;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.ProdottoRepository;
import org.example.repository.TipologiaRepository;
import org.example.service.ServizioTipologia;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServizioTipologiaImpl implements ServizioTipologia {

    @Inject
    TipologiaRepository tipologiaRepository;

    @Inject
    ProdottoRepository prodottoRepository;


    private TipologiaDTO toDTO(Tipologia entity) {
        if (entity == null) {
            return null;
        }
        return new TipologiaDTO(entity.getId(), entity.getNome());
    }

    private Tipologia toEntity(TipologiaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Il DTO della tipologia non può essere nullo per la conversione.");
        }
        Tipologia entity = new Tipologia();

        entity.setNome(dto.getNome());
        return entity;
    }

    private void updateEntityFromDTO(TipologiaDTO dto, Tipologia entity) {
        if (dto == null || entity == null) {
            throw new IllegalArgumentException("DTO o Entità non possono essere nulli per l'aggiornamento.");
        }
        entity.setNome(dto.getNome());
    }


    @Override
    @Transactional
    public TipologiaDTO salva(TipologiaDTO tipologiaDTO) {
        if (tipologiaDTO == null) {
            throw new IllegalArgumentException("TipologiaDTO non può essere nullo per il salvataggio.");
        }
        if (tipologiaDTO.getNome() == null || tipologiaDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della tipologia è obbligatorio.");
        }
        tipologiaRepository.findByNome(tipologiaDTO.getNome()).ifPresent(existing -> {
            throw new EccezioneAccessoDati("Una tipologia con nome '" + tipologiaDTO.getNome() + "' esiste già.");
        });

        Tipologia tipologia = toEntity(tipologiaDTO);
        tipologiaRepository.persist(tipologia);
        return toDTO(tipologia);
    }

    @Override
    public Optional<TipologiaDTO> trovaPerId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID della tipologia non può essere nullo.");
        }
        return tipologiaRepository.findByIdOptional(id).map(this::toDTO);
    }

    @Override
    public Optional<TipologiaDTO> trovaPerNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della tipologia non può essere nullo o vuoto per la ricerca.");
        }
        return tipologiaRepository.findByNome(nome).map(this::toDTO);
    }

    @Override
    public List<TipologiaDTO> trovaTutte() {
        return tipologiaRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TipologiaDTO modifica(TipologiaDTO tipologiaDTO) throws EccezioneRisorsaNonTrovata {
        if (tipologiaDTO == null || tipologiaDTO.getId() == null) {
            throw new IllegalArgumentException("TipologiaDTO e il suo ID non possono essere nulli per la modifica.");
        }
        if (tipologiaDTO.getNome() == null || tipologiaDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della tipologia è obbligatorio per la modifica.");
        }

        Tipologia tipologiaEsistente = tipologiaRepository.findByIdOptional(tipologiaDTO.getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Tipologia non trovata con ID: " + tipologiaDTO.getId()));

        Optional<Tipologia> optTipologiaConNuovoNome = tipologiaRepository.findByNome(tipologiaDTO.getNome());
        if (optTipologiaConNuovoNome.isPresent() && !optTipologiaConNuovoNome.get().getId().equals(tipologiaEsistente.getId())) {
            throw new EccezioneAccessoDati("Il nome '" + tipologiaDTO.getNome() + "' è già utilizzato da un'altra tipologia.");
        }

        updateEntityFromDTO(tipologiaDTO, tipologiaEsistente);

        tipologiaRepository.persist(tipologiaEsistente);
        return toDTO(tipologiaEsistente);
    }

    @Override
    @Transactional
    public void elimina(Long id) throws EccezioneRisorsaNonTrovata, EccezioneAccessoDati {
        if (id == null) {
            throw new IllegalArgumentException("L'ID della tipologia non può essere nullo per l'eliminazione.");
        }
        Tipologia tipologiaDaEliminare = tipologiaRepository.findByIdOptional(id)
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Tipologia non trovata con ID: " + id + " per l'eliminazione."));

        long countProdotti = prodottoRepository.countByTipologiaId(id);
        if (countProdotti > 0) {
            throw new EccezioneAccessoDati("Impossibile eliminare la tipologia con ID " + id +
                    " perché è utilizzata da " + countProdotti + " prodotti.");
        }

        tipologiaRepository.delete(tipologiaDaEliminare);
    }
}