/**
 * @class MediaPlayerController
 * @brief Controller per la barra del player audio globale.
 * @details Mantiene il nome originale ma implementa la logica di un componente riutilizzabile
 * che ascolta lo stato della riproduzione in background.
 * @author EmanuelChirico, LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.controller.ui.player;

import java.io.FileNotFoundException;

import it.unisa.diem.sad_gruppo6.controller.business.playback.PlaybackController;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackObserver;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
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

    private PlaybackState playbackState;
    private PlaybackController playbackController;

    /**
     * @brief Inizializzazione automatica del componente.
     */
    @FXML
    public void initialize() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackController = new PlaybackController();
        this.playbackState.registerObserver(this);
        refreshUI();
    }

    @Override
    public void update(PlaybackState state) {
        refreshUI();
    }

    /**
     * @brief Aggiorna l'interfaccia in base al brano e allo stato corrente.
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
                progressBar.setValue(currentPos);
            } else {
                progressBar.setValue(0);
            }
        } else {
            trackTitleLabel.setText("No track playing");
            trackAuthorLabel.setText("-");
            progressBar.setValue(0);
        }

        if ("Playing".equals(status)) {
            playPauseButton.setText("⏸");
        } else {
            playPauseButton.setText("⏵");
        }
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrevious(ActionEvent event) throws FileNotFoundException {
        playbackController.previous();
    }

    /**
     * @brief Rimuove l'osservatore per prevenire Memory Leak.
     */
    public void cleanup() {
        playbackState.removeObserver(this);
    }
}