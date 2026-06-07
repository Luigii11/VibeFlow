/**
 * @file TrackEditDialogController.java
 * @brief Controller UI per il popup modale di modifica di una traccia.
 * @details Si occupa di pre-popolare i campi con i dati esistenti, effettuare la validazione 
 * sintattica delle modifiche (campi obbligatori e formati) e delegare l'aggiornamento al business layer.
 * @authors LuigiAutorino, EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

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

    /* Attributi */
    private Track trackToEdit;

    // TODO: @EmanuelChirico - Inietta qui il TrackController di business per gestire l'aggiornamento
    // private TrackController trackController;

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
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String durationStr = durationField.getText().trim();
        String genre = genreField.getText().trim();
        String yearStr = yearField.getText().trim();

        // 1. Validazione UI: Controllo campi vuoti
        if (title.isEmpty() || author.isEmpty() || durationStr.isEmpty() || genre.isEmpty() || yearStr.isEmpty()) {
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

            // =========================================================
            // ---> AREA DI INTERVENTO BUSINESS LOGIC (TODO) <---
            // I dati sono stati validati dalla UI.
            // Il collega dovrà aggiornare i dati usando il suo command/controller.
            // Es: new EditTrackCommand(trackToEdit, title, author, duration, genre, year).execute();
            // =========================================================
            
            System.out.println("DEBUG UI: Modifica Validata -> Vecchio Titolo: " + trackToEdit.getTitle() + " | Nuovo: " + title);
            
            // Mock visivo: modifichiamo l'oggetto in ram per vedere subito il cambiamento (Da rimuovere)
            trackToEdit.setTitle(title);
            trackToEdit.setAuthor(author);
            trackToEdit.setGenre(genre);
            trackToEdit.setYear(year);
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