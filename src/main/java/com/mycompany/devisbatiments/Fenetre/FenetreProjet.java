/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        barreRecherche.setPromptText("Entrez le id du projet...");
        barreRecherche.setPrefWidth(300);

        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setStyle(" -fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");

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
           String nomCherche = barreRecherche.getText().trim();
           try {
               BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"));
               String ligne;
               boolean trouve = false;

                // Ignore la première ligne
                reader.readLine();

                while ((ligne = reader.readLine()) != null) {

                    // Ignore les lignes vides
                    if (ligne.trim().isEmpty()) {
                        continue;
                    }

                    String[] parties = ligne.split(";");

                    // Vérifie qu'il y a bien 8 colonnes
                    if (parties.length < 8) {
                        continue;
                    }

                    String id = parties[0];
                    String designation = parties[1];
                    String type = parties[2];
                    String etages = parties[3];
                    String hauteur = parties[4];
                    String surface = parties[5];
                    String appartements = parties[6];
                    String devis = parties[7];

                    // Recherche
                    if (id.equalsIgnoreCase(nomCherche)) {

                        detailsText.setText(
                        "ID Projet : " + id + "\n" +
                        "Nom : " + designation + "\n" +
                        "Type : " + type + "\n" +
                        "Nombre d'étages : " + etages + "\n" +
                        "Hauteur totale : " + hauteur + " m\n" +
                        "Surface totale : " + surface + " m²\n" +
                        "Nombre d'appartements : " + appartements + "\n" +
                        "ID Devis : " + devis
                        );

                        zoneResultats.setVisible(true);
                        trouve = true;
                        break;
                    }
                }

                reader.close();

                if (!trouve) {
                    detailsText.setText("Projet introuvable.");
                    zoneResultats.setVisible(true);
                }

            } catch (IOException ex) {

                detailsText.setText("Erreur : fichier introuvable.");
                zoneResultats.setVisible(true);

                ex.printStackTrace();
            }
        });
        
        
        
        
        

       
        Button retour = new Button("Retour");
        retour.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");
        retour.setOnAction(e -> new FenetreAccueil().afficher(stage));

        
        ComboBox<String> modificationBox = new ComboBox<>();

        modificationBox.getItems().addAll(
        "Étape 1 : Fondation",
        "Étape 2 : Structure",
        "Étape 3 : Finitions"
        );

        modificationBox.setPromptText("Modification");

        modificationBox.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");

        HBox bottomContainer = new HBox();

        bottomContainer.setPadding(new Insets(15));
        bottomContainer.setSpacing(20);

    // pousse les éléments aux extrémités
        bottomContainer.setAlignment(Pos.CENTER_LEFT);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        bottomContainer.getChildren().addAll(
        retour,
        espace,
        modificationBox
    );
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
