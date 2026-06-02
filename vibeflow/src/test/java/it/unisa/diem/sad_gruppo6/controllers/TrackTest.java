package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;
import it.unisa.diem.sad_gruppo6.models.Track;
import org.junit.jupiter.api.Test;

public class TrackTest {

    @Test
    public void testValidTrackCreation() {
        Track track = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals(354, track.getDuration());
        assertEquals(1975, track.getYear());
    }

    @Test
    public void testEmptyTitleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("   ", "Queen", 354, "Rock", 1975));
    }

    @Test
    public void testYearOutOfRangeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("Title", "Author", 200, "Pop", 2050));
    }

    @Test
    public void testNegativeDurationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("Title", "Author", -5, "Pop", 2000));
    }

    @Test
    public void testEqualsSameTitleAndAuthor() {
        Track t1 = new Track("Title", "Author", 200, "Pop", 2000);
        Track t2 = new Track("Title", "Author", 999, "Jazz", 1990);
        assertEquals(t1, t2);   // stesso titolo+autore → stessa traccia
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}