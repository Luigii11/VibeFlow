/**
 * @file PlaybackState.java
 * @brief La classe 'PlaybackState' rappresenta lo stato globale del player audio.
 * * Mantiene le informazioni sulla traccia corrente, la playlist corrente,
 * lo stato di riproduzione (Playing, Paused) e la modalità di scorrimento
 * (Sequenziale, Shuffle, Loop). Agisce da "Context" nel pattern State e da 
 * "Subject" nel pattern Observer, notificando le viste ad ogni cambiamento.
 *
 * @pattern Singleton
 * @pattern Observer
 * @pattern State (Context)
 *
 * @author EmanuelChirico, LuigiAutorino, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.model.playback.states;

import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.PlaybackMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;

import java.util.List;
import java.util.ArrayList;

public class PlaybackState {

    /* Attributi */
    private static PlaybackState instance;
    private Track currentTrack;
    private Playlist currentPlaylist;
    private PlayerState currentState;
    private List<PlaybackObserver> observers;
    private int currentPosition;
    private PlaylistIterator iterator;
    private PlaybackMode mode;

    /**
     * @brief Costruttore privato per il pattern Singleton.
     * * Inizializza la lista degli osservatori, imposta lo stato iniziale a 
     * PausedState e la modalità di riproduzione di default a SequentialMode.
     */
    private PlaybackState() {
        this.observers = new ArrayList<>();
        this.currentState = new PausedState();
        this.mode = new SequentialMode();
    }

    /**
     * @brief Restituisce l'unica istanza di PlaybackState.
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
     * @brief Avvia o riprende la riproduzione.
     * * Delega l'azione di "play" allo stato corrente (PlayerState), il quale 
     * deciderà come comportarsi ed eventualmente cambierà lo stato globale.
     */
    public void play() {
        currentState.play(this);
    }

    /**
     * @brief Mette in pausa la riproduzione.
     * * Delega l'azione di "pausa" allo stato corrente (PlayerState).
     */
    public void pause() {
        currentState.pause(this);
    }

    /**
     * @brief Cambia lo stato operativo del player e notifica gli observer.
     * * Metodo invocato dalle classi concrete di PlayerState quando è necessaria 
     * una transizione di stato (es. da Paused a Playing).
     *
     * @param newState Il nuovo stato da assegnare al player.
     */
    public void changeState(PlayerState newState) {
        this.currentState = newState;
        notifyObservers();
    }

    /**
     * @brief Imposta la traccia attualmente in riproduzione e avvisa la UI.
     * @param track La nuova traccia da impostare come corrente.
     */
    public void setCurrentTrack(Track track) {
        this.currentTrack = track;
        notifyObservers();
    }

    /**
     * @brief Imposta la playlist di contesto da cui pescare i brani.
     * @param playlist La playlist da impostare.
     */
    public void setCurrentPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
    }

    /**
     * @brief Restituisce la playlist attualmente in uso.
     * @return La playlist corrente.
     */
    public Playlist getCurrentPlaylist() {
        return this.currentPlaylist;
    }

    /**
     * @brief Restituisce la traccia correntemente caricata nel player.
     * @return La traccia corrente, o null se non c'è alcun brano caricato.
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * @brief Restituisce il nome in formato stringa dello stato corrente.
     * @return Il nome dello stato corrente (es. "Playing", "Paused").
     */
    public String getStatusName() {
        return currentState.getStatusName();
    }

    /**
     * @brief Salta alla traccia successiva.
     * * Delega la logica di skip in avanti allo stato corrente.
     */
    public void next() {
        currentState.next(this);
    }

    /**
     * @brief Torna alla traccia precedente.
     * * Delega la logica di skip all'indietro allo stato corrente.
     */
    public void previous() {
        currentState.previous(this);
    }

    /**
     * @brief Restituisce i secondi trascorsi dall'inizio del brano.
     * @return I secondi attuali di riproduzione.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * @brief Aggiorna il tempo di riproduzione e notifica la grafica.
     * * Chiamato frequentemente (es. ogni secondo) dai servizi audio di background.
     *
     * @param position La nuova posizione in secondi in cui posizionare l'indicatore.
     */
    public void seekTo(int position) {
        this.currentPosition = position;
        notifyObservers();
    }

    /**
     * @brief Restituisce l'iteratore usato per scorrere la playlist.
     * @return L'istanza corrente di PlaylistIterator.
     */
    public PlaylistIterator getIterator() {
        return iterator;
    }
    
    /**
     * @brief Sostituisce l'iteratore attivo con uno nuovo.
     * @param iterator Il nuovo iteratore da assegnare.
     */
    public void setIterator(PlaylistIterator iterator) {
        this.iterator = iterator;
    }

    /**
     * @brief Imposta la modalità logica di riproduzione (Sequenziale, Loop, ecc.).
     * @param mode La nuova modalità (Strategy) da adottare.
     */
    public void setMode(PlaybackMode mode) {
        this.mode = mode;
    }

    /**
     * @brief Restituisce la modalità logica di riproduzione attualmente attiva.
     * @return L'istanza della modalità in uso.
     */
    public PlaybackMode getMode() {
        return this.mode;
    }

    /**
     * @brief Aggiunge un osservatore alla lista per ricevere gli aggiornamenti di stato.
     * @pattern Observer
     * @param o L'osservatore da registrare (solitamente un Controller della UI).
     */
    public void registerObserver(PlaybackObserver o) {
        observers.add(o);
    }

    /**
     * @brief Rimuove un osservatore dalla lista per non ricevere più aggiornamenti.
     * @pattern Observer
     * @param o L'osservatore da disiscrivere.
     */
    public void removeObserver(PlaybackObserver o) {
        observers.remove(o);
    }

    /**
     * @brief Segnala a tutti gli osservatori registrati che qualcosa è cambiato.
     * * Itera sulla lista degli observer e chiama il loro metodo update().
     * @pattern Observer
     */
    private void notifyObservers() {
        for (PlaybackObserver o : observers) {
            o.update(this);
        }
    }
}