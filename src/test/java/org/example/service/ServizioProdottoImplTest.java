package org.example.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import it.nextre.nextcart.dto.CategoriaDTO;
import it.nextre.nextcart.dto.ProdottoDTO;
import jakarta.inject.Inject;
import org.example.entity.Categoria;
import org.example.entity.Prodotto;
import org.example.exception.EccezioneRisorsaNonTrovata;
import org.example.repository.CategoriaRepository;
import org.example.repository.ProdottoRepository;
import org.example.service.impl.ServizioProdottoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class ServizioProdottoImplTest {

    @Inject
    ServizioProdottoImpl servizioProdotto;

    @InjectMock
    ProdottoRepository prodottoRepository;

    @InjectMock
    CategoriaRepository categoriaRepository;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Frutta", "/img/frutta.png");
        categoriaDTO = new CategoriaDTO(1L, "Frutta", "/img/frutta.png");
    }

    @Test
    void salva_successo() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setNome("Mela");
        dto.setCategoriaDTO(categoriaDTO);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(categoria));

        servizioProdotto.salva(dto);

        ArgumentCaptor<Prodotto> captor = ArgumentCaptor.forClass(Prodotto.class);
        verify(prodottoRepository).persist(captor.capture());
        assertEquals("Mela", captor.getValue().getNome());
        assertEquals(categoria, captor.getValue().getCategoria());
    }

    @Test
    void salva_categoriaNonTrovata() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setNome("Mela");
        dto.setCategoriaDTO(categoriaDTO);
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioProdotto.salva(dto));
    }

    @Test
    void salva_categoriaIdNullo() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setCategoriaDTO(new CategoriaDTO());
        assertThrows(IllegalArgumentException.class, () -> servizioProdotto.salva(dto));
    }

    @Test
    void trovaPerId_trovato() {
        Prodotto p = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.findByIdOptional(1L)).thenReturn(Optional.of(p));
        Optional<ProdottoDTO> result = servizioProdotto.trovaPerId(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void trovaPerId_idNullo() {
        assertThrows(IllegalArgumentException.class, () -> servizioProdotto.trovaPerId(null));
    }

    @Test
    void trovaTutti() {
        Prodotto p = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.listAll()).thenReturn(List.of(p));
        List<ProdottoDTO> result = servizioProdotto.trovaTutti();
        assertEquals(1, result.size());
    }

    @Test
    void trovaPerIdCategoria_successo() {
        Prodotto p = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.findByCategoriaId(1L)).thenReturn(List.of(p));
        List<ProdottoDTO> result = servizioProdotto.trovaPerIdCategoria(1L);
        assertEquals(1, result.size());
    }

    @Test
    void trovaPerIdCategoria_idNullo() {
        assertThrows(IllegalArgumentException.class, () -> servizioProdotto.trovaPerIdCategoria(null));
    }

    @Test
    void modifica_successo() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(1L);
        dto.setNome("Mela Golden");
        dto.setCategoriaDTO(categoriaDTO);

        Prodotto esistente = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.findByIdOptional(1L)).thenReturn(Optional.of(esistente));
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.of(categoria));

        ProdottoDTO result = servizioProdotto.modifica(dto);
        assertEquals("Mela Golden", result.getNome());
        verify(prodottoRepository).persist(any(Prodotto.class));
    }

    @Test
    void modifica_idProdottoNullo() {
        ProdottoDTO dto = new ProdottoDTO();
        assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dto));
    }

    @Test
    void modifica_prodottoNonTrovato() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(99L);
        when(prodottoRepository.findByIdOptional(99L)).thenReturn(Optional.empty());
        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioProdotto.modifica(dto));
    }

    @Test
    void modifica_categoriaNonTrovata() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(1L);
        dto.setCategoriaDTO(categoriaDTO);
        Prodotto esistente = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.findByIdOptional(1L)).thenReturn(Optional.of(esistente));
        when(categoriaRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioProdotto.modifica(dto));
    }

    @Test
    void modifica_categoriaIdNullo() {
        ProdottoDTO dto = new ProdottoDTO();
        dto.setId(1L);
        dto.setCategoriaDTO(new CategoriaDTO());
        Prodotto esistente = new Prodotto(1L, "Mela", null, BigDecimal.ONE, null, categoria, "pz");
        when(prodottoRepository.findByIdOptional(1L)).thenReturn(Optional.of(esistente));

        assertThrows(IllegalArgumentException.class, () -> servizioProdotto.modifica(dto));
    }

    @Test
    void elimina_successo() {
        when(prodottoRepository.deleteById(1L)).thenReturn(true);
        servizioProdotto.elimina(1L);
        verify(prodottoRepository).deleteById(1L);
    }

    @Test
    void elimina_nonTrovato() {
        when(prodottoRepository.deleteById(99L)).thenReturn(false);
        assertThrows(EccezioneRisorsaNonTrovata.class, () -> servizioProdotto.elimina(99L));
    }
}