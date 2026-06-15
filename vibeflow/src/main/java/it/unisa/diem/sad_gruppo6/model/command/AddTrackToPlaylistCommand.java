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
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.playback.states.PausedState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

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
        PlaylistLibrary.getInstance().updatePlaylist(playlist);
        
    }

    /**
     * Annulla il comando rimuovendo la traccia dalla playlist.
     */
    @Override
    public void undo(){
        PlaybackState playbackState = PlaybackState.getInstance();
        if (track.equals(playbackState.getCurrentTrack()) &&
                playlist.equals(playbackState.getCurrentPlaylist())) {
            PlaybackService.getInstance().stop();
            playbackState.setCurrentTrack(null);
            playbackState.seekTo(0);
            playbackState.changeState(new PausedState());
        }
        playlist.removeTrack(track);
        PlaylistLibrary.getInstance().updatePlaylist(playlist);
    }
}
