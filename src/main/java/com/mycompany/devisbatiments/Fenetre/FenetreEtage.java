/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetreEtage {

    private final Batiments batiment;

    public FenetreEtage(Batiments batiment) {
        this.batiment = batiment;
    }

    public void afficher(Stage stage) {

        // --- TITRE ---
        Label titre = new Label("ÉTAGES DU BÂTIMENT : " + batiment.getId());
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        // --- LISTE DES ÉTAGES ---
        VBox listeEtages = new VBox(15);
        listeEtages.setAlignment(Pos.CENTER);
        listeEtages.setPadding(new Insets(20));

        String styleLigneEtage = "-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                               + "-fx-border-width: 1; -fx-padding: 10;";
        String styleBoutonEntrer = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                                 + "-fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 20;";

        // Surface de l'étage = largeur × longueur du bâtiment
        double surfaceEtage = batiment.getLargeur() * batiment.getLongueur();

        for (int i = 0; i <= batiment.getNbEtage(); i++) {

            String nomEtage = (i == 0) ? "Rez-de-chaussée" : "Étage " + i;

            Label lblEtage = new Label(nomEtage);
            lblEtage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblEtage.setMinWidth(300);

            Button btnEntrer = new Button("Entrer →");
            btnEntrer.setStyle(styleBoutonEntrer);

            final String nomEtageCapture = nomEtage;

            btnEntrer.setOnAction(e -> {
                if (batiment instanceof Maison) {
                    // Pour la maison on garde l'ancien flux
                    new FenetreListePieces(batiment, nomEtageCapture).afficher(stage);
                } else {
                    // Pour l'immeuble → FenetreAppartement avec la surface de l'étage
                    new FenetreAppartement(batiment, nomEtageCapture, surfaceEtage).afficher(stage);
                }
            });

            HBox ligne = new HBox(20, lblEtage, btnEntrer);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle(styleLigneEtage);
            ligne.setPadding(new Insets(10, 20, 10, 20));

            listeEtages.getChildren().add(ligne);
        }

        // --- BOUTON RETOUR ---
        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                           + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);

        btnRetour.setOnAction(e -> {
            if (batiment instanceof Maison) {
                new FenetreAttributsMaison().afficher(stage);
            } else {
                new FenetreAttributsImmeuble().afficher(stage);
            }
        });

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(listeEtages);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Étages");
        stage.setScene(scene);
        stage.show();
    }
}
