/**
 * @file UndoRemoveTrackTest.java
 * Classe di test per la validazione della funzionalità di undo della rimozione
 * di una traccia dalla libreria e da una playlist.
 *
 * Scenari coperti:
 * - undo rimozione traccia dalla TrackLibrary: la traccia viene ripristinata
 * - undo rimozione traccia dalla TrackLibrary: viene ripristinata nella posizione originale
 * - undo rimozione traccia dalla TrackLibrary: viene ripristinata anche nelle playlist che la contenevano
 * - undo rimozione traccia da una playlist: la traccia viene ripristinata
 * - undo rimozione traccia da una playlist: viene ripristinata nella posizione originale
 * - undo rimozione traccia da una playlist: le altre tracce rimangono intatte
 *
 * @author EmanuelaGraziuso
 */

package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromPlaylistCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class UndoRemoveTrackTest {

    private TrackLibrary testLibrary;
    private PlaylistLibrary testPlaylistLibrary;
    private CommandManager testCommandManager;
    private Playlist currentPlaylist;
    private Track trackA;
    private Track trackB;
    private Track trackC;

    /**
     * Metodo di setup eseguito prima di ogni singolo test.
     * Prepara la TrackLibrary con due tracce e una playlist con trackA già inserita.
     */
    @BeforeEach
    public void setUp() throws Exception {
        testLibrary = TrackLibrary.getInstance();

        Field tracksField = TrackLibrary.class.getDeclaredField("tracks");
        tracksField.setAccessible(true);
        ((LinkedHashSet<?>) tracksField.get(testLibrary)).clear();

        testPlaylistLibrary = PlaylistLibrary.getInstance();
        testPlaylistLibrary.clear();

        testCommandManager = CommandManager.getInstance();
        Field historyField = CommandManager.class.getDeclaredField("history");
        historyField.setAccessible(true);
        ((java.util.Stack<?>) historyField.get(testCommandManager)).clear();

        trackA = new Track("Napule è", "Pino Daniele", 227, "Pop", 1977, null);
        trackB = new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979, null);
        trackC = new Track("Quanno chiove", "Pino Daniele", 275, "Blues", 1980, null);

        testLibrary.addTrack(trackA);
        testLibrary.addTrack(trackB);

        currentPlaylist = new Playlist("Test Playlist", false);
        testPlaylistLibrary.addPlaylist(currentPlaylist);
        currentPlaylist.addTrack(trackA);
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromLibraryCommand
     * la traccia sia di nuovo presente nella TrackLibrary.
     */
    @Test
    public void testUndoRemoveFromLibrary_trackRestoredInLibrary() {
        testCommandManager.execute(new RemoveTrackFromLibraryCommand(trackA));
        assertFalse(testLibrary.getTracks().contains(trackA),
                "Precondizione: la traccia non deve essere in libreria dopo execute()");

        testCommandManager.undo();

        assertTrue(testLibrary.getTracks().contains(trackA),
                "Dopo undo() la traccia deve essere di nuovo presente in libreria");
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromLibraryCommand
     * la traccia venga ripristinata nella posizione originale.
     */
    @Test
    public void testUndoRemoveFromLibrary_trackRestoredAtOriginalIndex() {
        // trackA è in posizione 0, trackB in posizione 1
        testCommandManager.execute(new RemoveTrackFromLibraryCommand(trackA));
        testCommandManager.undo();

        assertEquals(0, testLibrary.getTracks().indexOf(trackA),
                "Dopo undo() trackA deve essere ripristinata alla posizione originale (indice 0)");
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromLibraryCommand
     * la traccia venga ripristinata anche nella playlist che la conteneva.
     */
    @Test
    public void testUndoRemoveFromLibrary_trackRestoredInPlaylist() {
        assertTrue(currentPlaylist.getTracks().contains(trackA),
                "Precondizione: trackA deve essere nella playlist");

        testCommandManager.execute(new RemoveTrackFromLibraryCommand(trackA));
        assertFalse(currentPlaylist.getTracks().contains(trackA),
                "Precondizione: dopo execute() trackA non deve essere nella playlist");

        testCommandManager.undo();

        assertTrue(currentPlaylist.getTracks().contains(trackA),
                "Dopo undo() trackA deve essere ripristinata anche nella playlist");
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromPlaylistCommand
     * la traccia sia di nuovo presente nella playlist.
     */
    @Test
    public void testUndoRemoveFromPlaylist_trackRestoredInPlaylist() {
        testCommandManager.execute(new RemoveTrackFromPlaylistCommand(currentPlaylist, trackA));
        assertFalse(currentPlaylist.getTracks().contains(trackA),
                "Precondizione: la traccia non deve essere nella playlist dopo execute()");

        testCommandManager.undo();

        assertTrue(currentPlaylist.getTracks().contains(trackA),
                "Dopo undo() la traccia deve essere di nuovo presente nella playlist");
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromPlaylistCommand
     * la traccia venga ripristinata nella posizione originale.
     */
    @Test
    public void testUndoRemoveFromPlaylist_trackRestoredAtOriginalIndex() {
        currentPlaylist.addTrack(trackB);
        currentPlaylist.addTrack(trackC);
        // ordine: [trackA(0), trackB(1), trackC(2)]

        testCommandManager.execute(new RemoveTrackFromPlaylistCommand(currentPlaylist, trackB));
        testCommandManager.undo();

        assertEquals(1, currentPlaylist.getTracks().indexOf(trackB),
                "Dopo undo() trackB deve essere ripristinata all'indice originale (1)");
    }

    /**
     * Verifica che dopo undo() di RemoveTrackFromPlaylistCommand
     * le altre tracce della playlist rimangano intatte.
     */
    @Test
    public void testUndoRemoveFromPlaylist_otherTracksUntouched() {
        currentPlaylist.addTrack(trackB);

        testCommandManager.execute(new RemoveTrackFromPlaylistCommand(currentPlaylist, trackA));
        testCommandManager.undo();

        assertEquals(2, currentPlaylist.getTracks().size(),
                "Dopo undo() la playlist deve contenere di nuovo 2 tracce");
        assertTrue(currentPlaylist.getTracks().contains(trackB),
                "La traccia non coinvolta nell'undo deve essere ancora presente");
    }
}