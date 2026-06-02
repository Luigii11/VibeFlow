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
 */

package it.unisa.diem.sad_gruppo6.playback;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

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
     * Avvia il flusso audio simulato, fermando eventuali flussi attivi e 
     * facendo avanzare la riproduzione ogni secondo.
     */

    public void start() 
    {
        stop();  
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
     * Chiamato ogni secondo: fa avanzare la riproduzione.
     */
    private void tick() {
        return;
        // Da implementare.
    }
}