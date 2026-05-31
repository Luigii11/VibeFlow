/**
 * @file CreatePlaylistCommand.java
 * Classe concreta che implementa l'interfaccia AppCommand.
 * Incapsula l'azione di creazione (aggiunta) di una nuova playlist all'interno della libreria,
 * permettendo al CommandManager di eseguirla ed eventualmente annullarla.
 * * @pattern Command
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.commands;

import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;

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
     
}