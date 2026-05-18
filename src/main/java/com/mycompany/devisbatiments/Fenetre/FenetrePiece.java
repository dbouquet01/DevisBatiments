/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Piece;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetrePiece {

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private final double surfacePiece;

    private final double xOrigine;
    private final double yOrigine;

    private Piece piece;

    public FenetrePiece(Batiments batiment, String nomEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = "Pièce";
        this.surfacePiece = 0;
        this.xOrigine = 0;
        this.yOrigine = 0;
    }

    public FenetrePiece(Batiments batiment, String nomEtage,
                        String nomPiece, double surfacePiece) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.surfacePiece = surfacePiece;
        this.xOrigine = 0;
        this.yOrigine = 0;
    }

    public FenetrePiece(Batiments batiment, String nomEtage,
                        String nomPiece, double xOrigine, double yOrigine,
                        double largeur, double longueur) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.surfacePiece = largeur * longueur;
        this.xOrigine = xOrigine;
        this.yOrigine = yOrigine;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        String styleLabel = "-fx-font-size: 14px; -fx-font-weight: bold;";
        String styleTitreSec = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F056B;";

        Label titre = new Label("PIÈCE : " + nomPiece + "  —  " + nomEtage);
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        Label titreDimensions = new Label("1. Dimensions de la pièce");
        titreDimensions.setStyle(styleTitreSec);

        GridPane gridDim = new GridPane();
        gridDim.setHgap(15);
        gridDim.setVgap(10);
        gridDim.setPadding(new Insets(10));
        gridDim.setAlignment(Pos.CENTER);

        TextField fieldLargeur = new TextField();
        TextField fieldLongueur = new TextField();
        TextField fieldHauteur = new TextField();

        if (surfacePiece > 0) {
            fieldLargeur.setPromptText("ex: 3.0");
            fieldLongueur.setPromptText("ex: " + String.format("%.1f", surfacePiece / 3.0));
        }

        ajouterLigne(gridDim, "Largeur (m) :", fieldLargeur, 0, styleLabel);
        ajouterLigne(gridDim, "Longueur (m) :", fieldLongueur, 1, styleLabel);
        ajouterLigne(gridDim, "Hauteur (m) :", fieldHauteur, 2, styleLabel);

        Label lblOrigine = new Label(
                String.format("Origine plan : x = %.2f ; y = %.2f", xOrigine, yOrigine)
        );
        lblOrigine.setStyle("-fx-font-size: 13px;");

        Label lblSurfaceMur = new Label("Surface murs : —");
        Label lblSurfaceSol = new Label("Surface sol : —");
        Label lblSurfacePlafond = new Label("Surface plafond : —");

        Button btnCalculer = new Button("Calculer les surfaces");
        btnCalculer.setStyle(styleBouton);

        Label lblErreurDim = new Label("");
        lblErreurDim.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        VBox zoneDimensions = new VBox(
                8,
                titreDimensions,
                gridDim,
                lblOrigine,
                btnCalculer,
                lblErreurDim,
                lblSurfaceMur,
                lblSurfaceSol,
                lblSurfacePlafond
        );
        zoneDimensions.setAlignment(Pos.CENTER);
        zoneDimensions.setPadding(new Insets(10));

        Label titreRevetements = new Label("2. Choix des revêtements");
        titreRevetements.setStyle(styleTitreSec);

        ComboBox<Revetement> comboMur = new ComboBox<>();
        ComboBox<Revetement> comboSol = new ComboBox<>();
        ComboBox<Revetement> comboPlafond = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());

        comboMur.setPromptText("Choisir revêtement mur...");
        comboSol.setPromptText("Choisir revêtement sol...");
        comboPlafond.setPromptText("Choisir revêtement plafond...");

        GridPane gridRev = new GridPane();
        gridRev.setHgap(15);
        gridRev.setVgap(12);
        gridRev.setPadding(new Insets(10));
        gridRev.setAlignment(Pos.CENTER);

        Label lblMur = new Label("Revêtement Mur :");
        lblMur.setStyle(styleLabel);

        Label lblSol = new Label("Revêtement Sol :");
        lblSol.setStyle(styleLabel);

        Label lblPlafond = new Label("Revêtement Plafond :");
        lblPlafond.setStyle(styleLabel);

        gridRev.add(lblMur, 0, 0);
        gridRev.add(comboMur, 1, 0);
        gridRev.add(lblSol, 0, 1);
        gridRev.add(comboSol, 1, 1);
        gridRev.add(lblPlafond, 0, 2);
        gridRev.add(comboPlafond, 1, 2);

        Label titreCout = new Label("3. Coût estimé");
        titreCout.setStyle(styleTitreSec);

        Label lblCoutMur = new Label("Coût murs : —");
        Label lblCoutSol = new Label("Coût sol : —");
        Label lblCoutPlafond = new Label("Coût plafond : —");
        Label lblCoutTotal = new Label("TOTAL : —");

        lblCoutTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F056B;");

        Button btnCalculerCout = new Button("Calculer le coût");
        btnCalculerCout.setStyle(styleBouton);
        btnCalculerCout.setDisable(true);

        Label lblErreurRev = new Label("");
        lblErreurRev.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        VBox zoneCout = new VBox(
                8,
                titreCout,
                lblCoutMur,
                lblCoutSol,
                lblCoutPlafond,
                lblCoutTotal,
                lblErreurRev
        );
        zoneCout.setAlignment(Pos.CENTER);

        btnCalculer.setOnAction(e -> {
            try {
                String txtLargeur = fieldLargeur.getText().trim().replace(",", ".");
                String txtLongueur = fieldLongueur.getText().trim().replace(",", ".");
                String txtHauteur = fieldHauteur.getText().trim().replace(",", ".");

                if (txtLargeur.isEmpty() || txtLongueur.isEmpty() || txtHauteur.isEmpty()) {
                    lblErreurDim.setText("Veuillez remplir toutes les dimensions.");
                    return;
                }

                double largeur = Double.parseDouble(txtLargeur);
                double longueur = Double.parseDouble(txtLongueur);
                double hauteur = Double.parseDouble(txtHauteur);

                if (largeur <= 0 || longueur <= 0 || hauteur <= 0) {
                    lblErreurDim.setText("Les dimensions doivent être positives.");
                    return;
                }

                piece = new Piece(nomPiece, xOrigine, yOrigine, largeur, longueur, hauteur);

                lblSurfaceMur.setText(String.format("Surface murs : %.2f m²", piece.calculerSurfaceMurs()));
                lblSurfaceSol.setText(String.format("Surface sol : %.2f m²", piece.calculerSurfaceSol()));
                lblSurfacePlafond.setText(String.format("Surface plafond : %.2f m²", piece.calculerSurfacePlafond()));

                lblErreurDim.setText("");
                lblErreurRev.setText("");
                btnCalculerCout.setDisable(false);

            } catch (Exception ex) {
                lblErreurDim.setText("Erreur : " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCalculerCout.setOnAction(e -> {
            try {
                if (piece == null) {
                    lblErreurRev.setText("Veuillez d'abord calculer les surfaces.");
                    return;
                }

                Revetement revMur = comboMur.getValue();
                Revetement revSol = comboSol.getValue();
                Revetement revPlafond = comboPlafond.getValue();

                if (revMur == null || revSol == null || revPlafond == null) {
                    lblErreurRev.setText("Veuillez choisir un revêtement pour chaque surface.");
                    return;
                }

                piece.appliquerRevetementMurs(revMur);
                piece.appliquerRevetementSol(revSol);
                piece.appliquerRevetementPlafond(revPlafond);

                lblCoutMur.setText(String.format("Coût murs : %.2f €", piece.calculerPrixMurs()));
                lblCoutSol.setText(String.format("Coût sol : %.2f €", piece.calculerPrixSol()));
                lblCoutPlafond.setText(String.format("Coût plafond : %.2f €", piece.calculerPrixPlafond()));
                lblCoutTotal.setText(String.format("TOTAL : %.2f €", piece.calculerPrixTotal()));

                lblErreurRev.setText("");

            } catch (Exception ex) {
                lblErreurRev.setText("Erreur : " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> {
            new FenetreListePieces(batiment, nomEtage).afficher(stage);
        });

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox col1 = new VBox(15, zoneDimensions);
        col1.setAlignment(Pos.TOP_CENTER);
        col1.setPrefWidth(320);

        VBox col2 = new VBox(15, titreRevetements, gridRev, btnCalculerCout);
        col2.setAlignment(Pos.TOP_CENTER);
        col2.setPrefWidth(350);

        VBox col3 = new VBox(15, zoneCout);
        col3.setAlignment(Pos.TOP_CENTER);
        col3.setPrefWidth(280);

        HBox centre = new HBox(20, col1, col2, col3);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("Pièce : " + nomPiece);
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