package ru.petrenko_alex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader( getClass().getResource("/ru/petrenko_alex/sample.fxml") );
        Parent root = loader.load();

        Controller controller = (Controller) loader.getController();
        controller.init(primaryStage);

        primaryStage.setTitle("Steganography");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
