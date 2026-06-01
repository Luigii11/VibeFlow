/**
 * @file PlaybackController.java
 * Classe di definizione di un oggetto di tipo 'PlaybackController', controller responsabile
 * della gestione della riproduzione audio. Coordina l'interazione tra PlaybackState e PlaybackService, 
 * garantendo un unico flusso attivo.
 * 
 * @see PlaybackState, PlaybackService
 *
 * @author EmanuelChirico
 */
package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.playback.PlaybackState;
import it.unisa.diem.sad_gruppo6.playback.PlaybackService;
import it.unisa.diem.sad_gruppo6.playback.states.PlayingState;

public class PlaybackController {

    
    private PlaybackState playbackState;
    private PlaybackService playbackService;

    /**
     * Costruttore di default, utilizzato dal caricamento FXML.
     * Recupera l'istanza singleton di PlaybackState e crea il PlaybackService associato.
     */
    public PlaybackController() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackService = new PlaybackService(playbackState);
    }

    /**
     * Costruttore utilizzato nei test per fornire
     * istanze controllate di PlaybackState e PlaybackService senza dipendere dal runtime JavaFX.
     *
     * @param playbackState Lo stato della riproduzione da utilizzare.
     * @param playbackService Il servizio di riproduzione da utilizzare.
     */
    public PlaybackController(PlaybackState playbackState, PlaybackService playbackService) {
        this.playbackState = playbackState;
        this.playbackService = playbackService;
    }

    /**
     * Avvia la riproduzione di una playlist. Verifica che la playlist non sia vuota,
     * imposta la playlist come contesto corrente e avvia la riproduzione dalla prima traccia.
     *
     * @param p La playlist da riprodurre.
     * @throws IllegalArgumentException Se la playlist è vuota.
     */
    public void play(Playlist p) {
        if (p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("La playlist è vuota, impossibile avviare la riproduzione.");
        }
        playbackState.setCurrentPlaylist(p);
        Track first = p.getTracks().get(0); 
        startPlayback(first);
    }

    /**
     * Avvia la riproduzione di un singolo brano.
     *
     * @param t La traccia da riprodurre.
     */
    public void play(Track t) {
        startPlayback(t);
    }

    /**
     * Logica condivisa dai due metodi play. Interrompe l'eventuale flusso audio attivo
     * prima di avviarne uno nuovo, imposta la traccia corrente, porta il player nello
     * stato di riproduzione e avvia il nuovo flusso. La notifica agli observer avviene
     * automaticamente all'interno di setCurrentTrack e changeState.
     *
     * @param t La traccia da cui avviare la riproduzione.
     */
    private void startPlayback(Track t) {
        playbackService.stop();                          
        playbackState.setCurrentTrack(t);                
        playbackState.changeState(new PlayingState());   
        playbackService.start();                         
    }

    /**
     * Mette in pausa la riproduzione corrente. Delega il cambio di stato al PlaybackState
     * (che, tramite il pattern State, passerà a PausedState) e interrompe il flusso audio.
     */
    public void pause() {
        playbackState.pause();
        playbackService.stop();
    }
}