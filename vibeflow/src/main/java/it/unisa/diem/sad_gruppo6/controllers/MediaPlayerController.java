/**
 * La classe 'MediaPlayerController' è il controller JavaFX per la vista del player.
 * Si occupa di aggiornare la UI in base allo stato del player e gestisce le interazioni dell'utente
 * con i controlli di riproduzione. Implementa l'interfaccia 'PlaybackObserver' per ricevere aggiornamenti.
 * 
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.playback.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.App;

public class MediaPlayerController implements Initializable, PlaybackObserver {

    private PlaybackState playbackState;
    private PlaybackController playbackController;

    @FXML private Label trackTitleLabel;
    @FXML private Label trackAuthorLabel;
    @FXML private Label statusLabel;
    @FXML private Button playPauseButton;

    public MediaPlayerController() 
    {
    this.playbackState = PlaybackState.getInstance();
    this.playbackController = new PlaybackController();
    }

    /**
     * Inizializza il controller, registrandolo come osservatore dello stato di riproduzione e aggiornando la UI.
      * Viene chiamato automaticamente da JavaFX quando la vista viene caricata.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        playbackState.registerObserver(this);
        update(playbackState);
    }

     /**
      * Aggiorna la UI in base allo stato attuale del player. Viene chiamato ogni volta che lo stato di riproduzione cambia.
      * 
      * 
      * @param state Lo stato di riproduzione aggiornato, utilizzato per aggiornare la UI.
     */
    @Override
    public void update(PlaybackState state) 
    {
        refreshUI();
    }

    /**
      * Aggiorna le etichette e il testo del pulsante in base allo stato attuale del player e alla traccia in riproduzione.
       * Viene chiamato da 'update' per riflettere i cambiamenti dello stato di riproduzione sulla UI.
       * 
       * @param state Lo stato di riproduzione aggiornato, utilizzato per aggiornare la UI.
     */
    private void refreshUI() 
    {
        Track actual_track = playbackState.getCurrentTrack();
        String status = playbackState.getStatusName();

        statusLabel.setText(status);

        if (actual_track != null) 
        {
            trackTitleLabel.setText(actual_track.getTitle());
            trackAuthorLabel.setText(actual_track.getAuthor());
        } 
        else 
        {
            trackTitleLabel.setText("Nessuna traccia selezionata");
            trackAuthorLabel.setText("");
        }
        if (status.equals("Playing")) 
        {
            playPauseButton.setText("Pausa");          
        } 
        else 
        {
            playPauseButton.setText("Play");
        }
    }

    @FXML
    private void handlePlayPause() {
        if (playbackState.getStatusName().equals("Playing")) 
        {
            playbackState.pause();
        } 
        else 
        {
            if (playbackState.getCurrentTrack() != null) 
            {
                playbackController.resume();
            }
        }

    }

    @FXML
    private void handleBack() 
    {
        playbackState.removeObserver(this);  
        try 
        {
            App.setRoot("TrackLibraryView");  
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}