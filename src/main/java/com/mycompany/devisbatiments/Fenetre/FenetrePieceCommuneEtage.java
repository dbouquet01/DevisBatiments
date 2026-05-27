/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Piece;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;

public class FenetrePieceCommuneEtage {

    private final Batiments batiment;
    private final String nomEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;

    private Canvas canvas;
    private Label lblMessage;

    private TextField fieldNom;
    private TextField fieldX;
    private TextField fieldY;
    private TextField fieldLargeur;
    private TextField fieldLongueur;
    private TextField fieldHauteur;

    private ComboBox<Revetement> comboMur;
    private ComboBox<Revetement> comboSol;
    private ComboBox<Revetement> comboPlafond;

    private GestionCouloirEtage.CouloirInfo couloir;

    public FenetrePieceCommuneEtage(Batiments batiment, String nomEtage,
                                    HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        couloir = GestionCouloirEtage.chargerCouloir(batiment.getId(), nomEtage);

        if (couloir == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Couloir obligatoire");
            alert.setHeaderText("Le couloir n'est pas encore placé.");
            alert.setContentText("Retourne sur le plan de l'étage et valide d'abord le couloir.");
            alert.showAndWait();
            new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage);
            return;
        }

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        String styleValider = "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("AJOUTER UNE PIÈCE COMMUNE — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label aide = new Label("La pièce ne doit pas chevaucher le couloir. L'aperçu se met à jour automatiquement.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox top = new VBox(5, titre, aide);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));

        fieldNom = new TextField();
        fieldX = new TextField("0");
        fieldY = new TextField("0");
        fieldLargeur = new TextField();
        fieldLongueur = new TextField();
        fieldHauteur = new TextField("3.0");

        fieldNom.setPromptText("Nom de la pièce");
        fieldX.setPromptText("X origine (m)");
        fieldY.setPromptText("Y origine (m)");
        fieldLargeur.setPromptText("Largeur (m)");
        fieldLongueur.setPromptText("Longueur (m)");
        fieldHauteur.setPromptText("Hauteur (m)");

        comboMur = new ComboBox<>();
        comboSol = new ComboBox<>();
        comboPlafond = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());

        comboMur.setPromptText("Revêtement murs");
        comboSol.setPromptText("Revêtement sol");
        comboPlafond.setPromptText("Revêtement plafond");

        if (!comboMur.getItems().isEmpty()) comboMur.setValue(comboMur.getItems().get(0));
        if (!comboSol.getItems().isEmpty()) comboSol.setValue(comboSol.getItems().get(0));
        if (!comboPlafond.getItems().isEmpty()) comboPlafond.setValue(comboPlafond.getItems().get(0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(fieldNom, 1, 0);

        grid.add(new Label("X :"), 0, 1);
        grid.add(fieldX, 1, 1);

        grid.add(new Label("Y :"), 0, 2);
        grid.add(fieldY, 1, 2);

        grid.add(new Label("Largeur :"), 0, 3);
        grid.add(fieldLargeur, 1, 3);

        grid.add(new Label("Longueur :"), 0, 4);
        grid.add(fieldLongueur, 1, 4);

        grid.add(new Label("Hauteur :"), 0, 5);
        grid.add(fieldHauteur, 1, 5);

        grid.add(new Label("Murs :"), 0, 6);
        grid.add(comboMur, 1, 6);

        grid.add(new Label("Sol :"), 0, 7);
        grid.add(comboSol, 1, 7);

        grid.add(new Label("Plafond :"), 0, 8);
        grid.add(comboPlafond, 1, 8);

        lblMessage = new Label("");
        lblMessage.setStyle("-fx-font-size: 13px;");

        Button btnEnregistrer = new Button("ENREGISTRER LA PIÈCE");
        btnEnregistrer.setStyle(styleValider);

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);

        VBox panneau = new VBox(12, grid, lblMessage, btnEnregistrer);
        panneau.setAlignment(Pos.TOP_CENTER);
        panneau.setPadding(new Insets(15));
        panneau.setPrefWidth(330);

        canvas = new Canvas(720, 430);
        dessinerApercu();

        fieldX.textProperty().addListener((obs, o, n) -> dessinerApercu());
        fieldY.textProperty().addListener((obs, o, n) -> dessinerApercu());
        fieldLargeur.textProperty().addListener((obs, o, n) -> dessinerApercu());
        fieldLongueur.textProperty().addListener((obs, o, n) -> dessinerApercu());
        comboSol.setOnAction(e -> dessinerApercu());

        btnEnregistrer.setOnAction(e -> enregistrerPiece());

        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        HBox centre = new HBox(20, panneau, canvas);
        centre.setAlignment(Pos.CENTER);
        centre.setPadding(new Insets(15));

        HBox bottom = new HBox(btnRetour);
        bottom.setPadding(new Insets(15));
        bottom.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(centre);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 1120, 680);
        stage.setTitle("Pièce commune");
        stage.setScene(scene);
        stage.show();
    }

    private void enregistrerPiece() {
        try {
            String nom = fieldNom.getText().trim();

            if (nom.isEmpty()) {
                erreur("Donne un nom à la pièce.");
                return;
            }

            Revetement revMur = comboMur.getValue();
            Revetement revSol = comboSol.getValue();
            Revetement revPlafond = comboPlafond.getValue();

            if (revMur == null || revSol == null || revPlafond == null) {
                erreur("Choisis les 3 revêtements.");
                return;
            }

            double x = parse(fieldX);
            double y = parse(fieldY);
            double largeur = parse(fieldLargeur);
            double longueur = parse(fieldLongueur);
            double hauteur = parse(fieldHauteur);

            String erreur = verifierPiece(x, y, largeur, longueur);

            if (erreur != null) {
                erreur(erreur);
                return;
            }

            Piece piece = new Piece(nom, x, y, largeur, longueur, hauteur);
            piece.appliquerRevetementMurs(revMur);
            piece.appliquerRevetementSol(revSol);
            piece.appliquerRevetementPlafond(revPlafond);

            double coutMurs = revMur.calculerPrix(piece.calculerSurfaceMurs());
            double coutSol = revSol.calculerPrix(piece.calculerSurfaceSol());
            double coutPlafond = revPlafond.calculerPrix(piece.calculerSurfacePlafond());
            double total = coutMurs + coutSol + coutPlafond;

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(),
                    nomEtage,
                    nom,
                    x,
                    y,
                    largeur,
                    longueur,
                    hauteur,
                    revSol.getIdRevetement()
            );

            SauvegardeProjet.sauvegarderPiece(
                    batiment.getId(),
                    nomEtage,
                    nom,
                    x,
                    y,
                    largeur,
                    longueur,
                    hauteur,
                    revMur.getIdRevetement(),
                    revSol.getIdRevetement(),
                    revPlafond.getIdRevetement(),
                    coutMurs,
                    coutSol,
                    coutPlafond,
                    total
            );

            SauvegardeProjet.sauvegarderDevis(
                    "D_" + batiment.getId(),
                    batiment.getId(),
                    nom,
                    coutMurs,
                    coutSol,
                    coutPlafond,
                    total
            );

            succes("Pièce commune enregistrée.");
            dessinerApercu();

        } catch (NumberFormatException ex) {
            erreur("Vérifie les valeurs numériques.");
        }
    }

    private String verifierPiece(double x, double y, double largeur, double longueur) {
        if (largeur <= 0 || longueur <= 0) {
            return "Les dimensions doivent être positives.";
        }

        if (x < 0 || y < 0 || x + largeur > batiment.getLargeur() || y + longueur > batiment.getLongueur()) {
            return "La pièce doit rester dans la surface de l'étage.";
        }

        double pieceY1 = y;
        double pieceY2 = y + longueur;

        double couloirY1 = couloir.yCouloir;
        double couloirY2 = couloir.yCouloir + couloir.largeurCouloir;

        boolean chevaucheY = pieceY1 < couloirY2 && pieceY2 > couloirY1;

        if (chevaucheY) {
            return "Pièce impossible : elle chevauche le couloir.";
        }

        return null;
    }

    private void dessinerApercu() {
        if (canvas == null || couloir == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double marge = 35;
        double largeurPlan = 630;
        double hauteurPlan = 330;

        double echelleX = largeurPlan / batiment.getLargeur();
        double echelleY = hauteurPlan / batiment.getLongueur();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(marge, marge, largeurPlan, hauteurPlan);

        gc.setFill(Color.BLACK);
        gc.fillText("Aperçu étage : " + batiment.getLargeur() + " m x " + batiment.getLongueur() + " m",
                marge, marge - 10);

        double yCouloir = marge + couloir.yCouloir * echelleY;
        double hCouloir = couloir.largeurCouloir * echelleY;

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillRect(marge, yCouloir, largeurPlan, hCouloir);
        gc.strokeRect(marge, yCouloir, largeurPlan, hCouloir);

        gc.setFill(Color.BLACK);
        gc.fillText("Couloir", marge + largeurPlan / 2 - 25, yCouloir + hCouloir / 2 + 4);

        try {
            double x = parse(fieldX);
            double y = parse(fieldY);
            double largeur = parse(fieldLargeur);
            double longueur = parse(fieldLongueur);

            String verif = verifierPiece(x, y, largeur, longueur);

            Color couleur = Color.web("#DFF3FF", 0.80);

            if (verif != null) {
                couleur = Color.web("#FFB3B3", 0.80);
                lblMessage.setStyle("-fx-text-fill: #B00020;");
                lblMessage.setText(verif);
            } else {
                lblMessage.setStyle("-fx-text-fill: green;");
                lblMessage.setText("Aperçu valide.");
            }

            if (comboSol != null && comboSol.getValue() != null && verif == null) {
                couleur = Color.web("#BFE7FF", 0.80);
            }

            double px = marge + x * echelleX;
            double py = marge + y * echelleY;
            double pw = largeur * echelleX;
            double ph = longueur * echelleY;

            gc.setFill(couleur);
            gc.setStroke(verif == null ? Color.web("#0F056B") : Color.web("#B00020"));
            gc.setLineWidth(3);
            gc.fillRect(px, py, pw, ph);
            gc.strokeRect(px, py, pw, ph);

            gc.setFill(Color.BLACK);
            String nom = fieldNom.getText().trim().isEmpty() ? "Nouvelle pièce" : fieldNom.getText().trim();
            gc.fillText(nom, px + 6, py + 18);

        } catch (Exception ignored) {
            // dimensions pas encore remplies
        }
    }

    private double parse(TextField field) {
        return Double.parseDouble(field.getText().trim().replace(",", "."));
    }

    private void erreur(String texte) {
        lblMessage.setStyle("-fx-text-fill: #B00020;");
        lblMessage.setText(texte);
    }

    private void succes(String texte) {
        lblMessage.setStyle("-fx-text-fill: green;");
        lblMessage.setText(texte);
    }
}
