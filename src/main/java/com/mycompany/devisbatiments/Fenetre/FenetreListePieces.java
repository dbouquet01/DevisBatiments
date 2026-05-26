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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class FenetreListePieces {

    private final Batiments batiment;
    private final String nomEtage;
    private final int numAppart;
    private final double surfaceAppart;
    private final int nbApparts;
    private final HashMap<String, Integer> nbAppartsParEtage;
    private final ArrayList<String> nomsPieces = new ArrayList<>();

    public FenetreListePieces(Batiments batiment, String nomEtage, int numAppart,
                              double surfaceAppart, int nbApparts,
                              HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.numAppart = numAppart;
        this.surfaceAppart = surfaceAppart;
        this.nbApparts = nbApparts;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public FenetreListePieces(Batiments batiment, String nomEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.numAppart = 0;
        this.surfaceAppart = batiment.getLargeur() * batiment.getLongueur();
        this.nbApparts = 0;
        this.nbAppartsParEtage = new HashMap<>();
    }

    public FenetreListePieces(Batiments batiment, String nomEtage, ArrayList<String> nomsPieces) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.numAppart = 0;
        this.surfaceAppart = batiment.getLargeur() * batiment.getLongueur();
        this.nbApparts = 0;
        this.nbAppartsParEtage = new HashMap<>();
        this.nomsPieces.addAll(nomsPieces);
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label(
                numAppart > 0
                        ? "PIÈCES — " + nomEtage + " | Appartement " + numAppart
                        : "PIÈCES — " + nomEtage + " | " + batiment.getId()
        );

        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        Label lblSurface = new Label("Surface : " + String.format("%.2f", surfaceAppart) + " m²");
        lblSurface.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        TextField fieldNomPiece = new TextField();
        fieldNomPiece.setPromptText("Nom de la pièce");
        fieldNomPiece.setMaxWidth(300);

        Button btnAjouter = new Button("+ AJOUTER UNE PIÈCE");
        btnAjouter.setStyle(styleBouton);

        HBox ligneAjout = new HBox(15, fieldNomPiece, btnAjouter);
        ligneAjout.setAlignment(Pos.CENTER);

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        VBox listePieces = new VBox(8);
        listePieces.setAlignment(Pos.CENTER);
        listePieces.setPadding(new Insets(10));

        for (String nomExistant : nomsPieces) {
            ajouterLignePiece(stage, listePieces, nomExistant, styleBouton);
        }

        btnAjouter.setOnAction(e -> {
            String nomPiece = fieldNomPiece.getText().trim();

            if (nomPiece.isEmpty()) {
                lblErreur.setText("Veuillez donner un nom à la pièce.");
                return;
            }

            lblErreur.setText("");
            nomsPieces.add(nomPiece);
            fieldNomPiece.clear();

            ajouterLignePiece(stage, listePieces, nomPiece, styleBouton);
        });

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);

        btnRetour.setOnAction(e -> {
            if (batiment instanceof Maison) {
                new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage);
            } else {
                new FenetreAppartement(
                        batiment,
                        nomEtage,
                        batiment.getLargeur() * batiment.getLongueur(),
                        nbApparts,
                        nbAppartsParEtage
                ).afficher(stage);
            }
        });

        Button btnDevis = new Button("VOIR LE DEVIS");
        btnDevis.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");

        btnDevis.setOnAction(e -> new FenetreRecapitulatif(batiment, nomEtage).afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnDevis);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox blocGauche = new VBox(15, lblSurface, ligneAjout, lblErreur, listePieces);
        blocGauche.setAlignment(Pos.TOP_CENTER);
        blocGauche.setPadding(new Insets(20));

        PlanDessin dessin;

        if (batiment instanceof Maison) {
            dessin = new PlanDessin(batiment, nomEtage);
        } else {
            double largeurAppart = batiment.getLargeur() / nbApparts;
            double longueurAppart = batiment.getLongueur();

            dessin = new PlanDessin(
                    batiment,
                    getVuePlanAppartement(),
                    largeurAppart,
                    longueurAppart
            );
        }

        HBox centre = new HBox(25, blocGauche, dessin);
        centre.setAlignment(Pos.CENTER);
        centre.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Liste Pièces - " + nomEtage);
        stage.setScene(scene);
        stage.show();
    }

    private void ajouterLignePiece(Stage stage, VBox listePieces, String nomPiece, String styleBouton) {

        Label lblNumero = new Label("Pièce " + nomsPieces.size());
        lblNumero.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");
        lblNumero.setMinWidth(80);

        Label lblNom = new Label(nomPiece);
        lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        lblNom.setMinWidth(250);

        Button btnEntrer = new Button("Entrer →");
        btnEntrer.setStyle(styleBouton);

        btnEntrer.setOnAction(e -> {
            String vuePlan = (batiment instanceof Maison)
                    ? nomEtage
                    : getVuePlanAppartement();

            new FenetrePiece(
                    batiment,
                    vuePlan,
                    nomPiece,
                    surfaceAppart,
                    nomsPieces
            ).afficher(stage);
        });

        HBox ligne = new HBox(20, lblNumero, lblNom, btnEntrer);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10 20;");

        listePieces.getChildren().add(ligne);
    }

    private String getVuePlanAppartement() {
        return nomEtage.replace(" ", "") + "_APPART" + numAppart;
    }
}