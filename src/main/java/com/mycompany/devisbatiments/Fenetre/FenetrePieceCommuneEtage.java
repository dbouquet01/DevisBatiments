/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class FenetrePieceCommuneEtage {

    private final Batiments batiment;
    private final String nomEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;

    private TextField fieldNom;
    private ComboBox<Revetement> comboMur;
    private ComboBox<Revetement> comboSol;
    private ComboBox<Revetement> comboPlafond;
    private Label lblMessage;
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
            alert.setContentText("Place et valide d'abord le couloir.");
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

        Label aide = new Label("La position et la surface sont calculées automatiquement selon le couloir et le nombre de blocs.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox top = new VBox(5, titre, aide);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(25));

        fieldNom = new TextField();
        fieldNom.setPromptText("Nom de la pièce commune");
        fieldNom.setMaxWidth(280);

        comboMur = new ComboBox<>();
        comboSol = new ComboBox<>();
        comboPlafond = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());

        if (!comboMur.getItems().isEmpty()) comboMur.setValue(comboMur.getItems().get(0));
        if (!comboSol.getItems().isEmpty()) comboSol.setValue(comboSol.getItems().get(0));
        if (!comboPlafond.getItems().isEmpty()) comboPlafond.setValue(comboPlafond.getItems().get(0));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(fieldNom, 1, 0);
        grid.add(new Label("Murs :"), 0, 1);
        grid.add(comboMur, 1, 1);
        grid.add(new Label("Sol :"), 0, 2);
        grid.add(comboSol, 1, 2);
        grid.add(new Label("Plafond :"), 0, 3);
        grid.add(comboPlafond, 1, 3);

        lblMessage = new Label("");
        lblMessage.setStyle("-fx-font-size: 13px;");

        Button btnEnregistrer = new Button("ENREGISTRER");
        btnEnregistrer.setStyle(styleValider);
        btnEnregistrer.setOnAction(e -> enregistrerPiece(stage));

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        VBox centre = new VBox(20, grid, lblMessage, btnEnregistrer);
        centre.setAlignment(Pos.CENTER);

        HBox bottom = new HBox(btnRetour);
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(centre);
        root.setBottom(bottom);

        Scene scene = new Scene(root);
        stage.setTitle("Pièce commune");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void enregistrerPiece(Stage stage) {
        try {
            String nom = fieldNom.getText().trim();

            if (nom.isEmpty()) {
                erreur("Donne un nom à la pièce commune.");
                return;
            }

            Revetement revMur = comboMur.getValue();
            Revetement revSol = comboSol.getValue();
            Revetement revPlafond = comboPlafond.getValue();

            if (revMur == null || revSol == null || revPlafond == null) {
                erreur("Choisis les 3 revêtements.");
                return;
            }

            Placement p = calculerPlacementAutomatique();
            Piece piece = new Piece(nom, p.x, p.y, p.largeur, p.longueur, p.hauteur);
            piece.appliquerRevetementMurs(revMur);
            piece.appliquerRevetementSol(revSol);
            piece.appliquerRevetementPlafond(revPlafond);

            double coutMurs = revMur.calculerPrix(piece.calculerSurfaceMurs());
            double coutSol = revSol.calculerPrix(piece.calculerSurfaceSol());
            double coutPlafond = revPlafond.calculerPrix(piece.calculerSurfacePlafond());
            double total = coutMurs + coutSol + coutPlafond;

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(), nomEtage, nom,
                    p.x, p.y, p.largeur, p.longueur, p.hauteur,
                    revSol.getIdRevetement()
            );

            SauvegardeProjet.sauvegarderPiece(
                    batiment.getId(), nomEtage, nom,
                    p.x, p.y, p.largeur, p.longueur, p.hauteur,
                    revMur.getIdRevetement(), revSol.getIdRevetement(), revPlafond.getIdRevetement(),
                    coutMurs, coutSol, coutPlafond, total
            );

            SauvegardeProjet.sauvegarderDevis(
                    "D_" + batiment.getId(), batiment.getId(), nom,
                    coutMurs, coutSol, coutPlafond, total
            );

            succes("Pièce commune enregistrée automatiquement.");
            new PlanEtage(batiment, nomEtage, batiment.getLargeur() * batiment.getLongueur(), nbAppartsParEtage).afficher(stage);

        } catch (Exception e) {
            erreur("Impossible d'enregistrer la pièce commune.");
            e.printStackTrace();
        }
    }

    private Placement calculerPlacementAutomatique() {
        ArrayList<String> elements = SauvegardeProjet.chargerNomsElementsPlan(batiment.getId(), nomEtage);
        int nbApparts = nbAppartsParEtage.getOrDefault(nomEtage, 0);
        int nbCommunes = 0;

        for (String element : elements) {
            if (!estAppartement(element) && !element.equalsIgnoreCase("Couloir")) {
                nbCommunes++;
            }
        }

        int nbBlocs = Math.max(1, nbApparts + nbCommunes + 1);
        double haut = couloir.yCouloir;
        double bas = batiment.getLongueur() - (couloir.yCouloir + couloir.largeurCouloir);

        double y = bas >= haut ? couloir.yCouloir + couloir.largeurCouloir : 0;
        double longueur = bas >= haut ? bas : haut;
        double largeur = batiment.getLargeur() / nbBlocs;
        double x = Math.min(nbBlocs - 1, nbApparts + nbCommunes) * largeur;

        return new Placement(x, y, largeur, longueur, 3.0);
    }

    private boolean estAppartement(String nom) {
        String n = nom.trim().toLowerCase();
        return n.startsWith("appart") || n.startsWith("appartement");
    }

    private void erreur(String texte) {
        lblMessage.setStyle("-fx-text-fill: #B00020;");
        lblMessage.setText(texte);
    }

    private void succes(String texte) {
        lblMessage.setStyle("-fx-text-fill: green;");
        lblMessage.setText(texte);
    }

    private static class Placement {
        double x, y, largeur, longueur, hauteur;

        Placement(double x, double y, double largeur, double longueur, double hauteur) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.longueur = longueur;
            this.hauteur = hauteur;
        }
    }
}
