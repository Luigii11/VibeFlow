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


package it.unisa.diem.sad_gruppo6.controller.ui.home;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistCreationDialogController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistDetailsController;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
     * Inizializza automaticamente il controller recuperando le dipendenze dai Singleton
     */
    @FXML
    public void initialize() {
        
        this.playlistLibrary = PlaylistLibrary.getInstance();
        TrackLibrary trackLibrary = TrackLibrary.getInstance();
        CommandManager commandManager = new CommandManager();

        this.playlistController = new PlaylistController(trackLibrary, this.playlistLibrary, commandManager);
        
        this.playlistLibrary.registerObserver(this);
        refresh();
        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { 
                Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                if (selectedPlaylist != null) {
                    openPlaylistDetails(selectedPlaylist);
                }
            }
        });
    }

    private void openPlaylistDetails(Playlist playlist) 
    {
        try 
        {
            PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
            controller.init(playlist, this.playlistController, TrackLibrary.getInstance(), this.playlistLibrary);
        } 
        catch (IOException e) 
        {
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


    /**
     * Gestisce la pressione del pulsante menu.
     * Naviga alla schermata di visualizzazione di tutte le tracce della libreria.
     */
    @FXML
    private void handleGoToAllTracks(ActionEvent event) {
        try {
            App.setRoot("library/TrackLibraryView");
        } catch (IOException e) {
            System.err.println("Errore nella navigazione a TrackLibraryView: " + e.getMessage());
            e.printStackTrace();
        }
    }

   @FXML
    private void handleGoToCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/view/playlist/PlaylistCreationDialog.fxml"));
            Parent root = loader.load();
            
            PlaylistCreationDialogController dialogController = loader.getController();
            dialogController.setPlaylistController(this.playlistController); 
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crea nuova playlist");
            dialogStage.setScene(new Scene(root));
            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            dialogStage.showAndWait(); 
            
            refresh(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce la pressione sul pulsante "Elimina playlist".
     * Mostra un popup di conferma; se confermato, elimina la playlist selezionata.
     * Se la playlist è autogenerata, mostra un alert di errore.
     */

    @FXML
    private void handleDeletePlaylist(ActionEvent event ){
        Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();

        if(selectedPlaylist == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleziona una playlist da eliminare.", ButtonType.OK);
            alert.setTitle("Nessuna playlist selezionata");
            alert.setHeaderText("Nessuna playlist selezionata");
            alert.showAndWait();
            
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Elimina playlist");
        confirm.setHeaderText("Sei sicuro?");
        confirm.setContentText("Vuoi eliminare la playlist \"" + selectedPlaylist.getName() + "\"?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            playlistController.deletePlaylist(selectedPlaylist);
        } catch (IllegalArgumentException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setTitle("Operazione non consentita");
            error.setHeaderText("Impossibile eliminare la playlist");
            error.showAndWait();
        }
    }
    }

  
}
