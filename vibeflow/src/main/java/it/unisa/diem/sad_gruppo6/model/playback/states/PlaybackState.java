/**
 * La classe 'PlaybackState' rappresenta lo stato attuale del player, inclusi la traccia corrente,
 * la playlist corrente e lo stato di riproduzione. Implementa il pattern Singleton per garantire
 * che ci sia una sola istanza durante l'esecuzione dell'applicazione, e agisce da Subject del
 * pattern Observer, notificando gli osservatori a ogni cambiamento di stato.
 *
 * @pattern Singleton
 * @pattern Observer
 * @pattern State (contesto)
 *
 * @author EmanuelChirico, LuigiAutorino, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.model.playback.states;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import java.util.List;
import java.util.ArrayList;

public class PlaybackState {

    // Attributi
    private static PlaybackState instance;
    private Track currentTrack;
    private Playlist currentPlaylist;
    private PlayerState currentState;
    private List<PlaybackObserver> observers;
    private int currentPosition;
    private PlaylistIterator iterator;

    /**
     * Pattern Singleton che impedisce l'istanziazione dall'esterno.
     * Inizializza la lista degli osservatori e imposta lo stato iniziale del player a PausedState.
     */
    private PlaybackState() {
        this.observers = new ArrayList<>();
        this.currentState = new PausedState();
    }

    /**
     * Metodo per ottenere l'istanza singleton di PlaybackState.
     * Se l'istanza non esiste, viene creata una nuova istanza.
     *
     * @pattern Singleton
     * @return L'istanza singleton di PlaybackState.
     */
    public static PlaybackState getInstance() {
        if (instance == null) {
            instance = new PlaybackState();
        }
        return instance;
    }

    /**
     * Esegue l'azione di play delegandola allo stato corrente.
     * Sarà lo stato attuale a decidere il comportamento ed eventualmente cambiare stato.
     */
    public void play() {
        currentState.play(this);
    }

    /**
     * Esegue l'azione di pausa delegandola allo stato corrente.
     * Sarà lo stato attuale a decidere il comportamento ed eventualmente cambiare stato.
     */
    public void pause() {
        currentState.pause(this);
    }

    /**
     * Cambia lo stato del player. Viene invocato dai PlayerState quando è necessaria una
     * transizione di stato. Notifica gli osservatori a ogni cambiamento.
     *
     * @param newState Il nuovo stato da assegnare al player.
     */
    public void changeState(PlayerState newState) {
        this.currentState = newState;
        notifyObservers();
    }

    /**
     * Imposta la traccia attualmente in riproduzione e notifica gli osservatori del cambiamento.
     *
     * @param track La traccia da impostare come corrente.
     */
    public void setCurrentTrack(Track track) {
        this.currentTrack = track;
        notifyObservers();
    }

    /**
     * Imposta la playlist attualmente in riproduzione come contesto corrente.
     *
     * @param playlist La playlist da impostare come corrente.
     */
    public void setCurrentPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
    }

    /**
     * Restituisce la playlist attualmente impostata come contesto di riproduzione.
     *
     * @return La playlist corrente.
     */
    public Playlist getCurrentPlaylist() {
        return this.currentPlaylist;
    }

    /**
     * Restituisce la traccia attualmente in riproduzione.
     *
     * @return La traccia corrente, o null se nessuna traccia è impostata.
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Restituisce il nome dello stato corrente del player (es. "Playing", "Paused"),
     * delegando allo stato attuale.
     *
     * @return Il nome dello stato corrente del player.
     */
    public String getStatusName() {
        return currentState.getStatusName();
    }

    /**
     * Esegue l'azione di skip in avanti delegandola allo stato corrente.
     */
    public void next() {
        currentState.next(this);
    }

    /**
     * Esegue l'azione di skip all'indietro delegandola allo stato corrente.
     */
    public void previous() {
        currentState.previous(this);
    }

    /**
     * Restituisce la posizione attuale di riproduzione in secondi.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Imposta il tempo di riproduzione e notifica la UI.
     * Viene chiamato ogni secondo da PlaybackService#tick(), quindi gli observer
     * registrati devono essere leggeri per non impattare le performance.
     *
     * @param position I secondi a cui posizionare la traccia.
     */
    public void seekTo(int position) {
        this.currentPosition = position;
        notifyObservers();
    }

    /**
     * Restituisce l'iteratore corrente per navigare la playlist.
     */
    public PlaylistIterator getIterator() {
        return iterator;
    }
    
    /**
     * Imposta l'iteratore per la modalità di riproduzione corrente.
     */
    public void setIterator(PlaylistIterator iterator) {
        this.iterator = iterator;
    }

    /**
     * Registra un osservatore che verrà notificato a ogni cambiamento di stato del player.
     *
     * @pattern Observer
     * @param o L'osservatore da registrare.
     */
    public void registerObserver(PlaybackObserver o) {
        observers.add(o);
    }

    /**
     * Rimuove un osservatore precedentemente registrato.
     *
     * @pattern Observer
     * @param o L'osservatore da rimuovere.
     */
    public void removeObserver(PlaybackObserver o) {
        observers.remove(o);
    }

    /**
     * Notifica tutti gli osservatori registrati di un cambiamento di stato,
     * invocando il metodo update() su ciascuno di essi.
     *
     * @pattern Observer
     */
    private void notifyObservers() {
        for (PlaybackObserver o : observers) {
            o.update(this);
        }
    }
}