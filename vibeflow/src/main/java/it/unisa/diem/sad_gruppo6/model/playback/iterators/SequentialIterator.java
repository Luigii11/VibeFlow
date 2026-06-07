/**
 * @file SequentialIterator.java
 * Implementazione concreta dell'interfaccia PlaylistIterator per la riproduzione
 * sequenziale dei brani. Gestisce l'avanzamento e l'arretramento lineare all'interno
 * della coda di riproduzione.
 *
 * @pattern Iterator
 * @see PlaylistIterator
 * @see Track
 * * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.model.playback.iterators;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.util.List;

public class SequentialIterator implements PlaylistIterator {

    /* Attributi */
    private List<Track> tracks;
    private int currentIndex;
    
    /**
     * Costruttore dell'iteratore sequenziale. Imposta la coda di riproduzione e 
     * determina l'indice di partenza in base alla traccia passata come parametro.
     * Se la traccia è null o non è presente nella playlist, la riproduzione parte dall'inizio.
     *
     * @param tracks   La playlist contenente i brani da riprodurre.
     * @param startTrack La traccia da cui avviare la riproduzione (può essere null).
     */
    public SequentialIterator(List<Track> tracks, Track startTrack) {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("Track list can't be empty.");
        }
        
        this.tracks = tracks;
        
        if (startTrack != null) {
            this.currentIndex = this.tracks.indexOf(startTrack);
            // Caso traccia non trovata
            if (this.currentIndex == -1) {
                throw new IllegalArgumentException("Critical error: couldn't find the requested track in this list.");
            }
        } else {
            // Se la startTrack è null, vuol dire che sto riproducendo l'intera playlist, dunque partiamo da 0
            this.currentIndex = 0;
        }
    }

    /**
     * Verifica se è presente una traccia successiva nella sequenza.
     * * @return true se non si è raggiunta la fine della playlist, false altrimenti.
     */
    @Override
    public boolean hasNext() {
        return currentIndex < tracks.size() - 1;
    }

    /**
     * Restituisce la traccia successiva e fa avanzare l'indice dell'iteratore.
     * * @return La traccia successiva, o null se si è giunti al termine.
     */
    @Override
    public Track next() {
        if (hasNext()) {
            currentIndex++;
            return tracks.get(currentIndex);
        }
        return null;
    }

    /**
     * Verifica se è presente una traccia precedente nella sequenza.
     * * @return true se l'indice corrente è maggiore di zero, false altrimenti.
     */
    @Override
    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    /**
     * Restituisce la traccia precedente e fa arretrare l'indice dell'iteratore.
     * * @return La traccia precedente, o null se si è già alla prima traccia.
     */
    @Override
    public Track previous() {
        if (hasPrevious()) {
            currentIndex--;
            return tracks.get(currentIndex);
        }
        return null;
    }

    /**
     * Riporta l'iteratore all'inizio della playlist (indice 0).
     */
    @Override
    public void reset() {
        this.currentIndex = 0;
    }
}
