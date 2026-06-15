/**
 * La classe 'PlaybackService' gestisce il flusso audio del player.
 * Utilizza MediaPlayer di JavaFX per riprodurre il file audio 
 * associato alla traccia corrente memorizzata in PlaybackState.
 *
 * Espone i metodi principali per il controllo della riproduzione:
 * start() per avviare una nuova riproduzione, 
 * pause() per mettere in pausa mantenendo la posizione corrente,
 * stop() per fermare definitivamente
 * 
 * @version 2.0 - versione con traccia .mp3 reale
 * @pattern Service
 * @pattern Singleton
 * @author EmanuelChirico
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.service;

import java.io.File;
import java.io.FileNotFoundException;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

public class PlaybackService {

    /* Attributi */
    private PlaybackState playbackState;
    private Track currentTrack;
    private static PlaybackService instance;
    private MediaPlayer player;

    /**
     * @brief Costruttore privato per implementare il pattern Singleton. 
     * @details Inizializza il riferimento allo stato di riproduzione.
     */

    private PlaybackService() {
        this.playbackState = PlaybackState.getInstance();  
    }
    
    /**
     * @brief Restituisce l'unica istanza di PlaybackService.
     * @pattern Singleton
     * @return L'istanza singleton di PlaybackService.
     */
    public static PlaybackService getInstance() {
        if (instance == null) {
            instance = new PlaybackService();
        }
        return instance;
    }

    /**
     * Avvia la riproduzione audio della traccia corrente memorizzata in PlaybackState.
     * Ferma e libera eventuali player attivi prima di crearne uno nuovo.
     * Registra un listener su {@code currentTimeProperty} che invoca {@link #tick()}
     * ogni secondo per aggiornare la posizione e notificare gli observer.
     * Registra inoltre il callback di fine traccia per l'auto-scorrimento.
     * @throws FileNotFoundException Se il file audio non esiste nel path originale.
     */
    public void start() throws FileNotFoundException {
        stop();
        currentTrack = playbackState.getCurrentTrack();
        if (currentTrack == null) {
            return;
        }
        String currentTrackPath = currentTrack.getPath();
        File filepath = new File(currentTrackPath);
        if (!filepath.exists()) {
            throw new FileNotFoundException("La traccia audio selezionata non esiste più nel path originale.");
        }
        String play = filepath.toURI().toString();
        Media playbleSong = new Media(play);
        player = new MediaPlayer(playbleSong);

        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            int seconds = (int) newTime.toSeconds();
            if (seconds != playbackState.getCurrentPosition()) {
                playbackState.seekTo(seconds);
            }
        });
        player.play();
    }

    /**
     * Mette in pausa la riproduzione corrente, mantenendo la posizione attuale
     * e le risorse del player allocate. La riproduzione può essere ripresa
     * chiamando nuovamente player.play() sul MediaPlayer attivo.
     */

    public void pause ()
    {
        if (player != null){
            player.pause();
        }
    }

    /**
     * Ferma definitivamente la riproduzione e libera tutte le risorse associate
     * al MediaPlayer tramite dispose(). Viene chiamato anche all'inizio
     * di start() per garantire la corretta gestione delle risorse
     * quando si cambia traccia.
     */
    
    public void stop() 
    {
        if (player != null){
            player.stop();
            player.dispose();
            player = null;
        }
        
    }

    public void resume() 
    {
        if (player != null) 
        {
            player.play();
        }
    }

    public void setOnEndOfTrack(Runnable callback)
    {
        if (player != null)
        {
            player.setOnEndOfMedia(callback);
        }
    }

    /**
     * Sposta la riproduzione alla posizione specificata in secondi.
     * @param seconds La posizione di destinazione in secondi.
     */
    public void seekTo(double seconds)
    {
        if (player != null)
        {
            player.seek(javafx.util.Duration.seconds(seconds));
        }
    }

}