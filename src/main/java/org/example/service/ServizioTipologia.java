package org.example.service;

import org.example.dto.TipologiaDTO;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;

import java.util.List;
import java.util.Optional;

public interface ServizioTipologia {
    /**
     * Salva una nuova tipologia.
     *
     * @param tipologiaDTO DTO della tipologia da salvare.
     * @return Il DTO della tipologia salvata.
     * @throws EccezioneAccessoDati se il nome è duplicato.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    TipologiaDTO salva(TipologiaDTO tipologiaDTO);

    /**
     * Trova una tipologia per ID.
     *
     * @param id L'ID della tipologia.
     * @return Optional del DTO se trovata.
     */
    Optional<TipologiaDTO> trovaPerId(Long id);

    /**
     * Trova una tipologia per nome.
     *
     * @param nome Il nome della tipologia.
     * @return Optional del DTO se trovata.
     */
    Optional<TipologiaDTO> trovaPerNome(String nome);

    /**
     * Recupera tutte le tipologie.
     *
     * @return Lista di tutti i DTO delle tipologie.
     */
    List<TipologiaDTO> trovaTutte();

    /**
     * Modifica una tipologia esistente.
     *
     * @param tipologiaDTO DTO con i dati aggiornati.
     * @return Il DTO della tipologia modificata.
     * @throws EccezioneRisorsaNonTrovata se la tipologia non esiste.
     * @throws EccezioneAccessoDati se il nuovo nome è duplicato.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    TipologiaDTO modifica(TipologiaDTO tipologiaDTO) throws EccezioneRisorsaNonTrovata;

    /**
     * Elimina una tipologia per ID.
     *
     * @param id L'ID della tipologia da eliminare.
     * @throws EccezioneRisorsaNonTrovata se la tipologia non esiste.
     * @throws EccezioneAccessoDati se la tipologia è referenziata (es. da Prodotti).
     */
    void elimina(Long id) throws EccezioneRisorsaNonTrovata, EccezioneAccessoDati;
}