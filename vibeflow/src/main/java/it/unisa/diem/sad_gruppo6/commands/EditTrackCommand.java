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
package it.unisa.diem.sad_gruppo6.commands;

import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;

public class EditTrackCommand implements AppCommand
{
    private final TrackLibrary library;
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
        this.oldTrack     = oldTrack;
        this.updatedTrack = updatedTrack;
    }

    @Override
    public void execute()
    {
        library.updateTrack(oldTrack, updatedTrack);
    }
}