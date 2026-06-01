/**
 * @file PlayingState.java
 * Classe concreta che implementa lo stato di "Riproduzione in corso" (Playing).
 * Definisce come il player reagisce ai comandi di navigazione (skip avanti/indietro)
 * mentre la musica sta suonando, applicando le regole di business specifiche.
 * * @pattern State
 * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.models;

public class PlayingState implements PlayerState {

    @Override
    public String getStatusName() {
        return "Playing";
    }    

    @Override
    public void play(PlaybackState ctx) {
        
    }

    @Override 
    public void pause(PlaybackState ctx) {

    }

    /**
     * Esegue lo skip in avanti interrogando il PlaylistIterator.
     * Se è presente una traccia successiva, aggiorna la traccia corrente 
     * e resetta il contatore di riproduzione a 0.
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
     * Esegue lo skip all'indietro o il riavvio della traccia attuale.
     * Se la traccia in esecuzione ha superato i 10 secondi, viene semplicemente riavviata da 0.
     * Se si trova prima dei 10 secondi, tenta di caricare la traccia precedente dalla coda.
     * @param ctx Il contesto globale dello stato di riproduzione.
     */
    @Override
    public void previous(PlaybackState ctx) {
        // Regola di business dei 10 secondi
        if (ctx.getCurrentPosition() >= 10) {
            ctx.setCurrentPosition(0);
        } else {
            PlaylistIterator iterator = ctx.getIterator();
            if (iterator != null && iterator.hasPrevious()) {
                ctx.setCurrentTrack(iterator.previous());
            }
            // Riporta a 0 sia in caso di cambio traccia che in caso di ritorno all'inizio del brano attuale
            ctx.setCurrentPosition(0);
        }
        
        ctx.notifyObservers();
    }
}
