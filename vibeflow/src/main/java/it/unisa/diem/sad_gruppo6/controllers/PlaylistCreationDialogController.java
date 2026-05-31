/**
 * @file PlaylistCreationDialogController.java
 * Controller della vista per la finestra di dialogo di creazione di una nuova playlist.
 * Gestisce l'interazione dell'utente con l'interfaccia grafica (inserimento del nome, 
 * salvataggio, annullamento e visualizzazione degli errori).
 * * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistCreationDialogController {

    // Attributi FXML
    @FXML
    private TextField playlistName;

    @FXML
    private Button saveButton;

    // Riferimento al controller di dominio
    private PlaylistController playlistController;

    /**
     * Imposta il riferimento al controller principale delle playlist.
     * Necessario per delegare la logica di business (es. creazione effettiva della playlist).
     * * @param controller L'istanza del 'PlaylistController' da utilizzare.
     */
    public void setPlaylistController(PlaylistController controller) {
        this.playlistController = controller;
    }

    /**
     * Gestisce l'evento scatenato dal click sul pulsante "Save".
     * Prende in input il testo inserito dall'utente e tenta di creare la playlist.
     * In caso di successo, chiude il dialog. In caso di errore (es. nome vuoto o duplicato),
     * intercetta l'eccezione e mostra un pop-up di errore.
     * * @param event L'evento generato dall'interazione con il pulsante.
     */
    @FXML
    private void handleSave(ActionEvent event) {
        String userInput = playlistName.getText();
        
        try {
            // Tenta la creazione della playlist
            playlistController.createPlaylist(userInput);
            
            // SCENARIO 3: Creazione avvenuta, chiudo la finestra.
            // Il PlaylistLibraryObserver notificherà la HomeView in automatico
            close();
            
        } catch (IllegalArgumentException e) {
            // SCENARIO 1 e 2: L'eccezione viene catturata. 
            // e.getMessage() conterrà "Il nome non può essere vuoto" (Scenario 1) 
            // oppure "Esiste già una playlist con questo nome" (Scenario 2).
            showError("Impossibile creare la playlist", e.getMessage());
        }
    }

    /**
     * Gestisce l'evento scatenato dal click sul pulsante "Back".
     * Chiude semplicemente la finestra di dialogo senza effettuare alcun salvataggio.
     * * @param event L'evento generato dall'interazione con il pulsante.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        close();
    }

    // Metodi di utilità per la UI

    /**
     * Chiude l'attuale finestra di dialogo (Stage) in cui risiede il controller.
     */
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostra un pop-up di errore a schermo con un titolo e un messaggio specifici.
     * * @param header L'intestazione in grassetto dell'errore.
     * @param content Il corpo del messaggio d'errore (solitamente generato dall'eccezione).
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}