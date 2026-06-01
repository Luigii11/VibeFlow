/**
 * Classe concreta che rappresenta lo stato di pausa del player.
 * Implementa l'interfaccia 'PlayerState' e definisce il comportamento specifico per lo stato di pausa.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.playback.states;
import it.unisa.diem.sad_gruppo6.playback.PlaybackState;

public class PausedState implements PlayerState 
{

    /**
     * Restituisce il nome dello stato attuale del player, in questo caso "Paused".
     * @return "Paused" come nome dello stato attuale del player.
    */

    @Override
    public String getStatusName() 
    {
        return "Paused";
    }

    /**
     * Gestisce l'azione di attivazione del player quando è in pausa. 
     * In questo caso, cambia lo stato del player a "PlayingState" per riprodurre la traccia.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato del player a "PlayingState".
    */

    @Override
    public void play(PlaybackState ctx) 
    {
        ctx.changeState(new PausedState());
    }

    /**
     * Gestisce l'azione di pause quando il player è già in stato di pausa. 
     * In questo caso, non è necessario eseguire alcuna azione poiché il player è già in pausa.
     * 
     * @param ctx Il contesto del player
    */

    @Override
    public void pause(PlaybackState ctx) 
    {
        return;
    }
    
}
