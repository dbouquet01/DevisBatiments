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



public class FenetrePiece {

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private final double surfacePiece;
    private final ArrayList<String> nomsPieces;

    private Piece piece;

    public FenetrePiece(Batiments batiment, String nomEtage,
                    String nomPiece, double surfacePiece,
                    ArrayList<String> nomsPieces) {
    this.batiment = batiment;
    this.nomEtage = nomEtage;
    this.nomPiece = nomPiece;
    this.surfacePiece = surfacePiece;
    this.nomsPieces = nomsPieces;
}

    public void afficher(Stage stage) {

        String styleBouton =
                "-fx-background-color: #0F056B;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;";

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F5F5;"
        );

        Label titre =
                new Label(
                        "CONFIGURATION PIÈCE — "
                                + nomPiece
                                + " / "
                                + nomEtage
                );

        titre.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        HBox top =
                new HBox(titre);

        top.setAlignment(Pos.CENTER);

        top.setPadding(
                new Insets(12)
        );

        root.setTop(top);

        VBox panneauGauche =
                new VBox(12);

        panneauGauche.setPadding(
                new Insets(15)
        );

        panneauGauche.setPrefWidth(760);

        TextField fieldX =
                new TextField();

        TextField fieldY =
                new TextField();

        TextField fieldLargeur =
                new TextField();

        TextField fieldLongueur =
                new TextField();

        TextField fieldHauteur =
                new TextField();

        GridPane gridDim =
                new GridPane();

        gridDim.setHgap(10);
        gridDim.setVgap(10);

        gridDim.add(new Label("X origine :"), 0, 0);
        gridDim.add(fieldX, 1, 0);

        gridDim.add(new Label("Y origine :"), 0, 1);
        gridDim.add(fieldY, 1, 1);

        gridDim.add(new Label("Largeur (m) :"), 0, 2);
        gridDim.add(fieldLargeur, 1, 2);

        gridDim.add(new Label("Longueur (m) :"), 0, 3);
        gridDim.add(fieldLongueur, 1, 3);

        gridDim.add(new Label("Hauteur (m) :"), 0, 4);
        gridDim.add(fieldHauteur, 1, 4);

        VBox boxDim =
                new VBox(
                        10,
                        new Label("1. Dimensions / position"),
                        gridDim
                );

        boxDim.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );

        ComboBox<Revetement> comboMur =
                new ComboBox<>();

        ComboBox<Revetement> comboSol =
                new ComboBox<>();

        ComboBox<Revetement> comboPlafond =
                new ComboBox<>();

        comboMur.getItems().addAll(
                Revetement.getRevetementsMur()
        );

        comboSol.getItems().addAll(
                Revetement.getRevetementsSol()
        );

        comboPlafond.getItems().addAll(
                Revetement.getRevetementsPlafond()
        );

        GridPane gridRev =
                new GridPane();

        gridRev.setHgap(10);
        gridRev.setVgap(10);

        gridRev.add(new Label("Murs :"), 0, 0);
        gridRev.add(comboMur, 1, 0);

        gridRev.add(new Label("Sol :"), 0, 1);
        gridRev.add(comboSol, 1, 1);

        gridRev.add(new Label("Plafond :"), 0, 2);
        gridRev.add(comboPlafond, 1, 2);

        VBox boxRev =
                new VBox(
                        10,
                        new Label("2. Revêtements"),
                        gridRev
                );

        boxRev.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );

        HBox ligneHaut =
                new HBox(
                        15,
                        boxDim,
                        boxRev
                );

        Label lblSurfaceMur =
                new Label("Surface murs : -");

        Label lblSurfaceSol =
                new Label("Surface sol : -");

        Label lblSurfacePlafond =
                new Label("Surface plafond : -");

        Label lblPrixMur =
                new Label("Prix murs : -");

        Label lblPrixSol =
                new Label("Prix sol : -");

        Label lblPrixPlafond =
                new Label("Prix plafond : -");

        Label lblPrixTotal =
                new Label("TOTAL : -");

        GridPane resultats =
                new GridPane();

        resultats.setHgap(30);
        resultats.setVgap(8);

        resultats.add(lblSurfaceMur, 0, 0);
        resultats.add(lblPrixMur, 1, 0);

        resultats.add(lblSurfaceSol, 0, 1);
        resultats.add(lblPrixSol, 1, 1);

        resultats.add(lblSurfacePlafond, 0, 2);
        resultats.add(lblPrixPlafond, 1, 2);

        resultats.add(lblPrixTotal, 0, 3);

        VBox boxCalcul =
                new VBox(
                        10,
                        new Label("3. Calcul"),
                        resultats
                );

        boxCalcul.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );

        Label lblMessage =
                new Label("");

        Button btnCalculer =
                new Button("CALCULER");

        btnCalculer.setStyle(styleBouton);

        Button btnEnregistrer =
                new Button("ENREGISTRER");

        btnEnregistrer.setStyle(styleBouton);

        Button btnRetour =
                new Button("RETOUR");

        btnRetour.setStyle(styleBouton);

        HBox boutons =
                new HBox(
                        15,
                        btnCalculer,
                        btnEnregistrer,
                        btnRetour
                );

        panneauGauche.getChildren().addAll(
                ligneHaut,
                boxCalcul,
                lblMessage,
                boutons
        );

        PlanDessin plan =
                new PlanDessin(batiment);

        VBox panneauDessin =
                new VBox(
                        10,
                        new Label("Plan du projet"),
                        plan
                );

        panneauDessin.setPadding(
                new Insets(15)
        );

        root.setLeft(panneauGauche);
        root.setCenter(panneauDessin);

        btnCalculer.setOnAction(e -> {

            try {

                double x =
                        Double.parseDouble(
                                fieldX.getText()
                                        .replace(",", ".")
                        );

                double y =
                        Double.parseDouble(
                                fieldY.getText()
                                        .replace(",", ".")
                        );

                double largeur =
                        Double.parseDouble(
                                fieldLargeur.getText()
                                        .replace(",", ".")
                        );

                double longueur =
                        Double.parseDouble(
                                fieldLongueur.getText()
                                        .replace(",", ".")
                        );

                double hauteur =
                        Double.parseDouble(
                                fieldHauteur.getText()
                                        .replace(",", ".")
                        );

                piece =
                        new Piece(
                                nomPiece,
                                x,
                                y,
                                largeur,
                                longueur,
                                hauteur
                        );

                Revetement revMur =
                        comboMur.getValue();

                Revetement revSol =
                        comboSol.getValue();

                Revetement revPlafond =
                        comboPlafond.getValue();

                piece.appliquerRevetementMurs(revMur);
                piece.appliquerRevetementSol(revSol);
                piece.appliquerRevetementPlafond(revPlafond);

                lblSurfaceMur.setText(
                        "Surface murs : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerSurfaceMurs()
                                )
                );

                lblSurfaceSol.setText(
                        "Surface sol : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerSurfaceSol()
                                )
                );

                lblSurfacePlafond.setText(
                        "Surface plafond : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerSurfacePlafond()
                                )
                );

                lblPrixMur.setText(
                        "Prix murs : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerPrixMurs()
                                )
                                + " €"
                );

                lblPrixSol.setText(
                        "Prix sol : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerPrixSol()
                                )
                                + " €"
                );

                lblPrixPlafond.setText(
                        "Prix plafond : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerPrixPlafond()
                                )
                                + " €"
                );

                lblPrixTotal.setText(
                        "TOTAL : "
                                + String.format(
                                        "%.2f",
                                        piece.calculerPrixTotal()
                                )
                                + " €"
                );

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        });

        btnEnregistrer.setOnAction(e -> {
            try {
                double x = Double.parseDouble(fieldX.getText().trim().replace(",", "."));
                double y = Double.parseDouble(fieldY.getText().trim().replace(",", "."));
                double largeur = Double.parseDouble(fieldLargeur.getText().trim().replace(",", "."));
                double longueur = Double.parseDouble(fieldLongueur.getText().trim().replace(",", "."));
                double hauteur = Double.parseDouble(fieldHauteur.getText().trim().replace(",", "."));

                Revetement revMur = comboMur.getValue();
                Revetement revSol = comboSol.getValue();
                Revetement revPlafond = comboPlafond.getValue();

                if (revMur == null || revSol == null || revPlafond == null) {
                    lblMessage.setStyle("-fx-text-fill: red;");
                    lblMessage.setText("Choisissez les 3 revêtements.");
                    return;
                }

        // IMPORTANT : on recrée la pièce avec les valeurs actuelles des champs
                piece = new Piece(nomPiece, x, y, largeur, longueur, hauteur);

                piece.appliquerRevetementMurs(revMur);
                piece.appliquerRevetementSol(revSol);
                piece.appliquerRevetementPlafond(revPlafond);

                SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(),
                    nomEtage,
                    nomPiece,
                    x,
                    y,
                    largeur,
                    longueur,
                    hauteur,
                    revSol.getIdRevetement()
                );

                SauvegardeProjet.sauvegarderDevis(
                    "D_" + batiment.getId(),
                    batiment.getId(),
                    nomPiece,
                    piece.calculerPrixMurs(),
                    piece.calculerPrixSol(),
                    piece.calculerPrixPlafond(),
                    piece.calculerPrixTotal()
                );

                plan.actualiser();

                    lblMessage.setStyle("-fx-text-fill: green;");
                    lblMessage.setText("Pièce enregistrée et plan mis à jour.");

            } catch (Exception ex) {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Impossible d'enregistrer la pièce.");
                ex.printStackTrace();
            }
        });

      btnRetour.setOnAction(e -> {
      new FenetreListePieces(batiment, nomEtage, nomsPieces).afficher(stage);
});

        Scene scene =
                new Scene(root, 1450, 820);

        stage.setMaximized(true);

        stage.setScene(scene);

        stage.show();
    }
}
