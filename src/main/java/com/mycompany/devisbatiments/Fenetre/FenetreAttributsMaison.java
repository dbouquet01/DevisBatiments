/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Maison;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.mycompany.devisbatiments.donnees.SauvegardeProjet;

public class FenetreAttributsMaison {

    public void afficher(Stage stage) {
        // --- TITRE ---
        Label titre = new Label("ATTRIBUTS DE LA MAISON");
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

        TextField fieldId          = new TextField();
        TextField fieldDesignation = new TextField();
        TextField fieldLargeur     = new TextField();  // séparé en 2 champs
        TextField fieldLongueur    = new TextField();  // pour passer à Maison(id, larg, long, nb)
        TextField fieldEtage       = new TextField();

        ajouterLigne(grid, "ID :",             fieldId,          0, styleLabel);
        ajouterLigne(grid, "Désignation :",    fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Largeur (m) :",    fieldLargeur,     2, styleLabel);
        ajouterLigne(grid, "Longueur (m) :",   fieldLongueur,    3, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage,      4, styleLabel);

        // --- LABEL D'ERREUR (caché par défaut) ---
        // On l'affiche si l'utilisateur laisse un champ vide ou met une valeur invalide
        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // --- BOUTONS ---
        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                           + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreNouveauProjet().afficher(stage));

        Button btnSuivant = new Button("ÉTAPE SUIVANTE →");
        btnSuivant.setStyle(styleBouton);

        btnSuivant.setOnAction(e -> {
            // 1. On récupère les valeurs saisies
            String id          = fieldId.getText().trim();
            String designation = fieldDesignation.getText().trim();
            String txtLargeur  = fieldLargeur.getText().trim();
            String txtLongueur = fieldLongueur.getText().trim();
            String txtEtage    = fieldEtage.getText().trim();

            // 2. Validation : aucun champ ne doit être vide
            if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                    || txtLongueur.isEmpty() || txtEtage.isEmpty()) {
                lblErreur.setText("Veuillez remplir tous les champs.");
                return;
            }

            // 3. Conversion des valeurs numériques avec gestion d'erreur
            try {
                double largeur  = Double.parseDouble(txtLargeur);
                double longueur = Double.parseDouble(txtLongueur);
                int    nbEtages = Integer.parseInt(txtEtage);

                // 4. Création de l'objet Maison avec les vraies données
               Maison maison = new Maison(id, largeur, longueur, nbEtages);

                String idDevis = "D_" + id;
                double hauteurTotale = nbEtages * 2.5;
                double surfaceTotale = largeur * longueur * nbEtages;

                SauvegardeProjet.sauvegarderProjet(
                    id,
                    designation,
                    "MAISON",
                    nbEtages,
                    hauteurTotale,
                    surfaceTotale,
                    0,
                    idDevis
                );

                new FenetreEtage(maison).afficher(stage);
            } catch (NumberFormatException ex) {
                // Si l'utilisateur a mis du texte dans un champ numérique
                lblErreur.setText("Largeur, longueur et étages doivent être des nombres.");
            }
        });

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(30));
        bottomBox.setSpacing(400);
        bottomBox.getChildren().addAll(btnRetour, btnSuivant);
        bottomBox.setAlignment(Pos.CENTER);

        // --- MISE EN PAGE FINALE ---
        VBox centre = new VBox(10, grid, lblErreur);
        centre.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Attributs Maison");
        stage.setScene(scene);
        stage.show();
    }

    private void ajouterLigne(GridPane grid, String texte, TextField field, int ligne, String style) {
        Label lbl = new Label(texte);
        lbl.setStyle(style);
        grid.add(lbl, 0, ligne);
        grid.add(field, 1, ligne);
    }
}

