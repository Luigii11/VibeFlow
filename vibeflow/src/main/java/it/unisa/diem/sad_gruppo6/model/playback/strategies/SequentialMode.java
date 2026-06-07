/**
 * @file SequentialMode.java
 * Implementazione concreta dell'interfaccia PlaybackMode per la modalità di
 * riproduzione sequenziale. Fornisce al sistema un SequentialIterator.
 * È la modalità di riproduzione di default del player.
 *
 * @pattern Strategy
 * @see PlaybackMode
 * @see SequentialIterator
 * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.model.playback.strategies;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.SequentialIterator;

import java.util.List;

public class SequentialMode implements PlaybackMode {

    /**
     * Crea e restituisce un iteratore sequenziale.
     * @param tracks   La lista di tracce da scorrere in modo sequenziale.
     * @param startTrack La traccia da cui far partire l'iteratore.
     * @return Un'istanza di SequentialIterator.
     */
    @Override
    public PlaylistIterator getIterator(List<Track> tracks, Track startTrack) {
        return new SequentialIterator(tracks, startTrack);
    }
    
}
