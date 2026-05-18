/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PlanVisualisation {

    private Pane zoneDessin;

    // =====================================
    // AFFICHER FENETRE
    // =====================================

    public void afficher() {

        Stage stage = new Stage();

        Label Projet = new Label("PROJET :");
        Projet.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");

        TextField champProjet = new TextField();

        champProjet.setPromptText("Ex : P1");

        Label Vue = new Label("VUE :");
        Vue.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;");

        TextField champVue = new TextField();
        champVue.setPromptText("Ex : ETAGE1 / RDC / FACE.."
        );

        

        Button btnAfficher = new Button("AFFICHER");
        btnAfficher.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");
       


        zoneDessin = new Pane();

        zoneDessin.setPrefSize(700, 550);

        zoneDessin.setStyle(
                "-fx-background-color: white;"
        );

        btnAfficher.setOnAction(e -> {

            String projet =
                    champProjet.getText();

            String vue =
                    champVue.getText();

            afficherPlan(
                    projet,
                    vue
            );
        });

        HBox topBar = new HBox(10);

        topBar.setPadding(
                new Insets(10)
        );

        topBar.getChildren().addAll(
                Projet,
                champProjet,
                Vue,
                champVue,
                btnAfficher
        );

        BorderPane root =
                new BorderPane();

        root.setTop(topBar);

        root.setCenter(zoneDessin);

        Scene scene =
                new Scene(root, 800, 550);

        stage.setScene(scene);

        stage.setTitle(
                "Visualisation des Plans"
        );

        stage.show();
    }

    // =====================================
    // RECUPERATION COULEUR
    // =====================================

    private Color getCouleurDepuisCatalogue(
            int idRevetement
    ) {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    "CatalogueRevetements.txt"
                            )
                    );

            String ligne;

            // ignorer en-tête
            reader.readLine();

            while (
                    (ligne = reader.readLine())
                            != null
            ) {

                if (
                        ligne.trim().isEmpty()
                ) {
                    continue;
                }

                String[] infos =
                        ligne.split(";");

                int id =
                        Integer.parseInt(
                                infos[0].trim()
                        );

                String couleur =
                        infos[6].trim();

                if (id == idRevetement) {

                    reader.close();

                    return Color.web(
                            couleur
                    );
                }
            }

            reader.close();

        }

        catch (Exception e) {

            e.printStackTrace();
        }

        return Color.WHITE;
    }

    // =====================================
    // AFFICHAGE PLAN
    // =====================================

    private void afficherPlan(
            String projetRecherche,
            String vueRecherche
    ) {

        zoneDessin.getChildren().clear();

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(
                                    "PlanProjets.txt"
                            )
                    );

            String ligne;

            reader.readLine();

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;

            int maxX = 0;
            int maxY = 0;

            while (
                    (ligne = reader.readLine())
                            != null
            ) {

                if (
                        ligne.trim().isEmpty()
                ) {
                    continue;
                }

                String[] infos =
                        ligne.split(";");

                if (infos.length < 8) {
                    continue;
                }

                String projet =
                        infos[0].trim();

                String vue =
                        infos[1].trim();

                String piece =
                        infos[2].trim();

                int x =
                        Integer.parseInt(
                                infos[3].trim()
                        );

                int y =
                        Integer.parseInt(
                                infos[4].trim()
                        );

                int largeur =
                        Integer.parseInt(
                                infos[5].trim()
                        );

                int hauteur =
                        Integer.parseInt(
                                infos[6].trim()
                        );

                int idRevetement =
                        Integer.parseInt(
                                infos[7].trim()
                        );

                if (
                        projet.equalsIgnoreCase(
                                projetRecherche.trim()
                        )
                        &&
                        vue.equalsIgnoreCase(
                                vueRecherche.trim()
                        )
                ) {

                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);

                    maxX = Math.max(
                            maxX,
                            x + largeur
                    );

                    maxY = Math.max(
                            maxY,
                            y + hauteur
                    );

                    Rectangle rect =
                            new Rectangle(
                                    x,
                                    y,
                                    largeur,
                                    hauteur
                            );

                    rect.setFill(
                            getCouleurDepuisCatalogue(
                                    idRevetement
                            )
                    );

                    rect.setStroke(
                            Color.BLACK
                    );

                    rect.setStrokeWidth(1);

                    zoneDessin
                            .getChildren()
                            .add(rect);

                    // =====================================
                    // TEXTE CENTRE
                    // =====================================

                    if (
                            !piece.equalsIgnoreCase(
                                    "Mur"
                            )
                            &&
                            !piece.equalsIgnoreCase(
                                    "Fenetre"
                            )
                            &&
                            !piece.equalsIgnoreCase(
                                    "Porte"
                            )
                            &&
                            !piece.equalsIgnoreCase(
                                    "LigneEtage"
                            )
                    ) {

                        Text texte =
                                new Text(piece);

                        texte.setStyle( "-fx-font-size: 14px;"
                                        +
                                        "-fx-font-weight: bold;"
                        );

                        texte.setX(
                                x
                                        + largeur / 2.0
                                        - piece.length() * 3
                        );

                        texte.setY(
                                y
                                        + hauteur / 2.0
                        );

                        zoneDessin
                                .getChildren()
                                .add(texte);
                    }
                }
            }

            // =====================================
            // CONTOUR EXTERIEUR
            // =====================================

            if (
                    minX != Integer.MAX_VALUE
            ) {

                Rectangle contour =
                        new Rectangle(
                                minX,
                                minY,
                                maxX - minX,
                                maxY - minY
                        );

                contour.setFill(
                        Color.TRANSPARENT
                );

                contour.setStroke(
                        Color.BLACK
                );

                contour.setStrokeWidth(6);

                zoneDessin
                        .getChildren()
                        .add(0, contour);
            }

            reader.close();
        }

        catch (IOException ex) {

            ex.printStackTrace();
        }
    }
}