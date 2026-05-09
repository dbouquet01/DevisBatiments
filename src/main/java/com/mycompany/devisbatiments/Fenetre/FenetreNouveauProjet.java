/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author delph
 */
public class FenetreNouveauProjet {
    
    public void afficher(Stage stage) {

        // Titre
        Label titre = new Label("TYPE DE PROJET");
        titre.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;");

        VBox Titre = new VBox(titre);
        Titre.setAlignment(Pos.CENTER);
        Titre.setPadding(new Insets(30));

        // Bouton nouveau projet
        Button btnimmeuble = new Button("IMMEUBLE");

        btnimmeuble.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        btnimmeuble.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-alignment: center;-fx-cursor: hand;");
        
        // Bouton historique
        Button btnmaison = new Button("MAISON");

        btnmaison.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        btnmaison.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-alignment: center;-fx-cursor: hand;");
        Button retour = new Button("Retour");

        retour.setStyle(
            "-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;"
        );

        // Action du bouton retour
        retour.setOnAction(e -> {
            FenetreAccueil accueil = new FenetreAccueil();
            accueil.afficher(stage);
        });

        // Placement du bouton retour
        HBox Retour = new HBox(retour);
        Retour.setPadding(new Insets(15));
        Retour.setAlignment(Pos.BOTTOM_LEFT);
        
        // Conteneur des boutons
        HBox Type = new HBox(20, btnimmeuble, btnmaison);
        Type.setPadding(new Insets(30));

        HBox.setHgrow(btnimmeuble, Priority.ALWAYS);
        HBox.setHgrow(btnmaison, Priority.ALWAYS);

        // Layout principal
        BorderPane root = new BorderPane();

        root.setTop(Titre);
        root.setCenter(Type);
        root.setBottom(Retour);
        

        Scene scene = new Scene(root, 1000, 600);

        stage.setTitle("Accueil");
        stage.setScene(scene);
        
        stage.show();
    }
}