package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;

public class PlaylistControllerTest {

    private PlaylistLibrary testLibrary;
    private PlaylistController testController;

    @BeforeEach
    public void setUp() {
        testLibrary = new PlaylistLibrary();
        testController = new PlaylistController(testLibrary);
    }

    @Test
    public void testCreatePlaylistSuccess() {
        // 1. Chiamo il metodo con un nome valido
        testController.createPlaylist("Rock Anni 80");

        // 2. Verifico che la libreria contenga esattamente 1 playlist
        assertEquals(1, testLibrary.getPlaylists().size(), "La libreria dovrebbe contenere 1 playlist");
        
        // 3. Verifico che il nome salvato sia corretto
        assertEquals("Rock Anni 80", testLibrary.getPlaylists().get(0).getName());
    }

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