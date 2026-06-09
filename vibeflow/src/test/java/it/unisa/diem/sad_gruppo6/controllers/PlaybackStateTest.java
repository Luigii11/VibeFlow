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
    private Track trackA;
    private Track trackB;
    private java.io.File f1;
    private java.io.File f2;

    /**
     * Setup eseguito prima di ogni test.
     * Ripristina il singleton a uno stato noto: PausedState, posizione 0, nessuna traccia.
     */
    @BeforeEach
    public void setUp() throws Exception {
        state = PlaybackState.getInstance();
        state.changeState(new PausedState());
        state.seekTo(0);
        state.setCurrentTrack(null);

        java.io.File fa = java.io.File.createTempFile("trackA", ".mp3");
        java.io.File fb = java.io.File.createTempFile("trackB", ".mp3");
        fa.deleteOnExit(); fb.deleteOnExit();

        trackA = new Track("Song A", "Artist A", 200, "Pop",  2020, fa.getAbsolutePath());
        trackB = new Track("Song B", "Artist B", 100, "Rock", 2021, fb.getAbsolutePath());
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
    public void testSetCurrentTrackUpdatesTrack() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("bohemian_", ".mp3");
        tmp.deleteOnExit();
        Track track = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975, tmp.getAbsolutePath());
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
    public void testSetTrackAndPlayingStateAreIndependent() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("imagine_", ".mp3");
        tmp.deleteOnExit();
        Track track = new Track("Imagine", "John Lennon", 187, "Pop", 1971, tmp.getAbsolutePath());
        state.setCurrentTrack(track);
        state.changeState(new PlayingState());

        assertEquals(track, state.getCurrentTrack(),
            "La traccia corrente deve essere quella impostata");
        assertEquals("Playing", state.getStatusName(),
            "Lo stato deve essere Playing");
    }

    /**
     * Verifica che una singola chiamata a {@code incrementPosition()} aumenti
     * {@code currentPosition} di esattamente 1.
     */
    @Test
    public void testIncrementPositionAdvancesOneSecond() {
        state.seekTo(0);
        state.incrementPosition();
        assertEquals(1, state.getCurrentPosition(),
            "incrementPosition() deve avanzare la posizione di 1 secondo");
    }

    /**
     * Verifica che chiamate multiple a {@code incrementPosition()} accumulino
     * correttamente i secondi.
     */
    @Test
    public void testIncrementPositionAccumulatesCorrectly() {
        state.seekTo(0);
        for (int i = 0; i < 10; i++) {
            state.incrementPosition();
        }
        assertEquals(10, state.getCurrentPosition(),
            "Dopo 10 chiamate la posizione deve essere 10 (AC1)");
    }
 
    /**
     * Verifica che {@code seekTo(0)} azzeri la posizione, simulando il cambio
     * traccia che deve azzerare il contatore.
     */
    @Test
    public void testSeekToZeroResetsPosition() {
        state.seekTo(50);
        state.seekTo(0);
        assertEquals(0, state.getCurrentPosition(),
            "seekTo(0) deve azzerare la posizione al cambio di traccia (AC2)");
    }
 
    /**
     * Verifica che dopo il cambio di traccia (seekTo(0) + setCurrentTrack()),
     * la posizione riparta da 0 e {@code incrementPosition()} funzioni
     * correttamente sulla nuova traccia.
     */
    @Test
    public void testPositionResetsOnTrackChange() {
        state.setCurrentTrack(trackA);
        state.seekTo(30);
        assertEquals(30, state.getCurrentPosition());
 
        // Simula cambio traccia (come fa startPlayback)
        state.seekTo(0);
        state.setCurrentTrack(trackB);
 
        assertEquals(0, state.getCurrentPosition(),
            "Al cambio di traccia la posizione deve essere 0");
 
        state.incrementPosition();
        assertEquals(1, state.getCurrentPosition(),
            "Dopo il cambio traccia, incrementPosition() deve ripartire da 1");
    }
 
    /**
     * Verifica che in PausedState la posizione rimanga ferma: simuliamo il
     * comportamento corretto verificando che la posizione non cambi senza
     * chiamare incrementPosition().
     */
    @Test
    public void testPositionDoesNotChangeInPausedState() {
        state.setCurrentTrack(trackA);
        state.changeState(new PlayingState());
        state.seekTo(15);
 
        // Mette in pausa
        state.changeState(new PausedState());
 
        // In pausa nessuno chiama incrementPosition(), la posizione resta ferma
        int positionAfterPause = state.getCurrentPosition();
        assertEquals(15, positionAfterPause,
            "La posizione non deve cambiare in PausedState senza chiamare incrementPosition()");
    }
 
    /**
     * Verifica che alla ripresa (da PausedState a PlayingState), la posizione
     * riparta esattamente dal punto in cui era stata sospesa.
     */
    @Test
    public void testPositionResumesFromExactPoint() {
        state.setCurrentTrack(trackA);
        state.changeState(new PlayingState());
        state.seekTo(42);
 
        // Pausa
        state.changeState(new PausedState());
        assertEquals(42, state.getCurrentPosition(),
            "La posizione deve rimanere 42 dopo la pausa");
 
        // Ripresa
        state.changeState(new PlayingState());
        state.incrementPosition();
 
        assertEquals(43, state.getCurrentPosition(),
            "Dopo la ripresa il primo tick deve portare la posizione a 43");
    }
 

    /**
     * Verifica che {@code getProgress()} restituisca 0.0 quando non c'è
     * nessuna traccia corrente.
     */
    @Test
    public void testGetProgressReturnsZeroWithNoTrack() {
        state.setCurrentTrack(null);
        assertEquals(0.0, state.getProgress(), 0.001,
            "getProgress() deve essere 0.0 se non c'è traccia corrente");
    }
 
    /**
     * Verifica che {@code getProgress()} restituisca 0.0 all'inizio della
     * riproduzione (posizione = 0).
     */
    @Test
    public void testGetProgressZeroAtStart() {
        state.setCurrentTrack(trackA); // durata 200 sec
        state.seekTo(0);
        assertEquals(0.0, state.getProgress(), 0.001,
            "getProgress() deve essere 0.0 all'inizio della riproduzione");
    }
 
    /**
     * Verifica che {@code getProgress()} restituisca 1.0 quando la posizione
     * raggiunge la durata totale della traccia.
     */
    @Test
    public void testGetProgressOneAtEnd() {
        state.setCurrentTrack(trackA); // durata 200 sec
        state.seekTo(200);
        assertEquals(1.0, state.getProgress(), 0.001,
            "getProgress() deve essere 1.0 quando la posizione è uguale alla durata");
    }
 
    /**
     * Verifica che {@code getProgress()} restituisca il valore corretto a metà
     * traccia (0.5).
     */
    @Test
    public void testGetProgressHalfway() {
        state.setCurrentTrack(trackA); // durata 200 sec
        state.seekTo(100);
        assertEquals(0.5, state.getProgress(), 0.001,
            "getProgress() deve essere 0.5 a metà della durata totale");
    }
 
    /**
     * Verifica che {@code getProgress()} si aggiorni correttamente dopo ogni
     * chiamata a {@code incrementPosition()} (la barra avanza gradualmente).
     */
    @Test
    public void testGetProgressUpdatesWithIncrementPosition() {
        state.setCurrentTrack(trackB); // durata 100 sec
        state.seekTo(0);
 
        double previousProgress = state.getProgress();
        state.incrementPosition();
        double newProgress = state.getProgress();
 
        assertTrue(newProgress > previousProgress,
            "getProgress() deve aumentare dopo incrementPosition()");
        assertEquals(0.01, newProgress, 0.001,
            "Dopo il primo tick su una traccia da 100s, getProgress() deve essere 0.01");
    }
 
    /**
     * Verifica che {@code incrementPosition()} notifichi gli observer registrati,
     * così la UI viene aggiornata ad ogni tick.
     */
    @Test
    public void testIncrementPositionNotifiesObservers() {
        int[] callCount = {0};
        state.registerObserver(s -> callCount[0]++);
 
        state.incrementPosition();
 
        assertTrue(callCount[0] > 0,
            "incrementPosition() deve notificare gli observer (la UI deve aggiornarsi)");
    }
}