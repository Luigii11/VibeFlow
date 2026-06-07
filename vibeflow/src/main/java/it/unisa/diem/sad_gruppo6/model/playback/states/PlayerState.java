/**
 * @file PlayerState.java
 * Interfaccia che definisce i metodi per le azioni di play e pause, next e 
 * previous e per ottenere il nome dello stato attuale del player.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.model.playback.states;

import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

public interface PlayerState 
{

    /**
     * Restituisce il nome dello stato attuale del player.
     * 
     * @return Il nome dello stato attuale del player.
     */

    String getStatusName();

    /**
     * Esegue l'azione di play e cambia lo stato del player se necessario.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato se necessario.
     */

    void play(PlaybackState ctx);

    /**
     * Esegue l'azione di pause e cambia lo stato del player se necessario.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato se necessario.
     */
    
    void pause(PlaybackState ctx); 

    /**
     * Gestisce la richiesta di avanzamento alla traccia successiva (Next).
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    default void next(PlaybackState ctx) 
    {
        PlaylistIterator iterator = ctx.getIterator();
        if (iterator != null && iterator.hasNext()) 
        {
            ctx.setCurrentTrack(iterator.next());
            ctx.seekTo(0);
        }
    }
    /**
     * Gestisce la richiesta di ritorno alla traccia precedente (Previous).
     * Implementa la regola di business sul minutaggio (es. 10 secondi).
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    default void previous(PlaybackState ctx) {
        if (ctx.getCurrentPosition() >= 10) {
            ctx.seekTo(0);
        } else {
            PlaylistIterator iterator = ctx.getIterator();
            if (iterator != null && iterator.hasPrevious()) {
                ctx.setCurrentTrack(iterator.previous());
            }
            ctx.seekTo(0);
        }
    }
}

