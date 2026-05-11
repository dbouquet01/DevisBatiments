/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author delph
 */
 import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetreAttributsImmeuble {

    public void afficher(Stage stage) {
        // --- TITRE ---
        Label titre = new Label("ATTRIBUTS DE L'IMMEUBLE");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        
        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        // --- FORMULAIRE ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20); 
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        String styleLabel = "-fx-font-size: 16px; -fx-font-weight: bold;";

        // Champs de saisie
        TextField fieldId = new TextField();
        TextField fieldDesignation = new TextField();
        TextField fieldEmpriseAuSol = new TextField(); 
        TextField fieldEtage = new TextField();
        TextField fieldnbApp = new TextField();
        

        // Ajout au Grid
        ajouterLigne(grid, "ID :", fieldId, 0, styleLabel);
        ajouterLigne(grid, "Désignation :", fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Emprise au sol : Long*Larg", fieldEmpriseAuSol, 2, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage, 3, styleLabel);
        ajouterLigne(grid, "Nombre d'appartements par étage : ", fieldnbApp, 4, styleLabel);

        // --- BOUTONS ---
        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> {
            // On retourne à la sélection du type de projet
            new FenetreNouveauProjet().afficher(stage); 
        });

        Button btnAfficher = new Button("VISUALISER");
        btnAfficher.setStyle(styleBouton);
        btnAfficher.setOnAction(e -> {
            System.out.println("Génération du plan pour : " + fieldDesignation.getText());
        });

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(30));
        bottomBox.setSpacing(400); // Ajusté pour éviter de sortir de l'écran
        bottomBox.getChildren().addAll(btnRetour, btnAfficher);
        bottomBox.setAlignment(Pos.CENTER);

        // --- MISE EN PAGE FINALE ---
        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(grid);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Attributs Immeuble");
        stage.setScene(scene);
    }

    // Petite méthode utilitaire pour éviter la répétition
    private void ajouterLigne(GridPane grid, String texte, TextField field, int ligne, String style) {
        Label lbl = new Label(texte);
        lbl.setStyle(style);
        grid.add(lbl, 0, ligne);
        grid.add(field, 1, ligne);
    }
}
