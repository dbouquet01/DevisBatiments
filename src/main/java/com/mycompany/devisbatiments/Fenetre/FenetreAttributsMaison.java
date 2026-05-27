package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Maison;
import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetreAttributsMaison {

    public void afficher(Stage stage, String idExistant, String designationExistante,
                         double largeurExistante, double longueurExistante, int nbEtagesExistant) {

        Label titre = new Label("ATTRIBUTS DE LA MAISON");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        String styleLabel = "-fx-font-size: 16px; -fx-font-weight: bold;";

        TextField fieldId = new TextField();
        TextField fieldDesignation = new TextField();
        TextField fieldLargeur = new TextField();
        TextField fieldLongueur = new TextField();
        TextField fieldEtage = new TextField();

        fieldId.setPromptText("Ex : M001");
        fieldDesignation.setPromptText("Ex : Maison familiale");
        fieldLargeur.setPromptText("Ex : 12.5");
        fieldLongueur.setPromptText("Ex : 18");
        fieldEtage.setPromptText("Ex : 1");
        fieldId.setText(idExistant);
        fieldDesignation.setText(designationExistante);

        if (largeurExistante > 0) {
            fieldLargeur.setText(String.valueOf(largeurExistante));
        }

        if (longueurExistante > 0) {
            fieldLongueur.setText(String.valueOf(longueurExistante));
        }

        if (nbEtagesExistant > 0) {
            fieldEtage.setText(String.valueOf(nbEtagesExistant));
        }

        ajouterLigne(grid, "ID :", fieldId, 0, styleLabel);
        ajouterLigne(grid, "Désignation :", fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Largeur (m) :", fieldLargeur, 2, styleLabel);
        ajouterLigne(grid, "Longueur (m) :", fieldLongueur, 3, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage, 4, styleLabel);

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreProjet().afficher(stage));

        Button btnSuivant = new Button("ÉTAPE SUIVANTE →");
        btnSuivant.setStyle(styleBouton);

        btnSuivant.setOnAction(e -> {
            String id = fieldId.getText().trim();
            String designation = fieldDesignation.getText().trim();
            String txtLargeur = fieldLargeur.getText().trim().replace(",", ".");
            String txtLongueur = fieldLongueur.getText().trim().replace(",", ".");
            String txtEtage = fieldEtage.getText().trim();

            if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                    || txtLongueur.isEmpty() || txtEtage.isEmpty()) {
                lblErreur.setText("Veuillez remplir tous les champs.");
                return;
            }

            try {
                double largeur = Double.parseDouble(txtLargeur);
                double longueur = Double.parseDouble(txtLongueur);
                int nbEtages = Integer.parseInt(txtEtage);

                Maison maison = new Maison(id, designation, largeur, longueur, nbEtages);
                String idDevis = "D_" + id;
                double hauteurTotale = nbEtages * 2.5;
                double surfaceTotale = largeur * longueur * (nbEtages + 1);

                SauvegardeProjet.sauvegarderProjet(
                        id,
                        designation,
                        "MAISON",
                        nbEtages,
                        hauteurTotale,
                        surfaceTotale,
                        0,
                        idDevis,
                        largeur,
                        longueur
                );

                new FenetreEtage(maison, new HashMap<>()).afficher(stage);

            } catch (NumberFormatException ex) {
                lblErreur.setText("Largeur, longueur et étages doivent être des nombres.");
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