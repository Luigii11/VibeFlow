/**
 * @file TrackController.java
 * Classe di definizione di un oggetto di tipo 'TrackController', controller per la gestione delle tracce musicali.
 * 
 * @author EmanuelChirico, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.controller.business.track;

import java.time.LocalDate;

import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.model.command.AddTrackToLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.command.EditTrackCommand;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.utility.AudioMetadataExtractor;
import java.util.Random;

public class TrackController 
{
    private TrackLibrary library;
    private CommandManager commandManager;
    private Track trackToEdit;  
    private PlaylistController playlistController;
    private static final Random RANDOM = new Random();
    

    public TrackController() 
    {
        this.library = TrackLibrary.getInstance();
        this.commandManager = CommandManager.getInstance();
        this.playlistController = new PlaylistController(
                this.library,
                PlaylistLibrary.getInstance(),
                this.commandManager
        );
    }

    /**
     * @brief Crea una nuova traccia, la aggiunge alla libreria e assegna i tag di sistema.
     * @details Dopo la costruzione della Track, assegna automaticamente Tag.NEW_RELEASE
     * se l'anno di pubblicazione coincide con l'anno corrente, e Tag.EXPLICIT in modo
     * casuale (simulazione dell'elaborazione dei metadati in fase di importazione).
     * Questi tag non sono modificabili dall'utente, in coerenza con
     * {@link it.unisa.diem.sad_gruppo6.model.domain.TagSet#setSystemTag(Tag)}.
     *
     * @param title Il titolo della traccia.
     * @param author L'autore della traccia.
     * @param genre Il genere musicale della traccia.
     * @param year L'anno di pubblicazione della traccia.
     * @param path Il percorso del file audio della traccia.
     */
    public void createTrack(String title, String author, String genre, int year, String path) 
    {
        int length = AudioMetadataExtractor.extractDuration(path);
        Track track = new Track(title, author, length, genre, year, path);

        boolean explicit = RANDOM.nextBoolean();
        assignSystemTags(track, explicit);

        AddTrackToLibraryCommand command = new AddTrackToLibraryCommand(library, track);
        commandManager.execute(command);
        playlistController.createAutoPlaylist(genre);
        playlistController.createAutoPlaylist(year);

        for (Tag t : track.getTagSet().getTags()) {
            if (t != Tag.FAVOURITE) {
                playlistController.createAutoPlaylist(t);
            }
        }
    }

    /**
     * @brief Assegna i tag di sistema (EXPLICIT, NEW_RELEASE) a una traccia appena creata.
     * @details Centralizza l'invariante "i tag di sistema vengono assegnati una sola volta,
     * in fase di creazione, e non sono modificabili dall'utente".
     * NEW_RELEASE viene assegnato se l'anno della traccia coincide con l'anno corrente;
     * EXPLICIT viene assegnato se il chiamante lo segnala tramite il parametro {@code explicit},
     * tipicamente derivato dai metadati del file audio.
     *
     * @param track La traccia appena costruita, non ancora aggiunta alla libreria.
     * @param explicit true se i metadati della traccia indicano contenuto esplicito.
     */
    private void assignSystemTags(Track track, boolean explicit)
    {
        if (explicit)
        {
            track.getTagSet().setSystemTag(Tag.EXPLICIT);
        }
        if (track.getYear() == LocalDate.now().getYear())
        {
            track.getTagSet().setSystemTag(Tag.NEW_RELEASE);
        }
    }

   
    public void editTrack(Track target, String title, String author, String genre, int year, String path)
    {
        String oldGenre = target.getGenre();
        int oldYear = target.getYear();
        int length = AudioMetadataExtractor.extractDuration(path);
        Track updatedTrack = new Track(title, author, length, genre, year, path);
        
        if (target.getTagSet().hasTag(Tag.FAVOURITE)) {
            updatedTrack.getTagSet().addTag(Tag.FAVOURITE);
        }
        assignSystemTags(updatedTrack, RANDOM.nextBoolean());
        EditTrackCommand command = new EditTrackCommand(target, updatedTrack);
        commandManager.execute(command);

        if (!oldGenre.equalsIgnoreCase(genre)) {
            playlistController.createAutoPlaylist(oldGenre);
            playlistController.removeGenrePlaylistIfEmpty(oldGenre);
        }

        playlistController.createAutoPlaylist(genre);
        playlistController.createAutoPlaylist(Tag.EXPLICIT);
        playlistController.removeTagPlaylistIfEmpty(Tag.EXPLICIT);
        playlistController.createAutoPlaylist(Tag.NEW_RELEASE);
        playlistController.removeTagPlaylistIfEmpty(Tag.NEW_RELEASE);

        if (oldYear != year) {
          playlistController.createAutoPlaylist(oldYear);
          playlistController.removeYearPlaylistIfEmpty(oldYear);
        }
        playlistController.createAutoPlaylist(year);


        PlaylistLibrary.getInstance().updatePlaylist(null);

        

        
    }

    /**
     * Rimuove una traccia dalla lista globale della libreria.
     *
     * @param track la traccia da rimuovere dalla libreria generale.
     * @throws IllegalArgumentException se la traccia è null o non presente in libreria.
     */
    public void deleteTrack(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("Impossibile rimuovere una traccia null.");
        }
        if (!library.getTracks().contains(track)) {
            throw new IllegalArgumentException("La traccia da rimuovere non è presente in libreria.");
        }

        String genre = track.getGenre();
        int year = track.getYear();
        // Incapsula l'azione nel comando richiesto dal task
        RemoveTrackFromLibraryCommand command = new RemoveTrackFromLibraryCommand(track);
        commandManager.execute(command);
        playlistController.removeGenrePlaylistIfEmpty(genre);
        playlistController.removeYearPlaylistIfEmpty(year);
        playlistController.removeTopPlayedPlaylistIfEmpty();
        playlistController.createAutoMostPlayedPlaylist();

        for (Tag t : track.getTagSet().getTags()) {
            if (t != Tag.FAVOURITE) {
                playlistController.createAutoPlaylist(t);
                playlistController.removeTagPlaylistIfEmpty(t);
            }
        }
    }

    /**
     * @brief Aggiunge un tag gestibile dall'utente a una traccia.
     * @details Delega al TagSet della traccia, notifica gli
     * observer della libreria affinché la UI aggiorni istantaneamente lo stato
     * dell'icona, e aggiorna/crea la playlist automatica corrispondente
     * al tag tramite PlaylistController#createAutoPlaylist(Tag).
     *
     * @param track La traccia a cui aggiungere il tag.
     * @param tag Il tag da assegnare (deve essere gestibile dall'utente, es. FAVOURITE).
     * @throws IllegalArgumentException Se track è null o tag è un tag di sistema.
     */
    public void addTag(Track track, Tag tag)
    {
        if (track == null)
        {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }
        track.getTagSet().addTag(tag);
        library.notifyTrackUpdated(track);

        if (tag != Tag.FAVOURITE) {
            playlistController.createAutoPlaylist(tag);
        }
    }

    /**
     * @brief Incrementa il playCount della traccia e aggiorna la playlist "Most_played".
     *
     * @param track La traccia appena terminata di riprodurre.
     */
    public void incrementPlayCount(Track track) {
        if (track == null) return;
        track.incrementPlayCount();
        library.notifyTrackUpdated(track);
        playlistController.createAutoMostPlayedPlaylist();
    }

    /**
     * @brief Rimuove un tag gestibile dall'utente da una traccia.
     * @details Delega al TagSet della traccia, notifica gli
     * observer della libreria affinché la UI aggiorni istantaneamente lo stato
     * dell'icona, aggiorna la playlist automatica corrispondente al tag
     * e, se nessuna traccia possiede più il tag, la rimuove dalla PlaylistLibrary.
     *
     * @param track La traccia da cui rimuovere il tag.
     * @param tag Il tag da rimuovere (deve essere gestibile dall'utente, es. FAVOURITE).
     * @throws IllegalArgumentException Se track è null o tag è un tag di sistema.
     */
    public void removeTag(Track track, Tag tag)
    {
        if (track == null)
        {
            throw new IllegalArgumentException("La traccia non può essere null.");
        }
        track.getTagSet().removeTag(tag);
        library.notifyTrackUpdated(track);

        if (tag != Tag.FAVOURITE) {
            playlistController.createAutoPlaylist(tag);
            playlistController.removeTagPlaylistIfEmpty(tag);
        }
    }


    

}