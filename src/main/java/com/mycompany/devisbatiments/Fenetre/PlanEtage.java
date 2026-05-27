/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

public class PlanEtage {

    private static final double LARGEUR_COULOIR_METRES = 1.50;

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;

    public PlanEtage(Batiments batiment, String nomEtage,
                     double surfaceEtage,
                     HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("PLAN DE L'ÉTAGE — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        int nbApparts = nbAppartsParEtage.getOrDefault(nomEtage, 0);

        Label info = creerLabelInfo(nbApparts);

        ComboBox<String> choixCouloir = new ComboBox<>();
        choixCouloir.getItems().addAll(
                "Couloir en haut",
                "Couloir au milieu",
                "Couloir en bas"
        );
        choixCouloir.setValue("Couloir en bas");
        choixCouloir.setStyle("-fx-font-size: 14px;");

        Pane dessin = creerDessinEtage(nbApparts, choixCouloir.getValue());

        VBox centre = new VBox(15, info, choixCouloir, dessin);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        choixCouloir.setOnAction(e -> {
            Pane nouveauDessin = creerDessinEtage(nbApparts, choixCouloir.getValue());
            centre.getChildren().set(2, nouveauDessin);
        });

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Plan étage");
        stage.setScene(scene);
        stage.show();
    }

    private Label creerLabelInfo(int nbApparts) {
        double surfaceCouloir = calculerSurfaceCouloir();
        double surfaceHabitable = calculerSurfaceHabitable();
        double surfaceParAppart = nbApparts > 0 ? surfaceHabitable / nbApparts : 0;

        Label info = new Label(
                nomEtage + " — " + nbApparts + " appartement(s)"
                        + " — Surface étage : " + String.format("%.2f", surfaceEtage) + " m²"
                        + " — Couloir : " + String.format("%.2f", surfaceCouloir) + " m²"
                        + " — Surface/appart : " + String.format("%.2f", surfaceParAppart) + " m²"
        );
        info.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F056B;");
        return info;
    }

    private Pane creerDessinEtage(int nbApparts, String positionCouloir) {

        Pane dessin = new Pane();
        dessin.setPrefSize(700, 360);
        dessin.setStyle("-fx-background-color: white;");

        if (nbApparts <= 0) {
            Text texte = new Text(240, 180, "Aucun appartement enregistré.");
            texte.setStyle("-fx-font-size: 18px;");
            dessin.getChildren().add(texte);
            return dessin;
        }

        double xDepart = 50;
        double yDepart = 30;

        double largeurTotale = 600;
        double hauteurTotale = 300;
        double hauteurCouloir = calculerHauteurCouloirDessin(hauteurTotale);

        if (positionCouloir.equals("Couloir en haut")) {

            dessinerCouloir(dessin, xDepart, yDepart, largeurTotale, hauteurCouloir);

            double hauteurApparts = hauteurTotale - hauteurCouloir;
            double largeurAppart = largeurTotale / nbApparts;

            for (int i = 0; i < nbApparts; i++) {
                double x = xDepart + i * largeurAppart;
                dessinerAppartement(
                        dessin,
                        x,
                        yDepart + hauteurCouloir,
                        largeurAppart,
                        hauteurApparts,
                        i + 1
                );
            }

        } else if (positionCouloir.equals("Couloir en bas")) {

            double hauteurApparts = hauteurTotale - hauteurCouloir;
            double largeurAppart = largeurTotale / nbApparts;

            for (int i = 0; i < nbApparts; i++) {
                double x = xDepart + i * largeurAppart;
                dessinerAppartement(
                        dessin,
                        x,
                        yDepart,
                        largeurAppart,
                        hauteurApparts,
                        i + 1
                );
            }

            dessinerCouloir(
                    dessin,
                    xDepart,
                    yDepart + hauteurApparts,
                    largeurTotale,
                    hauteurCouloir
            );

        } else if (positionCouloir.equals("Couloir au milieu")) {

            double hauteurAppart = (hauteurTotale - hauteurCouloir) / 2;

            if (nbApparts == 1) {

                dessinerAppartement(
                        dessin,
                        xDepart,
                        yDepart,
                        largeurTotale,
                        hauteurAppart,
                        1
                );

                dessinerCouloir(
                        dessin,
                        xDepart,
                        yDepart + hauteurAppart,
                        largeurTotale,
                        hauteurCouloir
                );

            } else if (nbApparts % 2 == 0) {

                int nbParCote = nbApparts / 2;
                double largeurAppart = largeurTotale / nbParCote;

                for (int i = 0; i < nbParCote; i++) {
                    double x = xDepart + i * largeurAppart;
                    dessinerAppartement(
                            dessin,
                            x,
                            yDepart,
                            largeurAppart,
                            hauteurAppart,
                            i + 1
                    );
                }

                dessinerCouloir(
                        dessin,
                        xDepart,
                        yDepart + hauteurAppart,
                        largeurTotale,
                        hauteurCouloir
                );

                for (int i = 0; i < nbParCote; i++) {
                    double x = xDepart + i * largeurAppart;
                    dessinerAppartement(
                            dessin,
                            x,
                            yDepart + hauteurAppart + hauteurCouloir,
                            largeurAppart,
                            hauteurAppart,
                            nbParCote + i + 1
                    );
                }

            } else {

                int nbParCote = (nbApparts - 1) / 2;

                double largeurBout = 140;
                double largeurCouloir = largeurTotale - largeurBout;
                double largeurAppart = largeurCouloir / nbParCote;

                for (int i = 0; i < nbParCote; i++) {
                    double x = xDepart + i * largeurAppart;
                    dessinerAppartement(
                            dessin,
                            x,
                            yDepart,
                            largeurAppart,
                            hauteurAppart,
                            i + 1
                    );
                }

                dessinerCouloir(
                        dessin,
                        xDepart,
                        yDepart + hauteurAppart,
                        largeurCouloir,
                        hauteurCouloir
                );

                for (int i = 0; i < nbParCote; i++) {
                    double x = xDepart + i * largeurAppart;
                    dessinerAppartement(
                            dessin,
                            x,
                            yDepart + hauteurAppart + hauteurCouloir,
                            largeurAppart,
                            hauteurAppart,
                            nbParCote + i + 1
                    );
                }

                dessinerAppartement(
                        dessin,
                        xDepart + largeurCouloir,
                        yDepart,
                        largeurBout,
                        hauteurTotale,
                        nbApparts
                );
            }
        }

        return dessin;
    }

    private double calculerSurfaceCouloir() {
        double largeurBatiment = batiment.getLargeur();
        double longueurBatiment = batiment.getLongueur();

        if (largeurBatiment <= 0 || longueurBatiment <= 0) {
            return 0;
        }

        double largeurCouloir = Math.min(LARGEUR_COULOIR_METRES, longueurBatiment);
        return largeurBatiment * largeurCouloir;
    }

    private double calculerSurfaceHabitable() {
        return Math.max(0, surfaceEtage - calculerSurfaceCouloir());
    }

    private double calculerHauteurCouloirDessin(double hauteurTotaleDessin) {
        double longueurBatiment = batiment.getLongueur();

        if (longueurBatiment <= 0) {
            return 0;
        }

        double proportion = LARGEUR_COULOIR_METRES / longueurBatiment;
        proportion = Math.max(0, Math.min(proportion, 1));

        return hauteurTotaleDessin * proportion;
    }

    private void dessinerAppartement(Pane dessin,
                                     double x,
                                     double y,
                                     double largeur,
                                     double hauteur,
                                     int numero) {

        Rectangle appart = new Rectangle(x, y, largeur, hauteur);
        appart.setFill(Color.web("#F8F8F8"));
        appart.setStroke(Color.BLACK);
        appart.setStrokeWidth(2);

        Text texte = new Text(x + 12, y + 25, "Appart " + numero);
        texte.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        dessin.getChildren().addAll(appart, texte);
    }

    private void dessinerCouloir(Pane dessin,
                                 double x,
                                 double y,
                                 double largeur,
                                 double hauteur) {

        Rectangle couloir = new Rectangle(x, y, largeur, hauteur);
        couloir.setFill(Color.web("#E8E8E8"));
        couloir.setStroke(Color.BLACK);
        couloir.setStrokeWidth(2);

        Text texte = new Text(
                x + largeur / 2 - 55,
                y + hauteur / 2 + 5,
                "Couloir 1,50 m"
        );
        texte.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        dessin.getChildren().addAll(couloir, texte);
    }
}
