/**
 * @file TrackCreationDialogController.java
 * @brief Controller UI per il popup modale di creazione/modifica di una traccia.
 * @details Gestisce la lettura dei campi dalla view, la validazione sintattica
 * (campi obbligatori, parsing numerici) e la visualizzazione di alert di errore.
 * Delega la logica di business a TrackController.
 * Supporta sia la modalità creazione che la modalità modifica (via setTrackToEdit).
 * @authors LuigiAutorino, EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

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
import java.io.File;

public class TrackCreationDialogController {

    /* Componenti grafici */
    @FXML private VBox rootContainer;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField pathField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private final TrackController trackController = new TrackController();
    private Track trackToEdit;

    // ---------------------------------------------------------------
    // API pubblica: modalità modifica
    // ---------------------------------------------------------------

    /**
     * @brief Popola i campi del form con i dati di una traccia esistente.
     * @details Chiamare prima di mostrare il popup per attivare la modalità edit.
     * @param track La traccia da modificare.
     */
    public void setTrackToEdit(Track track) {
        this.trackToEdit = track;
        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        durationField.setText(String.valueOf(track.getDuration()));
        genreField.setText(track.getGenre());
        yearField.setText(String.valueOf(track.getYear()));
        pathField.setText(track.getPath());
    }

    // ---------------------------------------------------------------
    // Handler FXML
    // ---------------------------------------------------------------

    /**
     * @brief Apre un FileChooser per selezionare il file audio.
     * @details Il campo durata rimane non editabile: verrà popolato
     * dai metadati dalla traccia al momento del salvataggio via TrackController.
     */
    @FXML
    private void handleBrowseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file audio");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("File audio", "*.mp3", "*.wav", "*.flac", "*.ogg")
        );
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * @brief Gestisce il salvataggio: validazione UI, poi delega al business controller.
     * @details In modalità create chiama trackController.createTrack();
     * in modalità edit chiama trackController.editTrack().
     */
    @FXML
    private void handleSave(ActionEvent event) {
        String title  = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre  = genreField.getText().trim();
        String yearStr = yearField.getText().trim();
        String path   = pathField.getText().trim();

        // 1. Validazione UI: campi obbligatori
        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()
                || yearStr.isEmpty() || path.isEmpty()) {
            showError("Missing Information",
                      "All fields are required. Please fill in every detail.");
            return;
        }

        // 2. Validazione UI: anno numerico e nel range atteso
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            showError("Format Error", "Year must contain only numeric characters.");
            return;
        }
        if (year < 1900 || year > 2026) {
            showError("Invalid Year", "Please enter a valid release year (1900–2026).");
            return;
        }

        // 3. Delega al business controller
        try {
            if (trackToEdit == null) {
                trackController.createTrack(title, author, genre, year, path);
            } else {
                trackController.editTrack(trackToEdit, title, author, genre, year, path);
            }
            close();
        } catch (IllegalArgumentException e) {
            showError("Invalid Data", e.getMessage());
        } catch (Exception e) {
            showError("Unexpected Error",
                      "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * @brief Annulla l'operazione e chiude il popup senza salvare.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        close();
    }

    // ---------------------------------------------------------------
    // Metodi privati di utilità
    // ---------------------------------------------------------------

    /**
     * @brief Chiude il popup modale corrente.
     */
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Mostra un Alert di errore con il tema scuro personalizzato.
     * @param header  Intestazione breve dell'errore.
     * @param content Messaggio esplicativo dell'errore.
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        DialogUtils.personalizza(alert, rootContainer, "❌", "#FF4C30");
        alert.showAndWait();
    }
}