/**
 * La classe 'PlaybackService' gestisce il flusso audio del player. 
 * Utilizza un 'Timeline' di JavaFX per simulare la riproduzione, avanzando ogni secondo. 
 * 
 * @version 1.0 - versione senza traccia audio reale
 * 
 * @pattern Service
 * @pattern Singleton
 * 
 * @author EmanuelChirico
 * @author ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.model.service;


import javafx.animation.Timeline;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import it.unisa.diem.sad_gruppo6.model.domain.Track;


public class PlaybackService {

    private PlaybackState playbackState;
    private static PlaybackService instance;
    private Timeline timeline;

    /**
     * Costruttore privato per implementare il pattern Singleton. 
     * Inizializza lo stato di riproduzione.
     * 
     */
    private PlaybackService() {
        this.playbackState = PlaybackState.getInstance();  
    }
    
    public static PlaybackService getInstance() {
        if (instance == null) {
            instance = new PlaybackService();
        }
        return instance;
    }

    /**
     * Avvia il flusso audio simulato ripartendo dalla posizione corrente memorizzata
     * in PlaybackState. Ferma eventuali flussi attivi prima di avviarne uno nuovo.
     * Non avvia la Timeline se non è presente alcuna traccia in riproduzione.
     */
    public void start() {
        stop();
        if (playbackState.getCurrentTrack() == null) {
            return; // Nessuna traccia: non ha senso avviare il timer
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Ferma il flusso audio simulato, se attivo. Viene chiamato quando si avvia un 
     * nuovo flusso o si mette in pausa la riproduzione.
     */

    public void stop() 
    {
        if (timeline != null) 
        {
            timeline.stop();
            timeline = null;
        }
    }

        /**
     * Chiamato ogni secondo dalla Timeline: aggiorna la posizione corrente di
     * riproduzione nel PlaybackState e ferma il servizio al termine della traccia.
     *
     * <p>Recupera la traccia corrente e la durata totale dal PlaybackState.
     * Se la posizione attuale è inferiore alla durata totale, incrementa di 1
     * secondo tramite {@link PlaybackState#seekTo(int)} (che notifica automaticamente
     * gli osservatori, aggiornando la UI). Quando la posizione raggiunge o supera
     * la durata totale, la Timeline viene fermata tramite {@link #stop()}.</p>
     *
     * @see PlaybackState#seekTo(int)
     * @see PlaybackState#getCurrentPosition()
     */
    private void tick() {
        it.unisa.diem.sad_gruppo6.model.domain.Track currentTrack = playbackState.getCurrentTrack();
        if (currentTrack == null) {
            stop();
            return;
        }

        int currentPos = playbackState.getCurrentPosition();
        int totalDuration = currentTrack.getDuration();

        if (currentPos < totalDuration) {
            playbackState.seekTo(currentPos + 1);
        } else {
            stop();
        }
    }
}