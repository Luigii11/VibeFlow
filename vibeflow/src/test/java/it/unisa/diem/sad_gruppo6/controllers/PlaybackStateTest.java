package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import it.unisa.diem.sad_gruppo6.playback.states.PausedState;
import it.unisa.diem.sad_gruppo6.playback.states.PlayingState;
import it.unisa.diem.sad_gruppo6.playback.PlaybackState;

public class PlaybackStateTest {

    private PlaybackState state;

    @BeforeEach
    public void setUp() {
        state = PlaybackState.getInstance();
        state.changeState(new PausedState());   // reset a stato noto
    }

    @Test
    public void testPlayFromPausedGoesToPlaying() {
        state.play();
        assertEquals("Playing", state.getStatusName());
    }

    @Test
    public void testPauseFromPlayingGoesToPaused() {
        state.changeState(new PlayingState());
        state.pause();
        assertEquals("Paused", state.getStatusName());
    }
}