/**
 * @file RemoveTrackFromLibraryCommand.java
 *
 * Classe concreta che implementa l'interfaccia {@link AppCommand} per
 * incapsulare l'azione di rimozione di una traccia dalla {@link TrackLibrary}.
 * Segue il pattern Command: l'operazione è reversibile tramite {@code undo()},
 * che reinserisce la traccia rimossa nella posizione originale.
 *
 * @pattern Command
 *
 * @see AppCommand
 * @see TrackLibrary
 * @see Track
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.command;

import java.util.List;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class RemoveTrackFromLibraryCommand implements AppCommand
{
    private final TrackLibrary library;
    private final PlaylistLibrary playlistLibrary;
    private final Track track;

    /**
     * Costruttore del comando RemoveTrackFromLibraryCommand.
     * Acquisisce l'istanza Singleton della {@link TrackLibrary}.
     *
     * @param track la traccia da rimuovere dalla libreria.
     */
    public RemoveTrackFromLibraryCommand(Track track)
    {
        this.library = TrackLibrary.getInstance();
        this.playlistLibrary = PlaylistLibrary.getInstance();
        this.track   = track;
    }

    /**
     * Esegue la rimozione della traccia dalla libreria.
     * Dopo l'esecuzione, la {@link TrackLibrary} notifica automaticamente
     * gli observer registrati tramite {@code notifyObservers()}.
     */
    @Override
    public void execute()
    {
        library.removeTrack(track);
        List<Playlist> allPlaylists = playlistLibrary.getPlaylists();
        for (Playlist p : allPlaylists) {
            if (p.getTracks().contains(track)) {
                p.getTracks().remove(track);
                playlistLibrary.updatePlaylist(p); 
            }
        }
    }
}