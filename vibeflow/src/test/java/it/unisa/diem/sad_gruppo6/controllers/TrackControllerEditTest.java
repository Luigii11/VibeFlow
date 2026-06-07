/**
 * @file TrackControllerEditTest.java
 * Classe di test per la validazione della logica di business relativa
 * alla modifica di una traccia (ID_2: Modifica Track).
 * Utilizza il framework JUnit 5 per verificare il corretto funzionamento
 * di {@link TrackController#editTrack(Track, String, String, String, int, String)}
 * e la corretta gestione delle eccezioni, inizializzando l'architettura completa.
 *
 * @see TrackController
 * @see TrackLibrary
 * @see it.unisa.diem.sad_gruppo6.model.command.EditTrackCommand
 *
 * @author ChiaraCrisci, EmanuelChirico
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

public class TrackControllerEditTest {

    /* Path delle tracce di test (file MP3 reali presenti nel progetto). */
    private static final String TEST_TRACK_PATH_1 = 
        new File("src/main/resources/test-tracks/canzone1.mp3").getAbsolutePath();
    private static final String TEST_TRACK_PATH_2 = 
        new File("src/main/resources/test-tracks/canzone2.mp3").getAbsolutePath();
    private static final String FAKE_PDF_PATH = "src/test/resources/fake.pdf";

    // Attributi
    private TrackLibrary testLibrary;
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

        // Svuota la libreria per garantire isolamento tra test
        java.util.List<Track> currentTracks = new java.util.ArrayList<>(testLibrary.getTracks());
        for (Track t : currentTracks) {
            testLibrary.removeTrack(t);
        }

        testController = new TrackController();

        // Traccia di partenza presente in libreria prima di ogni test.
        // Nota: la durata viene calcolata automaticamente dal file MP3 reale.
        existingTrack = new Track(
            "Bohemian Rhapsody", "Queen", 354, "Rock", 1975, TEST_TRACK_PATH_1
        );
        testLibrary.addTrack(existingTrack);
    }

    /**
     * Testa la modifica di una traccia con tutti i campi validi.
     * Verifica che la traccia nella libreria rifletta i nuovi metadati
     * (acceptance criteria 5: "la traccia nella libreria riflette le modifiche").
     */
    @Test
    public void testEditTrackSuccess() {
        // 1. Eseguo la modifica con dati validi e nuovo file MP3
        testController.editTrack(
            existingTrack, "We Will Rock You", "Queen", "Rock", 1977, TEST_TRACK_PATH_2
        );

        // 2. Verifico che la libreria contenga ancora 1 sola traccia
        assertEquals(1, testLibrary.getTracks().size());

        // 3. Verifico che i nuovi metadati siano stati salvati correttamente
        Track updated = testLibrary.getTracks().get(0);
        assertEquals("We Will Rock You", updated.getTitle());
        assertEquals("Queen", updated.getAuthor());
        assertEquals("Rock", updated.getGenre());
        assertEquals(1977, updated.getYear());
        assertEquals(TEST_TRACK_PATH_2, updated.getPath());
        // La durata deve essere positiva (calcolata dal file reale)
        assertTrue(updated.getDuration() > 0, "La durata deve essere positiva");
    }

    /**
     * Testa la modifica con titolo vuoto.
     * Verifica che venga lanciata {@link IllegalArgumentException} e che
     * la traccia originale rimanga invariata in libreria.
     */
    @Test
    public void testEditTrackEmptyTitleThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(
                existingTrack, "   ", "Queen", "Rock", 1975, TEST_TRACK_PATH_1
            )
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
            testController.editTrack(
                existingTrack, "Bohemian Rhapsody", "", "Rock", 1975, TEST_TRACK_PATH_1
            )
        , "Dovrebbe lanciare un'eccezione per autore vuoto");

        assertEquals(1, testLibrary.getTracks().size());
    }

    /**
     * Testa la modifica con anno fuori dal range consentito (precedente al 1900).
     * Verifica che venga lanciata {@link IllegalArgumentException}.
     */
    @Test
    public void testEditTrackInvalidYearThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(
                existingTrack, "Bohemian Rhapsody", "Queen", "Rock", 1800, TEST_TRACK_PATH_1
            )
        , "Dovrebbe lanciare un'eccezione per anno antecedente al 1900");

        assertEquals(1, testLibrary.getTracks().size());
    }

    /**
     * Testa il tentativo di modificare una traccia che non è presente in libreria.
     * Verifica che venga lanciata {@link IllegalArgumentException} da
     * {@link TrackLibrary#updateTrack(Track, Track)}.
     */
    @Test
    public void testEditTrackNotInLibraryThrowsException() {
        Track notInLibrary = new Track(
            "Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971, TEST_TRACK_PATH_2
        );

        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(
                notInLibrary, "Kashmir", "Led Zeppelin", "Rock", 1975, TEST_TRACK_PATH_1
            )
        , "Dovrebbe lanciare un'eccezione perché la traccia non è in libreria");
    }

    /**
     * Testa la modifica con un path che non punta a un file MP3 (es. .pdf).
     * Verifica che venga lanciata {@link IllegalArgumentException} dal setter
     * di {@link Track#setPath(String)}, che controlla l'estensione del file.
     */
    @Test
    public void testEditTrackNonMp3PathThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            testController.editTrack(
                existingTrack, "Bohemian Rhapsody", "Queen", "Rock", 1975, FAKE_PDF_PATH
            )
        , "Dovrebbe lanciare un'eccezione per file non MP3");

        // La traccia originale deve restare invariata
        assertEquals(1, testLibrary.getTracks().size());
        assertEquals(TEST_TRACK_PATH_1, testLibrary.getTracks().get(0).getPath());
    }
}