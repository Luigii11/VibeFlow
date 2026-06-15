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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class RemoveTrackFromLibraryCommand implements AppCommand
{
    private final TrackLibrary library;
    private final PlaylistLibrary playlistLibrary;
    private final Track track;

    private int originalIndex;
    private final Map<Playlist, Integer> originalPlaylistPositions = new HashMap<>();


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
        originalIndex = library.getTracks().indexOf(track);
        originalPlaylistPositions.clear();

        List<Playlist> allPlaylists = playlistLibrary.getPlaylists();
        for (Playlist p : allPlaylists) {
            int pos = p.getTracks().indexOf(track);
            if (pos>=0) {
                originalPlaylistPositions.put(p, pos);
                p.getTracks().remove(track);
                playlistLibrary.updatePlaylist(p); 
            }
        }
        library.removeTrack(track);
    }
     /**
     * Annulla la rimozione reinserendo la traccia nella posizione originale memorizzata.
     */
    @Override
    public void undo() {
        library.addTrackAtIndex(track, originalIndex);
        for (Map.Entry<Playlist, Integer> entry : originalPlaylistPositions.entrySet()) {
            Playlist p = entry.getKey();
            int safeIndex = Math.max(0, Math.min(entry.getValue(), p.getTracks().size()));
            p.getTracks().add(safeIndex, track);
            if (!playlistLibrary.getPlaylists().contains(p)) {
                playlistLibrary.addPlaylist(p);
            } else {
                playlistLibrary.updatePlaylist(p);
            }
        }
    }
}