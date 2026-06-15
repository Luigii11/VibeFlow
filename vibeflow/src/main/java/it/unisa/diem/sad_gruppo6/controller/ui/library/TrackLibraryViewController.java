/**
 * @file TrackLibraryViewController.java
 * @brief Controller per la visualizzazione dell'intero catalogo musicale.
 * @details Implementa una logica multi-contesto con gestione dinamica dei controlli di inserimento.
 * Agisce come Observer della TrackLibrary per aggiornare la tabella in tempo reale.
 * @author EmanuelaGraziuso, LuigiAutorino, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.controller.ui.library;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.player.MediaPlayerController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistDetailsController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.command.AddTrackToPlaylistCommand;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
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
    private PlaylistController playlistController;
    private boolean isSelectionMode = false;
    private Playlist targetPlaylist;
    private PlaybackState playbackState;
    private TrackController trackController;

    /**
     * @brief Metodo di inizializzazione standard invocato dal framework JavaFX.
     * @details Configura la TableView, inizializza i controller di business,
     * registra la classe come Observer della TrackLibrary e definisce 
     * l'evento di doppio click per avviare la riproduzione sequenziale.
     */
    @FXML
    public void initialize() {
        this.library = TrackLibrary.getInstance();
        this.trackController = new TrackController();
        this.playbackController = new PlaybackController();
        this.playlistController = new PlaylistController(
            this.library,
            PlaylistLibrary.getInstance(),
            CommandManager.getInstance()
        );
        this.playbackState = PlaybackState.getInstance();
        this.playbackState.registerObserver(state -> refreshTrackIcons());

        this.library.registerObserver(this);
        
        setupTableView();
        setupBrowsingColumn();
        onLibraryChanged();

        trackTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !isSelectionMode) {
                Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    try {
                        playbackController.play(library.getTracks(), selectedTrack);
                    } catch (FileNotFoundException e) {
                        playbackController.stop();
                        showError("File non trovato",
                            "Il file audio di \"" + selectedTrack.getTitle() + "\" non è più presente nel percorso originale.");
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
        titleCol.setCellValueFactory(data -> 
        new SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String title, boolean empty) {
                super.updateItem(title, empty);
                if (empty || title == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Track rowTrack = getTableRow().getItem();
                    if (rowTrack == null) {
                        setText(title);
                        setGraphic(null);
                        return;
                    }
                    Track current = playbackState.getCurrentTrack();
                    String icon = rowTrack.equals(current) ? "▶   " : "♫   ";

                    Label titleLabel = new Label(icon + title);
                    titleLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

                    HBox tagsBox = buildTagIconsBox(rowTrack);

                    HBox cellBox = new HBox(8, titleLabel, tagsBox);
                    cellBox.setAlignment(Pos.CENTER_LEFT);

                    setText(null);
                    setGraphic(cellBox);
                }
            }
        });
        
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
     * @brief Costruisce un HBox contenente le icone dei tag attivi su una traccia.
     * @param track La traccia di cui visualizzare i tag.
     * @return Un HBox con le icone dei tag, pronto per essere inserito nella cella.
     */
    private HBox buildTagIconsBox(Track track) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);

        Label favIcon = new Label(track.getTagSet().hasTag(Tag.Favourite) ? "♥" : "♡");
        favIcon.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: #FF4C30;");
        favIcon.setOnMouseClicked(e -> {
            if (track.getTagSet().hasTag(Tag.Favourite)) {
                trackController.removeTag(track, Tag.Favourite);
            } else {
                trackController.addTag(track, Tag.Favourite);
            }
            e.consume();
        });
        box.getChildren().add(favIcon);

        if (track.getTagSet().hasTag(Tag.Explicit)) {
            Label explicitIcon = new Label("E");
            explicitIcon.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #888888; -fx-padding: 1 4 1 4; -fx-background-radius: 3;");
            box.getChildren().add(explicitIcon);
        }

        if (track.getTagSet().hasTag(Tag.NewRelease)) {
            Label newIcon = new Label("NEW");
            newIcon.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #5E27BF; -fx-padding: 1 4 1 4; -fx-background-radius: 3;");
            box.getChildren().add(newIcon);
        }

        return box;
    }

    /**
     * @brief Forza il ridisegno delle righe per aggiornare l'icona ▶ sulla traccia corrente.
     */
    private void refreshTrackIcons() {
        if (trackTable != null) {
            trackTable.refresh();
        }
    }

    /**
     * @brief Configura la colonna "Azioni" per la modalità di navigazione standard.
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
     */
    private void setupSelectionColumn() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button addBtn = new Button("+");
            
            {
                addBtn.setStyle(BTN_ADD_STYLE_NORMAL);
                
                addBtn.setOnAction(e -> {
                    Track track = getTableView().getItems().get(getIndex());
                    try {
                        CommandManager.getInstance().execute(new AddTrackToPlaylistCommand(targetPlaylist, track));
                        PlaylistLibrary.getInstance().updatePlaylist(targetPlaylist);
                        navigateBack();
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
     * @brief Torna a PlaylistDetails dopo una selezione.
     */
    private void navigateBack() {
        prepareForNavigation();
        try {
            PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
            controller.init(targetPlaylist, false); 
        } catch (IOException e) {
            showError("Navigation Error", "Could not navigate to the previous view.");
        }
    }

    @Override
    public void onTrackAdded(Track track) {
        onLibraryChanged();
    }

    @Override
    public void onLibraryChanged() {
        if (trackTable != null) {
            trackTable.getItems().setAll(library.getTracks());
        }

        PlaybackState state = PlaybackState.getInstance();
        state.setCurrentTrackList(library.getTracks());
        
        var iterator = state.getIterator();
        if (iterator != null) {
            iterator.updateTracks(library.getTracks());
        }
    }

    private void prepareForNavigation() {
        this.library.removeObserver(this);
        this.playbackState.removeObserver(state -> refreshTrackIcons());
        if (mediaPlayerController != null) {
            mediaPlayerController.cleanup();
        }
    }

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
            
            Track playingTrack = playbackState.getCurrentTrack();
            boolean isPlaying = (playingTrack != null && playingTrack.equals(track));
            boolean wasAudioActive = "Playing".equals(playbackState.getStatusName());
            int trackIndexBefore = library.getTracks().indexOf(track);
            String oldMp3Path = track.getPath();

            dialogStage.showAndWait(); 
            onLibraryChanged();
            
            if (isPlaying && trackIndexBefore != -1) {
                if (trackIndexBefore < library.getTracks().size()) {
                    Track updatedTrack = library.getTracks().get(trackIndexBefore);
                    String newMp3Path = updatedTrack.getPath();
                    playbackState.setCurrentTrack(updatedTrack);

                    if (oldMp3Path != null && !oldMp3Path.equals(newMp3Path)) {
                        it.unisa.diem.sad_gruppo6.model.service.PlaybackService.getInstance().start();
                        
                        if (!wasAudioActive) {
                            playbackController.pause();
                        }
                    }
                }
            }
        } catch (IOException e) {
            showError("UI Error", "Could not load the track editing dialog.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack(ActionEvent event) {
        prepareForNavigation();

        try {
            if (isSelectionMode && targetPlaylist != null) {
                PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
                controller.init(targetPlaylist, false);
            } else {
                App.setRoot("home/Home");
            }
        } catch (IOException e) {
            showError("Navigation Error", "Could not navigate to the previous view.");
        }
    }

    private void handleDeleteButtonClick(Track track) {
        if (track == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Remove from Track Library?");
        alert.setContentText("Are you sure you want to permanently delete \"" + track.getTitle() + "\"?");
        DialogUtils.personalizza(alert, trackTable, "🗑", "#FF4C30");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                trackController.deleteTrack(track);
                playbackController.handleTrackRemoved(track);
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        DialogUtils.personalizza(alert, trackTable, "❌", "#FF4C30");
        alert.showAndWait();
    }
}