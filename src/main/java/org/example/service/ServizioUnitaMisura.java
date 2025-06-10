package org.example.service;

import org.example.dto.UnitaMisuraDTO;
import org.example.exception.EccezioneAccessoDati;
import org.example.exception.EccezioneRisorsaNonTrovata;

import java.util.List;
import java.util.Optional;

public interface ServizioUnitaMisura {
    /**
     * Salva una nuova unità di misura.
     *
     * @param unitaMisuraDTO DTO dell'unità di misura.
     * @return Il DTO salvato.
     * @throws EccezioneAccessoDati se il nome è duplicato.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    UnitaMisuraDTO salva(UnitaMisuraDTO unitaMisuraDTO);

    /**
     * Trova un'unità di misura per ID.
     *
     * @param id L'ID.
     * @return Optional del DTO.
     */
    Optional<UnitaMisuraDTO> trovaPerId(Long id);

    /**
     * Trova un'unità di misura per nome.
     *
     * @param nome Il nome.
     * @return Optional del DTO.
     */
    Optional<UnitaMisuraDTO> trovaPerNome(String nome);

    /**
     * Recupera tutte le unità di misura.
     *
     * @return Lista di tutti i DTO.
     */
    List<UnitaMisuraDTO> trovaTutte();

    /**
     * Modifica un'unità di misura esistente.
     *
     * @param unitaMisuraDTO DTO con i dati aggiornati.
     * @return Il DTO modificato.
     * @throws EccezioneRisorsaNonTrovata se non esiste.
     * @throws EccezioneAccessoDati se il nuovo nome è duplicato.
     * @throws IllegalArgumentException se i dati di input non sono validi.
     */
    UnitaMisuraDTO modifica(UnitaMisuraDTO unitaMisuraDTO) throws EccezioneRisorsaNonTrovata;

    /**
     * Elimina un'unità di misura per ID.
     *
     * @param id L'ID da eliminare.
     * @throws EccezioneRisorsaNonTrovata se non esiste.
     * @throws EccezioneAccessoDati se è referenziata.
     */
    void elimina(Long id) throws EccezioneRisorsaNonTrovata, EccezioneAccessoDati;
}