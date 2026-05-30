/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;


public class FenetreAccueil {
    
   
        public void afficher(Stage stage) {

       
        Label titre = new Label("Bienvenue");
        titre.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;");

        VBox haut = new VBox(titre);
        haut.setAlignment(Pos.CENTER);
        haut.setPadding(new Insets(30));

        
        Button btnnewproject = new Button("CREER UN NOUVEAU PROJET");

        btnnewproject.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        btnnewproject.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-alignment: center;-fx-cursor: hand;");
        
        
        Button historique = new Button("VOIR LES PROJETS EXISTANTS");

        historique.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        historique.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-alignment: center;-fx-cursor: hand;");

        
        btnnewproject.setOnAction(e -> {
            FenetreNouveauProjet accueil = new FenetreNouveauProjet();
            accueil.afficher(stage);
        });

                
        historique.setOnAction(e -> {
         FenetreProjet recherche = new FenetreProjet();
    
         recherche.afficher(stage);
         
    });

        
        
        
        HBox centre = new HBox(20, btnnewproject, historique);
        centre.setPadding(new Insets(30));

        HBox.setHgrow(btnnewproject, Priority.ALWAYS);
        HBox.setHgrow(historique, Priority.ALWAYS);

        
        BorderPane root = new BorderPane();

        root.setTop(haut);
        root.setCenter(centre);
        

        Scene scene = new Scene(root);
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

 
}
