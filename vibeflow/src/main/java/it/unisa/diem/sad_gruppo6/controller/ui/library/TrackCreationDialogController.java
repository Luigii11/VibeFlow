/**
 * @file TrackCreationDialogController.java
 * @brief Controller UI per il popup modale di creazione di una nuova traccia.
 * @details Si occupa della lettura dei dati dalla view, della validazione sintattica 
 * (campi obbligatori e parsing dei tipi numerici) e della gestione degli alert di errore.
 * Delega la creazione effettiva dell'entità Track al livello di business logic.
 * @authors LuigiAutorino, EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TrackCreationDialogController {

    /* Componenti grafici */
    @FXML private VBox rootContainer;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    // TODO: @EmanuelChirico - Inietta qui il TrackController di business per gestire il salvataggio
    // private TrackController trackController;

    /**
     * @brief Gestisce l'azione di salvataggio della traccia.
     * @details Preleva i testi, li valida sintatticamente e, se corretti, invoca 
     * il controller di business per la persistenza.
     */
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
                showError("Invalid Year", "Please enter a valid release year (1900 < year < 2026).");
                return;
            }

            // =========================================================
            // ---> AREA DI INTERVENTO BUSINESS LOGIC (TODO) <---
            // I dati sono stati validati dalla UI. 
            // Inserire qui la chiamata al controller di business:
            // trackController.createTrack(title, author, duration, genre, year);
            // =========================================================
            
            System.out.println("DEBUG UI: Traccia Validata -> " + title + " (" + year + ")");
            
            // Mock temporaneo per test visivo (da rimuovere una volta implementato il controller)
            it.unisa.diem.sad_gruppo6.model.library.TrackLibrary.getInstance().addTrack(
                new it.unisa.diem.sad_gruppo6.model.domain.Track(title, author, duration, genre, year)
            );
            close();

        } catch (NumberFormatException e) {
            showError("Format Error", "Duration and Year must contain only numeric characters.");
        } catch (IllegalArgumentException e) {
            showError("Invalid Data", e.getMessage());
        }
    }

    /**
     * @brief Annulla l'operazione e chiude la finestra senza salvare.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        close();
    }

    /**
     * @brief Metodo di utilità per chiudere il popup modale corrente.
     */
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Mostra un pop-up d'errore applicando il tema scuro personalizzato.
     * @param header Intestazione dell'errore (titolo breve).
     * @param content Corpo del messaggio d'errore esplicativo.
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        DialogUtils.personalizza(alert, rootContainer, "❌", "#FF4C30");
        alert.showAndWait();
    }
}