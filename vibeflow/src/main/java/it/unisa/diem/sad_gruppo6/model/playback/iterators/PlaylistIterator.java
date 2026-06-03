/**
 * @file PlaylistIterator.java
 * Interfaccia per l'implementazione del pattern Iterator sulle playlist.
 * Fornisce i metodi standard per l'attraversamento in avanti e all'indietro 
 * della coda di riproduzione, astraendo la logica dalla modalità specifica 
 * (es. Sequenziale, Shuffle, Loop).
 * @pattern Iterator
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.model.playback.iterators;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

public interface PlaylistIterator {
    
    /**
     * Verifica se è presente una traccia successiva nella coda di riproduzione.
     * @return true se c'è una traccia successiva, false altrimenti.
     */
    boolean hasNext();

    /**
     * Restituisce la traccia successiva e fa avanzare l'iteratore.
     * @return La traccia successiva.
     */
    Track next();

    /**
     * Verifica se è presente una traccia precedente nella coda di riproduzione.
     * @return true se c'è una traccia precedente, false altrimenti.
     */
    boolean hasPrevious();

    /**
     * Restituisce la traccia precedente e fa arretrare l'iteratore.
     * @return La traccia precedente.
     */
    Track previous();

    /**
     * Resetta l'iteratore allo stato iniziale.
     */
    void reset();
}