/**
 * @file PlaybackMode.java
 * Interfaccia che definisce il contratto per le diverse modalità di riproduzione
 * (Sequential, Shuffle, Loop). Rappresenta il componente 'Strategy' del pattern.
 * Delega alle classi concrete la responsabilità di istanziare l'iteratore appropriato.
 *
 * @pattern Strategy
 * @see PlaylistIterator
 * @see PlaybackState
 * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.model.playback.strategies;

import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.util.List;

public interface PlaybackMode {

    /**
     * Restituisce l'iteratore per la modalità corrente.
     * @param tracks     La lista dei brani su cui iterare (da Playlist o TrackLibrary).
     * @param startTrack La traccia di partenza (se null, parte da 0).
     * @return Un'istanza configurata di PlaylistIterator.
     */
    PlaylistIterator getIterator(List<Track> tracks, Track startTrack);
}
