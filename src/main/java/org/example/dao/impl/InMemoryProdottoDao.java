package org.example.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.dao.ProdottoDao;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneAccessoDati;

public class InMemoryProdottoDao implements ProdottoDao {

    private static final List<Prodotto> archivioProdotti = new ArrayList<>();
    private static long contatoreId = 0;

    @Override
    public Prodotto salva(Prodotto prodotto) {
        if (prodotto == null) throw new IllegalArgumentException("Il prodotto non può essere nullo");

        if (prodotto.getId() == null) { // Nuovo prodotto
            prodotto.setId(++contatoreId);
            archivioProdotti.add(prodotto);
        } else {
            boolean esiste = trovaPerId(prodotto.getId()).isPresent();
            if (!esiste) {
                throw new EccezioneAccessoDati("Impossibile aggiornare prodotto non esistente con id: " + prodotto.getId());
            }
            archivioProdotti.removeIf(p -> p.getId().equals(prodotto.getId()));
            archivioProdotti.add(prodotto);
        }

        return prodotto;
    }

    @Override
    public Optional<Prodotto> trovaPerId(Long id) {
        if (id == null) return Optional.empty();
        return archivioProdotti.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
    }

    @Override
    public List<Prodotto> trovaTutti() {
        return new ArrayList<>(archivioProdotti);
    }

    @Override
    public List<Prodotto> trovaPerIdCategoria(Long idCategoria) {
        if (idCategoria == null) return List.of();
        return archivioProdotti.stream()
                .filter(p -> p.getCategoria() != null && idCategoria.equals(p.getCategoria().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void elimina(Long id) {
        Optional<Prodotto> prodotto = trovaPerId(id);
        if (prodotto.isPresent()) {
            archivioProdotti.remove(prodotto.get());
        } else {
            throw new IllegalArgumentException("Prodotto non trovato per ID: " + id);
        }
    }

    @Override
    public Prodotto modifica(Prodotto prodotto) {
        Optional<Prodotto> prodottoEsistente = trovaPerId(prodotto.getId());
        if (prodottoEsistente.isEmpty()) {
            throw new IllegalArgumentException("Prodotto non trovato per ID: " + prodotto.getId());
        }

        Prodotto esistente = prodottoEsistente.get();
        esistente.setNome(prodotto.getNome());
        esistente.setDescrizione(prodotto.getDescrizione());
        esistente.setQuantita(prodotto.getQuantita());
        esistente.setPercorsoImmagine(prodotto.getPercorsoImmagine());
        esistente.setCategoria(prodotto.getCategoria());

        return esistente;
    }
}
