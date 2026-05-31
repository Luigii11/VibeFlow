/**
 * @file PlaylistController.java
 * Classe controller che gestisce la logica di business relativa alle playlist.
 * Si occupa della validazione dei dati e di orchestrare le operazioni tra i modelli 
 * (PlaylistLibrary, TrackLibrary) e il gestore dei comandi (CommandManager).
 * * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;

import it.unisa.diem.sad_gruppo6.commands.CommandManager;
import it.unisa.diem.sad_gruppo6.commands.CreatePlaylistCommand;
import it.unisa.diem.sad_gruppo6.commands.AppCommand;

public class PlaylistController {

    // Attributi
    private TrackLibrary trackLibrary;
    private PlaylistLibrary playlistLibrary;
    private CommandManager commandManager;

    /**
     * Costruttore completo della classe 'PlaylistController'.
     * * @param trackLibrary La libreria generale delle tracce.
     * @param playlistLibrary La libreria delle playlist dell'utente.
     * @param commandManager Il gestore dei comandi per abilitare le funzionalità di Undo.
     */
    public PlaylistController (TrackLibrary trackLibrary, PlaylistLibrary playlistLibrary, CommandManager commandManager) {
        this.trackLibrary = trackLibrary;
        this.playlistLibrary = playlistLibrary;
        this.commandManager = commandManager;
    }

    /**
     * Crea una nuova playlist vuota (non autogenerata) e la aggiunge alla libreria tramite CommandManager.
     * * @param name Il nome desiderato per la nuova playlist.
     * @throws IllegalArgumentException Se il nome è null, vuoto, o se esiste già una playlist con lo stesso nome.
     */
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
        AppCommand command = new CreatePlaylistCommand(playlistLibrary, newPlaylist);
        commandManager.execute(command);
    }

    /* Metodi da implementare

    /*
    /**
     * Elimina una playlist esistente dalla libreria.
     * * @param p La playlist da eliminare.
     */
    /*
    public void deletePlaylist(Playlist p) {

    }
    */

    /*
    /**
     * Rinomina una playlist esistente, verificando che il nuovo nome sia valido e univoco.
     * * @param p La playlist da rinominare.
     * @param newName Il nuovo nome da assegnare.
     */
    /*
    public void renamePlaylist(Playlist p, String newName) {

    }
    */

    /*
    /**
     * Aggiunge una traccia a una specifica playlist.
     * * @param t La traccia da aggiungere.
     * @param p La playlist di destinazione.
     */
    /*
    public void addTrackToPlaylist(Track t, Playlist p) {

    }
    */

    /*
    /**
     * Rimuove una traccia da una specifica playlist.
     * * @param t La traccia da rimuovere.
     * @param p La playlist da cui rimuovere la traccia.
     */
    /*
    public void removeTrackFromPlaylist(Track t, Playlist p) {

    }
    */

    /*
    /**
     * Cambia la posizione di una traccia all'interno di una playlist.
     * * @param t La traccia da spostare.
     * @param p La playlist che contiene la traccia.
     * @param newIndex Il nuovo indice in cui posizionare la traccia.
     */
    /*
    public void reorderTrack(Track t, Playlist p, int newIndex) {

    }
    */

    /*
    /**
     * Crea una playlist autogenerata basata su uno specifico Tag.
     * * @param tag Il tag da utilizzare come filtro.
     */
    /*
    public void createAutoPlaylist(Tag tag) {

    }
    */

    /*
    /**
     * Crea una playlist autogenerata basata su uno specifico genere musicale.
     * * @param genre Il genere musicale da utilizzare come filtro.
     */
    /*
    public void createAutoPlaylist(String genre) {
        
    }
    */

    /*
    /**
     * Crea una playlist autogenerata basata su uno specifico anno di pubblicazione.
     * * @param year L'anno da utilizzare come filtro.
     */
    /*
    public void createAutoPlaylist(int year) {
        
    }
    */
}
