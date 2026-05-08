package com.mycompany.devisbatiments;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        Button bouton = new Button("Clique!");
        bouton.setOnAction(e -> {
        System.out.println("Bouton cliqué !");
        });
        StackPane root = new StackPane();
        root.getChildren().add(bouton);
        Scene scene = new Scene(root, 300, 200);

        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch();
    }

}