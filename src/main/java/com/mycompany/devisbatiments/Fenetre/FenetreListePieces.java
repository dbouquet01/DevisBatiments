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

public class FenetreListePieces {

    private final Batiments batiment;
    private final String nomEtage;
    private final int numAppart;
    private final double surfaceAppart;
    private final ArrayList<String> nomsPieces = new ArrayList<>();

    // Constructeur pour Immeuble (depuis FenetreAppartement)
    public FenetreListePieces(Batiments batiment, String nomEtage, int numAppart, double surfaceAppart) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.numAppart = numAppart;
        this.surfaceAppart = surfaceAppart;
    }

    // Constructeur pour Maison (depuis FenetreEtage)
    public FenetreListePieces(Batiments batiment, String nomEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.numAppart = 0;
        this.surfaceAppart = batiment.getLargeur() * batiment.getLongueur();
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        String styleLabel = "-fx-font-size: 14px; -fx-font-weight: bold;";

        // --- TITRE ---
        String titreTxt = (numAppart > 0)
                ? "PIÈCES — " + nomEtage + "  |  Appartement " + numAppart
                : "PIÈCES — " + nomEtage + "  |  " + batiment.getId();
        Label titre = new Label(titreTxt);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        // --- INFO SURFACE ---
        Label lblSurface = new Label(
                "Surface : " + String.format("%.2f", surfaceAppart) + " m²"
        );
        lblSurface.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        // --- CHAMP + BOUTON AJOUTER ---
        TextField fieldNomPiece = new TextField();
        fieldNomPiece.setPromptText("Nom de la pièce (ex: Salon, Chambre...)");
        fieldNomPiece.setMaxWidth(300);

        Button btnAjouter = new Button("+ AJOUTER UNE PIÈCE");
        btnAjouter.setStyle(styleBouton);

        HBox ligneAjout = new HBox(15, fieldNomPiece, btnAjouter);
        ligneAjout.setAlignment(Pos.CENTER);

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // --- LISTE DES PIÈCES ---
        VBox listePieces = new VBox(8);
        listePieces.setAlignment(Pos.CENTER);
        listePieces.setPadding(new Insets(10));

        // --- LOGIQUE AJOUT PIÈCE ---
        btnAjouter.setOnAction(e -> {
            String nomPiece = fieldNomPiece.getText().trim();
            if (nomPiece.isEmpty()) {
                lblErreur.setText("Veuillez donner un nom à la pièce.");
                return;
            }

            lblErreur.setText("");
            nomsPieces.add(nomPiece);
            fieldNomPiece.clear();

            Label lblNumero = new Label("Pièce " + nomsPieces.size());
            lblNumero.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");
            lblNumero.setMinWidth(80);

            Label lblNom = new Label(nomPiece);
            lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            lblNom.setMinWidth(250);

            Button btnEntrer = new Button("Entrer →");
            btnEntrer.setStyle(styleBouton);

            final String nomCapture = nomPiece;

            btnEntrer.setOnAction(ev -> {
                new FenetrePiece(batiment, nomEtage, nomCapture, surfaceAppart).afficher(stage);
            });

            HBox ligne = new HBox(20, lblNumero, lblNom, btnEntrer);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                    + "-fx-border-width: 1; -fx-padding: 10 20;");
            listePieces.getChildren().add(ligne);
        });

        // --- BOUTON RETOUR ---
        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> {
            if (batiment instanceof Maison) {
                new FenetreEtage(batiment).afficher(stage);
            } else {
                new FenetreAppartement(batiment, nomEtage, batiment.getLargeur() * batiment.getLongueur()).afficher(stage);
            }
        });

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        // --- MISE EN PAGE ---
        VBox centre = new VBox(15, lblSurface, ligneAjout, lblErreur, listePieces);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Liste Pièces - " + nomEtage);
        stage.setScene(scene);
        stage.show();
    }
}
