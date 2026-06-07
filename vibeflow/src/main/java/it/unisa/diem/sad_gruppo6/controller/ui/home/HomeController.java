/**
 * @file HomeController.java
 * @brief Controller principale per la schermata Home.
 * @details Si occupa di mostrare a schermo tutte le playlist dell'utente e di gestire la barra 
 * del player audio integrata. Si aggiorna da solo ogni volta che viene creata o eliminata una playlist.
 * * @author EmanuelaGraziuso, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.ui.home;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.player.MediaPlayerController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistCreationDialogController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistDetailsController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.geometry.Side;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 * @class HomeController
 * @brief Controller architetturale accoppiato al file Home.fxml.
 * @details Implementa il pattern Observer tramite l'interfaccia {@link PlaylistLibraryObserver} 
 * per garantire il refresh reattivo dell'interfaccia grafica a fronte di modifiche
 * nel sottostante strato di business logic.
 */
public class HomeController implements PlaylistLibraryObserver {

    /* Componenti grafici */ 
    @FXML private TilePane playlistTilePane;
    @FXML private MediaPlayerController mediaPlayerController;

    /* Dati e componenti logici */ 
    private PlaylistLibrary playlistLibrary;
    private PlaylistController playlistController;
    private Playlist selectedPlaylist = null;

    /**
     * @brief Metodo di avvio chiamato in automatico da JavaFX.
     * @details Recupera i dati salvati (i Singleton) e prepara la schermata disegnando 
     * le playlist per la prima volta.
     */
    @FXML
    public void initialize() {
        this.playlistLibrary = PlaylistLibrary.getInstance();
        TrackLibrary trackLibrary = TrackLibrary.getInstance();
        CommandManager commandManager = new CommandManager();

        this.playlistController = new PlaylistController(trackLibrary, this.playlistLibrary, commandManager);
        this.playlistLibrary.registerObserver(this);
        refresh();
    }

    /**
     * @brief Cambia schermata per mostrare le canzoni dentro una specifica playlist.
     * @param playlist La playlist cliccata dall'utente.
     */
    private void openPlaylistDetails(Playlist playlist) {
        try {
            // Prima di cambiare pagina scolleghiamo la Home e il Player per liberare memoria
            this.playlistLibrary.removeObserver(this);
            if (this.mediaPlayerController != null) {
                this.mediaPlayerController.cleanup();
            }
            
            PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
            controller.init(playlist);
        } catch (IOException e) {
            System.err.println("Error loading PlaylistDetails.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    } 

    /**
     * @brief Reagisce in automatico se la libreria delle playlist subisce modifiche.
     */
    @Override
    public void onPlaylistLibraryChanged() {
        refresh();
    }

    /**
     * @brief Svuota la griglia e ridisegna tutte le playlist aggiornate.
     */
    private void refresh() {
        playlistTilePane.getChildren().clear();
        this.selectedPlaylist = null;

        for (Playlist playlist : playlistLibrary.getPlaylists()) {
            VBox card = creaCardPlaylist(playlist);
            playlistTilePane.getChildren().add(card);
        }
    }

    /**
     * @brief Crea il "quadrato" (Card) grafico per una singola playlist.
     * @details Imposta l'icona, il nome, i pulsanti per rinominare/eliminare e l'azione per il doppio click.
     * @param playlist I dati della playlist da trasformare in grafica.
     * @return VBox Il contenitore pronto per essere aggiunto alla schermata.
     */
    private VBox creaCardPlaylist(Playlist playlist) {
        VBox card = new VBox();
        card.setPrefSize(200, 150);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("playlist-card");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(5, 5, 0, 0));

        Button menuButton = new Button("⋮");
        menuButton.getStyleClass().add("card-menu-btn");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename ✎");
        renameItem.setOnAction(e -> {
            this.selectedPlaylist = playlist;
            handleRenamePlaylist(e);
        });

        MenuItem deleteItem = new MenuItem("Delete 🗑");
        deleteItem.getStyleClass().add("delete-menu-item"); 
        deleteItem.setOnAction(e -> {
            this.selectedPlaylist = playlist;
            handleDeletePlaylist(e);
        });

        contextMenu.getItems().addAll(renameItem, deleteItem);

        menuButton.setOnAction(event -> {
            contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
            event.consume(); 
        });

        topBar.getChildren().add(menuButton);

        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(iconBox, Priority.ALWAYS);
        
        Label iconLabel = new Label("♫");
        iconLabel.getStyleClass().add("card-icon");
        iconBox.getChildren().add(iconLabel);

        VBox textBox = new VBox();
        textBox.getStyleClass().add("card-text-box");
        
        Label nameLabel = new Label(playlist.getName());
        nameLabel.getStyleClass().add("card-name-label");
        textBox.getChildren().add(nameLabel);

        card.getChildren().addAll(topBar, iconBox, textBox);

        // Gestione del click sulla Card
        card.setOnMouseClicked(event -> {
            // Rimuove il bordo colorato dalle altre card e lo aggiunge a questa
            playlistTilePane.getChildren().forEach(node -> 
                node.getStyleClass().remove("selected")
            );
            card.getStyleClass().add("selected");
            this.selectedPlaylist = playlist;

            // Se l'utente fa doppio click, entra nella playlist
            if (event.getClickCount() == 2) {
                openPlaylistDetails(playlist);
            }
        });

        return card;
    }

    /* Azioni sui pulsanti dell'interfaccia */ 

    /**
     * @brief Apre un pop-up per cambiare il nome alla playlist selezionata.
     */
    @FXML
    private void handleRenamePlaylist(ActionEvent event) {
        Playlist selected = this.selectedPlaylist;
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rename Playlist");
        dialog.setHeaderText("Enter new name for \"" + selected.getName() + "\"");
        dialog.setContentText("New name:");
        
        DialogUtils.personalizza(dialog, playlistTilePane, "✎", "#5E27BF");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            try {
                playlistController.renamePlaylist(selected, newName);
                refresh(); // Aggiorna visivamente il nuovo nome
            } catch (IllegalArgumentException e) {
                showErrorAlert("Rename failed", e.getMessage());
            }
        });
    }

    /**
     * @brief Chiede conferma all'utente e poi cancella la playlist selezionata.
     */
    @FXML
    private void handleDeletePlaylist(ActionEvent event) {
        Playlist selected = this.selectedPlaylist;

        if (selected == null) {
            showWarningAlert("No playlist selected", "Please select a playlist by clicking on its card before deleting.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Playlist");
        confirm.setContentText("Are you sure you want to delete the playlist \"" + selected.getName() + "\"?");
        
        DialogUtils.personalizza(confirm, playlistTilePane, "🗑", "#FF4C30");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistController.deletePlaylist(selected);
            } catch (IllegalArgumentException e) {
                showErrorAlert("Deletion failed", e.getMessage());
            }
        }
    }

    /**
     * @brief Passa alla schermata che mostra TrackLibrary.
     */
    @FXML
    private void handleGoToAllTracks(ActionEvent event) {
        try {
            this.playlistLibrary.removeObserver(this);
            if (this.mediaPlayerController != null) {
                this.mediaPlayerController.cleanup();
            }
            App.setRoot("library/TrackLibraryView");
        } catch (IOException e) {
            System.err.println("Error routing to TrackLibraryView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre la finestra CreatePlaylist.
     */
    @FXML
    private void handleGoToCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/view/playlist/PlaylistCreationDialog.fxml"));
            Parent root = loader.load();
            
            PlaylistCreationDialogController dialogController = loader.getController();
            dialogController.setPlaylistController(this.playlistController); 
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create new playlist");
            dialogStage.setScene(new Scene(root));
            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            dialogStage.showAndWait(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Gestione Alert */

    /**
     * @brief Mostra un pop-up di errore con il tema scuro personalizzato.
     * @param titoloHeader Il titolo principale dell'errore.
     * @param messaggioContenuto Il dettaglio che spiega cosa è andato storto.
     */
    private void showErrorAlert(String titoloHeader, String messaggioContenuto) {
        Alert error = new Alert(Alert.AlertType.ERROR, messaggioContenuto, ButtonType.OK);
        error.setTitle("Operation failed");
        error.setHeaderText(titoloHeader);
        DialogUtils.personalizza(error, playlistTilePane, "❌", "#FF4C30");
        error.showAndWait();
    }

    /**
     * @brief Mostra un pop-up di avviso con il tema scuro personalizzato.
     * @param titoloHeader Il titolo principale dell'avviso.
     * @param messaggioContenuto Il testo che spiega all'utente cosa deve fare.
     */
    private void showWarningAlert(String titoloHeader, String messaggioContenuto) {
        Alert alert = new Alert(Alert.AlertType.WARNING, messaggioContenuto, ButtonType.OK);
        alert.setTitle(titoloHeader);
        alert.setHeaderText(titoloHeader);
        DialogUtils.personalizza(alert, playlistTilePane, "⚠", "#FF6E57");
        alert.showAndWait();
    }
}