/**
 * @file TrackEditDialogController.java
 * @brief Controller UI per il popup modale di modifica di una traccia.
 * @details Si occupa di pre-popolare i campi con i dati esistenti, effettuare la validazione 
 * sintattica delle modifiche (campi obbligatori e formati) e delegare l'aggiornamento al business layer.
 * @authors LuigiAutorino, EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;


import java.io.File;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;

import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TrackEditDialogController {

    /* Componenti grafici */
    @FXML private VBox rootContainer;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private TextField pathField; 

    /* Attributi */
    private final TrackController trackController = new TrackController();
    private Track trackToEdit;

    /**
     * @brief Inizializza il form con i dati della traccia selezionata.
     * @param track La traccia da modificare.
     */
    public void setTrackToEdit(Track track) {
        this.trackToEdit = track;
        
        // Popola i campi di testo con i dati attuali
        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        durationField.setText(String.valueOf(track.getDuration()));
        genreField.setText(track.getGenre() != null ? track.getGenre() : "");
        yearField.setText(track.getYear() > 0 ? String.valueOf(track.getYear()) : "");
        pathField.setText(track.getPath() != null ? track.getPath() : "");
    }


    /**
     * @brief Apre un FileChooser per cambiare il file audio (opzionale).
     */
    @FXML
    private void handleBrowseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select audio file");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File MP3", "*.mp3")
        );
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
        }
    }
   
    @FXML
    private void handleSave(ActionEvent event) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String durationStr = durationField.getText().trim();
        String genre = genreField.getText().trim();
        String yearStr = yearField.getText().trim();
        String path = pathField.getText().trim();

        // 1. Validazione UI: Controllo campi vuoti
        if (title.isEmpty() || author.isEmpty() || durationStr.isEmpty() || genre.isEmpty() || yearStr.isEmpty() || path.isEmpty()) {
            showError("Missing Information", "All fields are required. Please fill in every detail.");
            return;
        }

        try {
            // 2. Validazione UI: Controllo valori numerici
            int duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                showError("Invalid Duration", "Duration must be a positive number of seconds.");
                return;
            }

            int year = Integer.parseInt(yearStr);
            if (year < 1900 || year > 2026) {
                showError("Invalid Year", "Please enter a valid release year (1900 - 2026).");
                return;
            }
        
            trackController.editTrack(trackToEdit, title, author, genre, year, path);
            close();

        } catch (NumberFormatException e) {
            showError("Format Error", "Duration and Year must contain only numeric characters.");
        } catch (IllegalArgumentException e) {
            showError("Invalid Data", e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        close();
    }

    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        DialogUtils.personalizza(alert, rootContainer, "❌", "#FF4C30");
        alert.showAndWait();
    }
}