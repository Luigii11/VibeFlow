package it.unisa.diem.sad_gruppo6;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Schermata provvisoria per non far crashare l'app all'avvio
        Label placeholder = new Label("VibeFlow - In attesa della HomeView...");
        StackPane root = new StackPane(placeholder);
        
        Scene scene = new Scene(root, 640, 480);
        
        stage.setTitle("VibeFlow");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}