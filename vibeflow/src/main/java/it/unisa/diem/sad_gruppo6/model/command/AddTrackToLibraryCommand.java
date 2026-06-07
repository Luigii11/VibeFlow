/**
 * @file AddTrackToLibraryCommand.java
 * Classe di definizione di un comando per aggiungere una traccia alla libreria. Implementa 
 * l'interfaccia AppCommand e utilizza il 
 * pattern Command per incapsulare l'azione di aggiunta di una traccia alla TrackLibrary.
 * 
 * 
 * @pattern Command
 * 
 * @see AppCommand
 * @see TrackLibrary 
 * 
 * @author EmanuelChirico
 */
package it.unisa.diem.sad_gruppo6.model.command;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class AddTrackToLibraryCommand implements AppCommand 
{
    private TrackLibrary library;
    private Track track;
    
    /** 
     * Costruttore del comando AddTrackToLibraryCommand.
     * 
     * @param library la libreria a cui aggiungere la traccia.
     * @param track la traccia da aggiungere alla libreria.
     */
    public AddTrackToLibraryCommand(TrackLibrary library, Track track) 
    {
        this.library = library;
        this.track = track;
    }

    /**
     * Metodo execute sovrascritto per eseguire l'azione di aggiunta della traccia alla libreria.
     * 
     */

    @Override
    public void execute() 
    {
        library.addTrack(track);
    }
    
    /**
     * Annulla il comando rimuovendo la traccia dalla libreria.
     */
    @Override
    public void undo() {
        library.removeTrack(track);
    }
}
