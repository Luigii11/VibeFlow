/**
 * @file UndoAddTrackTest.java
 * Classe di test per la validazione della funzionalità di undo dell'aggiunta
 * di una traccia alla libreria e a una playlist.
 *
 * Scenari coperti:
 * - undo aggiunta traccia alla TrackLibrary: la traccia viene rimossa
 * - undo aggiunta traccia alla TrackLibrary: la posizione originale viene ripristinata
 * - undo aggiunta traccia a una playlist: la traccia viene rimossa dalla playlist
 * - undo aggiunta traccia a una playlist: le altre tracce rimangono intatte
 * - doppio undo: annulla due aggiunte consecutive in ordine LIFO
 *
 * @author EmanuelaGraziuso
 */

package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.command.AddTrackToLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.command.AddTrackToPlaylistCommand;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class UndoAddTrackTest {

    private TrackLibrary testLibrary;
    private PlaylistLibrary testPlaylistLibrary;
    private CommandManager testCommandManager;
    private Playlist currentPlaylist;
    private Track trackA;
    private Track trackB;

    /**
     * Metodo di setup eseguito prima di ogni singolo test.
     * Svuota la TrackLibrary e la PlaylistLibrary per garantire
     * un ambiente pulito ad ogni esecuzione.
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

        currentPlaylist = new Playlist("Test Playlist", false);
        testPlaylistLibrary.addPlaylist(currentPlaylist);

        trackA = new Track("Napule è", "Pino Daniele", 227, "Pop", 1977, null);
        trackB = new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979, null);
    }

    /**
     * Verifica che dopo undo() di AddTrackToLibraryCommand
     * la traccia non sia più presente nella TrackLibrary.
     */
    @Test
    public void testUndoAddToLibrary_trackRemovedFromLibrary() {
        testCommandManager.execute(new AddTrackToLibraryCommand(testLibrary, trackA));
        assertTrue(testLibrary.getTracks().contains(trackA),
                "Precondizione: la traccia deve essere in libreria dopo execute()");

        testCommandManager.undo();

        assertFalse(testLibrary.getTracks().contains(trackA),
                "Dopo undo() la traccia non deve essere più presente in libreria");
    }

    /**
     * Verifica che dopo undo() la libreria sia tornata allo stato precedente all'aggiunta,
     * ovvero che il numero di tracce sia quello originale.
     */
    @Test
    public void testUndoAddToLibrary_librarySizeRestored() {
        testLibrary.addTrack(trackB);
        int sizeBeforeAdd = testLibrary.getTracks().size();

        testCommandManager.execute(new AddTrackToLibraryCommand(testLibrary, trackA));
        assertEquals(sizeBeforeAdd + 1, testLibrary.getTracks().size(),
                "Precondizione: la libreria deve avere una traccia in più dopo execute()");

        testCommandManager.undo();

        assertEquals(sizeBeforeAdd, testLibrary.getTracks().size(),
                "Dopo undo() la libreria deve avere la stessa dimensione di prima dell'aggiunta");
    }

    /**
     * Verifica che dopo undo() di AddTrackToPlaylistCommand
     * la traccia non sia più presente nella playlist.
     */
    @Test
    public void testUndoAddToPlaylist_trackRemovedFromPlaylist() {
        testCommandManager.execute(new AddTrackToPlaylistCommand(currentPlaylist, trackA));
        assertTrue(currentPlaylist.getTracks().contains(trackA),
                "Precondizione: la traccia deve essere nella playlist dopo execute()");

        testCommandManager.undo();

        assertFalse(currentPlaylist.getTracks().contains(trackA),
                "Dopo undo() la traccia non deve essere più presente nella playlist");
    }

    /**
     * Verifica che dopo undo() di AddTrackToPlaylistCommand
     * le altre tracce già presenti nella playlist rimangano intatte.
     */
    @Test
    public void testUndoAddToPlaylist_otherTracksUntouched() {
        currentPlaylist.addTrack(trackB);

        testCommandManager.execute(new AddTrackToPlaylistCommand(currentPlaylist, trackA));
        testCommandManager.undo();

        assertEquals(1, currentPlaylist.getTracks().size(),
                "Dopo undo() la playlist deve contenere solo le tracce precedenti all'aggiunta");
        assertTrue(currentPlaylist.getTracks().contains(trackB),
                "La traccia non coinvolta nell'undo deve essere ancora presente");
    }

    /**
     * Verifica che due undo() consecutivi annullino due aggiunte in ordine LIFO:
     * prima viene annullata l'ultima aggiunta, poi la prima.
     */
    @Test
    public void testUndoAddToLibrary_doubleUndoRestoredInLIFOOrder() {
        testCommandManager.execute(new AddTrackToLibraryCommand(testLibrary, trackA));
        testCommandManager.execute(new AddTrackToLibraryCommand(testLibrary, trackB));
        assertEquals(2, testLibrary.getTracks().size(),
                "Precondizione: la libreria deve contenere 2 tracce");

        testCommandManager.undo();
        assertFalse(testLibrary.getTracks().contains(trackB),
                "Il primo undo() deve rimuovere l'ultima traccia aggiunta (trackB)");
        assertTrue(testLibrary.getTracks().contains(trackA),
                "Dopo il primo undo() trackA deve essere ancora presente");

        testCommandManager.undo();
        assertFalse(testLibrary.getTracks().contains(trackA),
                "Il secondo undo() deve rimuovere anche trackA");
        assertTrue(testLibrary.getTracks().isEmpty(),
                "Dopo due undo() la libreria deve essere vuota");
    }
}