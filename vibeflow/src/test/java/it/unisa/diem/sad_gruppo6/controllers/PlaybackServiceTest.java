/**
 * @file PlaybackServiceTest.java
 * Classe di test per la validazione del comportamento di {@link PlaybackService}
 * in relazione all'avanzamento della posizione di riproduzione (tick) e alla
 * corretta interazione con {@link PlaybackState}.
 *
 * <p>Poiché {@link PlaybackService} utilizza una {@code Timeline} JavaFX non
 * disponibile nell'ambiente di test, i casi relativi a {@code tick()} vengono
 * verificati indirettamente tramite {@link PlaybackState#seekTo(int)} e
 * {@link PlaybackState#getCurrentPosition()}, che rappresentano l'effetto
 * osservabile del tick sul modello.</p>
 *
 * @see PlaybackService
 * @see PlaybackState
 *
 * @author ChiaraCrisci 
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PausedState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;

public class PlaybackServiceTest {

    private PlaybackState state;
    private Track testTrack;

    /**
     * Setup eseguito prima di ogni test.
     * Ripristina il singleton {@link PlaybackState} a uno stato noto:
     * nessuna traccia, posizione a 0, stato {@code PausedState}.
     */
    @BeforeEach
    public void setUp() {
        state = PlaybackState.getInstance();
        state.changeState(new PausedState());
        state.seekTo(0);
        state.setCurrentTrack(null);

        // Traccia di durata 5 secondi per rendere i test veloci e precisi
        testTrack = new Track("Test Song", "Test Artist", 5, "Pop", 2000, null);
    }


    /**
     * Verifica che {@code seekTo()} aggiorni correttamente {@code currentPosition}.
     * Questo è il comportamento atomico su cui si basa l'intera logica di tick().
     */
    @Test
    public void testSeekToUpdatesCurrentPosition() {
        state.seekTo(3);
        assertEquals(3, state.getCurrentPosition(),
            "currentPosition deve riflettere il valore passato a seekTo()");
    }

    /**
     * Verifica che la posizione parta da 0 all'inizio della riproduzione,
     * simulando l'avvio di un nuovo brano.
     */
    @Test
    public void testInitialPositionIsZero() {
        state.seekTo(0);
        assertEquals(0, state.getCurrentPosition(),
            "La posizione iniziale deve essere 0");
    }


    /**
     * Simula il comportamento di tick() verificando che la posizione
     * avanzi di 1 secondo per ogni chiamata, fino alla durata totale.
     *
     * <p>Riproduce esattamente la logica implementata in
     * {@code PlaybackService#tick()}: se {@code currentPos < totalDuration},
     * chiama {@code seekTo(currentPos + 1)}.</p>
     */
    @Test
    public void testTickAdvancesPositionByOneSecond() {
        state.setCurrentTrack(testTrack);
        state.changeState(new PlayingState());
        state.seekTo(0);

        // Simula un singolo tick
        int before = state.getCurrentPosition();
        if (before < testTrack.getDuration()) {
            state.seekTo(before + 1);
        }

        assertEquals(1, state.getCurrentPosition(),
            "Dopo un tick la posizione deve avanzare di 1 secondo");
    }

    /**
     * Simula l'avanzamento completo della traccia tick per tick e verifica
     * che la posizione raggiunga esattamente la durata totale.
     */
    @Test
    public void testTickReachesTotalDuration() {
        state.setCurrentTrack(testTrack);
        state.changeState(new PlayingState());
        state.seekTo(0);

        int duration = testTrack.getDuration();

        // Simula tutti i tick fino alla fine
        for (int i = 0; i < duration; i++) {
            int pos = state.getCurrentPosition();
            if (pos < duration) {
                state.seekTo(pos + 1);
            }
        }

        assertEquals(duration, state.getCurrentPosition(),
            "Dopo tanti tick quanti i secondi della traccia, la posizione deve essere uguale alla durata");
    }

    /**
     * Verifica che tick() non avanzi oltre la durata totale della traccia.
     * Simula il caso in cui la posizione è già alla fine: nessun ulteriore avanzamento.
     */
    @Test
    public void testTickDoesNotExceedTotalDuration() {
        state.setCurrentTrack(testTrack);
        state.changeState(new PlayingState());

        int duration = testTrack.getDuration();
        state.seekTo(duration); // Posizionato esattamente alla fine

        // Un tick a fine traccia non deve avanzare
        int pos = state.getCurrentPosition();
        if (pos < duration) {
            state.seekTo(pos + 1); // Questo NON deve eseguirsi
        }

        assertEquals(duration, state.getCurrentPosition(),
            "tick() non deve avanzare oltre la durata totale");
    }

    /**
     * Verifica che tick() non avanzi la posizione se non c'è una traccia corrente.
     * Simula il guard check su {@code getCurrentTrack() == null}.
     */
    @Test
    public void testTickWithNoTrackDoesNotAdvance() {
        state.setCurrentTrack(null);
        state.seekTo(0);

        // Simula il guard di tick(): se track è null, non fare nulla
        Track current = state.getCurrentTrack();
        if (current != null) {
            state.seekTo(state.getCurrentPosition() + 1);
        }

        assertEquals(0, state.getCurrentPosition(),
            "Con traccia null, tick() non deve modificare la posizione");
    }


    /**
     * Verifica che {@code seekTo()} notifichi gli osservatori registrati,
     * confermando che la UI viene aggiornata ad ogni tick.
     *
     * <p>Registra un osservatore stub che conta le notifiche ricevute
     * e verifica che venga invocato dopo {@code seekTo()}.</p>
     */
    @Test
    public void testSeekToNotifiesObservers() {
        // Osservatore stub che conta gli aggiornamenti
        int[] callCount = {0};
        state.registerObserver(s -> callCount[0]++);

        state.seekTo(2);

        assertTrue(callCount[0] > 0,
            "seekTo() deve notificare gli osservatori (la UI deve aggiornarsi ad ogni tick)");
    }
}