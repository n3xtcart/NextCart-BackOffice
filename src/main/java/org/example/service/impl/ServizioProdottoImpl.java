package org.example.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import it.nextre.nextcart.dto.CategoriaDTO;
import it.nextre.nextcart.dto.ProdottoDTO;
import it.nextre.nextcart.service.ServizioProdotto;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServizioProdottoImpl implements ServizioProdotto {

    @Inject
    ProdottoRepository prodottoRepository;

    @Inject
    CategoriaRepository categoriaRepository;

    private ProdottoDTO toDTO(Prodotto prodotto) {
        if (prodotto == null) {
            return null;
        }
        Categoria c = prodotto.getCategoria();
        CategoriaDTO cDto = new CategoriaDTO(
                c.getId(),
                c.getNome(),
                c.getPercorsoImmagine()
        );

        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(prodotto.getId());
        dto.setNome(prodotto.getNome());
        dto.setQuantita(prodotto.getQuantita());
        dto.setPercorsoImmagine(prodotto.getPercorsoImmagine());
        dto.setTipologia(prodotto.getTipologia());
        dto.setCategoriaDTO(cDto);

        return dto;
    }

    private Prodotto toEntity(ProdottoDTO dto) {
        if (dto.getCategoriaDTO() == null || dto.getCategoriaDTO().getId() == null) {
            throw new IllegalArgumentException("ID categoria obbligatorio.");
        }
        Categoria categoria = categoriaRepository.findByIdOptional(dto.getCategoriaDTO().getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata(
                        "Categoria non trovata con ID: " + dto.getCategoriaDTO().getId()
                ));

        Prodotto p = new Prodotto();
        p.setNome(dto.getNome());
        p.setQuantita(dto.getQuantita());
        p.setPercorsoImmagine(dto.getPercorsoImmagine());
        p.setTipologia(dto.getTipologia());
        p.setCategoria(categoria);

        return p;
    }


    private void updateEntity(ProdottoDTO dto, Prodotto p) {
        if (dto.getCategoriaDTO() == null || dto.getCategoriaDTO().getId() == null) {
            throw new IllegalArgumentException("ID categoria obbligatorio.");
        }
        Categoria categoria = categoriaRepository.findByIdOptional(dto.getCategoriaDTO().getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata(
                        "Categoria non trovata con ID: " + dto.getCategoriaDTO().getId()
                ));

        p.setNome(dto.getNome());
        p.setQuantita(dto.getQuantita());
        p.setPercorsoImmagine(dto.getPercorsoImmagine());
        p.setTipologia(dto.getTipologia());
        p.setCategoria(categoria);

    }

    @Override
    @Transactional
    public ProdottoDTO salva(ProdottoDTO dto) {
        Prodotto p = toEntity(dto);
        prodottoRepository.persist(p);

        return toDTO(p);
    }

    @Override
    public Optional<ProdottoDTO> trovaPerId(Long id) {
        if (id == null) throw new IllegalArgumentException("ID prodotto nullo.");
        return prodottoRepository.findByIdOptional(id)
                .map(this::toDTO);
    }

    @Override
    public List<ProdottoDTO> trovaTutti() {
        return prodottoRepository.listAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdottoDTO> trovaPerIdCategoria(Long idCategoria) {
        if (idCategoria == null) throw new IllegalArgumentException("ID categoria nullo.");
        return prodottoRepository.findByCategoriaId(idCategoria)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProdottoDTO modifica(ProdottoDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID prodotto obbligatorio per modifica.");
        }
        Prodotto p = prodottoRepository.findByIdOptional(dto.getId())
                .orElseThrow(() -> new EccezioneRisorsaNonTrovata(
                        "Prodotto non trovato con ID: " + dto.getId()
                ));
        updateEntity(dto, p);

        prodottoRepository.persist(p);
        return toDTO(p);
    }

    @Override
    @Transactional
    public void elimina(Long id) {
        if (!prodottoRepository.deleteById(id)) {
            throw new EccezioneRisorsaNonTrovata(
                    "Prodotto non trovato con ID: " + id + " per eliminazione."
            );
        }
    }
}