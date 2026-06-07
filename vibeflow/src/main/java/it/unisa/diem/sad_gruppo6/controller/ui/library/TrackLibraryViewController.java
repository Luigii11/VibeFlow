/**
 * @file TrackLibraryViewController.java
 * @brief Controller per la visualizzazione dell'intero catalogo musicale.
 * @details Implementa una logica multi-contesto con gestione dinamica dei controlli di inserimento.
 * Agisce come Observer della TrackLibrary per aggiornare la tabella in tempo reale.
 * @author EmanuelaGraziuso, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
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

import java.io.FileNotFoundException;
import java.io.IOException;

public class TrackLibraryViewController implements TrackLibraryObserver {

    private static final String BTN_ICON_STYLE_NORMAL = "-fx-background-color: transparent; -fx-text-fill: #888888; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ICON_STYLE_HOVER  = "-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ICON_STYLE_DANGER = "-fx-background-color: transparent; -fx-text-fill: #FF4C30; -fx-font-size: 18px; -fx-cursor: hand;";
    private static final String BTN_ADD_STYLE_NORMAL  = "-fx-background-color: transparent; -fx-text-fill: #5E27BF; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand;";
    private static final String BTN_ADD_STYLE_DONE    = "-fx-background-color: transparent; -fx-text-fill: #888888; -fx-font-size: 20px;";

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

    private TrackLibrary library;
    private PlaybackController playbackController;
    private boolean isSelectionMode = false;
    private Playlist targetPlaylist;

    /**
     * @brief Metodo di inizializzazione standard invocato dal framework JavaFX.
     * @details Configura la TableView, inizializza i controller di business,
     * registra la classe come Observer della TrackLibrary e definisce 
     * l'evento di doppio click per avviare la riproduzione sequenziale.
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
                    // Avvia la riproduzione passando l'intera lista e il brano di partenza
                    try {
                        playbackController.play(library.getTracks(), selectedTrack);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * @brief Configura la UI per funzionare come schermata di selezione brani.
     * @details Nasconde i pulsanti di creazione globale e sostituisce i tasti di modifica con il tasto "+".
     * @param targetPlaylist La playlist a cui l'utente intende aggiungere i brani scelti.
     */
    public void initSelectionMode(Playlist targetPlaylist) {
        this.isSelectionMode = true;
        this.targetPlaylist = targetPlaylist;

        headerTitleLabel.setText("Add to: " + targetPlaylist.getName());
        headerSubtitleLabel.setText("Select tracks to add to your playlist");
        
        if (addTrackButton != null) {
            addTrackButton.setVisible(false);
            addTrackButton.setManaged(false);
        }
        
        setupSelectionColumn(); 
    }

    /**
     * @brief Imposta le logiche di estrazione dati per le colonne della tabella.
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
     * @brief Configura la colonna "Azioni" per la modalità di navigazione standard.
     * @details Inserisce dinamicamente in ogni riga i bottoni "Modifica" e "Rimuovi".
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
     * @brief Configura la colonna "Azioni" per la modalità di selezione (Aggiunta a playlist).
     * @details Genera un pulsante "+" e controlla se la traccia è già presente nella playlist.
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

    /**
     * @brief Metodo richiamato dall'Observer quando viene inserita una nuova traccia.
     * @param track La nuova traccia aggiunta alla TrackLibrary.
     */
    @Override
    public void onTrackAdded(Track track) {
        onLibraryChanged();
    }

    /**
     * @brief Metodo richiamato dall'Observer per aggiornare l'intera tabella.
     */
    @Override
    public void onLibraryChanged() {
        if (trackTable != null) {
            trackTable.getItems().setAll(library.getTracks());
        }
    }

    /**
     * @brief Gestisce le operazioni di pulizia della memoria prima del cambio scena.
     */
    private void prepareForNavigation() {
        this.library.removeObserver(this);
        if (mediaPlayerController != null) {
            mediaPlayerController.cleanup();
        }
    }

    /**
     * @brief Genera e apre la finestra di dialogo modale per la creazione di una nuova traccia.
     * @param event L'evento di click sul pulsante "Aggiungi Traccia".
     */
    @FXML
    private void handleAddTrack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unisa/diem/sad_gruppo6/view/library/TrackCreationDialog.fxml"));
            Parent root = loader.load();

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
     * @brief Genera e apre la finestra di dialogo modale per la modifica di una traccia esistente.
     * @param track La traccia da modificare passata in ingresso al controller del popup.
     */
    private void handleEditButtonClick(Track track) {
        if (track == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unisa/diem/sad_gruppo6/view/library/TrackEditDialog.fxml"));
            Parent root = loader.load();
            TrackEditDialogController dialogController = loader.getController();
            dialogController.setTrackToEdit(track);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Track");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT); 
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT); 
            dialogStage.setScene(scene);
            dialogStage.showAndWait(); 
            trackTable.refresh();
            
        } catch (IOException e) {
            showError("UI Error", "Could not load the track editing dialog.");
            e.printStackTrace();
        }
    }

    /**
     * @brief Riconduce l'utente alla schermata di navigazione precedente.
     * @details Se è in modalità selezione, torna alla playlist passando un solo parametro a init().
     * @param event L'evento generato dal click sul pulsante "Indietro".
     */
    @FXML
    private void handleGoBack(ActionEvent event) {
        prepareForNavigation();

        try {
            if (isSelectionMode && targetPlaylist != null) {
                PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
                controller.init(targetPlaylist);
            } else {
                App.setRoot("home/Home");
            }
        } catch (IOException e) {
            showError("Navigation Error", "Could not navigate to the previous view.");
        }
    }

    /**
     * @brief Mostra il prompt di conferma ed esegue il pattern Command per rimuovere un brano.
     * @param track L'oggetto Track da rimuovere definitivamente.
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
     * @brief Helper utility per mostrare messaggi di errore a schermo in formato modale.
     * @param title Intestazione principale del popup.
     * @param message Descrizione dettagliata dell'errore.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        DialogUtils.personalizza(alert, trackTable, "❌", "#FF4C30");
        alert.showAndWait();
    }
}