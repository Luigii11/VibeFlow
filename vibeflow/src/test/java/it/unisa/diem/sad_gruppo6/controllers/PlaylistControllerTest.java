/**
 * @file PlaylistControllerTest.java
 * Classe di test per la validazione della logica di business all'interno di 'PlaylistController'.
 * Utilizza il framework JUnit 5 per verificare il corretto funzionamento dei metodi 
 * e la corretta gestione delle eccezioni, inizializzando l'architettura completa.
 * * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;
import it.unisa.diem.sad_gruppo6.commands.CommandManager;

public class PlaylistControllerTest {

    // Attributi
    private TrackLibrary testTrackLibrary;
    private PlaylistLibrary testLibrary;
    private CommandManager testCommandManager;
    private PlaylistController testController;

    /**
     * Metodo di setup eseguito prima di ogni singolo test (@BeforeEach).
     * Inizializza un ambiente pulito creando le dipendenze necessarie 
     * (TrackLibrary, PlaylistLibrary, CommandManager) e il controller definitivo.
     */
    @BeforeEach
    public void setUp() {
        testTrackLibrary = TrackLibrary.getInstance();
        testLibrary = PlaylistLibrary.getInstance();
        testLibrary.clear();
        testCommandManager = new CommandManager();

        testController = new PlaylistController(testTrackLibrary, testLibrary, testCommandManager);
    }

    /**
     * Testa la creazione di una playlist con un nome valido.
     * Verifica che la playlist venga effettivamente inserita nella libreria
     * e che il nome salvato corrisponda a quello inserito.
     */
    @Test
    public void testCreatePlaylistSuccess() {
        // 1. Chiamo il metodo con un nome valido
        testController.createPlaylist("Rock Anni 80");

        // 2. Verifico che la libreria contenga esattamente 1 playlist
        assertEquals(1, testLibrary.getPlaylists().size(), "La libreria dovrebbe contenere 1 playlist");
        
        // 3. Verifico che il nome salvato sia corretto
        assertEquals("Rock Anni 80", testLibrary.getPlaylists().get(0).getName());
    }

    /**
     * Testa la creazione di una playlist utilizzando un nome vuoto (o composto da soli spazi).
     * Verifica che venga lanciata un'eccezione 'IllegalArgumentException' e che 
     * la libreria rimanga vuota.
     */
    @Test
    public void testCreatePlaylistEmptyNameThrowsException() {
        // Verifico che, passando una stringa vuota, il controller lanci l'eccezione
        // IllegalArgumentException come abbiamo programmato.
        assertThrows(IllegalArgumentException.class, () -> {
            testController.createPlaylist("   ");
        }, "Dovrebbe lanciare un'eccezione per il nome vuoto");
        
        // Verifico che la libreria sia rimasta vuota
        assertEquals(0, testLibrary.getPlaylists().size());
    }

    /**
     * Testa la creazione di una playlist con un nome già esistente nella libreria.
     * Verifica che venga lanciata un'eccezione 'IllegalArgumentException' 
     * impedendo la creazione di duplicati, e che il numero di playlist rimanga inalterato.
     */
    @Test
    public void testCreatePlaylistDuplicateNameThrowsException() {
        // 1. Creo una playlist iniziale
        testController.createPlaylist("Allenamento");

        // 2. Provo a creare una seconda playlist con lo STESSO nome, mi aspetto l'eccezione
        assertThrows(IllegalArgumentException.class, () -> {
            testController.createPlaylist("Allenamento");
        }, "Dovrebbe lanciare un'eccezione per nome duplicato");
        
        // 3. Verifico che le playlist totali siano rimaste 1
        assertEquals(1, testLibrary.getPlaylists().size());
    }
}