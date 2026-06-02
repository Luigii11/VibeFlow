package it.unisa.diem.sad_gruppo6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Carica la schermata corretta trovata nel progetto
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/TrackLibraryView.fxml"));
            Parent root = fxmlLoader.load();
            
            Scene scene = new Scene(root);
            
            stage.setTitle("VibeFlow - Test Crea Traccia");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Errore nel caricamento del file FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}