/**
 * @file ShuffleIterator.java
 * @brief Implementazione concreta dell'interfaccia {@link PlaylistIterator} per la
 *        modalità di riproduzione casuale (Shuffle).
 *
 * @details Gestisce la selezione casuale dei brani da una lista {@code remaining}
 *          di tracce non ancora riprodotte. Ad ogni invocazione di {@link #next()}
 *          un brano viene estratto casualmente dalla lista e spostato nella
 *          {@code history}, consentendo la navigazione a ritroso tramite
 *          {@link #previous()}. L'ordine originale della playlist non viene mai
 *          modificato, in piena conformità con l'acceptance criteria ID_13-AC3.
 *
 * @pattern Iterator
 * @see PlaylistIterator
 * @see ShuffleMode
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.playback.iterators;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShuffleIterator implements PlaylistIterator {

    /**
     * Lista delle tracce non ancora riprodotte in questa sessione shuffle.
     * Viene popolata al momento della costruzione e svuotata man mano che
     * {@link #next()} viene invocato.
     */
    private List<Track> remaining;

    /**
     * Storico delle tracce già riprodotte, nell'ordine in cui sono state
     * selezionate casualmente. Consente la navigazione a ritroso con
     * {@link #previous()}.
     */
    private List<Track> history;

    /**
     * Puntatore all'elemento corrente all'interno di {@code history}.
     * Vale {@code -1} prima che venga invocato il primo {@link #next()}.
     */
    private int historyIndex;

    /** Generatore di numeri casuali utilizzato in {@link #next()}. */
    private final Random random;

    /**
     * @brief Crea uno ShuffleIterator a partire da una lista di tracce e da una
     *        traccia di partenza opzionale.
     *
     * @details Se {@code startTrack} non è {@code null} e appartiene alla lista,
     *          viene aggiunta direttamente alla {@code history} come primo elemento
     *          (indice 0) e rimossa da {@code remaining}, così da simulare il fatto
     *          che sia già "la traccia corrente" senza che venga rirpodotta due
     *          volte. In questo modo {@link #hasNext()} e {@link #next()} si
     *          comportano correttamente sin dal primo utilizzo.
     *
     * @param tracks     La lista di tracce da riprodurre in modalità casuale;
     *                   non deve essere {@code null} né vuota.
     * @param startTrack La traccia da impostare come corrente al momento
     *                   dell'attivazione dello shuffle; può essere {@code null}.
     * @throws IllegalArgumentException Se {@code tracks} è {@code null} o vuota.
     */
    public ShuffleIterator(List<Track> tracks, Track startTrack) {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("La lista delle tracce non può essere vuota.");
        }

        this.remaining = new ArrayList<>(tracks);
        this.history = new ArrayList<>();
        this.random = new Random();
        this.historyIndex = -1;

        // Se è fornita una traccia di partenza, la segniamo come "già corrente"
        if (startTrack != null && this.remaining.contains(startTrack)) {
            this.remaining.remove(startTrack);
            this.history.add(startTrack);
            this.historyIndex = 0;
        }
    }

    /**
     * @brief Verifica se è disponibile almeno un brano non ancora riprodotto.
     *
     * @details Restituisce {@code true} sia quando l'iteratore sta navigando
     *          in avanti attraverso la {@code history} (l'utente ha premuto
     *          "previous" ed è nel mezzo della cronologia), sia quando esistono
     *          ancora tracce nella lista {@code remaining}.
     *
     * @return {@code true} se è possibile avanzare, {@code false} altrimenti.
     */
    @Override
    public boolean hasNext() {
        // Caso 1: stiamo navigando all'interno della history (dopo un previous())
        if (historyIndex < history.size() - 1) {
            return true;
        }
        // Caso 2: ci sono ancora tracce non riprodotte
        return !remaining.isEmpty();
    }

    /**
     * @brief Restituisce il brano successivo con selezione casuale dalla lista
     *        {@code remaining}.
     *
     * @details Se l'utente aveva premuto "previous" e si trova nel mezzo della
     *          {@code history}, riprende la navigazione in avanti senza
     *          reintrodurre casualità. Solo quando è in cima alla cronologia
     *          viene estratto casualmente un nuovo brano da {@code remaining},
     *          che viene poi aggiunto alla {@code history}.
     *
     * @return La traccia successiva, oppure {@code null} se non ve ne sono.
     */
    @Override
    public Track next() {
        if (!hasNext()) {
            return null;
        }

        // Caso: stiamo ripercorrendo la history in avanti (dopo un previous())
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            return history.get(historyIndex);
        }

        // Caso normale: scegliamo casualmente da remaining
        int index = random.nextInt(remaining.size());
        Track chosen = remaining.remove(index);
        history.add(chosen);
        historyIndex = history.size() - 1;
        return chosen;
    }

    /**
     * @brief Verifica se è disponibile almeno un brano precedente nella cronologia.
     *
     * @return {@code true} se {@code historyIndex} è maggiore di zero.
     */
    @Override
    public boolean hasPrevious() {
        return historyIndex > 0;
    }

    /**
     * @brief Restituisce il brano precedente navigando a ritroso nella
     *        {@code history} senza alterare la casualità futura.
     *
     * @return La traccia precedente, oppure {@code null} se non ve ne sono.
     */
    @Override
    public Track previous() {
        if (!hasPrevious()) {
            return null;
        }
        historyIndex--;
        return history.get(historyIndex);
    }

    /**
     * @brief Reimposta l'iteratore allo stato iniziale.
     *
     * @details Tutti i brani vengono reinseriti in {@code remaining}, la
     *          {@code history} viene azzerata e {@code historyIndex} viene
     *          riportato a {@code -1}. La prossima chiamata a {@link #next()}
     *          partirà quindi da una selezione casuale su tutti i brani.
     */
    @Override
    public void reset() {
        // Reintroduce nella remaining tutti i brani (sia history che quelli rimasti)
        remaining.addAll(history);
        history.clear();
        historyIndex = -1;
    }
}