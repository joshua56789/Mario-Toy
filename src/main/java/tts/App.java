package tts;

import java.util.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class App extends Application {
    private static Stage stage;
    @Override
    public void start(Stage primaryStage) {
        loadFonts();
        loadScenes();
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/tts/UserInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("ECSE Toy");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
