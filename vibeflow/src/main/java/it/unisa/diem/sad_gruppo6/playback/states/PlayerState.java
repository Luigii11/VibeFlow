/**
 * Interfaccia che definisce i metodi per le azioni di play e pause, next e 
 * previous e per ottenere il nome dello stato attuale del player.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.playback.states;
import it.unisa.diem.sad_gruppo6.playback.PlaybackState;

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

    
}
