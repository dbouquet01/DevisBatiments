package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Vue de configuration d'une pièce.
 *
 * Cette classe ne contient que la création de l'interface JavaFX.
 * La logique métier, les calculs, la navigation et la sauvegarde sont dans
 * {@link FenetrePieceController}.
 */
public class FenetrePiece {

    final Batiments batiment;
    final String nomEtage;
    final String nomPiece;
    final double surfacePiece;
    final ArrayList<String> nomsPieces;

    TextField fieldX;
    TextField fieldY;
    TextField fieldLargeur;
    TextField fieldLongueur;
    TextField fieldHauteur;

    TextField fieldNbFenetre;
    TextField fieldNbPorte;

    TextField fieldNbTremie;
    TextField fieldLargeurTremie;
    TextField fieldLongueurTremie;

    ComboBox<Revetement> comboMur;
    ComboBox<Revetement> comboSol;
    ComboBox<Revetement> comboPlafond;
    ComboBox<Revetement> comboTremie;

    Label lblSurfaceMur;
    Label lblSurfaceSol;
    Label lblSurfacePlafond;
    Label lblSurfaceTremie;

    Label lblPrixMur;
    Label lblPrixSol;
    Label lblPrixPlafond;
    Label lblPrixTremie;
    Label lblPrixTotal;

    Label lblMessage;

    Button btnRetour;
    Button btnCalculer;
    Button btnEnregistrer;
    Button btnMenu;
    Button btnPlacerOuverture;

    Node plan;

    public FenetrePiece(Batiments batiment, String nomEtage,
                        String nomPiece, double surfacePiece,
                        ArrayList<String> nomsPieces) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.surfacePiece = surfacePiece;
        this.nomsPieces = nomsPieces == null ? new ArrayList<>() : nomsPieces;
    }

    public void afficher(Stage stage) {
        FenetrePieceController controller = new FenetrePieceController(this);

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 24; -fx-cursor: hand;";

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F5;");

        Label titre = new Label("CONFIGURATION PIÈCE — " + nomPiece + " / " + nomEtage);
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        HBox top = new HBox(titre);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        root.setTop(top);

        creerChamps();
        creerCombos();
        creerLabelsResultats();
        controller.chargerDonnees();

        VBox boxDim = creerBox("1. Dimensions / position",
                ligne("X origine :", fieldX, 140, 220),
                ligne("Y origine :", fieldY, 140, 220),
                ligne("Largeur (m) :", fieldLargeur, 140, 220),
                ligne("Longueur (m) :", fieldLongueur, 140, 220),
                ligne("Hauteur (m) :", fieldHauteur, 140, 220)
        );

        VBox boxOuv = creerBox("2. Ouvertures / escalier",
                ligne("Fenêtres :", fieldNbFenetre, 160, 180),
                ligne("Portes :", fieldNbPorte, 160, 180),
                ligne("Nombre escaliers :", fieldNbTremie, 160, 180),
                ligne("Largeur escalier :", fieldLargeurTremie, 160, 180),
                ligne("Longueur escalier :", fieldLongueurTremie, 160, 180)
        );

        VBox boxRev = creerBox("3. Revêtements",
                ligne("Murs :", comboMur, 140, 260),
                ligne("Sol :", comboSol, 140, 260),
                ligne("Plafond :", comboPlafond, 140, 260),
                ligne("Escalier :", comboTremie, 140, 260)
        );

        VBox resultatsCalcul = new VBox(8,
                lblSurfaceMur,
                lblSurfaceSol,
                lblSurfacePlafond,
                lblSurfaceTremie,
                lblPrixMur,
                lblPrixSol,
                lblPrixPlafond,
                lblPrixTremie,
                lblPrixTotal
        );

        VBox boxCalcul = creerBox("4. Calcul", resultatsCalcul);

        lblMessage = new Label("");
        lblMessage.setStyle("-fx-font-size: 13px;");

        HBox ligneHaut = new HBox(15, boxDim, boxOuv);
        HBox ligneBas = new HBox(15, boxRev, boxCalcul);

        HBox.setHgrow(boxDim, Priority.ALWAYS);
        HBox.setHgrow(boxOuv, Priority.ALWAYS);
        HBox.setHgrow(boxRev, Priority.ALWAYS);
        HBox.setHgrow(boxCalcul, Priority.ALWAYS);

        VBox panneauGauche = new VBox(12, ligneHaut, ligneBas, lblMessage);
        panneauGauche.setPadding(new Insets(0, 15, 0, 25));
        panneauGauche.setMaxWidth(Double.MAX_VALUE);

        plan = controller.creerPlan();
        if (plan instanceof Region) {
            ((Region) plan).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        Label titrePlan = new Label(controller.getTitrePlan());
        VBox panneauDroit = new VBox(8, titrePlan, plan);
        panneauDroit.setAlignment(Pos.TOP_CENTER);
        panneauDroit.setPadding(new Insets(0, 25, 0, 15));
        panneauDroit.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(plan, Priority.ALWAYS);

        HBox centre = new HBox(10, panneauGauche, panneauDroit);
        centre.setPadding(new Insets(0, 10, 0, 10));

        HBox.setHgrow(panneauGauche, Priority.ALWAYS);
        HBox.setHgrow(panneauDroit, Priority.ALWAYS);

        panneauGauche.prefWidthProperty().bind(centre.widthProperty().multiply(0.55));
        panneauDroit.prefWidthProperty().bind(centre.widthProperty().multiply(0.45));

        root.setCenter(centre);

        creerBoutons(styleBouton);

        HBox bottom = new HBox(20, btnRetour, btnCalculer, btnEnregistrer, btnMenu, btnPlacerOuverture);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(10, 30, 10, 30));
        bottom.setMinHeight(58);
        bottom.setPrefHeight(58);
        bottom.setStyle("-fx-background-color: #F5F5F5;");
        root.setBottom(bottom);

        controller.initialiserActions(stage);

        Scene scene = new Scene(root, 1500, 850);
        stage.setTitle("Configuration pièce");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void creerChamps() {
        fieldX = new TextField();
        fieldY = new TextField();
        fieldLargeur = new TextField();
        fieldLongueur = new TextField();
        fieldHauteur = new TextField();

        fieldNbFenetre = new TextField("0");
        fieldNbPorte = new TextField("0");

        fieldNbTremie = new TextField("0");
        fieldLargeurTremie = new TextField("1.0");
        fieldLongueurTremie = new TextField("2.5");
    }

    private void creerCombos() {
        comboMur = new ComboBox<>();
        comboSol = new ComboBox<>();
        comboPlafond = new ComboBox<>();
        comboTremie = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());
        comboTremie.getItems().addAll(Revetement.getRevetementsSol());

        if (!comboMur.getItems().isEmpty()) comboMur.setValue(comboMur.getItems().get(0));
        if (!comboSol.getItems().isEmpty()) comboSol.setValue(comboSol.getItems().get(0));
        if (!comboPlafond.getItems().isEmpty()) comboPlafond.setValue(comboPlafond.getItems().get(0));
        if (!comboTremie.getItems().isEmpty()) comboTremie.setValue(comboTremie.getItems().get(0));
    }

    private void creerLabelsResultats() {
        lblSurfaceMur = new Label("Surface murs : -");
        lblSurfaceSol = new Label("Surface sol : -");
        lblSurfacePlafond = new Label("Surface plafond : -");
        lblSurfaceTremie = new Label("Surface escalier : -");

        lblPrixMur = new Label("Prix murs : -");
        lblPrixSol = new Label("Prix sol : -");
        lblPrixPlafond = new Label("Prix plafond : -");
        lblPrixTremie = new Label("Prix escalier : -");
        lblPrixTotal = new Label("TOTAL : -");
    }

    private void creerBoutons(String styleBouton) {
        btnRetour = new Button("RETOUR");
        btnCalculer = new Button("CALCULER");
        btnEnregistrer = new Button("ENREGISTRER");
        btnMenu = new Button("MENU PRINCIPAL");
        btnPlacerOuverture = new Button("PLACER LES OUVERTURES");

        btnRetour.setStyle(styleBouton);
        btnCalculer.setStyle(styleBouton);
        btnEnregistrer.setStyle(styleBouton);
        btnMenu.setStyle(styleBouton);
        btnPlacerOuverture.setStyle(styleBouton);
    }

    private VBox creerBox(String titre, Node... contenus) {
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-size: 15px;");

        VBox box = new VBox(10);
        box.getChildren().add(lblTitre);
        box.getChildren().addAll(contenus);
        box.setPadding(new Insets(12));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color: white; -fx-border-color: #0F056B; -fx-border-width: 1;");

        return box;
    }

    private HBox ligne(String texte, Node champ, double largeurLabel, double largeurChamp) {
        Label lbl = new Label(texte);
        lbl.setMinWidth(largeurLabel);

        if (champ instanceof Control) {
            ((Control) champ).setPrefWidth(largeurChamp);
        }

        HBox ligne = new HBox(10, lbl, champ);
        ligne.setAlignment(Pos.CENTER_LEFT);
        return ligne;
    }
}

