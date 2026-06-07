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
>>>>>>> Stashed changes
 * @pattern Service
 * @pattern Singleton
 * @author EmanuelChirico
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.service;

import javafx.animation.Timeline;

import java.io.File;
import java.io.FileNotFoundException;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

public class PlaybackService {

    /* Attributi */
    private PlaybackState playbackState;
    private Track currentTrack;
    private static PlaybackService instance;
    private Timeline timeline;
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
     * Avvia la riproduzione audio della traccia corrente memorizzata in
     * PlaybackState. Ferma e libera eventuali player attivi prima di
     * crearne uno nuovo, garantendo che la riproduzione riparta sempre dall'inizio.
     * Non avvia alcuna riproduzione se non è presente una traccia corrente.
     */

    public void start()  throws FileNotFoundException {
        stop();
        currentTrack = playbackState.getCurrentTrack();
        if (currentTrack == null) {
            return; 
        }
        String currentTrackPath = currentTrack.getPath();   
        File filepath = new File(currentTrackPath);
        if (!filepath.exists()){
            throw new FileNotFoundException("La traccia audio selezionata non esiste più nel path originale.");
        }     
        String play = filepath.toURI().toString();
        Media playbleSong = new Media(play);
        player = new MediaPlayer(playbleSong);
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
        Track currentTrack = playbackState.getCurrentTrack();
        if (currentTrack == null) {
            stop();
            return;
        }

        int currentPos = playbackState.getCurrentPosition();
        int totalDuration = currentTrack.getDuration();

        if (currentPos < totalDuration) {
            // La traccia è ancora in corso: avanza di 1 secondo
            playbackState.seekTo(currentPos + 1);
        } else {
            // La traccia corrente è terminata. Controlliamo se c'è un brano successivo.
            PlaylistIterator iterator = playbackState.getIterator();
            
            if (iterator != null && iterator.hasNext()) {
                // C'è un'altra traccia. Auto-scorrimento in avanti.
                Track nextTrack = iterator.next();
                playbackState.setCurrentTrack(nextTrack);
                playbackState.seekTo(0);
                // Non serve chiamare start(), la timeline è "INDEFINITE" e continuerà a ticchettare
            } else {
                // La playlist è finita. Nessun brano successivo.
                stop();
                playbackState.pause();
                playbackState.seekTo(0);
            }
        }
    }
}