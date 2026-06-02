/**
 * @file PlaylistDetailsController.java
 * Controller della vista dei dettagli di una playlist.
 * Mostra i brani contenuti in una specifica playlist e permette all'utente di:
 * - Aggiungere una traccia alla playlist selezionandola dalla libreria globale.
 * - Rimuovere una traccia dalla playlist.
 * 
 * Si aggiorna automaticamente tramite il pattern Observer sulla PlaylistLibrary.
 * 
 * @pattern Observer
 * 
 * @see PlaylistController
 * @see PlaylistLibraryObserver
 * 
 * @author EmanuelaGraziuso
 */



package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.Playlist;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.models.Track;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Optional;

public class PlaylistDetailsController implements PlaylistLibraryObserver{
    
    @FXML
    private Label playlistNameLabel;

    @FXML
    private Label trackCountLabel;

    @FXML
    private ListView<Track> playlistTrackListView;

    @FXML
    private ListView<Track> allTracksListView;

    private Playlist currentPlaylist;
    private PlaylistController playlistController;
    private PlaylistLibrary playlistLibrary;
    private TrackLibrary trackLibrary;
    
    /**
     * Inizializza il controller con le dipendenze necessarie e si registra come observer.
     * 
     * @param playlist           La playlist corrente da visualizzare e gestire.
     * @param playlistController Il controller di dominio per le operazioni sulle playlist.
     * @param trackLibrary       La libreria globale delle tracce (per la lista di selezione).
     * @param playlistLibrary    La libreria delle playlist (per registrarsi come observer).
     */
    public void init(Playlist playlist, PlaylistController playlistController,
                     TrackLibrary trackLibrary, PlaylistLibrary playlistLibrary) {
        this.currentPlaylist = playlist;
        this.playlistController = playlistController;
        this.trackLibrary = trackLibrary;
        this.playlistLibrary = playlistLibrary;
        this.playlistLibrary.registerObserver(this);
        
        refresh();
    }

    /**
     * Aggiorna i dati visualizzati nella lista.
     */

    @Override
    public void onPlaylistLibraryChanged() {
        refresh();
    }

    /**
     * Aggiorna la vista con i dati aggiornati della playlist corrente: nome, numero di tracce e lista dei brani.
     */
    private void refresh() {
        if(currentPlaylist != null){
        playlistNameLabel.setText(currentPlaylist.getName());
        trackCountLabel.setText("Tracce:" + currentPlaylist.getTracks().size());
        playlistTrackListView.getItems().setAll(currentPlaylist.getTracks());
    }

    }

    /**
     * Gestisce la pressione del pulsante "+" (aggiungi traccia).
     * Prende la traccia selezionata dalla lista globale e la aggiunge alla playlist.
     * Mostra un alert se la traccia è già presente nella playlist.
     * 
     * @see PlaylistController#addTrackToPlaylist(Track, Playlist)
     */

    @FXML
    private void handleAddTrack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/views/TrackLibraryView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
                
            } catch (IOException e) {
                System.err.println("Errore nel caricamento di TrackLibraryView.fxml: " + e.getMessage());
                e.printStackTrace();
            }
        
    }

    /**
     * Mostra un popup di Alert con tipo, titolo e messaggio specificati.
     *
     * @param type    Il tipo di Alert (ERROR, WARNING, ecc.).
     * @param title   Il titolo del popup.
     * @param message Il messaggio da mostrare nel corpo del popup.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.showAndWait();
    }
}
