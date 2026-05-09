package com.mycompany.devisbatiments;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mycompany.devisbatiments.Fenetre.FenetreAccueil;



/**
 * JavaFX App
 */
public class App extends Application {

    public static String currentUserRole = "";  // "Administrateur" ou "Opérateur"

    @Override
    public void start(Stage primaryStage) {
    Stage loginStage = new Stage();
    VBox loginLayout = new VBox(10);
    loginLayout.setPadding(new Insets(20));
    loginLayout.setAlignment(Pos.CENTER);
    
    Label titre = new Label("DEVIS BATIMENT");
    titre.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");

    Button btnCo = new Button("Connexion");
    
    btnCo.setStyle("-fx-font-size: 14px;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;-fx-cursor: hand;");
    

    btnCo.setOnAction(e -> {
    FenetreAccueil accueil = new FenetreAccueil();
    accueil.afficher(primaryStage);
    });
    
        // Bouton historique
    loginLayout.getChildren().addAll(titre, btnCo );
    Scene loginScene = new Scene(loginLayout, 300, 250);
    loginStage.setScene(loginScene);
    loginStage.setTitle("Connexion");
    loginStage.show();
    }
}

