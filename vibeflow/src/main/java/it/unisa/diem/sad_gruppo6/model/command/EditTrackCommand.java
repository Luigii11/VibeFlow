/**
 * @file EditTrackCommand.java
 *
 * Classe concreta che implementa l'interfaccia AppCommand per incapsulare
 * l'azione di modifica dei metadati di una traccia già presente nella
 * TrackLibrary.
 *
 * @pattern Command
 * @see AppCommand
 * @see TrackLibrary
 * @see Track
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.command;

import java.util.List;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;

public class EditTrackCommand implements AppCommand
{
    private final TrackLibrary library;
    private final PlaylistLibrary playlistLibrary;
    private final Track oldTrack;
    private final Track updatedTrack;

    /**
     * Costruttore del comando EditTrackCommand.
     *
     * @param oldTrack     la traccia originale da sostituire.
     * @param updatedTrack la traccia con i nuovi metadati.
     */
    public EditTrackCommand(Track oldTrack, Track updatedTrack)
    {
        // Utilizza il Singleton strutturato da Luigi
        this.library      = TrackLibrary.getInstance();
        this.playlistLibrary = PlaylistLibrary.getInstance();
        this.oldTrack     = oldTrack;
        this.updatedTrack = updatedTrack;
    }

    @Override
    public void execute()
    {
        library.updateTrack(oldTrack, updatedTrack);

        List<Playlist> allPlaylists = playlistLibrary.getPlaylists();
        for (Playlist p : allPlaylists) {
            List<Track> tracksInPlaylist = p.getTracks();
            
            // Se la playlist contiene la vecchia traccia
            if (tracksInPlaylist.contains(oldTrack)) {
                // Sostituisci l'oggetto vecchio con quello nuovo
                int index = tracksInPlaylist.indexOf(oldTrack);
                tracksInPlaylist.set(index, updatedTrack);
            }
        }
        
        playlistLibrary.updatePlaylist(null); // O il metodo equivalente per forzare il notifyObservers()
    }
}