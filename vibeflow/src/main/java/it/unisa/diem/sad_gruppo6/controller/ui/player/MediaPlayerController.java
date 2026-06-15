/**
 * @class MediaPlayerController
 * @brief Controller per la barra del player audio globale.
 * @details Mantiene il nome originale ma implementa la logica di un componente riutilizzabile
 * che ascolta lo stato della riproduzione in background.
 * @author EmanuelChirico, LuigiAutorino, ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.controller.ui.player;

import java.io.FileNotFoundException;

import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackObserver;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.PlaybackMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.ShuffleMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.LoopMode;
import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class MediaPlayerController implements PlaybackObserver {

    @FXML private Label trackTitleLabel;
    @FXML private Label trackAuthorLabel;
    @FXML private Button playPauseButton;
    @FXML private Slider progressBar;
    @FXML private Button shuffleButton;
    @FXML private Label timeLabel;
    @FXML private Button loopButton;
    @FXML private HBox trackTagsBox;

    private PlaybackState playbackState;
    private PlaybackController playbackController;
    private boolean userSeeking = false;

    /**
     * @brief Inizializzazione automatica del componente.
     */
    @FXML
    public void initialize() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackController = new PlaybackController();
        this.playbackState.registerObserver(this);

        progressBar.setOnMousePressed(e -> userSeeking = true);
        progressBar.setOnMouseReleased(e -> {
            userSeeking = false;
            if (playbackState.getCurrentTrack() != null) {
                playbackController.seekTo((int) progressBar.getValue());
            }
        });

        PlaybackService.getInstance().setOnFileNotFound(() -> {
            Track failedTrack = playbackState.getCurrentTrack();
            playbackController.stop();
            showFileNotFoundAlert(failedTrack);
        });

        refreshUI();
    }

    @Override
    public void update(PlaybackState state) {
        refreshUI();
    }

    /**
     * @brief Aggiorna l'interfaccia in base al brano e allo stato corrente.
     * @details Aggiorna titolo, autore, barra di avanzamento, contatore del tempo (AC1, AC2)
     *          e lo stile del pulsante shuffle. Viene chiamato ad ogni notifica Observer.
     */
    private void refreshUI() {
        Track actualTrack = playbackState.getCurrentTrack();
        String status = playbackState.getStatusName();

        if (actualTrack != null) {
            trackTitleLabel.setText(actualTrack.getTitle());
            trackAuthorLabel.setText(actualTrack.getAuthor());
            
            int currentPos = playbackState.getCurrentPosition();
            int totalDuration = actualTrack.getDuration();
            
            if (totalDuration > 0) {
                progressBar.setMax(totalDuration);
                if (!userSeeking) {
                    progressBar.setValue(currentPos);
                }
            } else {
                progressBar.setValue(0);
            }
            
            if (timeLabel != null) {
                timeLabel.setText(String.format("%d:%02d / %d:%02d",
                    currentPos / 60, currentPos % 60,
                    totalDuration / 60, totalDuration % 60));
            }

            refreshTrackTags(actualTrack);
        } else {
            trackTitleLabel.setText("No track playing");
            trackAuthorLabel.setText("-");
            progressBar.setValue(0);
            if (timeLabel != null) {
                timeLabel.setText("0:00 / 0:00");
            }
            if (trackTagsBox != null) {
                trackTagsBox.getChildren().clear();
            }
        }

        if ("Playing".equals(status)) {
            playPauseButton.setText("⏸");
        } else {
            playPauseButton.setText("⏵");
        }

        updateShuffleButtonStyle();
        updateLoopButtonStyle();
    }

    @FXML
    private void handlePlayPause(ActionEvent event) {
        if (playbackState.getStatusName().equals("Playing")) {
            playbackController.pause();   
        } else if (playbackState.getCurrentTrack() != null) {
            playbackController.resume();
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        try {
            playbackController.next();
        } catch (FileNotFoundException e) {
            Track failedTrack = playbackState.getCurrentTrack();
            playbackController.stop();
            showFileNotFoundAlert(failedTrack);
        }
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        try {
            playbackController.previous();
        } catch (FileNotFoundException e) {
            Track failedTrack = playbackState.getCurrentTrack();
            playbackController.stop();
            showFileNotFoundAlert(failedTrack);
        }
    }

    private void showFileNotFoundAlert(Track track) {
        String trackName = (track != null) ? track.getTitle() : "sconosciuta";
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "Il file audio \"" + trackName + "\" non è presente nel percorso originale",
                ButtonType.OK);
        alert.setTitle("File non trovato");
        alert.setHeaderText("Traccia non disponibile");
        DialogUtils.personalizza(alert, playPauseButton, "❌", "#FF4C30");
        alert.showAndWait();
    }

    /**
     * @brief Rimuove l'osservatore per prevenire Memory Leak.
     */
    public void cleanup() {
        playbackState.removeObserver(this);
    }

    /**
     * @brief Gestisce il click sul pulsante Shuffle, alternando tra modalità casuale
     *        e sequenziale (toggle on/off).
     *
     * @details Se la modalità corrente è già {@link ShuffleMode}, la disattiva
     *          ripristinando un {@link SequentialMode} posizionato sulla traccia
     *          corrente (AC5). Altrimenti attiva lo Shuffle (AC1-AC3). In entrambi
     *          i casi l'aggiornamento visivo del pulsante avviene tramite
     *          {@link #updateShuffleButtonStyle()}, chiamato da {@link #refreshUI()}
     *          in risposta alla notifica Observer.
     *
     * @param event L'evento JavaFX generato dal click sul pulsante.
     */
    @FXML
    private void handleShuffle(ActionEvent event) {
        PlaybackMode currentMode = playbackState.getMode();
        PlaybackMode newMode;

        if (currentMode instanceof ShuffleMode) {
            // Shuffle attivo → disattiva, torna alla modalità sequenziale (AC4, AC5)
            newMode = new SequentialMode();
        } else {
            // Shuffle non attivo → attiva la modalità casuale (AC1, AC2, AC3)
            newMode = new ShuffleMode();
        }

        playbackController.setMode(newMode);
        updateLoopButtonStyle();
    }

    /**
     * @brief Gestisce il click sul pulsante Loop, alternando tra modalità ciclica
     *        e sequenziale.
     *
     * @details Se la modalità corrente è già LoopMode, la disattiva
     *          ripristinando un SequentialMode posizionato sulla traccia
     *          corrente. Altrimenti attiva il Loop. In entrambi
     *          i casi l'aggiornamento visivo del pulsante avviene tramite updateLoopButtonStyle(), chiamato da refreshUI()
     *          in risposta alla notifica Observer.
     *
     * @param event L'evento JavaFX generato dal click sul pulsante.
     */
    @FXML
    private void handleLoop(ActionEvent event) {
        PlaybackMode currentMode = playbackState.getMode();
        PlaybackMode newMode;

        if (currentMode instanceof LoopMode) {
            // Loop attivo --> disattiva, torna alla modalità sequenziale
            newMode = new SequentialMode();
        } else {
            // Loop non attivo --> attiva la modalità ciclica
            newMode = new LoopMode();
        }

        playbackController.setMode(newMode);
        updateShuffleButtonStyle();
    }

    /**
     * @brief Aggiorna lo stile grafico del pulsante Shuffle in base alla modalità attiva.
     *
     * @details Se la modalità corrente è {@link ShuffleMode}, aggiunge la CSS class
     *          {@code "active"} al pulsante per evidenziarlo visivamente (AC2).
     *          La rimozione della classe ripristina l'aspetto normale (AC5).
     *
     */
    private void updateShuffleButtonStyle() {
        if (shuffleButton == null) return;

        boolean isShuffleActive = playbackState.getMode() instanceof ShuffleMode;

        if (isShuffleActive) {
            if (!shuffleButton.getStyleClass().contains("active-mode-btn")) {
                shuffleButton.getStyleClass().add("active-mode-btn");
            }
        } else {
            shuffleButton.getStyleClass().remove("active-mode-btn");
        }
    }

    /**
     * @brief Aggiorna lo stile grafico del pulsante Loop in base alla modalità attiva.
     *
     * @details Se la modalità corrente è LoopMode, aggiunge la CSS class
     *         "active" al pulsante per evidenziarlo visivamente.
     *          La rimozione della classe ripristina l'aspetto normale.
     */
    private void updateLoopButtonStyle() {
        if (loopButton == null) return;

        boolean isLoopActive = playbackState.getMode() instanceof LoopMode;

        if (isLoopActive) {
            if (!loopButton.getStyleClass().contains("active-mode-btn")) {
                loopButton.getStyleClass().add("active-mode-btn");
            }
        } else {
            loopButton.getStyleClass().remove("active-mode-btn");
        }
    }

    /**
     * @brief Aggiorna le icone dei tag visivi della traccia attualmente in riproduzione.
     * @details Mostra l'icona FAVOURITE (cuore), e le icone di sistema EXPLICIT e
     * NEW_RELEASE se presenti nel TagSet della traccia, in linea con il requisito
     * "ogni tag deve essere visibile in ogni contesto in cui compare la traccia,
     * incluso il player".
     *
     * @param track La traccia correntemente in riproduzione.
     */
    private void refreshTrackTags(Track track) {
        if (trackTagsBox == null) return;
        trackTagsBox.getChildren().clear();

        if (track.getTagSet().hasTag(Tag.Favourite)) {
            Label fav = new Label("♥");
            fav.setStyle("-fx-font-size: 12px; -fx-text-fill: #FF4C30;");
            trackTagsBox.getChildren().add(fav);
        }
        if (track.getTagSet().hasTag(Tag.Explicit)) {
            Label explicit = new Label("E");
            explicit.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #888888; -fx-padding: 0 4 0 4; -fx-background-radius: 3;");
            trackTagsBox.getChildren().add(explicit);
        }
        if (track.getTagSet().hasTag(Tag.NewRelease)) {
            Label newRel = new Label("NEW");
            newRel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; "
                    + "-fx-background-color: #5E27BF; -fx-padding: 0 4 0 4; -fx-background-radius: 3;");
            trackTagsBox.getChildren().add(newRel);
        }
    }

}