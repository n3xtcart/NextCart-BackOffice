package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import it.nextre.nextcart.dto.CategoriaDTO;
import jakarta.inject.Inject;
import org.example.entity.Categoria;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;
import org.example.service.impl.ServizioCategoriaImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ServizioCategoriaImplTest {

    @Inject
    ServizioCategoriaImpl servizioCategoria;

    @InjectMock
    CategoriaRepository categoriaRepository;

    @InjectMock
    ProdottoRepository prodottoRepository;

    @Test
    void salva_successo() {
        CategoriaDTO dto = new CategoriaDTO(null, "Nuova Categoria", "/img.png");
        when(categoriaRepository.findByNome(anyString())).thenReturn(Optional.empty());

        servizioCategoria.salva(dto);

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).persist(captor.capture());
        assertEquals("Nuova Categoria", captor.getValue().getNome());
    }

    @Test
    void salva_nomeNulloOVuoto() {
        assertThrows(IllegalArgumentException.class, () -> servizioCategoria.salva(new CategoriaDTO(null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> servizioCategoria.salva(new CategoriaDTO(null, "  ", null)));
    }

    @Test
    void salva_nomeGiaEsistente() {
        CategoriaDTO dto = new CategoriaDTO(null, "Esistente", null);
        when(categoriaRepository.findByNome("Esistente".toLowerCase())).thenReturn(Optional.of(new Categoria()));
        assertThrows(EccezioneAccessoDati.class, () -> servizioCategoria.salva(dto));
    }

    @Test
    void trovaPerId_trovato() {
        Categoria cat = new Categoria(1L, "Test", null);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(cat));
        Optional<CategoriaDTO> result = servizioCategoria.trovaPerId(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void trovaPerId_nonTrovato() {
        when(categoriaRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());
        Optional<CategoriaDTO> result = servizioCategoria.trovaPerId(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void trovaPerNome_trovato() {
        Categoria cat = new Categoria(1L, "Test", null);
        when(categoriaRepository.findByNome("test")).thenReturn(Optional.of(cat));
        Optional<CategoriaDTO> result = servizioCategoria.trovaPerNome("Test");
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getNome());
    }

    @Test
    void trovaTutte() {
        List<Categoria> categorie = List.of(new Categoria(1L, "Cat1", null), new Categoria(2L, "Cat2", null));
        when(categoriaRepository.listAll()).thenReturn(categorie);
        List<CategoriaDTO> result = servizioCategoria.trovaTutte();
        assertEquals(2, result.size());
    }

    @Test
    void trovaTutte_vuoto() {
        when(categoriaRepository.listAll()).thenReturn(Collections.emptyList());
        List<CategoriaDTO> result = servizioCategoria.trovaTutte();
        assertTrue(result.isEmpty());
    }

    @Test
    void modifica_successo() {
        CategoriaDTO dto = new CategoriaDTO(1L, "Nome Modificato", null);
        Categoria esistente = new Categoria(1L, "Nome Vecchio", null);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(esistente));
        when(categoriaRepository.findByNome("nome modificato")).thenReturn(Optional.empty());

        CategoriaDTO result = servizioCategoria.modifica(dto);
        assertEquals("Nome Modificato", result.getNome());
        verify(categoriaRepository).persist(any(Categoria.class));
    }

    @Test
    void modifica_idNullo() {
        assertThrows(IllegalArgumentException.class, () -> servizioCategoria.modifica(new CategoriaDTO(null, "Test", null)));
    }

    @Test
    void modifica_nomeNullo() {
        assertThrows(IllegalArgumentException.class, () -> servizioCategoria.modifica(new CategoriaDTO(1L, null, null)));
    }

    @Test
    void modifica_nonTrovata() {
        when(categoriaRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());
        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioCategoria.modifica(new CategoriaDTO(1L, "Test", null)));
    }

    @Test
    void modifica_nomeConflitto() {
        CategoriaDTO dto = new CategoriaDTO(1L, "Nome Conflitto", null);
        Categoria esistente = new Categoria(1L, "Nome Vecchio", null);
        Categoria altra = new Categoria(2L, "Nome Conflitto", null);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(esistente));
        when(categoriaRepository.findByNome("nome conflitto")).thenReturn(Optional.of(altra));

        assertThrows(EccezioneAccessoDati.class, () -> servizioCategoria.modifica(dto));
    }

    @Test
    void elimina_successo() {
        Categoria cat = new Categoria(1L, "Da eliminare", null);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(cat));
        when(prodottoRepository.countByCategoriaId(1L)).thenReturn(0L);

        servizioCategoria.elimina(1L);
        verify(categoriaRepository).delete(cat);
    }

    @Test
    void elimina_nonTrovata() {
        when(categoriaRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());
        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioCategoria.elimina(99L));
    }

    @Test
    void elimina_inUso() {
        Categoria cat = new Categoria(1L, "In Uso", null);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(cat));
        when(prodottoRepository.countByCategoriaId(1L)).thenReturn(5L);

        assertThrows(EccezioneAccessoDati.class, () -> servizioCategoria.elimina(1L));
    }
}