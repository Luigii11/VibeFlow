/**
 * @file RenamePlaylistCommand.java
 * Comando concreto che incapsula l'operazione di rinomina di una playlist.
 * Memorizza il vecchio nome per supportare l'annullamento dell'operazione (undo).
 *
 * @pattern Command
 *
 * @see AppCommand
 * @see PlaylistLibrary
 * @see Playlist
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.command;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;

public class RenamePlaylistCommand implements AppCommand {

    private final Playlist playlist;
    private final String oldName;
    private final String newName;
    private final PlaylistLibrary playlistLibrary;

    /**
     * Costruttore del comando di rinomina.
     *
     * @param playlistLibrary La libreria delle playlist, usata per notificare gli observer.
     * @param playlist        La playlist da rinominare.
     * @param oldName         Il nome corrente della playlist, salvato per l'eventuale undo.
     * @param newName         Il nuovo nome da assegnare alla playlist.
     */
    public RenamePlaylistCommand(PlaylistLibrary playlistLibrary, Playlist playlist,
                                  String oldName, String newName) {
        this.playlistLibrary = playlistLibrary;
        this.playlist = playlist;
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * Esegue il comando: assegna il nuovo nome alla playlist tramite {@link Playlist#setName(String)}
     * e notifica gli observer tramite {@link PlaylistLibrary#updatePlaylist(Playlist)}.
     */
    @Override
    public void execute() {
        playlist.setName(newName);
        playlistLibrary.updatePlaylist(playlist);
    }

    /**
     * Annulla il comando: ripristina il nome precedente della playlist.
     * Supporta il meccanismo di undo del {@link CommandManager}.
     */
    public void undo() {
        playlist.setName(oldName);
        playlistLibrary.updatePlaylist(playlist);
    }
}