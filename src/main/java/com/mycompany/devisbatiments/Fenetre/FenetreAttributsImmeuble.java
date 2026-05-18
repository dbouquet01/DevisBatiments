/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Immeuble;
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

        TextField fieldId          = new TextField();
        TextField fieldDesignation = new TextField();
        TextField fieldLargeur     = new TextField();
        TextField fieldLongueur    = new TextField();
        TextField fieldEtage       = new TextField();

        ajouterLigne(grid, "ID :",              fieldId,          0, styleLabel);
        ajouterLigne(grid, "Désignation :",     fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Largeur (m) :",     fieldLargeur,     2, styleLabel);
        ajouterLigne(grid, "Longueur (m) :",    fieldLongueur,    3, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage,       4, styleLabel);

        // --- LABEL D'ERREUR ---
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
            String id          = fieldId.getText().trim();
            String designation = fieldDesignation.getText().trim();
            String txtLargeur  = fieldLargeur.getText().trim();
            String txtLongueur = fieldLongueur.getText().trim();
            String txtEtage    = fieldEtage.getText().trim();

            if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                    || txtLongueur.isEmpty() || txtEtage.isEmpty()) {
                lblErreur.setText("Veuillez remplir tous les champs.");
                return;
            }

            try {
                double largeur  = Double.parseDouble(txtLargeur);
                double longueur = Double.parseDouble(txtLongueur);
                int    nbEtages = Integer.parseInt(txtEtage);

                Immeuble immeuble = new Immeuble(id, largeur, longueur, nbEtages);

                // On passe juste l'immeuble, plus de nbApp ici
                new FenetreEtage(immeuble).afficher(stage);

            } catch (NumberFormatException ex) {
                lblErreur.setText("Les valeurs numériques sont invalides.");
            }
        });

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(30));
        bottomBox.setSpacing(400);
        bottomBox.getChildren().addAll(btnRetour, btnSuivant);
        bottomBox.setAlignment(Pos.CENTER);

        VBox centre = new VBox(10, grid, lblErreur);
        centre.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Attributs Immeuble");
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
