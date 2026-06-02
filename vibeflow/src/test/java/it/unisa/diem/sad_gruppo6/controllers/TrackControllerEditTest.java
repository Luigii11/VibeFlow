/**
 * @file TrackControllerEditTest.java
 * Classe di test per la validazione della logica di business relativa
 * alla modifica di una traccia (ID_2: Modifica Track).
 * Utilizza il framework JUnit 5 per verificare il corretto funzionamento
 * di {@link TrackController#editTrack(Track, String, String, int, String, int)}
 * e la corretta gestione delle eccezioni, inizializzando l'architettura completa.
 *
 * @see TrackController
 * @see TrackLibrary
 * @see it.unisa.diem.sad_gruppo6.commands.EditTrackCommand
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.commands.CommandManager;
import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;

public class TrackControllerEditTest {

    // Attributi
    private TrackLibrary testLibrary;
    private CommandManager testCommandManager;
    private TrackController testController;
    private Track existingTrack;

    /**
     * Metodo di setup eseguito prima di ogni singolo test (@BeforeEach).
     * Reinizializza il singleton di TrackLibrary a uno stato pulito,
     * aggiunge una traccia di partenza e ricrea il controller.
     */
    @BeforeEach
    public void setUp() {
        testLibrary = TrackLibrary.getInstance();
    
        // Recupera la lista attuale e rimuove le canzoni una alla volta 
        // usando il metodo di rimozione che avete già (es. removeTrack)
        java.util.List<Track> currentTracks = new java.util.ArrayList<>(testLibrary.getTracks());
        for (Track t : currentTracks) {
            testLibrary.removeTrack(t); 
        }

        testCommandManager = new CommandManager();
        testController = new TrackController();

        // Traccia di partenza presente in libreria prima di ogni test
        existingTrack = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
        testLibrary.addTrack(existingTrack);
    }

    /**
     * Testa la modifica di una traccia con tutti i campi validi.
     * Verifica che la traccia nella libreria rifletta i nuovi metadati
     * (acceptance criteria 5: "la traccia nella libreria riflette le modifiche").
     */
    @Test
    public void testEditTrackSuccess() {
        // 1. Eseguo la modifica con dati validi
        testController.editTrack(existingTrack, "We Will Rock You", "Queen", 122, "Rock", 1977);

        // 2. Verifico che la libreria contenga ancora 1 sola traccia
        assertEquals(1, testLibrary.getTracks().size());

        // 3. Verifico che i nuovi metadati siano stati salvati correttamente
        Track updated = testLibrary.getTracks().get(0);
        assertEquals("We Will Rock You", updated.getTitle());
        assertEquals("Queen", updated.getAuthor());
        assertEquals(122, updated.getDuration());
        assertEquals("Rock", updated.getGenre());
        assertEquals(1977, updated.getYear());
    }

    /**
     * Testa la modifica con titolo vuoto.
     * Verifica che venga lanciata {@link IllegalArgumentException} e che
     * la traccia originale rimanga invariata in libreria
     * (acceptance criteria 4: "se la modifica non è valida, viene mostrato
     * un alert e la modifica viene annullata").
     */
    @Test
    public void testEditTrackEmptyTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(existingTrack, "   ", "Queen", 354, "Rock", 1975)
        , "Dovrebbe lanciare un'eccezione per titolo vuoto");

        // La traccia originale deve essere ancora presente e invariata
        assertEquals(1, testLibrary.getTracks().size());
        assertEquals("Bohemian Rhapsody", testLibrary.getTracks().get(0).getTitle());
    }

    /**
     * Testa la modifica con autore vuoto.
     * Verifica che venga lanciata {@link IllegalArgumentException} e che
     * la libreria rimanga invariata.
     */
    @Test
    public void testEditTrackEmptyAuthorThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(existingTrack, "Bohemian Rhapsody", "", 354, "Rock", 1975)
        , "Dovrebbe lanciare un'eccezione per autore vuoto");

        assertEquals(1, testLibrary.getTracks().size());
    }

    /**
     * Testa la modifica con durata non positiva (zero o negativa).
     * Verifica che venga lanciata {@link IllegalArgumentException}.
     */
    @Test
    public void testEditTrackInvalidDurationThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(existingTrack, "Bohemian Rhapsody", "Queen", 0, "Rock", 1975)
        , "Dovrebbe lanciare un'eccezione per durata non positiva");

        assertEquals(1, testLibrary.getTracks().size());
    }

    /**
     * Testa la modifica con anno fuori dal range consentito (precedente al 1970).
     * Verifica che venga lanciata {@link IllegalArgumentException}.
     */
    @Test
    public void testEditTrackInvalidYearThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(existingTrack, "Bohemian Rhapsody", "Queen", 354, "Rock", 1960)
        , "Dovrebbe lanciare un'eccezione per anno antecedente al 1970");

        assertEquals(1, testLibrary.getTracks().size());
    }

    /**
     * Testa il tentativo di modificare una traccia che non è presente in libreria.
     * Verifica che venga lanciata {@link IllegalArgumentException} da
     * {@link TrackLibrary#updateTrack(Track, Track)}.
     */
    @Test
    public void testEditTrackNotInLibraryThrowsException() {
        Track notInLibrary = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971);

        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(notInLibrary, "Kashmir", "Led Zeppelin", 518, "Rock", 1975)
        , "Dovrebbe lanciare un'eccezione perché la traccia non è in libreria");
    }
}