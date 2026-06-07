/**
 * @file RemoveTrackFromPlaylistCommand.java
 * Classe completa che implementa l'interfaccia 'AppCommand'. 
 * Incapsula l'azione di rimozione di una traccia da una specifica playlist.
 * 
 * @pattern Command
 * 
 * @see AppCommand
 * @see Playlist
 * @see Track
 * 
 * @author EmanuelaGraziuso
 * */

package it.unisa.diem.sad_gruppo6.model.command;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;

public class RemoveTrackFromPlaylistCommand implements AppCommand {

    private final Playlist playlist;
    private final Track track;
    private int originalIndex;

    /**
     * Costruttore della classe.
     * * 
     * @param playlist La playlist da cui rimuovere la traccia.
     * @param track    La traccia da rimuovere dalla playlist.
     */
    
    public RemoveTrackFromPlaylistCommand(Playlist playlist, Track track) {
        this.playlist = playlist;
        this.track = track;
    }

    /**
     * Metodo che esegue l'azione di rimozione della traccia dalla playlist.
     * Verifica se la traccia è presente nella playlist prima di tentare la rimozione.
     * Se la traccia non è presente, non viene eseguita alcuna azione.
     */
    @Override
    public void execute() {
        originalIndex = playlist.getTracks().indexOf(track);

        if (playlist.getTracks().contains(track)) {
            playlist.getTracks().remove(track);
        }
    }
     /**
     * Annulla la rimozione reinserendo la traccia nella posizione originale.
     */
    @Override
    public void undo() {
        int safeIndex = Math.max(0, Math.min(originalIndex, playlist.getTracks().size()));
        playlist.getTracks().add(safeIndex, track);
    }
}

