package it.unisa.diem.sad_gruppo6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import it.unisa.diem.sad_gruppo6.controllers.HomeController;
import it.unisa.diem.sad_gruppo6.controllers.PlaylistController;
import it.unisa.diem.sad_gruppo6.models.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.models.TrackLibrary;
import it.unisa.diem.sad_gruppo6.commands.CommandManager;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
           
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/Home.fxml"));
            Parent root = fxmlLoader.load();
            HomeController homeController = fxmlLoader.getController();

            PlaylistLibrary playlistLibrary = PlaylistLibrary.getInstance();
            TrackLibrary trackLibrary = TrackLibrary.getInstance();
            CommandManager commandManager = new CommandManager();

            PlaylistController playlistController;
            playlistController = new PlaylistController(trackLibrary, playlistLibrary, commandManager);
            
            homeController.init(playlistLibrary, playlistController);

            Scene scene = new Scene(root);
            
            stage.setTitle("VibeFlow - Test Crea Playlist");
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