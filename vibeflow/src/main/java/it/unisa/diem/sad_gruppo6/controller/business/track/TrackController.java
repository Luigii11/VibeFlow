/**
 * @file TrackController.java
 * Classe di definizione di un oggetto di tipo 'TrackController', controller per la gestione delle tracce musicali.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.business.track;

import it.unisa.diem.sad_gruppo6.model.command.AddTrackToLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.command.EditTrackCommand;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.io.IOException;

public class TrackController 
{
    private TrackLibrary library;
    private CommandManager commandManager;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Label feedbackLabel;
    private Track trackToEdit;   // NON @FXML: non arriva da un nodo dell'FXML

    public TrackController() 
    {
        this.library = TrackLibrary.getInstance();
        this.commandManager = CommandManager.getInstance();
    }

    public void createTrack(String title, String author, int duration, String genre, int year) 
    {
        Track track = new Track(title, author, duration, genre, year);
        AddTrackToLibraryCommand command = new AddTrackToLibraryCommand(library, track);
        commandManager.execute(command);
    }

    @FXML
    private void handleAddTrack() 
    {
        try 
        {
            createTrack(
                titleField.getText(),
                authorField.getText(),
                Integer.parseInt(durationField.getText()),
                genreField.getText(),
                Integer.parseInt(yearField.getText())
            );
            App.setRoot("library/TrackLibraryView");
        } 
        catch (NumberFormatException e) 
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Durata e anno devono essere numeri.");
        } 
        catch (IllegalArgumentException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(e.getMessage());
        } 
        catch (IOException e) 
        { 
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel caricamento della libreria.");
        }
    }

    public void editTrack(Track target, String title, String author, int duration, String genre, int year)
    {
        Track updatedTrack = new Track(title, author, duration, genre, year);
        EditTrackCommand command = new EditTrackCommand(target, updatedTrack);
        commandManager.execute(command);
    }

    public void setTrackToEdit(Track track)
    {
        this.trackToEdit = track;
        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        durationField.setText(String.valueOf(track.getDuration()));
        genreField.setText(track.getGenre());
        yearField.setText(String.valueOf(track.getYear()));
    }   

    @FXML
    private void handleEditTrack()
    {
        if (trackToEdit == null) return;

        try
        {
            editTrack(
                trackToEdit,
                titleField.getText(),
                authorField.getText(),
                Integer.parseInt(durationField.getText()),
                genreField.getText(),
                Integer.parseInt(yearField.getText())
            );
            App.setRoot("library/TrackLibraryView");
        }
        catch (NumberFormatException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Durata e anno devono essere numeri.");
        }
        catch (IllegalArgumentException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(e.getMessage());
        }
        catch (IOException e) 
        { 
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel tornare alla libreria.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event)
    {
        try
        {
            App.setRoot("library/TrackLibraryView");
        }
        catch (IOException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel tornare alla libreria.");
        }
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

        // Incapsula l'azione nel comando richiesto dal task
        RemoveTrackFromLibraryCommand command = new RemoveTrackFromLibraryCommand(track);
        commandManager.execute(command);
    }

}
