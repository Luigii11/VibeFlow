/**
 * @file LoopIterator.java
 * @brief Implementazione concreta dell'interfaccia PlaylistIterator per la
 *        modalità di riproduzione ciclica (Loop).
 *
 * @details Gestisce la riproduzione circolare dei brani: al termine dell'ultima
 *          traccia, hasNext() ritorna sempre true e next() riparte automaticamente dalla prima traccia (indice 0). La navigazione
 *          a ritroso si comporta analogamente in modo circolare. L'ordine originale della playlist non viene mai modificato.
 *
 * @pattern Iterator
 * @see PlaylistIterator
 * @see LoopMode
 * @author EmanuelaGraziuso
 */
package it.unisa.diem.sad_gruppo6.model.playback.iterators;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.util.List;

public class LoopIterator implements PlaylistIterator {

    /**
     * Lista delle tracce su cui eseguire la riproduzione ciclica.
     * L'ordine originale non viene mai alterato.
     */
    private List<Track> tracks;

    /**
     * Indice della traccia correntemente in riproduzione all'interno di tracks.
     */
    private int currentIndex;

    /**
     * @brief Crea un LoopIterator a partire da una lista di tracce e da una
     *        traccia di partenza opzionale.
     *
     * @details Se startTrack non è null, l'iteratore viene
     *          posizionato su di essa come traccia corrente; altrimenti parte
     *          dall'indice 0.
     *
     * @param tracks     La lista di tracce da riprodurre in modalità loop;
     *                   non deve essere  null né vuota.
     * @param startTrack La traccia da impostare come corrente al momento
     *                   dell'attivazione del loop; può essere null.
     * @throws IllegalArgumentException Se tracks è null o vuota.
     */
    public LoopIterator(List<Track> tracks, Track startTrack) {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("La lista delle tracce non può essere vuota.");
        }

        this.tracks = tracks;

        if (startTrack != null && this.tracks.contains(startTrack)) {
            this.currentIndex = this.tracks.indexOf(startTrack);
        } else {
            this.currentIndex = 0;
        }
    }

    /**
     * @brief Verifica se è disponibile una traccia successiva.
     *
     * @details In modalità loop restituisce sempre true finché la
     *          lista non è vuota, garantendo la riproduzione ciclica infinita.
     *
     * @return Sempre true (la lista è garantita non vuota dal costruttore).
     */
    @Override
    public boolean hasNext() {
        // In modalità loop c'è sempre una traccia successiva (iterazione circolare)
        return true;
    }

    /**
     * @brief Restituisce il brano successivo con avanzamento circolare.
     *
     * @details Incrementa currentIndex; se supera l'ultimo indice,
     *          riparte da 0. Garantisce così la riproduzione
     *          infinita e ciclica della playlist.
     *
     * @return La traccia successiva (mai null).
     */
    @Override
    public Track next() {
        // Avanzamento circolare: dopo l'ultima traccia si ricomincia dalla prima
        currentIndex = (currentIndex + 1) % tracks.size();
        return tracks.get(currentIndex);
    }

    /**
     * @brief Verifica se è disponibile una traccia precedente.
     *
     * @details In modalità loop restituisce sempre true.
     *
     * @return Sempre true.
     */
    @Override
    public boolean hasPrevious() {
        // In modalità loop c'è sempre una traccia precedente (iterazione circolare)
        return true;
    }

    /**
     * @brief Restituisce il brano precedente con arretramento circolare.
     *
     * @details Decrementa currentIndex; se scende sotto 0, si posiziona
     *          sull'ultima traccia.
     *
     * @return La traccia precedente (mai null).
     */
    @Override
    public Track previous() {
        // Arretramento circolare: prima della prima traccia si va all'ultima
        currentIndex = (currentIndex - 1 + tracks.size()) % tracks.size();
        return tracks.get(currentIndex);
    }

    /**
     * @brief Reimposta l'iteratore all'indice 0.
     *
     * @details La prossima chiamata a next() restituirà la seconda
     *          traccia della lista (indice 1).
     */
    @Override
    public void reset() {
        this.currentIndex = 0;
    }

    @Override
    public void updateTracks(List<Track> newTracks) {
        // 1. Aggiorniamo il riferimento della lista
        this.tracks = newTracks;
        
        // 2. Chiediamo allo stato globale qual è la traccia in riproduzione
        Track playingTrack = it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState.getInstance().getCurrentTrack();
        
        // 3. Riallineiamo l'indice circolare
        if (playingTrack != null) {
            int newPosition = this.tracks.indexOf(playingTrack);
            if (newPosition != -1) {
                this.currentIndex = newPosition;
            }
        }
    }
}
