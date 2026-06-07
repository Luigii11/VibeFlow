/**
 * @file PlaylistDetailsController.java
 * @brief Controller della vista dei dettagli di una playlist.
 * @details Mostra i brani contenuti in una specifica playlist e permette all'utente di:
 * - Aggiungere una traccia alla playlist selezionandola dalla libreria globale.
 * - Rimuovere una traccia dalla playlist.
 * L'interfaccia si aggiorna automaticamente tramite il pattern Observer sulla classe {@link PlaylistLibrary}.
 * Inoltre, delega la gestione della barra del player audio inferiore a un sotto-controller integrato.
 * @see PlaylistController
 * @see PlaylistLibraryObserver
 * @see PlayerBarController
 * @author EmanuelaGraziuso, ChiaraCrisci, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.ui.playlist;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.library.TrackLibraryViewController;
import it.unisa.diem.sad_gruppo6.controller.ui.player.MediaPlayerController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackObserver;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class PlaylistDetailsController implements PlaylistLibraryObserver, PlaybackObserver {
    
    /* Componenti grafici */
    @FXML private Label playlistNameLabel;
    @FXML private Label trackCountLabel;
    @FXML private Button playlistPlayPauseButton;
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleCol;
    @FXML private TableColumn<Track, String> authorCol;
    @FXML private TableColumn<Track, String> metaCol;
    @FXML private TableColumn<Track, String> durationCol;
    @FXML private TableColumn<Track, Void> actionCol;
    @FXML private MediaPlayerController mediaPlayerController;

    /* Attributi */
    private Playlist currentPlaylist;
    private PlaylistController playlistController;
    private PlaylistLibrary playlistLibrary;
    private PlaybackState playbackState;
    private PlaybackController playbackController;
    
    /**
     * @brief Inizializzazione del contesto e registrazione degli Observer.
     * @details Configura il controller in base alla playlist selezionata, recupera i Singleton 
     * necessari per il business logic e inizializza i listener per i click sulla tabella.
     * @param playlist La playlist corrente di cui mostrare i dettagli.
     */
    public void init(Playlist playlist) {
        this.currentPlaylist = playlist;
        this.playlistLibrary = PlaylistLibrary.getInstance();
        
        this.playlistController = new PlaylistController(
            TrackLibrary.getInstance(), 
            this.playlistLibrary, 
            new it.unisa.diem.sad_gruppo6.model.command.CommandManager() 
        );

        this.playbackState = PlaybackState.getInstance();
        this.playbackController = new PlaybackController();
        this.playlistLibrary.registerObserver(this);
        this.playbackState.registerObserver(this); 
        
        setupTableView(); 
        refresh();
        updateHeaderPlayButton(); 

       trackTable.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2) {
        Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            try {
                playbackController.play(selectedTrack);
            } catch (FileNotFoundException e) {
                showAlert(AlertType.ERROR, "File Not Found", 
                    "La traccia non è più disponibile nel percorso originale.");
            }
        }
    }
});
    }

    /**
     * @brief Collega dinamicamente le colonne della TableView alle proprietà dei brani.
     * @details Configura le formattazioni di testo per ogni colonna e genera dinamicamente 
     * un pulsante di eliminazione per la colonna delle azioni.
     */
    private void setupTableView() {
        titleCol.setCellValueFactory(data -> new SimpleStringProperty("♫   " + data.getValue().getTitle()));
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        
        metaCol.setCellValueFactory(data -> {
            String genre = data.getValue().getGenre() != null ? data.getValue().getGenre() : "Unknown";
            String year = data.getValue().getYear() > 0 ? String.valueOf(data.getValue().getYear()) : "----";
            return new SimpleStringProperty(genre + "  •  " + year);
        });

        durationCol.setCellValueFactory(data -> {
            int totalSeconds = data.getValue().getDuration();
            return new SimpleStringProperty(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60));
        });

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            {
                deleteBtn.getStyleClass().add("cell-inline-delete-btn");
                deleteBtn.setOnAction(e -> {
                    Track track = getTableView().getItems().get(getIndex());
                    handleRemoveTrack(track);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    /**
     * @brief Callback invocata quando la libreria delle playlist subisce una modifica.
     * @details Scatena l'aggiornamento visivo della tabella e dei contatori.
     */
    @Override
    public void onPlaylistLibraryChanged() { 
        refresh(); 
    }

    /**
     * @brief Callback invocata quando il player audio cambia stato.
     * @details Permette di mantenere sincronizzata l'icona Play/Pausa dell'intestazione.
     * @param state Lo stato globale di riproduzione.
     */
    @Override
    public void update(PlaybackState state) { 
        updateHeaderPlayButton(); 
    }

    /**
     * @brief Svuota e ricarica i dati delle righe della tabella e delle etichette informative.
     */
    private void refresh() {
        if (currentPlaylist != null) {
            playlistNameLabel.setText(currentPlaylist.getName());
            trackCountLabel.setText("Total tracks: " + currentPlaylist.getTracks().size());
            trackTable.getItems().setAll(currentPlaylist.getTracks());
        }
    }

    /**
     * @brief Aggiorna l'icona del pulsante Play/Pausa nell'intestazione in base allo stato del player.
     * @details Controlla se la playlist attualmente visualizzata coincide con quella in riproduzione.
     */
    private void updateHeaderPlayButton() {
        String status = playbackState.getStatusName();
        
        if ("Playing".equals(status) && currentPlaylist != null && 
            playbackState.getCurrentTrack() != null && currentPlaylist.getTracks().contains(playbackState.getCurrentTrack())) {
            playlistPlayPauseButton.setText("⏸");
        } else {
            playlistPlayPauseButton.setText("▶");
        }
    }

    /**
     * @throws FileNotFoundException 
     * @brief Gestisce l'avvio della riproduzione dell'intera playlist o il cambio di stato pausa/play.
     * @param event L'evento di click sul pulsante circolare di Playback nell'intestazione.
     */
    @FXML
    private void handlePlayPlaylist(ActionEvent event) throws FileNotFoundException {
        if (currentPlaylist == null || currentPlaylist.getTracks().isEmpty()) {
            showAlert(AlertType.WARNING, "Empty Playlist", "This playlist has no tracks to play.");
            return;
        }
        
        String status = playbackState.getStatusName();
        if (playbackState.getCurrentTrack() != null && currentPlaylist.getTracks().contains(playbackState.getCurrentTrack())) {
            if ("Playing".equals(status)) {
                playbackController.pause();
            } else {
                playbackController.resume();
            }
        } else {
            try {
            playbackController.play(currentPlaylist);
        } catch (IllegalArgumentException e) {
            showAlert(AlertType.ERROR, "Playback Error", e.getMessage());
        } catch (FileNotFoundException e) {
            showAlert(AlertType.ERROR, "File Not Found", 
                "La traccia non è più disponibile nel percorso originale.");
        }
        }
    }

    /**
     * @brief Apre la schermata della libreria globale per consentire l'aggiunta di un nuovo brano.
     * @param event L'evento generato dal click sul pulsante "Aggiungi Traccia".
     */
    @FXML
    private void handleAddTrack(ActionEvent event) {
        try {
            TrackLibraryViewController controller = App.setRootAndGetController("library/TrackLibraryView");
            controller.initSelectionMode(this.currentPlaylist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Rimuove gli osservatori e ritorna in modo sicuro alla schermata Home.
     * @details Previene memory leak deregistrando il controller e arrestando il widget media player.
     * @param event L'evento generato dal click sul pulsante "Indietro".
     */
    @FXML
    private void handleGoBack(ActionEvent event) {
        try {
            this.playlistLibrary.removeObserver(this);
            this.playbackState.removeObserver(this);
            if (this.mediaPlayerController != null) {
                this.mediaPlayerController.cleanup();
            }
            App.setRoot("home/Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Chiede conferma all'utente ed elimina il brano selezionato dalla playlist.
     * @param track La traccia da rimuovere.
     */
    private void handleRemoveTrack(Track track) {
        if (track == null) return;
        
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove Track");
        confirm.setContentText("Are you sure you want to remove \"" + track.getTitle() + "\"?");
        DialogUtils.personalizza(confirm, trackTable, "🗑", "#FF4C30");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            playlistController.removeTrackFromPlaylist(track, currentPlaylist);
            refresh();
        }
    }

    /**
     * @brief Crea e mostra una finestra di avviso o errore personalizzata con il tema scuro.
     * @param type Il tipo di Alert (WARNING, ERROR, ecc.).
     * @param title Il titolo della finestra di dialogo.
     * @param message Il messaggio dettagliato da mostrare all'utente.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        DialogUtils.personalizza(alert, trackTable, type == AlertType.ERROR ? "❌" : "⚠", type == AlertType.ERROR ? "#FF4C30" : "#FF6E57");
        alert.showAndWait();
    }
}