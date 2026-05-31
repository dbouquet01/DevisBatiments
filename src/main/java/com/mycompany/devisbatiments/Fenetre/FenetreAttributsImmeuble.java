package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Immeuble;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FenetreAttributsImmeuble {

    public void afficher(Stage stage, String idExistant, String designationExistante,
                         double largeurExistante, double longueurExistante, int nbEtagesExistant) {

        HashMap<String, Integer> nbAppartsCharges = chargerNbAppartementsDepuisEtage(idExistant);

        afficher(
                stage,
                idExistant,
                designationExistante,
                largeurExistante,
                longueurExistante,
                nbEtagesExistant,
                nbAppartsCharges
        );
    }

    public void afficher(Stage stage, String idExistant, String designationExistante,
                         double largeurExistante, double longueurExistante,
                         int nbEtagesExistant,
                         HashMap<String, Integer> nbAppartsExistants) {

        if (nbAppartsExistants == null || nbAppartsExistants.isEmpty()) {
            nbAppartsExistants = chargerNbAppartementsDepuisEtage(idExistant);
        }

        final HashMap<String, Integer> nbAppartsPreRemplis = nbAppartsExistants;

        Label titre = new Label("ATTRIBUTS DE L'IMMEUBLE");
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

        fieldId.setPromptText("Ex : IMB001");
        fieldDesignation.setPromptText("Ex : Résidence Les Lilas");
        fieldLargeur.setPromptText("Ex : 25.5");
        fieldLongueur.setPromptText("Ex : 40");
        fieldEtage.setPromptText("Ex : 5");
        fieldHauteur.setPromptText("Ex : 3.0");

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

            Revetement facade = comboFacade.getValue();
            Revetement isolation = comboIsolation.getValue();

            if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                    || txtLongueur.isEmpty() || txtEtage.isEmpty() || txtHauteur.isEmpty()
                    || facade == null || isolation == null) {
                lblErreur.setText("Veuillez remplir tous les champs et faire vos sélections de revêtements.");
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
                double coutFacade = facade.getPrixUnitaire() * surfaceFacade;
                double coutIsolation = isolation.getPrixUnitaire() * surfaceIsolation;

                SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Facade", surfaceFacade, 0, 0, coutFacade);
                SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Isolation", surfaceIsolation, 0, 0, coutIsolation);

                afficherSaisieAppartements(
                        stage, id, designation, largeur, longueur, nbEtages,
                        hauteurEtage,
                        nbAppartsPreRemplis,
                        facade.getIdRevetement(),
                        isolation.getIdRevetement()
                );

            } catch (NumberFormatException ex) {
                lblErreur.setText("Les valeurs numériques sont invalides.");
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
        stage.setTitle("Attributs Immeuble");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void afficherSaisieAppartements(Stage stage, String id, String designation,
                                            double largeur, double longueur, int nbEtages,
                                            double hauteurEtage,
                                            HashMap<String, Integer> nbAppartsExistants,
                                            int idFacade, int idIsolation) {

        if (nbAppartsExistants == null || nbAppartsExistants.isEmpty()) {
            nbAppartsExistants = chargerNbAppartementsDepuisEtage(id);
        }

        Label titre = new Label("APPARTEMENTS PAR ÉTAGE");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        String styleLabel = "-fx-font-size: 15px; -fx-font-weight: bold;";

        VBox listeChamps = new VBox(12);
        listeChamps.setAlignment(Pos.CENTER);
        listeChamps.setPadding(new Insets(20));

        ArrayList<TextField> champsApparts = new ArrayList<>();

        for (int i = 0; i <= nbEtages; i++) {
            String nomEtage = (i == 0) ? "RDC" : "Etage " + i;

            Label lblEtage = new Label(nomEtage + " :");
            lblEtage.setStyle(styleLabel);
            lblEtage.setMinWidth(120);

            TextField fieldNbApparts = new TextField();
            fieldNbApparts.setPromptText("Nombre d'appartements");
            fieldNbApparts.setMaxWidth(180);

            if (nbAppartsExistants.containsKey(nomEtage)) {
                fieldNbApparts.setText(String.valueOf(nbAppartsExistants.get(nomEtage)));
            }

            champsApparts.add(fieldNbApparts);

            HBox ligne = new HBox(15, lblEtage, fieldNbApparts);
            ligne.setAlignment(Pos.CENTER);
            listeChamps.getChildren().add(ligne);
        }

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        final HashMap<String, Integer> nbAppartsPourRetour = new HashMap<>(nbAppartsExistants);

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> afficher(
                stage,
                id,
                designation,
                largeur,
                longueur,
                nbEtages,
                nbAppartsPourRetour
        ));

        Button btnValider = new Button("VALIDER L'IMMEUBLE →");
        btnValider.setStyle(styleBouton);

        btnValider.setOnAction(e -> {
            HashMap<String, Integer> nbAppartsParEtage = new HashMap<>();
            int totalAppartements = 0;

            try {
                for (int i = 0; i <= nbEtages; i++) {
                    String nomEtage = (i == 0) ? "RDC" : "Etage " + i;
                    String txt = champsApparts.get(i).getText().trim();

                    if (txt.isEmpty()) {
                        lblErreur.setText("Veuillez remplir le nombre d'appartements pour " + nomEtage + ".");
                        return;
                    }

                    int nbApparts = Integer.parseInt(txt);

                    if (nbApparts < 0) {
                        lblErreur.setText("Le nombre d'appartements doit être positif pour " + nomEtage + ".");
                        return;
                    }

                    nbAppartsParEtage.put(nomEtage, nbApparts);
                    totalAppartements += nbApparts;
                }

                Immeuble immeuble = new Immeuble(id, designation, largeur, longueur, nbEtages);

                String idDevis = "D_" + id;
                double hauteurTotale = hauteurEtage * (nbEtages + 1);
                double surfaceEtage = largeur * longueur;
                double surfaceTotale = surfaceEtage * (nbEtages + 1);

                SauvegardeProjet.sauvegarderProjet(
                        id, designation, "IMMEUBLE",
                        nbEtages, hauteurTotale, surfaceTotale,
                        totalAppartements, idDevis, largeur, longueur,
                        idFacade, idIsolation
                );

                for (int i = 0; i <= nbEtages; i++) {
                    String nomEtage = (i == 0) ? "RDC" : "Etage " + i;
                    int nbApparts = nbAppartsParEtage.get(nomEtage);

                    SauvegardeProjet.sauvegarderEtage(
                            id,
                            nomEtage,
                            nbApparts,
                            surfaceEtage
                    );
                }

                new FenetreEtage(immeuble, nbAppartsParEtage).afficher(stage);

            } catch (NumberFormatException ex) {
                lblErreur.setText("Veuillez entrer uniquement des nombres entiers.");
            }
        });

        HBox bottomBox = new HBox(30, btnRetour, btnValider);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(25));

        VBox centre = new VBox(15, listeChamps, lblErreur);
        centre.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(centre);
        scrollPane.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Appartements par étage");
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

    private HashMap<String, Integer> chargerNbAppartementsDepuisEtage(String idProjet) {
        HashMap<String, Integer> nbApparts = new HashMap<>();
        if (idProjet == null || idProjet.trim().isEmpty()) return nbApparts;

        try (BufferedReader reader = new BufferedReader(new FileReader("Etage.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 4) continue;

                String idProjetLigne = p[1].trim();
                String nomEtage = p[2].trim();
                int nbAppartements = Integer.parseInt(p[3].trim());

                if (idProjetLigne.equalsIgnoreCase(idProjet)) {
                    nbApparts.put(nomEtage, nbAppartements);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbApparts;
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
