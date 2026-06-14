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
 * @author EmanuelaGraziuso, ChiaraCrisci, LuigiAutorino, EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.playlist;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.library.TrackLibraryViewController;
import it.unisa.diem.sad_gruppo6.controller.ui.player.MediaPlayerController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackObserver;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.PlaybackMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.ShuffleMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.LoopMode;
import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import javafx.scene.control.Label;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.TableRow;




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
    @FXML private Button addTrackButton;

    @FXML private HBox undoNotificationBar;
    @FXML private Label  undoMessageLabel;
    @FXML private Label  undoCountdownLabel;
    @FXML private Button undoCancelButton;

  
    @FXML private Button playlistShuffleButton;
    @FXML private Button playlistLoopButton;


    /* Attributi */
    private Playlist currentPlaylist;
    private PlaylistController playlistController;
    private PlaylistLibrary playlistLibrary;
    private PlaybackState playbackState;
    private PlaybackController playbackController;
    private Timeline undoTimeline;
    private TrackController trackController;

    /**
     * @brief Inizializzazione del contesto e registrazione degli Observer.
     * @details Configura il controller in base alla playlist selezionata, recupera i Singleton 
     * necessari per il business logic e inizializza i listener per i click sulla tabella.
     * Se trackJustAdded è true, mostra subito la notifica Undo.
     * @param playlist La playlist corrente di cui mostrare i dettagli.
     */
    public void init(Playlist playlist, boolean trackJustAdded) {
        this.currentPlaylist = playlist;
        this.playlistLibrary = PlaylistLibrary.getInstance();
        
        this.playlistController = new PlaylistController(
            TrackLibrary.getInstance(), 
            this.playlistLibrary, 
            CommandManager.getInstance()
        );

        this.playbackState = PlaybackState.getInstance();
        this.playbackController = new PlaybackController();
        this.playlistLibrary.registerObserver(this);
        this.playbackState.registerObserver(this); 
        this.trackController = new TrackController();

        setupTableView(); 
        refresh();
        updateHeaderPlayButton(); 

       trackTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    try {
                        playbackController.play(currentPlaylist.getTracks(), selectedTrack);
                    } catch (FileNotFoundException e) {
                        showAlert(AlertType.ERROR, "File Not Found", 
                            "La traccia non è più disponibile nel percorso originale.");
                    }
                }
            }
        });

        if (trackJustAdded) {
        showUndoNotification("Track added to playlist.");
        } 
    }

    /**
     * @brief Overload di init() per il caso standard (nessuna traccia appena aggiunta).
     * @details Delega all'overload completo passando trackJustAdded = false.
     * @param playlist La playlist corrente di cui mostrare i dettagli.
     */
    public void init(Playlist playlist) {
        init(playlist, false);

        }

    /**
     * @brief Collega dinamicamente le colonne della TableView alle proprietà dei brani.
     * @details Configura le formattazioni di testo per ogni colonna e genera dinamicamente 
     * un pulsante di eliminazione per la colonna delle azioni. Gestisce inoltre il 
     * Drag&Drop per il riordinamento manuale delle tracce.
     */
    private void setupTableView() {
      titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
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
                if (empty || currentPlaylist.isAutoGenerated()) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        

        // --- SETUP DRAG & DROP PER IL RIORDINAMENTO ---
        trackTable.setRowFactory(tv -> {
            
            TableRow<Track> row = new TableRow<>(); 

            // 1. Inizio del trascinamento
            row.setOnDragDetected(event -> {
                // Consentito solo se la riga non è vuota e la playlist NON è autogenerata
                if (!row.isEmpty() && currentPlaylist != null && !currentPlaylist.isAutoGenerated()) {
                    
                    // LA SOLUZIONE UX: Il cursore diventa un pugno SOLO quando inizi a trascinare!
                    row.getScene().setCursor(javafx.scene.Cursor.CLOSED_HAND);
                    
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    
                    // Mostra un'anteprima visiva della riga trascinata
                    db.setDragView(row.snapshot(null, null)); 
                    
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(index.toString()); 
                    db.setContent(cc);
                    event.consume();
                }
            });

            // 2. Passaggio sopra un'altra riga
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString() && event.getGestureSource() != row && currentPlaylist != null && !currentPlaylist.isAutoGenerated()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            // 3. Rilascio del mouse (Drop)
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    int dropIndex = row.isEmpty() ? trackTable.getItems().size() - 1 : row.getIndex();
                    
                    Track draggedTrack = trackTable.getItems().get(draggedIndex);

                    try {
                        playlistController.reorderTrack(draggedTrack, currentPlaylist, dropIndex);
                        success = true;
                        showUndoNotification("Track reordered.");
                        
                    } catch (IllegalArgumentException ex) {
                        showAlert(AlertType.WARNING, "Operazione non consentita", ex.getMessage());
                    } catch (Exception ex) {
                        showAlert(AlertType.ERROR, "Errore di Salvataggio", ex.getMessage());
                        refresh(); 
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            // 4. Fine del trascinamento (sia che abbia avuto successo, sia che sia stato annullato)
            row.setOnDragDone(event -> {
                // Ripristiniamo la freccia normale per tutta la scena
                row.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            });

            return row;
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
        updatePlaylistShuffleButton();
        updatePlaylistLoopButton();
        trackTable.refresh();
    }

    /**
     * @brief Mostra il pulsante "+ Add a track" solo per le playlist create manualmente.
     * @details Le playlist autogenerate (genere, anno, EXPLICIT, NEW_RELEASE) e la
     * playlist virtuale "Preferiti" hanno un contenuto calcolato esclusivamente da
     * PlaylistController/XxxPlaylistCreator e non ammettono l'aggiunta manuale
     * di tracce.
     */
    private void updateAddTrackButtonVisibility() {
        if (addTrackButton == null) return;
        boolean isManual = currentPlaylist != null && !currentPlaylist.isAutoGenerated();
        addTrackButton.setVisible(isManual);
        addTrackButton.setManaged(isManual);
    }

    /**
     * @brief Svuota e ricarica i dati delle righe della tabella e delle etichette informative.
     */
    private void refresh() {
                if (currentPlaylist != null) {
            playlistNameLabel.setText(currentPlaylist.getName());
            trackCountLabel.setText("Total tracks: " + currentPlaylist.getTracks().size());
            trackTable.getItems().setAll(currentPlaylist.getTracks());
            PlaybackState.getInstance().setCurrentTrackList(currentPlaylist.getTracks());
        }

        updateAddTrackButtonVisibility();

        var iterator = PlaybackState.getInstance().getIterator();
        if (iterator != null) {
            iterator.updateTracks(currentPlaylist.getTracks());
        }
    }

    /**
     * Aggiorna l'icona del pulsante Play/Pausa nell'intestazione in base allo stato del player.
     * Controlla se la playlist attualmente visualizzata coincide con quella in riproduzione.
     */
    private void updateHeaderPlayButton() 
    {
        String status = playbackState.getStatusName();
        Playlist playingPlaylist = playbackState.getCurrentPlaylist();
    
        if ("Playing".equals(status) && playingPlaylist != null && playingPlaylist.equals(currentPlaylist)) 
        {
            playlistPlayPauseButton.setText("⏸");
        } 
        else 
        {
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
        Playlist playingPlaylist = playbackState.getCurrentPlaylist();
        if (playingPlaylist != null && playingPlaylist.equals(currentPlaylist)) {
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
         * @brief Gestisce il click sul pulsante Shuffle nell'header della playlist.
         *
         * @details Commuta la modalità tra {@link ShuffleMode} e {@link SequentialMode}
         *          senza interrompere la traccia corrente; lo shuffle ha effetto dal
         *          brano successivo (AC3). Se non è in corso alcuna riproduzione il
         *          pulsante non produce effetti sulla riproduzione stessa: si limita
         *          a impostare la modalità che verrà usata al prossimo avvio (AC1).
         *
         * @param event L'evento JavaFX generato dal click.
         */
        @FXML
        private void handlePlaylistShuffle(ActionEvent event) {
            PlaybackMode currentMode = playbackState.getMode();

            if (currentMode instanceof ShuffleMode) {
                // Shuffle attivo → disattiva, ripristina sequenziale (AC4, AC5)
                playbackController.setMode(new SequentialMode());
            } else {
                // Shuffle non attivo → attiva (AC1, AC2, AC3)
                playbackController.setMode(new ShuffleMode());
                updatePlaylistLoopButton();
            }
        }

        /**
         * @brief Gestisce il click sul pulsante Loop nell'header della playlist.
         *
         * @details Commuta la modalità tra LoopMode e SequentialMode
         *          senza interrompere la traccia corrente. Loop e shuffle sono
         *          mutuamente esclusivi: attivare uno disattiva l'altro visivamente.
         *
         * @param event L'evento JavaFX generato dal click.
         */
        @FXML
        private void handlePlaylistLoop(ActionEvent event) {
            PlaybackMode currentMode = playbackState.getMode();

            if (currentMode instanceof LoopMode) {
                // Loop attivo --> disattiva, ripristina sequenziale
                playbackController.setMode(new SequentialMode());
            } else {
                // Loop non attivo --> attiva
                playbackController.setMode(new LoopMode());
                updatePlaylistShuffleButton();
            }
        }

        /**
         * @brief Aggiorna lo stile grafico del pulsante Shuffle nella vista playlist.
         *
         * @details Aggiunge la CSS class {@code "active"} quando la modalità corrente
         *          è {@link ShuffleMode} (AC2), la rimuove altrimenti (AC5).
         *
         * @author ChiaraCrisci
         */
        private void updatePlaylistShuffleButton() {
            if (playlistShuffleButton == null) return;
            boolean isShuffleActive = playbackState.getMode() instanceof ShuffleMode;
            if (isShuffleActive) {
                if (!playlistShuffleButton.getStyleClass().contains("active-mode-btn")) {
                    playlistShuffleButton.getStyleClass().add("active-mode-btn");
                }
            } else {
                playlistShuffleButton.getStyleClass().remove("active-mode-btn");
            }
        }

        /**
         * @brief Aggiorna lo stile grafico del pulsante Loop nella vista playlist.
         *
         * @details Aggiunge la CSS class "active" quando la modalità corrente
         *          è LoopMode, la rimuove altrimenti.
         */
        private void updatePlaylistLoopButton() {
            if (playlistLoopButton == null) return;
            boolean isLoopActive = playbackState.getMode() instanceof LoopMode;
            if (isLoopActive) {
                if (!playlistLoopButton.getStyleClass().contains("active-mode-btn")) {
                    playlistLoopButton.getStyleClass().add("active-mode-btn");
                }
            } else {
                playlistLoopButton.getStyleClass().remove("active-mode-btn");
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
            if(undoTimeline!=null) undoTimeline.stop();
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
            playbackController.handleTrackRemoved(track);
            refresh();
            showUndoNotification("\"" + track.getTitle() + "\" removed from playlist.");
        }

    }

    /**
     * Mostra il pannello di notifica con countdown per annullare l'aggiunta della traccia alla playlist.
     * Avvia un timeline javafx di 10 secondi. Allo scadere dei 10 secondi l'operazione di aggiunta diventa permanente.
     * 
     * @param message Testo da mostrare nel banner della notifica.
     */
    private void showUndoNotification(String message){
        if(undoTimeline != null) undoTimeline.stop();

        undoMessageLabel.setText(message);
        undoNotificationBar.setVisible(true);
        undoNotificationBar.setManaged(true);

        final int[] secondsLeft = {10};
        undoCountdownLabel.setText(String.valueOf(secondsLeft[0]));

        undoTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                secondsLeft[0]--;
                undoCountdownLabel.setText(String.valueOf(secondsLeft[0]));

                // Scaduto il tempo: notifica scompare, nessun undo 
                if (secondsLeft[0] <= 0) {
                    undoTimeline.stop();
                    undoNotificationBar.setVisible(false);
                    undoNotificationBar.setManaged(false);
                }
            })
        );
        undoTimeline.setCycleCount(10);
        undoTimeline.play();
    }

    /**
     * Handler del pulsante "Annulla" nella notifica. Ferma il countdown, nasconde la notifica e invoca CommandManager.unod() per 
     * rimuovere la traccia appena aggiunta alla playlist. 
     */
    @FXML
    private void handleUndo(){
        if (undoTimeline != null) undoTimeline.stop();
    
        undoNotificationBar.setVisible(false);
        undoNotificationBar.setManaged(false);

        CommandManager.getInstance().undo();
        refresh();
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

    /**
     * @brief Costruisce un HBox contenente le icone dei tag attivi su una traccia.
     * @details Per ogni Tag presente nel TagSet della traccia, crea una Label-icona
     * (FAVOURITE = cuore pieno, EXPLICIT = "E", NEW_RELEASE = "NEW") e, per il tag
     * FAVOURITE, registra un handler di click che invoca TrackController.addTag()/removeTag()
     * a seconda dello stato corrente. I tag di sistema
     * (EXPLICIT, NEW_RELEASE) sono mostrati ma non cliccabili.
     *
     * @param track La traccia di cui visualizzare i tag.
     * @return Un HBox con le icone dei tag, pronto per essere inserito nella cella.
     */
    private HBox buildTagIconsBox(Track track) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);

        // Tag manuale: FAVOURITE (cuore)
        Label favIcon = new Label(track.getTagSet().hasTag(Tag.FAVOURITE) ? "♥" : "♡");
        favIcon.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: #FF4C30;");
        favIcon.setOnMouseClicked(e -> {
            boolean isFavouritePlaylist = currentPlaylist != null
                    && currentPlaylist.isAutoGenerated()
                    && Tag.FAVOURITE.name().equals(currentPlaylist.getName());

            if (track.getTagSet().hasTag(Tag.FAVOURITE)) {
                trackController.removeTag(track, Tag.FAVOURITE);

                if (isFavouritePlaylist) {
                    currentPlaylist.getTracks().remove(track);
                    refresh();
                }
            } else {
                trackController.addTag(track, Tag.FAVOURITE);

                if (isFavouritePlaylist && !currentPlaylist.getTracks().contains(track)) {
                    currentPlaylist.getTracks().add(track);
                    refresh();
                }
            }
            trackTable.refresh();
            e.consume();
        });
        box.getChildren().add(favIcon);

        // Tag di sistema: EXPLICIT
        if (track.getTagSet().hasTag(Tag.EXPLICIT)) {
            Label explicitIcon = new Label("E");
            explicitIcon.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #888888; -fx-padding: 1 4 1 4; -fx-background-radius: 3;");
            box.getChildren().add(explicitIcon);
        }

        // Tag di sistema: NEW_RELEASE
        if (track.getTagSet().hasTag(Tag.NEW_RELEASE)) {
            Label newIcon = new Label("NEW");
            newIcon.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #5E27BF; -fx-padding: 1 4 1 4; -fx-background-radius: 3;");
            box.getChildren().add(newIcon);
        }

        return box;
    }
}