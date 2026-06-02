package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unisa.diem.sad_gruppo6.models.Playlist;

public class PlaybackControllerTest {

    @Test
    public void testPlayEmptyPlaylistThrows() {
        PlaybackController controller = new PlaybackController();
        Playlist empty = new Playlist("Nome",false);
        assertThrows(IllegalArgumentException.class, () -> controller.play(empty));
    }
}