package it.unisa.diem.sad_gruppo6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("TrackLibraryView"), 720, 560);
        stage.setTitle("VibeFlow");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static <T> T setRootAndGetController(String fxml) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
    Parent root = loader.load();
    scene.setRoot(root);              // usa la stessa scena (niente sgancio)
    return loader.getController();    // restituisce il controller per configurarlo
}

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}