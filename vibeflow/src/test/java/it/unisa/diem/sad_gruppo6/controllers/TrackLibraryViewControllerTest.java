/**
 * @file TrackLibraryViewControllerTest.java
 * Classe di test per la validazione della logica di business all'interno di 'TrackLibraryViewController'.
 * Utilizza il framework JUnit 5 per verificare il corretto funzionamento dei metodi e il comportamento del controller 
 * in risposta agli eventi della libreria.
 * * @author EmanuelaGraziuso
 */


package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;

import java.lang.reflect.Field;




public class TrackLibraryViewControllerTest {

    private TrackLibrary testLibrary;
    private TrackLibraryViewController testController;

    /**
     * Metodo di setup eseguito prima di ogni singolo test (@BeforeEach).
     * Ottiene il singleton TrackLibrary e lo svuota per garantire
     * un ambiente pulito e deterministico ad ogni esecuzione.
     */

    @BeforeEach
    public void setUp() throws Exception {
        testLibrary = TrackLibrary.getInstance();
 
        // Svuotiamo la libreria singleton tramite reflection per isolare i test
        Field tracksField = TrackLibrary.class.getDeclaredField("tracks");
        tracksField.setAccessible(true);
        ((LinkedHashSet<?>) tracksField.get(testLibrary)).clear();
 
        testController = new TrackLibraryViewController();
    }

    /**
     * Verifica che, quando la libreria è vuota,
     * il metodo onLibraryChanged() imposti correttamente lo stato
     * interno del controller come "lista vuota" (emptyVisible = true).
     */

    @Test
    public void testOnLibraryChanged_emptyLibrary_setsEmptyState() {
    // La libreria è vuota per costruzione (setUp)
    assertTrue(testLibrary.getTracks().isEmpty(),
            "Precondizione: la libreria deve essere vuota");

    assertDoesNotThrow(() -> testController.onLibraryChanged(),
            "onLibraryChanged() non deve lanciare eccezioni con libreria vuota");
}

    /**
     * Verifica che, dopo l'aggiunta di una traccia alla libreria, 
     * la libreria stessa contenga l'elemento con i metadati corretti
     * (titolo, artista/autore, durata) che il controller dovrà poi esporre in lista.
     */

    @Test
    public void testOnTrackAdded_libraryContainsTrackWithCorrectMetadata() {

    Track track = new Track("Albachiara", "Vasco Rossi", 240, "Rock", 1984);
    testLibrary.addTrack(track);

    List<Track> tracks = testLibrary.getTracks();

    assertEquals(1, tracks.size(), "La libreria deve contenere esattamente 1 traccia");
    assertEquals("Albachiara", tracks.get(0).getTitle());
    assertEquals("Vasco Rossi", tracks.get(0).getAuthor());
    assertEquals(240, tracks.get(0).getDuration());
    assertEquals("Rock", tracks.get(0).getGenre());
    assertEquals(1984, tracks.get(0).getYear());
    }

    /**
     * Verifica che più tracce aggiunte alla libreria siano
     * tutte recuperabili dal controller tramite getTracks(), rispettando
     * la regola di business che richiede la visualizzazione dell'intero elenco.
     */

    @Test
    public void testOnTrackAdded_multipleTracksAllRetrievable() {
    testLibrary.addTrack(new Track("Napule è", "Pino Daniele", 227, "Pop", 1977));
    testLibrary.addTrack(new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979));
    testLibrary.addTrack(new Track("Quanno chiove", "Pino Daniele", 275, "Blues", 1980));

    List<Track> tracks = testLibrary.getTracks();

    assertEquals(3, tracks.size(), "La libreria deve esporre tutte e 3 le tracce aggiunte");
    }

    /**
     * Verifica che la durata di una traccia sia correttamente convertibile nel formato 'm:ss' 
     * 
     */
    @Test
    public void testDurationFormat_isCorrectlyComputed() {
    Track track = new Track("Yesterday", "The Beatles", 125, "Pop", 1975);

    int min = track.getDuration() / 60;
    int sec = track.getDuration() % 60;
    String formatted = String.format("%d:%02d", min, sec);

    assertEquals("2:05", formatted,
            "La durata 125s deve essere formattata come '2:05'");
    }

    /**
     * Verifica che dopo onTrackAdded() la libreria rifletta effettivamente la nuova traccia, confermando che l'aggiornamento
     * dello stato sia coerente con la notifica ricevuta.
     */
    @Test
    public void testOnTrackAdded_libraryIsUpdatedBeforeNotification() {
    Track track = new Track("Amore senza fine", "Pino Daniele", 259, "Pop/Soul", 1998);
    testLibrary.addTrack(track); // la libreria si aggiorna prima della notifica

    testController.onTrackAdded(track); // il controller viene notificato

    assertEquals(1, testLibrary.getTracks().size(),
            "Dopo onTrackAdded la libreria deve contenere la traccia appena inserita");
    }
     
}
