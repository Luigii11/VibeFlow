/**
 * @file TrackLibraryViewController.java
 * @brief Controller per la visualizzazione dell'intero catalogo musicale.
 * @details Implementa una logica multi-contesto con gestione dinamica dei controlli di inserimento.
 * Rispetta i principi DRY e Single Responsibility separando la configurazione UI dalla logica di navigazione.
 * @author EmanuelaGraziuso, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.controller.ui.player.MediaPlayerController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistDetailsController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.command.AddTrackToPlaylistCommand;
import it.unisa.diem.sad_gruppo6.model.command.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibraryObserver;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TrackLibraryViewController implements TrackLibraryObserver {

    /* Costanti per stile pulsanti */
    private static final String BTN_ICON_STYLE_NORMAL = "-fx-background-color: transparent; -fx-text-fill: #888888; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ICON_STYLE_HOVER  = "-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ICON_STYLE_DANGER = "-fx-background-color: transparent; -fx-text-fill: #FF4C30; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ADD_STYLE_NORMAL  = "-fx-background-color: transparent; -fx-text-fill: #5E27BF; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String BTN_ADD_STYLE_DONE    = "-fx-background-color: transparent; -fx-text-fill: #888888; -fx-font-size: 20px;";

    /* Componenti grafici */
    @FXML private Label headerTitleLabel;
    @FXML private Label headerSubtitleLabel;
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleCol;
    @FXML private TableColumn<Track, String> authorCol;
    @FXML private TableColumn<Track, String> metaCol;
    @FXML private TableColumn<Track, String> durationCol;
    @FXML private TableColumn<Track, Void> actionCol;
    @FXML private Button addTrackButton; 
    
    @FXML private MediaPlayerController mediaPlayerController;

    /* Attributi */
    private TrackLibrary library;
    private PlaybackController playbackController;
    private boolean isSelectionMode = false;
    private Playlist targetPlaylist;
    private PlaylistController playlistController;

    /**
     * @brief Inizializzazione standard invocata da JavaFX all'avvio della schermata.
     * @details Prepara la tabella, popola i dati e imposta il comportamento di default (sola lettura/riproduzione).
     * Registra inoltre il controller come osservatore della TrackLibrary.
     */
    @FXML
    public void initialize() {
        this.library = TrackLibrary.getInstance();
        this.playbackController = new PlaybackController();
        
        this.library.registerObserver(this);
        
        setupTableView();
        setupBrowsingColumn();

        onLibraryChanged();

        trackTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !isSelectionMode) {
                Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    playbackController.play(selectedTrack);
                }
            }
        });
    }

    /**
     * @brief Attiva la modalità di "Selezione Brani" per aggiungerli a una playlist.
     * @details Modifica dinamicamente l'interfaccia: nasconde il bottone di creazione di nuove tracce, 
     * aggiorna il titolo della pagina e sostituisce la colonna delle azioni per mostrare i tasti "+".
     * @param targetPlaylist La playlist di destinazione a cui aggiungere i brani.
     * @param playlistController Il controller di business che gestirà l'aggiunta.
     */
    public void initSelectionMode(Playlist targetPlaylist, PlaylistController playlistController) {
        this.isSelectionMode = true;
        this.targetPlaylist = targetPlaylist;
        this.playlistController = playlistController;

        headerTitleLabel.setText("Add to: " + targetPlaylist.getName());
        headerSubtitleLabel.setText("Select tracks to add to your playlist");
        
        if (addTrackButton != null) {
            addTrackButton.setVisible(false);
            addTrackButton.setManaged(false);
        }
        
        setupSelectionColumn(); 
    }

    /* Configurazione tabella */

    /**
     * @brief Configura il binding dei dati tra la TableView e gli oggetti Track.
     * @details Associa le proprietà delle tracce (titolo, autore, metadati, durata) 
     * alle rispettive colonne visive, formattando le stringhe per l'interfaccia.
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
    }

    /**
     * @brief Configura la colonna delle azioni per la visualizzazione standard (Libreria globale).
     * @details Genera dinamicamente per ogni riga i pulsanti di "Modifica" (✎) ed "Eliminazione" (🗑)
     * e gestisce i relativi effetti di hover e click.
     */
    private void setupBrowsingColumn() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox container = new HBox(15, editBtn, deleteBtn);
            
            {
                container.setAlignment(Pos.CENTER);
                
                editBtn.setStyle(BTN_ICON_STYLE_NORMAL);
                deleteBtn.setStyle(BTN_ICON_STYLE_NORMAL);
                
                editBtn.setOnMouseEntered(e -> editBtn.setStyle(BTN_ICON_STYLE_HOVER));
                editBtn.setOnMouseExited(e -> editBtn.setStyle(BTN_ICON_STYLE_NORMAL));
                
                deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(BTN_ICON_STYLE_DANGER));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(BTN_ICON_STYLE_NORMAL));
                
                editBtn.setOnAction(e -> handleEditButtonClick(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDeleteButtonClick(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    /**
     * @brief Configura la colonna delle azioni per la modalità selezione (Aggiunta a playlist).
     * @details Sostituisce le opzioni di modifica/eliminazione con un pulsante "+". 
     * Se la traccia è già presente nella playlist di destinazione, mostra una spunta (✓) disabilitata.
     */
    private void setupSelectionColumn() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button addBtn = new Button("+");
            
            {
                addBtn.setStyle(BTN_ADD_STYLE_NORMAL);
                
                addBtn.setOnAction(e -> {
                    Track track = getTableView().getItems().get(getIndex());
                    try {
                        new AddTrackToPlaylistCommand(targetPlaylist, track).execute();
                        PlaylistLibrary.getInstance().updatePlaylist(targetPlaylist);
                        
                        // Ritona immediatamente ai Dettagli della Playlist
                        handleGoBack(null);
                        
                    } catch (IllegalArgumentException ex) {
                        showError("Cannot add track", ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Track track = getTableView().getItems().get(getIndex());
                    if (targetPlaylist != null && targetPlaylist.getTracks().contains(track)) {
                        addBtn.setText("✓");
                        addBtn.setDisable(true);
                        addBtn.setStyle(BTN_ADD_STYLE_DONE);
                    } else {
                        addBtn.setText("+");
                        addBtn.setDisable(false);
                        addBtn.setStyle(BTN_ADD_STYLE_NORMAL);
                    }
                    setGraphic(addBtn);
                }
            }
        });
    }

    /* Interfaccia observer */
    
    /**
     * @brief Callback invocata quando una singola traccia viene aggiunta.
     * @param track La traccia appena aggiunta.
     */
    @Override
    public void onTrackAdded(Track track) {
        onLibraryChanged();
    }

    /**
     * @brief Ricarica i dati della TableView quando la libreria globale subisce modifiche.
     */
    @Override
    public void onLibraryChanged() {
        if (trackTable != null) {
            trackTable.getItems().setAll(library.getTracks());
        }
    }

    /* Logica di navigazione */

    /**
     * @brief Esegue la deregistrazione sicura prima di abbandonare la vista.
     * @details Rimuove gli observer e invoca il cleanup del player per evitare memory leak.
     */
    private void prepareForNavigation() {
        this.library.removeObserver(this);
        if (mediaPlayerController != null) {
            mediaPlayerController.cleanup();
        }
    }

    /**
     * @brief Naviga verso la schermata di creazione di una nuova traccia.
     */
    @FXML
    private void handleAddTrack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unisa/diem/sad_gruppo6/view/library/TrackCreationDialog.fxml"));
            Parent root = loader.load();

            TrackCreationDialogController dialogController = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Track");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT); 
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT); 
            dialogStage.setScene(scene);
            dialogStage.showAndWait(); 
            
        } catch (IOException e) {
            showError("UI Error", "Could not load the track creation dialog.");
            e.printStackTrace();
        }
    }

    /**
     * @brief Prepara la traccia per la modifica e naviga verso la schermata dedicata.
     * @param track La traccia selezionata per la modifica.
     */
    private void handleEditButtonClick(Track track) {
        if (track == null) return;
        try {
            prepareForNavigation();
            TrackController controller = App.setRootAndGetController("library/editTrack");
            controller.setTrackToEdit(track);
        } catch (IOException e) {
            showError("Navigation Error", "Could not load the track editing view.");
        }
    }

    /**
     * @brief Gestisce il pulsante "Indietro" riconducendo l'utente alla vista di origine.
     * @details Se la vista era in Selection Mode, riapre i dettagli della playlist. 
     * Altrimenti, riconduce alla Home principale.
     */
    @FXML
    private void handleGoBack(ActionEvent event) {
        prepareForNavigation();

        try {
            if (isSelectionMode && targetPlaylist != null) {
                PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
                controller.init(targetPlaylist, playlistController, TrackLibrary.getInstance(), PlaylistLibrary.getInstance());
            } else {
                App.setRoot("home/Home");
            }
        } catch (IOException e) {
            showError("Navigation Error", "Could not navigate to the previous view.");
        }
    }

    /* Comandi sui dati */

    /**
     * @brief Avvia la procedura di eliminazione permanente di una traccia.
     * @details Mostra un Dialog di conferma prima di eseguire il comando di rimozione.
     * @param track La traccia selezionata per l'eliminazione.
     */
    private void handleDeleteButtonClick(Track track) {
        if (track == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Remove from Track Library?");
        alert.setContentText("Are you sure you want to permanently delete \"" + track.getTitle() + "\"?");
        DialogUtils.personalizza(alert, trackTable, "🗑", "#FF4C30");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new RemoveTrackFromLibraryCommand(track).execute();
            }
        });
    }

    /**
     * @brief Mostra un pop-up d'errore applicando il tema scuro personalizzato.
     * @param title Intestazione dell'errore.
     * @param message Corpo del messaggio d'errore.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        DialogUtils.personalizza(alert, trackTable, "❌", "#FF4C30");
        alert.showAndWait();
    }
}