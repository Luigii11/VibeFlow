/**
 * @file CreatePlaylistCommand.java
 * Classe concreta che implementa l'interfaccia AppCommand.
 * Incapsula l'azione di creazione (aggiunta) di una nuova playlist all'interno della libreria,
 * permettendo al CommandManager di eseguirla ed eventualmente annullarla.
 * * @pattern Command
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.model.command;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.playback.states.PausedState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

public class CreatePlaylistCommand implements AppCommand {

    private PlaylistLibrary playlistLibrary;
    private Playlist playlistToAdd;

    /**
     * Costruttore del comando.
     * * @param playlistLibrary La libreria su cui operare.
     * @param playlistToAdd La playlist da aggiungere.
     */
    public CreatePlaylistCommand(PlaylistLibrary playlistLibrary, Playlist playlistToAdd) {
        this.playlistLibrary = playlistLibrary;
        this.playlistToAdd = playlistToAdd;
    }

    /**
     * Esegue il comando aggiungendo la playlist alla libreria.
     */
    @Override
    public void execute() {
        playlistLibrary.addPlaylist(playlistToAdd);
    }
     
     /**
     * Annulla il comando rimuovendo la playlist dalla libreria e fermando la riproduzione se era in corso su di essa.
     */
    @Override
    public void undo() {
        PlaybackState playbackState = PlaybackState.getInstance();
        if (playlistToAdd.equals(playbackState.getCurrentPlaylist())) {
            PlaybackService.getInstance().stop();
            playbackState.setCurrentTrack(null);
            playbackState.seekTo(0);
            playbackState.changeState(new PausedState());
        }
        playlistLibrary.removePlaylist(playlistToAdd);
    }
}