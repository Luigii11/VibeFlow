/**
 * Classe concreta che rappresenta lo stato di riproduzione del player.
 * Implementa l'interfaccia 'PlayerState' e definisce il comportamento specifico per lo stato di riproduzione.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.playback.states;
import it.unisa.diem.sad_gruppo6.playback.PlaybackState;

public class PlayingState implements PlayerState {

    /**
     * Restituisce il nome dello stato attuale del player, in questo caso "Playing".
     * @return "Playing" come nome dello stato attuale del player.
    */
    @Override
    public String getStatusName() 
    {
        return "Playing";
    }

    /**
     * Gestisce l'azione di play quando il player è già in stato di riproduzione. 
     * In questo caso, non è necessario eseguire alcuna azione poiché il player è già in riproduzione.
     * 
     * @param ctx Il contesto del player
    */
   
    @Override
    public void play(PlaybackState ctx) 
    {
        return;
    }

    /**
     * Gestisce l'azione di pausa quando il player è in stato di riproduzione. 
     * In questo caso, cambia lo stato del player a "PausedState" per mettere in pausa la riproduzione.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato del player a "PausedState".
    */

    @Override
    public void pause(PlaybackState ctx) 
    {
        ctx.changeState(new PausedState());
    }

    
}
