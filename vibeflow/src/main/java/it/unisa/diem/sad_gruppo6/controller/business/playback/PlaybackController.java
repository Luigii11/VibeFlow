/**
 * @file PlaybackController.java
 * @brief Controller di business responsabile della gestione globale della riproduzione audio.
 * @details Coordina l'interazione tra lo stato dell'applicazione (PlaybackState) e il servizio 
 * fisico di riproduzione (PlaybackService), garantendo che ci sia sempre un unico flusso audio attivo.
 * @see PlaybackState
 * @see PlaybackService
 * @author EmanuelChirico, LuigiAutorino, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.controller.business.playback;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.PlaybackMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.ShuffleMode;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

import java.util.List;

public class PlaybackController {

    /* Attributi */
    private PlaybackState playbackState;
    private PlaybackService playbackService;

    /**
     * @brief Costruttore di default.
     * @details Recupera le istanze Singleton dello stato e del servizio di riproduzione.
     */
    public PlaybackController() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackService = PlaybackService.getInstance();
    }

    /**
     * @brief Costruttore parametrizzato per il Dependency Injection (usato principalmente nei Test).
     * @param playbackState Lo stato logico della riproduzione da utilizzare.
     * @param playbackService Il gestore del flusso audio fisico da utilizzare.
     */
    public PlaybackController(PlaybackState playbackState, PlaybackService playbackService) {
        this.playbackState = playbackState;
        this.playbackService = playbackService;
    }

    /**
     * @brief Avvia l'ascolto di un'intera playlist partendo dalla prima traccia.
     * @param p La playlist da riprodurre.
     */
    public void play(Playlist p) {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play it.");
        }
        // Delega al metodo sottostante passando il primo brano
        play(p, p.getTracks().get(0));
    }

    /**
     * @brief Avvia l'ascolto di una playlist partendo da una traccia specifica.
     * @param p La playlist da riprodurre come contesto.
     * @param startTrack La traccia da cui iniziare l'ascolto.
     */
    public void play(Playlist p, Track startTrack) {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play.");
        }
        playbackState.setCurrentPlaylist(p);
        play(p.getTracks(), startTrack);
    }

    /**
     * @brief Avvia l'ascolto di una lista generica di brani partendo da uno specifico.
     * @details Configura l'iteratore per permettere lo scorrimento (usato dalla TrackLibrary).
     * @param tracks La lista dei brani da usare come contesto.
     * @param startTrack La traccia da cui iniziare la riproduzione.
     */
    public void play(List<Track> tracks, Track startTrack) {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("Empty list, impossible to play it.");
        }
        playbackState.setCurrentTrackList(tracks);
        PlaylistIterator iterator = playbackState.getMode().getIterator(tracks, startTrack);
        playbackState.setIterator(iterator);
        startPlayback(startTrack);
    }

    /**
     * @brief Logica interna unificata per iniziare l'esecuzione di una traccia.
     * @details Interrompe ogni eventuale audio in corso, aggiorna la traccia corrente nel Singleton,
     * commuta lo State logico su "Playing" e avvia fisicamente il motore di riproduzione.
     * @param t La traccia da cui avviare il flusso.
     */
    private void startPlayback(Track t) {
        playbackService.stop();                                  
        playbackState.setCurrentTrack(t);  
        playbackState.seekTo(0);              
        playbackState.changeState(new PlayingState());   
        playbackService.start();                                 
    }

    /**
     * @brief Ferma temporaneamente la riproduzione corrente.
     * @details Trasmette il cambio di stato al pattern State logico e interrompe il flusso audio.
     */
    public void pause() {
        playbackState.pause();
        playbackService.stop();
    }

    /**
     * @brief Riprende la riproduzione musicale precedentemente messa in pausa.
     */
    public void resume() {
        playbackState.play();        
        playbackService.start();     
    }

    /**
     * @brief Salta al brano successivo nella playlist.
     */
    public void next() {
        if (playbackState != null) {
            playbackState.next(); 
        }
    }

    /**
     * @brief Torna al brano precedente nella playlist.
     */
    public void previous() {
        if (playbackState != null) {
            playbackState.previous();
        }
    }

    /**
     * @brief Imposta la modalità di riproduzione attiva e aggiorna l'iteratore corrente.
     *
     * @details Quando l'utente attiva o disattiva la modalità Shuffle, questo metodo
     *          sostituisce la {@link PlaybackMode} nel {@link PlaybackState} e ricrea
     *          immediatamente l'iteratore a partire dalla traccia attualmente in ascolto,
     *          in modo che la modalità abbia effetto dal brano successivo senza
     *          interrompere né riavviare la riproduzione corrente.
     *          In conformità con l'acceptance criteria, l'ordine
     *          originale della playlist non viene mai alterato.
     *
     * @param mode La nuova {@link PlaybackMode} da adottare (es. {@link ShuffleMode}
     *             o {@link SequentialMode}).
     */
    public void setMode(PlaybackMode mode) {
    if (mode == null) {
        throw new IllegalArgumentException("PlaybackMode cannot be null.");
    }

    playbackState.setMode(mode);

    List<Track> tracks = playbackState.getCurrentTrackList();
    Track currentTrack = playbackState.getCurrentTrack();

    if (tracks != null && !tracks.isEmpty()) {
        PlaylistIterator newIterator = mode.getIterator(tracks, currentTrack);
        playbackState.setIterator(newIterator);
    }
    }
}