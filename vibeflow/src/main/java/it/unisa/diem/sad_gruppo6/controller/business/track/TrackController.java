/**
 * @file TrackController.java
 * Classe di definizione di un oggetto di tipo 'TrackController', controller per la gestione delle tracce musicali.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.business.track;

import it.unisa.diem.sad_gruppo6.model.command.AddTrackToLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.command.EditTrackCommand;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.utility.AudioMetadataExtractor;

public class TrackController 
{
    private TrackLibrary library;
    private CommandManager commandManager;
    private Track trackToEdit;   

    public TrackController() 
    {
        this.library = TrackLibrary.getInstance();
        this.commandManager = CommandManager.getInstance();
    }

    public void createTrack(String title, String author, String genre, int year, String path) 
    {
        int length = AudioMetadataExtractor.extractDuration(path);
        Track track = new Track(title, author, length, genre, year, path);
        AddTrackToLibraryCommand command = new AddTrackToLibraryCommand(library, track);
        commandManager.execute(command);
    }

   
    public void editTrack(Track target, String title, String author, String genre, int year, String path)
    {
        int length = AudioMetadataExtractor.extractDuration(path);
        Track updatedTrack = new Track(title, author, length, genre, year, path);
        EditTrackCommand command = new EditTrackCommand(target, updatedTrack);
        commandManager.execute(command);
    }

    /**
     * Rimuove una traccia dalla lista globale della libreria.
     *
     * @param track la traccia da rimuovere dalla libreria generale.
     * @throws IllegalArgumentException se la traccia è null o non presente in libreria.
     */
    public void deleteTrack(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("Impossibile rimuovere una traccia null.");
        }
        if (!library.getTracks().contains(track)) {
            throw new IllegalArgumentException("La traccia da rimuovere non è presente in libreria.");
        }

        // Incapsula l'azione nel comando richiesto dal task
        RemoveTrackFromLibraryCommand command = new RemoveTrackFromLibraryCommand(track);
        commandManager.execute(command);
    }

}