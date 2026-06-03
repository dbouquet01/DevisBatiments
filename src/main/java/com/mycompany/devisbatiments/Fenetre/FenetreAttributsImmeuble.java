package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class FenetreAttributsImmeuble {

    TextField fieldId;
    TextField fieldDesignation;
    TextField fieldLargeur;
    TextField fieldLongueur;
    TextField fieldEtage;
    TextField fieldHauteur;

    ComboBox<Revetement> comboFacade;
    ComboBox<Revetement> comboIsolation;
    Label lblErreur;

    ArrayList<TextField> champsApparts;
    Label lblErreurAppartements;

    private final FenetreAttributsImmeubleController controller;

    public FenetreAttributsImmeuble() {
        controller = new FenetreAttributsImmeubleController(this);
    }

    public void afficher(Stage stage, String idExistant, String designationExistante,
                         double largeurExistante, double longueurExistante, int nbEtagesExistant) {
        HashMap<String, Integer> nbAppartsCharges = controller.chargerNbAppartementsDepuisEtage(idExistant);
        afficher(stage, idExistant, designationExistante, largeurExistante, longueurExistante,
                nbEtagesExistant, nbAppartsCharges);
    }

    public void afficher(Stage stage, String idExistant, String designationExistante,
                         double largeurExistante, double longueurExistante,
                         int nbEtagesExistant,
                         HashMap<String, Integer> nbAppartsExistants) {

        if (nbAppartsExistants == null || nbAppartsExistants.isEmpty()) {
            nbAppartsExistants = controller.chargerNbAppartementsDepuisEtage(idExistant);
        }

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

        fieldId = new TextField();
        fieldDesignation = new TextField();
        fieldLargeur = new TextField();
        fieldLongueur = new TextField();
        fieldEtage = new TextField();
        fieldHauteur = new TextField();

        fieldId.setPromptText("Ex : IMB001");
        fieldDesignation.setPromptText("Ex : Résidence Les Lilas");
        fieldLargeur.setPromptText("Ex : 25.5");
        fieldLongueur.setPromptText("Ex : 40");
        fieldEtage.setPromptText("Ex : 5");
        fieldHauteur.setPromptText("Ex : 3.0");

        comboFacade = new ComboBox<>();
        comboFacade.setPromptText("Choisir une façade");
        comboFacade.setMinWidth(180);
        comboFacade.getItems().addAll(Revetement.getRevetementsFacade());

        comboIsolation = new ComboBox<>();
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

        controller.preRemplirInfosProjet(idExistant, nbEtagesExistant);

        ajouterLigne(grid, "ID :", fieldId, 0, styleLabel);
        ajouterLigne(grid, "Désignation :", fieldDesignation, 1, styleLabel);
        ajouterLigne(grid, "Largeur (m) :", fieldLargeur, 2, styleLabel);
        ajouterLigne(grid, "Longueur (m) :", fieldLongueur, 3, styleLabel);
        ajouterLigne(grid, "Nombre d'étages :", fieldEtage, 4, styleLabel);
        ajouterLigne(grid, "Hauteur par étage (m) :", fieldHauteur, 5, styleLabel);
        ajouterLigneCombo(grid, "Façade extérieure :", comboFacade, 6, styleLabel);
        ajouterLigneCombo(grid, "Isolation extérieure :", comboIsolation, 7, styleLabel);

        lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> controller.retourProjet(stage));

        Button btnSuivant = new Button("ÉTAPE SUIVANTE →");
        btnSuivant.setStyle(styleBouton);
        final HashMap<String, Integer> nbAppartsPreRemplis = nbAppartsExistants;
        btnSuivant.setOnAction(e -> controller.validerAttributs(stage, nbAppartsPreRemplis));

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

    void afficherSaisieAppartements(Stage stage, String id, String designation,
                                    double largeur, double longueur, int nbEtages,
                                    double hauteurEtage,
                                    HashMap<String, Integer> nbAppartsExistants,
                                    int idFacade, int idIsolation) {

        if (nbAppartsExistants == null || nbAppartsExistants.isEmpty()) {
            nbAppartsExistants = controller.chargerNbAppartementsDepuisEtage(id);
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

        champsApparts = new ArrayList<>();

        for (int i = 0; i <= nbEtages; i++) {
            String nomEtage = controller.nomEtage(i);

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

        lblErreurAppartements = new Label("");
        lblErreurAppartements.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        final HashMap<String, Integer> nbAppartsPourRetour = new HashMap<>(nbAppartsExistants);

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> afficher(stage, id, designation, largeur, longueur, nbEtages, nbAppartsPourRetour));

        Button btnValider = new Button("VALIDER L'IMMEUBLE →");
        btnValider.setStyle(styleBouton);
        btnValider.setOnAction(e -> controller.validerAppartements(stage, id, designation, largeur, longueur,
                nbEtages, hauteurEtage, idFacade, idIsolation));

        HBox bottomBox = new HBox(30, btnRetour, btnValider);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(25));

        VBox centre = new VBox(15, listeChamps, lblErreurAppartements);
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
