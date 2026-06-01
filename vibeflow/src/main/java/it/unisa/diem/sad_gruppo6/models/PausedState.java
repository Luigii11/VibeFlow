/**
 * @file PausedState.java
 * Classe concreta che implementa lo stato di "In Pausa" (Paused).
 * Definisce come il player reagisce ai comandi di navigazione (skip avanti/indietro)
 * quando la riproduzione è sospesa.
 * * @pattern State
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.models;

public class PausedState implements PlayerState {

    @Override
    public String getStatusName() {
        return "Paused";
    }

    @Override
    public void play(PlaybackState ctx) {
    
    }

    @Override
    public void pause(PlaybackState ctx) {
        
    }

    /**
     * Esegue lo skip in avanti mentre è in pausa.
     * Cambia la traccia e la riporta a 0, ma NON avvia la riproduzione.
     * @param ctx Il contesto globale dello stato di riproduzione.
     */
    @Override
    public void next(PlaybackState ctx) {
        PlaylistIterator iterator = ctx.getIterator();
        
        if (iterator != null && iterator.hasNext()) {
            ctx.setCurrentTrack(iterator.next());
            ctx.setCurrentPosition(0);
            ctx.notifyObservers(); 
        }
    }

    /**
     * Esegue lo skip all'indietro mentre è in pausa.
     * Applica la regola dei 10 secondi e resetta a 0, mantenendo lo stato di pausa.
     * @param ctx Il contesto globale dello stato di riproduzione.
     */
    @Override
    public void previous(PlaybackState ctx) {
        // La regola dei 10 secondi vale anche in pausa
        if (ctx.getCurrentPosition() >= 10) {
            ctx.setCurrentPosition(0);
        } else {
            PlaylistIterator iterator = ctx.getIterator();
            if (iterator != null && iterator.hasPrevious()) {
                ctx.setCurrentTrack(iterator.previous());
            }
            ctx.setCurrentPosition(0);
        }
        
        ctx.notifyObservers(); 
    }
}