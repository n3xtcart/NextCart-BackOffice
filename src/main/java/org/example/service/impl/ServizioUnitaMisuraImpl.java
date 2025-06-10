package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.UnitaMisuraDTO;
import org.example.entity.UnitaMisura;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.ProdottoRepository;
import org.example.repository.UnitaMisuraRepository;
import org.example.service.ServizioUnitaMisura;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServizioUnitaMisuraImpl implements ServizioUnitaMisura {

    @Inject
    UnitaMisuraRepository unitaMisuraRepository;

    @Inject
    ProdottoRepository prodottoRepository;

    private UnitaMisuraDTO toDTO(UnitaMisura entity) {
        if (entity == null) {
            return null;
        }
        return new UnitaMisuraDTO(entity.getId(), entity.getNome());
    }

    private UnitaMisura toEntity(UnitaMisuraDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Il DTO dell'unità di misura non può essere nullo per la conversione.");
        }
        UnitaMisura entity = new UnitaMisura();
        entity.setNome(dto.getNome());
        return entity;
    }

    private void updateEntityFromDTO(UnitaMisuraDTO dto, UnitaMisura entity) {
        if (dto == null || entity == null) {
            throw new IllegalArgumentException("DTO o Entità non possono essere nulli per l'aggiornamento.");
        }
        entity.setNome(dto.getNome());
    }

    @Override
    @Transactional
    public UnitaMisuraDTO salva(UnitaMisuraDTO unitaMisuraDTO) {
        if (unitaMisuraDTO == null) {
            throw new IllegalArgumentException("UnitaMisuraDTO non può essere nullo per il salvataggio.");
        }
        if (unitaMisuraDTO.getNome() == null || unitaMisuraDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'unità di misura è obbligatorio.");
        }
        unitaMisuraRepository.findByNome(unitaMisuraDTO.getNome()).ifPresent(existing -> {
            throw new EccezioneAccessoDati("Un'unità di misura con nome '" + unitaMisuraDTO.getNome() + "' esiste già.");
        });

        UnitaMisura unitaMisura = toEntity(unitaMisuraDTO);
        unitaMisuraRepository.persist(unitaMisura);
        return toDTO(unitaMisura);
    }

    @Override
    public Optional<UnitaMisuraDTO> trovaPerId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID dell'unità di misura non può essere nullo.");
        }
        return unitaMisuraRepository.findByIdOptional(id).map(this::toDTO);
    }

    @Override
    public Optional<UnitaMisuraDTO> trovaPerNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'unità di misura non può essere nullo o vuoto per la ricerca.");
        }
        return unitaMisuraRepository.findByNome(nome).map(this::toDTO);
    }

    @Override
    public List<UnitaMisuraDTO> trovaTutte() {
        return unitaMisuraRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UnitaMisuraDTO modifica(UnitaMisuraDTO unitaMisuraDTO) throws EccezioneRisorsaNonTrovata {
        if (unitaMisuraDTO == null || unitaMisuraDTO.getId() == null) {
            throw new IllegalArgumentException("UnitaMisuraDTO e il suo ID non possono essere nulli per la modifica.");
        }
        if (unitaMisuraDTO.getNome() == null || unitaMisuraDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'unità di misura è obbligatorio per la modifica.");
        }

        UnitaMisura unitaMisuraEsistente = unitaMisuraRepository.findByIdOptional(unitaMisuraDTO.getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Unità di misura non trovata con ID: " + unitaMisuraDTO.getId()));

        Optional<UnitaMisura> optUnitaConNuovoNome = unitaMisuraRepository.findByNome(unitaMisuraDTO.getNome());
        if (optUnitaConNuovoNome.isPresent() && !optUnitaConNuovoNome.get().getId().equals(unitaMisuraEsistente.getId())) {
            throw new EccezioneAccessoDati("Il nome '" + unitaMisuraDTO.getNome() + "' è già utilizzato da un'altra unità di misura.");
        }

        updateEntityFromDTO(unitaMisuraDTO, unitaMisuraEsistente);
        unitaMisuraRepository.persist(unitaMisuraEsistente);
        return toDTO(unitaMisuraEsistente);
    }

    @Override
    @Transactional
    public void elimina(Long id) throws EccezioneRisorsaNonTrovata, EccezioneAccessoDati {
        if (id == null) {
            throw new IllegalArgumentException("L'ID dell'unità di misura non può essere nullo per l'eliminazione.");
        }
        UnitaMisura unitaMisuraDaEliminare = unitaMisuraRepository.findByIdOptional(id)
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Unità di misura non trovata con ID: " + id + " per l'eliminazione."));

        long countProdotti = prodottoRepository.countByUnitaMisuraId(id);
        if (countProdotti > 0) {
            throw new EccezioneAccessoDati("Impossibile eliminare l'unità di misura con ID " + id +
                    " perché è utilizzata da " + countProdotti + " prodotti.");
        }

        unitaMisuraRepository.delete(unitaMisuraDaEliminare);
    }
}