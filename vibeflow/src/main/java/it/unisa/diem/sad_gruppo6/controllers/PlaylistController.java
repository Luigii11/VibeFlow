package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;

//import it.unisa.diem.sad_gruppo6.models.TrackLibrary;
//import it.unisa.diem.sad_gruppo6.commands.CommandManager;

public class PlaylistController {
    // Attributi
    //private TrackLibrary trackLibrary;
    private PlaylistLibrary playlistLibrary;
    //private CommandManager commandManager;

    /* Metodo Costruttore
    public PlaylistController (TrackLibrary trackLibrary, PlaylistLibrary playlistLibrary, CommandManager commandManager) {
        this.trackLibrary = trackLibrary;
        this.playlistLibrary = playlistLibrary;
        this.commandManager = commandManager;
    }*/

    // Metodo per Creazione Playlist dall'utente
    public void createPlaylist(String name) throws IllegalArgumentException {
        // Controllo su nome vuoto
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della playlist non può essere vuoto.");
        }

        // Controllo su unicità nome
        if (playlistLibrary.containsPlaylistName(name)) {
            throw new IllegalArgumentException("Esiste già una playlist con questo nome.");
        }

        Playlist newPlaylist = new Playlist(name.trim(), false);
        playlistLibrary.addPlaylist(newPlaylist);
    }

    /* Metodi da implementare

    public void deletePlaylist(Playlist p) {

    }

    public void renamePlaylist(Playlist p, String newName) {

    }

    public void addTrackToPlaylist(Track t, Playlist p) {

    }

    public void removeTrackFromPlaylist(Track t, Playlist p) {

    }

    public void reorderTrack(Track t, Playlist p, int newIndex) {

    }

    public void createAutoPlaylist(Tag tag) {

    }

    public void createAutoPlaylist(String genre) {
        
    }

    public void createAutoPlaylist(int year) {
        
    }

    */
}
