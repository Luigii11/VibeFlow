/**
 * @file AddTrackToPlaylistCommand.java
 * Classe concreta che implementa l'interfaccia AppCommand.
 * Incapsula l'azione di aggiunta di una traccia a una specifica playlist, permettendo al CommandManager di eseguirla.
 * 
 * @pattern Command
 * 
 * @see AppCommand
 * @see Playlist
 * @see Track
 * 
 * @author EmanuelaGraziuso
 */




package it.unisa.diem.sad_gruppo6.model.command;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.domain.Track;

public class AddTrackToPlaylistCommand implements AppCommand {

    private final Playlist playlist;
    private final Track track;
    

    /**
     * Costruttore del comando.
     * 
     * @param playlist  La playlist a cui aggiungere la traccia.
     * @param track     La traccia da aggiungere alla playlist.
     */

    public AddTrackToPlaylistCommand(Playlist playlist, Track track) {
        this.playlist = playlist;
        this.track = track; 
    }

    /**
     * Esegue il comando aggiungendo l traccia alla playlist.
     */
    @Override
    public void execute() {
        playlist.addTrack(track);
        
    }

    /**
     * Annulla il comando rimuovendo la traccia dalla playlist.
     */
    @Override
    public void undo(){
        playlist.removeTrack(track);

    }
}
