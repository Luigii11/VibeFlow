/**
 * @file PlaybackController.java
 * @brief Controller di business responsabile della gestione globale della riproduzione audio.
 * @details Coordina l'interazione tra lo stato dell'applicazione (PlaybackState) e il servizio 
 * fisico di riproduzione (PlaybackService), garantendo che ci sia sempre un unico flusso audio attivo.
 * @see PlaybackState
 * @see PlaybackService
 * @author EmanuelChirico, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.business.playback;

import java.io.FileNotFoundException;
import java.util.List;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

public class PlaybackController {

    /* Attributi */
    private PlaybackState playbackState;
    private PlaybackService playbackService;

    /**
     * @brief Costruttore di default.
     */
    public PlaybackController() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackService = PlaybackService.getInstance();
    }

    /**
     * @brief Costruttore parametrizzato per Dependency Injection (test).
     */
    public PlaybackController(PlaybackState playbackState, PlaybackService playbackService) {
        this.playbackState = playbackState;
        this.playbackService = playbackService;
    }

    /**
     * @brief Avvia la riproduzione di una singola traccia.
     */
    public void play(Track selectedTrack) throws FileNotFoundException {
        if (selectedTrack == null) {
            throw new IllegalArgumentException("Track cannot be null.");
        }
        startPlayback(selectedTrack);
    }

    /**
     * @brief Avvia l'ascolto di un'intera playlist partendo dalla prima traccia.
     */
    public void play(Playlist p) throws FileNotFoundException {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play it.");
        }
        play(p, p.getTracks().get(0));
    }

    /**
     * @brief Avvia l'ascolto di una playlist partendo da una traccia specifica.
     */
    public void play(Playlist p, Track startTrack) throws FileNotFoundException {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play.");
        }
        playbackState.setCurrentPlaylist(p);
        play(p.getTracks(), startTrack);
    }

    /**
     * @brief Avvia l'ascolto di una lista generica di brani partendo da uno specifico.
     */
    public void play(List<Track> tracks, Track startTrack) throws FileNotFoundException {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("Empty list, impossible to play it.");
        }
        PlaylistIterator iterator = playbackState.getMode().getIterator(tracks, startTrack);
        playbackState.setIterator(iterator);
        startPlayback(startTrack);
    }

    /**
     * @brief Logica interna unificata per iniziare l'esecuzione di una traccia.
     */
    private void startPlayback(Track t) throws FileNotFoundException {
        playbackService.stop();
        playbackState.setCurrentTrack(t);
        playbackState.seekTo(0);
        playbackState.changeState(new PlayingState());
        playbackService.start();
        playbackService.setOnEndOfTrack(() -> onTrackEnded());
    }

    /**
     * @brief Ferma temporaneamente la riproduzione corrente mantenendo posizione e risorse.
     */
    public void pause() {
        playbackState.pause();
        playbackService.pause();
    }

    /**
     * @brief Riprende la riproduzione musicale precedentemente messa in pausa.
     */
    public void resume() {
        playbackState.play();
        playbackService.resume();
    }

    /**
     * @brief Salta al brano successivo nella playlist.
     */
    public void next() throws FileNotFoundException {
    Track previousTrack = playbackState.getCurrentTrack();
    playbackState.next();
    Track currentTrack = playbackState.getCurrentTrack();
    
    if (currentTrack != null && currentTrack != previousTrack) {
        playbackService.start();
        playbackService.setOnEndOfTrack(() -> onTrackEnded());
    }
}

    /**
     * @brief Torna al brano precedente nella playlist.
     */
    public void previous() throws FileNotFoundException {
        if (playbackState != null) {
            playbackState.previous();
        }
    }

    /**
     * @brief Callback invocata automaticamente alla fine della traccia corrente.
     * @details Avanza alla traccia successiva e ne avvia la riproduzione se esiste.
     */
    private void onTrackEnded() {
        Track previousTrack = playbackState.getCurrentTrack();
        playbackState.next();
        Track currentTrack = playbackState.getCurrentTrack();

        if (currentTrack != null && currentTrack != previousTrack) {
            try {
                playbackService.start();
                playbackService.setOnEndOfTrack(() -> onTrackEnded());
            } catch (FileNotFoundException e) {
                System.err.println("Impossibile riprodurre la traccia: " + e.getMessage());
            }
        }
    }
}