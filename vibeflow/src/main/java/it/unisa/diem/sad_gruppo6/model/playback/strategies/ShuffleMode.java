/**
 * @file ShuffleMode.java
 * @brief Implementazione concreta dell'interfaccia {@link PlaybackMode} per la
 *        modalità di riproduzione casuale (Shuffle).
 *
 * @details Funge da Strategy concreta: quando viene richiesto un iteratore,
 *          produce un'istanza di {@link ShuffleIterator}, delegando ad essa
 *          tutta la logica di selezione casuale. Il design rispetta il principio
 *          Open/Closed: per aggiungere nuove modalità (es. Loop) sarà
 *          sufficiente creare una nuova coppia {@code *Mode}/{@code *Iterator}
 *          senza modificare il codice esistente.
 *
 * @pattern Strategy
 * @see PlaybackMode
 * @see ShuffleIterator
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.playback.strategies;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.ShuffleIterator;

import java.util.List;

public class ShuffleMode implements PlaybackMode {

    /**
     * @brief Crea e restituisce un iteratore in modalità casuale.
     *
     * @param tracks     La lista dei brani su cui applicare lo shuffle.
     * @param startTrack La traccia attualmente in riproduzione al momento
     *                   dell'attivazione dello shuffle; può essere {@code null}.
     * @return Un'istanza configurata di {@link ShuffleIterator}.
     */
    @Override
    public PlaylistIterator getIterator(List<Track> tracks, Track startTrack) {
        return new ShuffleIterator(tracks, startTrack);
    }
}