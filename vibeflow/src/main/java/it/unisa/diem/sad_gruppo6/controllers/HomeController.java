/**
 * @file HomeController.java
 * Controller della vista Home.
 * Mostra all'utente tutte le playlist da lui create o per lui generate dal sistema.
 * Permette di eliminare le playlist create manualmente.
 * 
 * @see PlaylistController
 * @see PlaylistController
 * 
 * @author EmanuelaGraziuso
 * 
 */


package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class HomeController implements PlaylistLibraryObserver {

    /**
     * ListView mostra l'elenco delle playlist dell'utente.
     */
    @FXML private ListView<Playlist> playlistListView;

    private PlaylistLibrary playlistLibrary;
    private PlaylistController playlistController;

    /**
     * Inizializza il controller con le dipendenze necessarie e si registra come observer.
     * 
     * @param playlistLibrary La libreria delle playlist da osservare.
     * @param playlistController Il controller per gestire le azioni sulle playlist.
     * 
     */

    public void init(PlaylistLibrary playlistLibrary, PlaylistController playlistController) {
        if (playlistLibrary == null) {
            throw new IllegalArgumentException("PlaylistLibrary non può essere null");
        }
        if (playlistController == null) {
            throw new IllegalArgumentException("PlaylistController non può essere null");
        }
        this.playlistLibrary = playlistLibrary;
        this.playlistController = playlistController;
        playlistLibrary.registerObserver(this);
        refresh();

       
        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doppio click per aprire la playlist
                Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                if (selectedPlaylist != null) {
                    openPlaylistDetails(selectedPlaylist);
                }
            }
        });
    }

private void openPlaylistDetails(Playlist playlist) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/views/PlaylistDetails.fxml"));
        Parent root = loader.load();
        
        PlaylistDetailsController detailsController = loader.getController();
        detailsController.init(playlist, this.playlistController, TrackLibrary.getInstance(), this.playlistLibrary);
        
        Stage stage = (Stage) playlistListView.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Errore nel caricamento di PlaylistDetails.fxml: " + e.getMessage());
        e.printStackTrace();
    }
}
    

    /**
     * Aggiorna la lista delle playlist visualizzate.
     */

    @Override
    public void onPlaylistLibraryChanged() {
        refresh();
    }

    /**
     * Aggiorna la ListView con il contenuto corrente della PlaylistLibrary.
     * 
     */

    private void refresh() {
        playlistListView.getItems().setAll(playlistLibrary.getPlaylists());
    }

   @FXML
    private void handleGoToCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/views/PlaylistCreationDialog.fxml"));
            Parent root = loader.load();
            
            PlaylistCreationDialogController dialogController = loader.getController();
            dialogController.setPlaylistController(this.playlistController); 
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crea nuova playlist");
            dialogStage.setScene(new Scene(root));
            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Il codice si "mette in pausa" qui finché la finestra non viene chiusa
            dialogStage.showAndWait(); 
            
            // AGGIUNTA FONDAMENTALE: Quando la finestra si chiude, forza l'aggiornamento della UI
            refresh(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo elimina playlist
     */

  
}
