package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.CategoriaDTO;
import org.example.entity.Categoria;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;
import org.example.service.ServizioCategoria;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServizioCategoriaImpl implements ServizioCategoria {

    @Inject
    CategoriaRepository categoriaRepository;

    @Inject
    ProdottoRepository prodottoRepository;

    private CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getPercorsoImmagine());
    }

    private Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) return null;
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setPercorsoImmagine(dto.getPercorsoImmagine());
        return categoria;
    }

    private void updateEntityFromDTO(CategoriaDTO dto, Categoria entity) {
        entity.setNome(dto.getNome());
        entity.setPercorsoImmagine(dto.getPercorsoImmagine());
    }

    @Override
    @Transactional
    public CategoriaDTO salva(CategoriaDTO categoriaDTO) {
        if (categoriaDTO.getNome() == null || categoriaDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della categoria è obbligatorio.");
        }
        categoriaRepository.findByNome(categoriaDTO.getNome()).ifPresent(c -> {
            throw new EccezioneAccessoDati("Categoria con nome '" + categoriaDTO.getNome() + "' già esistente.");
        });
        Categoria categoria = toEntity(categoriaDTO);
        categoriaRepository.persist(categoria);
        return toDTO(categoria);
    }

    @Override
    public Optional<CategoriaDTO> trovaPerId(Long id) {
        return categoriaRepository.findByIdOptional(id).map(this::toDTO);
    }

    @Override
    public Optional<CategoriaDTO> trovaPerNome(String nome) {
        return categoriaRepository.findByNome(nome).map(this::toDTO);
    }

    @Override
    public List<CategoriaDTO> trovaTutte() {
        return categoriaRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaDTO modifica(CategoriaDTO categoriaDTO) throws EccezioneRisorsaNonTrovata {
        if (categoriaDTO.getId() == null) {
            throw new IllegalArgumentException("ID Categoria è richiesto per la modifica.");
        }
        if (categoriaDTO.getNome() == null || categoriaDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della categoria è obbligatorio.");
        }

        Categoria categoriaEsistente = categoriaRepository.findByIdOptional(categoriaDTO.getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Categoria non trovata con ID: " + categoriaDTO.getId()));

        Optional<Categoria> optCategoriaConNuovoNome = categoriaRepository.findByNome(categoriaDTO.getNome());
        if (optCategoriaConNuovoNome.isPresent() && !optCategoriaConNuovoNome.get().getId().equals(categoriaEsistente.getId())) {
            throw new EccezioneAccessoDati("Il nome '" + categoriaDTO.getNome() + "' è già utilizzato da un'altra categoria.");
        }

        updateEntityFromDTO(categoriaDTO, categoriaEsistente);
        categoriaRepository.persist(categoriaEsistente);
        return toDTO(categoriaEsistente);
    }

    @Override
    @Transactional
    public void elimina(Long id) throws EccezioneRisorsaNonTrovata {
        Categoria categoria = categoriaRepository.findByIdOptional(id)
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata("Categoria non trovata con ID: " + id + " per l'eliminazione."));

        if (prodottoRepository.countByCategoriaId(id) > 0) {
            throw new EccezioneAccessoDati("Impossibile eliminare la categoria con ID " + id + " perché è utilizzata da uno o più prodotti.");
        }
        categoriaRepository.delete(categoria);
    }
}