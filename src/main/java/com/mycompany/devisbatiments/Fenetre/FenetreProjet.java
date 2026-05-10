/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 


public class FenetreProjet {

    public void afficher(Stage stage) {
        Label titre = new Label("RECHERCHER UN PROJET");
        titre.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;");
        VBox topContainer = new VBox(titre);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(30));

        
        
        
        
        
        
        TextField barreRecherche = new TextField();
        barreRecherche.setPromptText("Entrez le nom du projet...");
        barreRecherche.setPrefWidth(300);

        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setStyle("-fx-background-color: #0F056B; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox searchBar = new HBox(10, barreRecherche, btnRechercher);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(20));

        
        
        
        
        VBox zoneResultats = new VBox(15);
        zoneResultats.setAlignment(Pos.TOP_LEFT);
        zoneResultats.setPadding(new Insets(20));
        zoneResultats.setStyle("-fx-border-color: #0F056B; -fx-border-width: 2; -fx-background-color: #F4F4F4;");
        zoneResultats.setVisible(false); // On la cache tant qu'on n'a pas cherché

        Label lblInfos = new Label("Caractéristiques du bâtiment :");
        lblInfos.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Text detailsText = new Text(); // Pour afficher les infos (ID, largeur, longueur, etc.)
        zoneResultats.getChildren().addAll(lblInfos, detailsText);

        
        
        
        
        
        btnRechercher.setOnAction(e -> {
            String nomCherche = barreRecherche.getText();
            // Ici, tu devras appeler ta base de données ou ta liste de projets
            // Pour l'exemple, j'affiche du texte brut :
            zoneResultats.setVisible(true);
        });
        
        
        
        
        
        

       
        Button retour = new Button("Retour");
        retour.setStyle("-fx-background-color: #0F056B; -fx-text-fill: white; -fx-cursor: hand;");
        retour.setOnAction(e -> new FenetreAccueil().afficher(stage));

        HBox bottomContainer = new HBox(retour);
        bottomContainer.setPadding(new Insets(15));

        // Layout Principal
        VBox layoutCentre = new VBox(20, searchBar, zoneResultats);
        layoutCentre.setPadding(new Insets(0, 50, 0, 50));

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(layoutCentre);
        root.setBottom(bottomContainer);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Recherche de Projet");
        stage.setScene(scene);
        stage.show();
    }
}