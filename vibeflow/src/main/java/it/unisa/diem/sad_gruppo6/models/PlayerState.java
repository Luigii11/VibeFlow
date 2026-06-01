/**
 * @file PlayerState.java
 * Interfaccia per l'implementazione del pattern State sul media player.
 * Definisce i comportamenti standard di riproduzione che ogni stato concreto 
 * (PlayingState, PausedState) deve implementare.
 * * @pattern State
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.models;

public interface PlayerState {
    
    /**
     * Restituisce il nome dello stato corrente (es. "Playing", "Paused").
     * @return Una stringa rappresentante il nome dello stato.
     */
    String getStatusName();

    /**
     * Gestisce la richiesta di riproduzione (Play) in base allo stato corrente.
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    void play(PlaybackState ctx);

    /**
     * Gestisce la richiesta di pausa (Pause) in base allo stato corrente.
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    void pause(PlaybackState ctx);

    /**
     * Gestisce la richiesta di avanzamento alla traccia successiva (Next).
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    void next(PlaybackState ctx);

    /**
     * Gestisce la richiesta di ritorno alla traccia precedente (Previous).
     * Implementa la regola di business sul minutaggio (es. 10 secondi).
     * @param ctx Il contesto del PlaybackState contenente le informazioni globali.
     */
    void previous(PlaybackState ctx);
}