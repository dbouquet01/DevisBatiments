package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Maison;
import com.mycompany.devisbatiments.elements.Revetement;

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
        TextField fieldHauteur = new TextField();

        fieldId.setPromptText("Ex : M001");
        fieldDesignation.setPromptText("Ex : Maison familiale");
        fieldLargeur.setPromptText("Ex : 12.5");
        fieldLongueur.setPromptText("Ex : 18");
        fieldEtage.setPromptText("Ex : 1");
        fieldHauteur.setPromptText("Ex : 2.5");

        ComboBox<Revetement> comboFacade = new ComboBox<>();
        comboFacade.setPromptText("Choisir une façade");
        comboFacade.setMinWidth(180);
        comboFacade.getItems().addAll(Revetement.getRevetementsFacade());

        ComboBox<Revetement> comboIsolation = new ComboBox<>();
        comboIsolation.setPromptText("Choisir une isolation");
        comboIsolation.setMinWidth(180);
        comboIsolation.getItems().addAll(Revetement.getRevetementsIsolation());

        fieldId.setText(idExistant);
        fieldDesignation.setText(designationExistante);

        if (largeurExistante > 0) {
            fieldLargeur.setText(String.valueOf(largeurExistante));
        }
        if (longueurExistante > 0) {
            fieldLongueur.setText(String.valueOf(longueurExistante));
        }
        if (nbEtagesExistant >= 0) {
            fieldEtage.setText(String.valueOf(nbEtagesExistant));
        }

        // Complète les infos déjà sauvegardées dans Projets.txt.
        // La méthode centralisée est dans SauvegardeProjet, donc on évite de relire le fichier ici.
        String[] projet = SauvegardeProjet.chargerProjet(idExistant);
        preRemplirInfosProjet(projet, nbEtagesExistant, fieldHauteur, comboFacade, comboIsolation);

        ajouterLigne(grid, "ID :", fieldId, 0, styleLabel);
        ajouterLigne(grid, "Désignation :", fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Largeur (m) :", fieldLargeur, 2, styleLabel);
        ajouterLigne(grid, "Longueur (m) :", fieldLongueur, 3, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage, 4, styleLabel);
        ajouterLigne(grid, "Hauteur par étage (m) :", fieldHauteur, 5, styleLabel);
        ajouterLigneCombo(grid, "Façade extérieure :", comboFacade, 6, styleLabel);
        ajouterLigneCombo(grid, "Isolation extérieure :", comboIsolation, 7, styleLabel);

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
            String txtHauteur = fieldHauteur.getText().trim().replace(",", ".");

            Revetement facadeSelectionnee = comboFacade.getValue();
            Revetement isolationSelectionnee = comboIsolation.getValue();

            if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                    || txtLongueur.isEmpty() || txtEtage.isEmpty() || txtHauteur.isEmpty()
                    || facadeSelectionnee == null || isolationSelectionnee == null) {
                lblErreur.setText("Veuillez remplir tous les champs et sélectionner la façade et l'isolation.");
                return;
            }

            try {
                double largeur = Double.parseDouble(txtLargeur);
                double longueur = Double.parseDouble(txtLongueur);
                int nbEtages = Integer.parseInt(txtEtage);
                double hauteurEtage = Double.parseDouble(txtHauteur);

                if (largeur <= 0 || longueur <= 0 || nbEtages < 0 || hauteurEtage <= 0) {
                    lblErreur.setText("Les dimensions doivent être positives.");
                    return;
                }

                double hauteurTotale = hauteurEtage * (nbEtages + 1);
                double perimetre = 2 * (largeur + longueur);
                double surfaceFacade = perimetre * hauteurTotale;
                double surfaceIsolation = perimetre * hauteurTotale;

                double coutFacade = facadeSelectionnee.getPrixUnitaire() * surfaceFacade;
                double coutIsolation = isolationSelectionnee.getPrixUnitaire() * surfaceIsolation;

                Maison maison = new Maison(id, designation, largeur, longueur, nbEtages);
                String idDevis = "D_" + id;
                double surfaceTotale = largeur * longueur * (nbEtages + 1);

                SauvegardeProjet.sauvegarderProjet(
                        id, designation, "MAISON",
                        nbEtages, hauteurTotale, surfaceTotale,
                        0, idDevis, largeur, longueur,
                        facadeSelectionnee.getIdRevetement(),
                        isolationSelectionnee.getIdRevetement()
                );

                SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Facade", surfaceFacade, 0, 0, coutFacade);
                SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Isolation", surfaceIsolation, 0, 0, coutIsolation);

                new FenetreEtage(maison, new HashMap<>()).afficher(stage);

            } catch (NumberFormatException ex) {
                lblErreur.setText("Largeur, longueur, étages et hauteur doivent être des nombres.");
            }
        });

        HBox bottomBox = new HBox(30, btnRetour, btnSuivant);
        bottomBox.setPadding(new Insets(30));
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
        stage.setFullScreen(true);
        stage.show();
    }

    private void preRemplirInfosProjet(String[] projet, int nbEtagesExistant,
                                       TextField fieldHauteur,
                                       ComboBox<Revetement> comboFacade,
                                       ComboBox<Revetement> comboIsolation) {
        if (projet == null) return;

        try {
            if (projet.length >= 5 && nbEtagesExistant >= 0) {
                double hauteurTotale = Double.parseDouble(projet[4].trim().replace(",", "."));
                double hauteurParEtage = hauteurTotale / (nbEtagesExistant + 1);

                if (hauteurParEtage > 0) {
                    fieldHauteur.setText(String.valueOf(hauteurParEtage));
                }
            }

            if (projet.length >= 12) {
                int idFacade = Integer.parseInt(projet[10].trim());
                int idIsolation = Integer.parseInt(projet[11].trim());

                selectionnerRevetement(comboFacade, idFacade);
                selectionnerRevetement(comboIsolation, idIsolation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectionnerRevetement(ComboBox<Revetement> combo, int idRevetement) {
        for (Revetement r : combo.getItems()) {
            if (r.getIdRevetement() == idRevetement) {
                combo.setValue(r);
                return;
            }
        }
    }

    private void ajouterLigne(GridPane grid, String texte, TextField field, int ligne, String style) {
        Label lbl = new Label(texte);
        lbl.setStyle(style);
        grid.add(lbl, 0, ligne);
        grid.add(field, 1, ligne);
    }

    private void ajouterLigneCombo(GridPane grid, String texte, ComboBox<?> combo, int ligne, String style) {
        Label lbl = new Label(texte);
        lbl.setStyle(style);
        grid.add(lbl, 0, ligne);
        grid.add(combo, 1, ligne);
    }
}
