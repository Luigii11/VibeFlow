package it.unisa.diem.sad_gruppo6.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistCreationDialogController {

    // Nome Playlist
    @FXML
    private TextField playlistName;

    // Pulsante Salva
    @FXML
    private Button saveButton;

    // Riferimento al controller
    private PlaylistController playlistController;

    public void setPlaylistController(PlaylistController controller) {
        this.playlistController = controller;
    }

    // Azione saveButton: se non ci sono errori chiude la finestra, altrimenti mostra errore.
    @FXML
    private void handleSave(ActionEvent event) {
        String userInput = playlistName.getText();
        
        try {
            playlistController.createPlaylist(userInput);
            close();
        } catch (IllegalArgumentException e) {
            showError("INVALID ENTRY / NAME ALREADY EXISTS", e.getMessage());
        }
    }

    // Azione backButton: chiude la finestra senza salvare
    @FXML
    private void handleBack(ActionEvent event) {
        close();
    }

    // Utilities per UI
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
