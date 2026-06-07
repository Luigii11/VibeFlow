/**
 * @file TrackControllerLibraryDeleteTest.java
 * Classe di test per la validazione della logica di business relativa
 * alla rimozione definitiva di una traccia dalla LIBRERIA GENERALE (ID_3: Rimuovi Track).
 * * <p>Nota di ambito: Questa suite si occupa esclusivamente dell'eliminazione del 
 * record dal catalogo globale (TrackLibrary). La rimozione di tracce dalle singole 
 * playlist (ID_Playlist) esula dal presente task ed è gestita da controller separati.</p>
 *
 * @see TrackController
 * @see TrackLibrary
 * @see it.unisa.diem.sad_gruppo6.commands.RemoveTrackFromLibraryCommand
 *
 * @author ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class TrackControllerLibraryDeleteTest{

    private TrackLibrary testLibrary;
    private TrackController testController;
    private Track existingTrack;

    /**
     * Setup eseguito prima di ogni test.
     * Svuota il catalogo globale e inserisce una traccia di partenza nella libreria generale.
     */
    @BeforeEach
    public void setUp() {
        testLibrary = TrackLibrary.getInstance();
        
        // Svuota la libreria in modo manuale se non esiste il metodo clear()
        java.util.List<Track> currentTracks = new java.util.ArrayList<>(testLibrary.getTracks());
        for (Track t : currentTracks) {
            testLibrary.removeTrack(t); 
        }

        testController = new TrackController();

        existingTrack = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975, null);
        testLibrary.addTrack(existingTrack);
    }

    /**
     * Testa la rimozione corretta di una traccia presente nel catalogo globale.
     * Verifica che la libreria generale sia vuota dopo la rimozione.
     */
    @Test
    public void testDeleteTrackFromGeneralLibrarySuccess() {
        testController.deleteTrack(existingTrack);

        assertEquals(0, testLibrary.getTracks().size(),
            "La libreria generale dovrebbe essere vuota dopo la rimozione");
    }

    /**
     * Verifica che la traccia rimossa non compaia più nell'elenco generale della libreria,
     * assicurando che altre tracce eventualmente presenti rimangano intatte nel catalogo.
     */
    @Test
    public void testDeletedTrackNotInGeneralLibrary() {
        Track secondTrack = new Track("Imagine", "John Lennon", 187, "Pop", 1971, null);
        testLibrary.addTrack(secondTrack);

        testController.deleteTrack(existingTrack);

        assertFalse(testLibrary.getTracks().contains(existingTrack),
            "La traccia eliminata non dovrebbe essere presente nella libreria generale");
        assertEquals(1, testLibrary.getTracks().size(), 
            "La libreria generale deve conservare le altre tracce non eliminate");
    }

    /**
     * Testa il tentativo di rimozione di una traccia mai inserita nel catalogo generale.
     * Verifica che venga lanciata {@link IllegalArgumentException} e che la libreria rimanga invariata.
     */
    @Test
    public void testDeleteTrackNotInGeneralLibraryThrowsException() {
        Track notInLibrary = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971, null);

        assertThrows(IllegalArgumentException.class, () ->
            testController.deleteTrack(notInLibrary),
            "Dovrebbe lanciare un'eccezione per traccia non presente nella libreria generale"
        );

        assertEquals(1, testLibrary.getTracks().size(),
            "Il catalogo generale deve rimanere invariato");
    }

    /**
     * Testa il tentativo di rimozione passando un parametro null al controller della libreria generale.
     * Verifica che venga lanciata {@link IllegalArgumentException}.
     */
    @Test
    public void testDeleteNullTrackThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.deleteTrack(null),
            "Dovrebbe lanciare un'eccezione per traccia null"
        );
    }
}