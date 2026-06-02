/**
 * @file TrackLibraryViewController.java
 * 
 * Controller della vista 'TrackLibraryView.fxml'
 * Gestisce esclusivamente la UI.
 * 
 * @author EmanuelaGraziuso
 */

package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.commands.RemoveTrackFromLibraryCommand;
import it.unisa.diem.sad_gruppo6.models.*;
import it.unisa.diem.sad_gruppo6.App;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackLibraryViewController implements Initializable, TrackLibraryObserver {

    private static final double TITLE = 200;
    private static final double GENRE = 160;
    private static final double AUTHOR = 160;
    private static final double DURATION = 80;

    private TrackLibrary library;
    private PlaybackController playbackController = new PlaybackController();

    @FXML private ListView<Track> trackListView;
    @FXML private Label emptyLabel;
    @FXML private Button addTrackButton;

    /**
     * Costruttore: ottiene il singleton della libreria.
     */
    public TrackLibraryViewController() {
        this.library = TrackLibrary.getInstance();
    }

    /**
     * Inizializzazione JavaFX: registra la vista come osservatore della libreria e
     * configura la ListView.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        library.registerObserver(this);

        if (trackListView != null) {
            trackListView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
                javafx.scene.control.ScrollBar hBar =
                    (javafx.scene.control.ScrollBar) trackListView.lookup(".scroll-bar:horizontal");
                if (hBar != null) {
                    hBar.setVisible(false);
                    hBar.setManaged(false);
                }
            });

            trackListView.setCellFactory(lv -> new ListCell<Track>() {
                private final Label lblTitle = makeCellLabel(TITLE);
                private final Label lblGenre = makeCellLabel(GENRE);
                private final Label lblAuthor = makeCellLabel(AUTHOR);
                private final Label lblDuration = makeCellLabel(DURATION);
                private final Button btnEdit = new Button("✏️");
                private final Button btnDelete = new Button("🗑");
                private final HBox content = new HBox(8, lblTitle, lblGenre, lblAuthor, lblDuration, btnEdit, btnDelete);
                {
                    setStyle("-fx-padding: 6 16 6 16;");
                    setText(null);
                    HBox.setHgrow(lblTitle, javafx.scene.layout.Priority.NEVER);

                    btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4;");
                    btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4;");

                    btnEdit.setOnMouseEntered(e -> btnEdit.setStyle("-fx-background-color: #e0e0e0; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4; -fx-background-radius: 4;"));
                    btnEdit.setOnMouseExited(e -> btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4;"));

                    btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #ffebee; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4; -fx-background-radius: 4;"));
                    btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 4 2 4;"));

                    btnEdit.setOnAction(event -> {
                        Track track = getItem();
                        if (track != null) {
                            handleEditButtonClick(track);
                        }
                    });

                    btnDelete.setOnAction(event -> {
                        Track track = getItem();
                        if (track != null) {
                            handleDeleteButtonClick(track);
                        }
                    });
                }

                @Override
                protected void updateItem(Track track, boolean empty) {
                    super.updateItem(track, empty);
                    if (empty || track == null) {
                        setGraphic(null);
                    } else {
                        int min = track.getDuration() / 60;
                        int sec = track.getDuration() % 60;
                        lblTitle.setText(track.getTitle());
                        lblGenre.setText(track.getGenre());
                        lblAuthor.setText(track.getAuthor());
                        lblDuration.setText(String.format("%d:%02d", min, sec));
                        setGraphic(content);
                    }
                }
            });

            onLibraryChanged();

            trackListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Track selected = trackListView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        try {
                            playbackController.play(selected);
                            App.setRoot("MediaPlayer");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * Gestione pulsante "+": naviga verso la vista di creazione traccia.
     */
    @FXML
    private void handleAddTrack(ActionEvent event) {
        try {
            App.setRoot("createTrack");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackAdded(Track track) {
        onLibraryChanged();
    }

    @Override
    public void onLibraryChanged() {
        if (trackListView == null) return;
        var tracks = library.getTracks();
        if (tracks.isEmpty()) {
            emptyLabel.setVisible(true);
            trackListView.setItems(FXCollections.emptyObservableList());
        } else {
            emptyLabel.setVisible(false);
            trackListView.setItems(FXCollections.observableArrayList(tracks));
        }
    }

    /**
     * Crea una Label con larghezza fissa per le celle della lista.
     * 
     * @param width La larghezza fissa della Label.
     */
    private Label makeCellLabel(double width) {
        Label label = new Label();
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setStyle("-fx-font-size: 13px;");
        return label;
    }

    /**
     * Apre la vista di modifica traccia pre-popolando il form con i metadati
     * della traccia selezionata. Naviga tramite App.setRootAndGetController per
     * ottenere il TrackController della vista di destinazione e configurarlo.
     *
     * @param track la traccia selezionata su cui è stato premuto il bottone modifica.
     */
    private void handleEditButtonClick(Track track) {
        try {
            TrackController controller = App.setRootAndGetController("editTrack");
            controller.setTrackToEdit(track);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento di editTrack.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il click sul pulsante cestino di una riga della lista.
     * Mostra un Alert di conferma prima di procedere con la rimozione,
     * in coerenza con il comportamento standard per le operazioni distruttive.
     *
     * @param track la traccia selezionata da rimuovere dalla libreria.
     */
    private void handleDeleteButtonClick(Track track) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Rimozione");
        alert.setHeaderText("Sei sicuro di voler rimuovere questa traccia?");
        alert.setContentText(String.format("%s - %s", track.getTitle(), track.getAuthor()));

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                new RemoveTrackFromLibraryCommand(track).execute();
            }
        });
    }
}