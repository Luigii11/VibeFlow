package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PausedState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;

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

        /**
     * Verifica che setCurrentTrack() imposti correttamente la traccia corrente.
     */
    @Test
    public void testSetCurrentTrackUpdatesTrack() {
        Track track = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975, null);
        state.setCurrentTrack(track);
        assertEquals(track, state.getCurrentTrack(),
            "getCurrentTrack() deve restituire la traccia impostata con setCurrentTrack()");
    }

    /**
     * Verifica che changeState(PlayingState) porti lo stato a "Playing".
     */
    @Test
    public void testChangeStateToPlayingReturnsCorrectName() {
        state.changeState(new PlayingState());
        assertEquals("Playing", state.getStatusName(),
            "Dopo changeState(PlayingState) il nome dello stato deve essere 'Playing'");
    }

    /**
     * Verifica che impostare una traccia e portarsi in PlayingState
     * sia coerente: entrambe le proprietà devono essere aggiornate.
     */
    @Test
    public void testSetTrackAndPlayingStateAreIndependent() {
        Track track = new Track("Imagine", "John Lennon", 187, "Pop", 1971, "");
        state.setCurrentTrack(track);
        state.changeState(new PlayingState());

        assertEquals(track, state.getCurrentTrack(),
            "La traccia corrente deve essere quella impostata");
        assertEquals("Playing", state.getStatusName(),
            "Lo stato deve essere Playing");
    }
}