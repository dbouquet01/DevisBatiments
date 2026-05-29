/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;

public class FenetreAppartement {

    private static final double LARGEUR_COULOIR_METRES = 1.50;

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;
    private final int nbApparts;
    private final HashMap<String, Integer> nbAppartsParEtage;

    public FenetreAppartement(Batiments batiment, String nomEtage, double surfaceEtage,
                              int nbApparts, HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
        this.nbApparts = nbApparts;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("APPARTEMENTS — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        if (nbApparts <= 0) {
            afficherErreur(stage, topBox, styleBouton);
            return;
        }

        double surfaceCouloir = calculerSurfaceCouloir();
        double surfaceHabitable = calculerSurfaceHabitable();
        double surfaceParAppart = surfaceHabitable / nbApparts;
       

        Label lblSurfaceInfo = new Label(
                "Surface étage : " + String.format("%.2f", surfaceEtage)
                        + " m² — Couloir : " + String.format("%.2f", surfaceCouloir)
                        + " m² — Surface restante : " + String.format("%.2f", surfaceHabitable)
                        + " m² — Surface par appartement : "
                        + String.format("%.2f", surfaceParAppart) + " m²"
        );
        lblSurfaceInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        VBox listeApparts = new VBox(10);
        listeApparts.setAlignment(Pos.CENTER);
        listeApparts.setPadding(new Insets(20));

        for (int i = 1; i <= nbApparts; i++) {

            Label lblAppart = new Label("Appartement " + i
                    + " — " + String.format("%.2f", surfaceParAppart) + " m²");
            lblAppart.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            lblAppart.setMinWidth(250);

            Button btnEntrer = new Button("Entrer →");
            btnEntrer.setStyle(styleBouton);

            final int numAppart = i;

            btnEntrer.setOnAction(e -> {
                new FenetreListePieces(
                        batiment,
                        nomEtage,
                        numAppart,
                        surfaceParAppart,
                        nbApparts,
                        nbAppartsParEtage
                ).afficher(stage);
            });

            HBox ligne = new HBox(20, lblAppart, btnEntrer);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                    + "-fx-border-width: 1; -fx-padding: 10 20;");

            listeApparts.getChildren().add(ligne);
        }

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox centre = new VBox(20, lblSurfaceInfo, listeApparts);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Appartement — " + nomEtage);
        stage.setScene(scene);
        stage.show();
    }

    private void afficherErreur(Stage stage, VBox topBox, String styleBouton) {
        Label erreur = new Label("Aucun appartement n'est enregistré pour cet étage.");
        erreur.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        VBox centre = new VBox(20, erreur);
        centre.setAlignment(Pos.CENTER);

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Appartement — " + nomEtage);
        stage.setScene(scene);
        stage.show();
    }

    private double calculerSurfaceCouloir() {
        double largeurBatiment = batiment.getLargeur();
        double longueurBatiment = batiment.getLongueur();

        if (largeurBatiment <= 0 || longueurBatiment <= 0) {
            return 0;
        }

        double largeurCouloir = Math.min(LARGEUR_COULOIR_METRES, longueurBatiment);
        return largeurBatiment * largeurCouloir;
    }

    private double calculerSurfaceHabitable() {
        return Math.max(0, surfaceEtage - calculerSurfaceCouloir());
    }
    
}
