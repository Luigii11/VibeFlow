/**
 * @file TrackController.java
 * Classe di definizione di un oggetto di tipo 'TrackController', controller per la gestione delle tracce musicali.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.*;

import java.io.IOException;

import it.unisa.diem.sad_gruppo6.commands.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Node;

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
    @FXML private Track trackToEdit; 

    /**
     * Costruttore del TrackController, inizializza la libreria delle tracce e il gestore dei comandi.
     * 
     * @param library la libreria delle tracce da gestire.
     * @param commandManager il gestore dei comandi per eseguire azioni sulla libreria delle tracce.
     */
    public TrackController() 
    {
        this.library = TrackLibrary.getInstance();
        this.commandManager = new CommandManager();
    }

    /**
     * Metodo per creare una nuova traccia e aggiungerla alla libreria, utilizzando un comando per incapsulare l'azione.
     * 
     * @param title Il titolo della traccia da creare.
     * @param author L'autore della traccia da creare.
     * @param duration La durata della traccia da creare in secondi.
     * @param genre Il genere musicale della traccia da creare.
     * @param year L'anno di pubblicazione della traccia da creare.
     */

    public void createTrack(String title, String author, int duration, String genre, int year) 
    {
        Track track = new Track(title, author, duration, genre, year);
        AddTrackToLibraryCommand command = new AddTrackToLibraryCommand(library, track);
        commandManager.execute(command);
    }

    @FXML
    private void handleAddTrack() 
    {
        try {
            createTrack(
                titleField.getText(),
                authorField.getText(),
                Integer.parseInt(durationField.getText()),
                genreField.getText(),
                Integer.parseInt(yearField.getText())
            );
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("Traccia aggiunta!");
        } catch (NumberFormatException e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Durata e anno devono essere numeri.");
        } catch (IllegalArgumentException e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(e.getMessage());
        }
    }

    /**
     * Modifica i metadati di una traccia esistente nella libreria.
     * Crea un nuovo oggetto {@link Track} con i dati aggiornati e delega
     * l'operazione al CommandManager tramite {@link EditTrackCommand}.
     *
     * <p>La validazione degli input avviene implicitamente nel costruttore
     * di {@link Track}: se un parametro non è valido viene sollevata
     * {@link IllegalArgumentException} prima che il comando venga eseguito,
     * lasciando la libreria invariata.</p>
     *
     * @param target   la traccia originale presente in libreria da modificare.
     * @param title    il nuovo titolo.
     * @param author   il nuovo autore.
     * @param duration la nuova durata in secondi.         
     * @param genre    il nuovo genere musicale.
     * @param year     il nuovo anno di pubblicazione.
     * @throws IllegalArgumentException se uno dei parametri non supera la validazione definita nei setter di {@link Track}.
     */
        
    public void editTrack(Track target, String title, String author, int duration, String genre, int year)
    {
        Track updatedTrack = new Track(title, author, duration, genre, year);
        EditTrackCommand command = new EditTrackCommand(target, updatedTrack);
        commandManager.execute(command);
    }

    /**
     * Pre-popola i campi del form con i metadati della traccia selezionata,
     * preparando la vista per la modalità di modifica.
     * Deve essere invocato dalla vista prima che il form venga mostrato all'utente
     * (acceptance criteria 2: "il form si popola con i dati esistenti per quella traccia").
     *
     * @param track la traccia selezionata dalla libreria da modificare.
     */
    public void setTrackToEdit(Track track)
    {
        this.trackToEdit = track;
        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        durationField.setText(String.valueOf(track.getDuration()));
        genreField.setText(track.getGenre());
        yearField.setText(String.valueOf(track.getYear()));
    }   

    /**
     * Gestisce l'evento di click sul pulsante "Save" del form di modifica.
     * Legge i nuovi valori dai campi FXML e tenta la modifica della traccia
     * correntemente selezionata ({@code trackToEdit}).
     *
     * <p>Flusso (acceptance criteria ID_2):</p>
     * <ol>
     *   <li>Il form è già pre-popolato grazie a {@link #setTrackToEdit(Track)}.</li>
     *   <li>L'utente modifica i campi e preme "Save".</li>
     *   <li>Se la modifica non è valida, viene mostrato un messaggio di errore
     *       e la libreria rimane invariata (acceptance criteria 4).</li>
     *   <li>Se la modifica è corretta, la traccia nella libreria riflette
     *       le modifiche (acceptance criteria 5).</li>
     * </ol>
     */
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
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText("Traccia modificata con successo!");
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
    }

    /**
     * Gestisce il click sul pulsante "Annulla": torna alla vista della libreria
     * senza applicare modifiche.
     *
     * @param event l'evento di azione generato dal click sul pulsante.
     */
    @FXML
    private void handleBack(ActionEvent event)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/unisa/diem/sad_gruppo6/views/TrackLibraryView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel tornare alla libreria.");
        }
    }

}
