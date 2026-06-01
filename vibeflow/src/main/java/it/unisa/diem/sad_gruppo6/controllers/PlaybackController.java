/**
 * @file PlaybackController.java
 * Classe controller che agisce da intermediario tra l'interfaccia utente (View)
 * e la macchina a stati del player musicale (PlaybackState).
 * Delega i comandi di navigazione e gestione flusso allo stato globale.
 * * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.controllers;
import it.unisa.diem.sad_gruppo6.models.PlaybackState;
import it.unisa.diem.sad_gruppo6.models.PlaybackService;

public class PlaybackController {
    
    // Attributi
    private PlaybackState playbackState;
    private PlaybackService playbackService;

    /**
     * Costruttore base per il PlaybackController.
     * @param playbackState L'istanza Singleton dello stato del player.
     */
    public PlaybackController(PlaybackState playbackState) {
        this.playbackState = playbackState;
    }

    /**
     * Invoca l'avanzamento alla traccia successiva delegando l'azione
     * al metodo next() di PlaybackState (che userà lo stato corrente).
     */
    public void next() {
        if (playbackState != null) {
            playbackState.next(); 
        }
    }

    /**
     * Invoca il ritorno alla traccia precedente delegando l'azione
     * al metodo previous() di PlaybackState (che userà lo stato corrente).
     */
    public void previous() {
        if (playbackState != null) {
            playbackState.previous();
        }
    }
    
}

