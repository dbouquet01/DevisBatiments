/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
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

        Label titre = new Label("BLOCS / SALLES — " + nomEtage);
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        double surfaceCouloir = calculerSurfaceCouloir();
        double surfaceHabitable = calculerSurfaceHabitable();
        double surfaceParAppart = nbApparts > 0 ? surfaceHabitable / nbApparts : 0;

        Label lblSurfaceInfo = new Label(
                "Surface étage : " + String.format("%.2f", surfaceEtage)
                        + " m² — Couloir théorique : " + String.format("%.2f", surfaceCouloir)
                        + " m² — Surface restante théorique : " + String.format("%.2f", surfaceHabitable)
                        + " m² — Surface/appartement théorique : " + String.format("%.2f", surfaceParAppart) + " m²"
        );
        lblSurfaceInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        VBox listeBlocs = new VBox(10);
        listeBlocs.setAlignment(Pos.TOP_CENTER);
        listeBlocs.setPadding(new Insets(20));

        ArrayList<String> blocsEtage = SauvegardeProjet.chargerNomsElementsPlan(batiment.getId(), nomEtage);

        if (blocsEtage.isEmpty()) {
            Label vide = new Label("Aucun bloc enregistré sur cet étage.");
            vide.setStyle("-fx-font-size: 15px; -fx-text-fill: grey;");
            listeBlocs.getChildren().add(vide);
        } else {
            for (String nomBloc : blocsEtage) {
                ajouterLigneBloc(stage, listeBlocs, blocsEtage, nomBloc, styleBouton);
            }
        }

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        Button btnMenu = new Button("MENU PRINCIPAL");
        btnMenu.setStyle(styleBouton);
        btnMenu.setOnAction(e -> new FenetreProjet().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnMenu);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.setStyle("-fx-background-color: #F5F5F5;");

        VBox centre = new VBox(20, lblSurfaceInfo, listeBlocs);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(centre);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        stage.setTitle("Blocs / Salles — " + nomEtage);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void ajouterLigneBloc(Stage stage, VBox listeBlocs, ArrayList<String> blocsEtage,
                                  String nomBloc, String styleBouton) {
        Label lblBloc = new Label(nomBloc + calculerSurfaceBlocTexte(nomBloc));
        lblBloc.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        lblBloc.setMinWidth(330);

        Button btnModifier = new Button("Modifier →");
        btnModifier.setStyle(styleBouton);
        btnModifier.setOnAction(e -> new FenetrePiece(
                batiment,
                nomEtage,
                nomBloc,
                surfaceEtage,
                blocsEtage
        ).afficher(stage));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #B00020; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> {
            SauvegardeProjet.supprimerPiece(batiment.getId(), nomEtage, nomBloc);
            afficher(stage);
        });

        HBox ligne = new HBox(20, lblBloc, btnModifier, btnSupprimer);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(950);
        ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10 20;");

        listeBlocs.getChildren().add(ligne);
    }

    private String calculerSurfaceBlocTexte(String nomBloc) {
        String[] element = SauvegardeProjet.chargerElementPlan(batiment.getId(), nomEtage, nomBloc);

        if (element == null || element.length < 7) {
            return "";
        }

        try {
            double largeur = Double.parseDouble(element[5].trim().replace(",", "."));
            double longueur = Double.parseDouble(element[6].trim().replace(",", "."));
            return " — " + String.format("%.2f", largeur * longueur) + " m²";
        } catch (Exception e) {
            return "";
        }
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
