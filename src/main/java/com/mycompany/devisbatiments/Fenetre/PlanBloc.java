package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;

public class PlanBloc extends Pane {

    private final Batiments batiment;
    private final String vueEtage;
    private final String nomBloc;
    private final String vueInterne;

    private double largeurBloc = 1;
    private double longueurBloc = 1;

    public PlanBloc(Batiments batiment, String vueEtage, String nomBloc) {
        this.batiment = batiment;
        this.vueEtage = vueEtage;
        this.nomBloc = nomBloc;
        this.vueInterne = vueEtage.replace(" ", "") + "_" + nomBloc.replace(" ", "");

        chargerDimensionsBloc();
        initialiser();
    }

    private void initialiser() {
        setPrefSize(520, 360);
        setMinSize(420, 280);
        setMaxSize(520, 360);

        setStyle("-fx-background-color: white; "
                + "-fx-border-color: #0F056B; "
                + "-fx-border-width: 2;");

        widthProperty().addListener((obs, oldVal, newVal) -> actualiser());
        heightProperty().addListener((obs, oldVal, newVal) -> actualiser());

        actualiser();
    }

    private void chargerDimensionsBloc() {
        String[] bloc = SauvegardeProjet.chargerElementPlan(
                batiment.getId(),
                vueEtage,
                nomBloc
        );

        if (bloc != null && bloc.length >= 7) {
            largeurBloc = Double.parseDouble(bloc[5]);
            longueurBloc = Double.parseDouble(bloc[6]);
        }
    }

    public void actualiser() {
        getChildren().clear();

        double largeurPane = getWidth() > 0 ? getWidth() : getPrefWidth();
        double hauteurPane = getHeight() > 0 ? getHeight() : getPrefHeight();

        double marge = 35;
        double zoneLargeur = largeurPane - 2 * marge;
        double zoneHauteur = hauteurPane - 2 * marge;

        if (largeurBloc <= 0 || longueurBloc <= 0) {
            return;
        }

        double echelle = Math.min(zoneLargeur / largeurBloc, zoneHauteur / longueurBloc);

        double largeurDessinee = largeurBloc * echelle;
        double longueurDessinee = longueurBloc * echelle;

        double origineX = (largeurPane - largeurDessinee) / 2;
        double origineY = (hauteurPane - longueurDessinee) / 2;

        Rectangle contour = new Rectangle(
                origineX,
                origineY,
                largeurDessinee,
                longueurDessinee
        );

        contour.setFill(Color.web("#F7F7DC"));
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(4);

        Text titre = new Text(
                origineX,
                origineY - 10,
                nomBloc + " — " + String.format("%.2f", largeurBloc)
                        + " m x " + String.format("%.2f", longueurBloc) + " m"
        );

        getChildren().addAll(contour, titre);

        boolean vide = true;

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 9) {
                    continue;
                }

                if (!infos[0].trim().equalsIgnoreCase(batiment.getId())) {
                    continue;
                }

                if (!infos[1].trim().equalsIgnoreCase(vueInterne)) {
                    continue;
                }

                vide = false;

                String nomPiece = infos[2].trim();

                double x = Double.parseDouble(infos[3]);
                double y = Double.parseDouble(infos[4]);
                double largeur = Double.parseDouble(infos[5]);
                double longueur = Double.parseDouble(infos[6]);

                int idRevetement = Integer.parseInt(infos[8]);

                Rectangle piece = new Rectangle(
                        origineX + x * echelle,
                        origineY + y * echelle,
                        largeur * echelle,
                        longueur * echelle
                );

                piece.setFill(getCouleurDepuisCatalogue(idRevetement));
                piece.setStroke(Color.BLACK);

                Text texte = new Text(
                        origineX + x * echelle + 5,
                        origineY + y * echelle + 18,
                        nomPiece
                );

                getChildren().addAll(piece, texte);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (vide) {
            Text texteVide = new Text(
                    origineX + 15,
                    origineY + 30,
                    "Plan intérieur vide"
            );

            getChildren().add(texteVide);
        }
    }

    private Color getCouleurDepuisCatalogue(int idRevetement) {
        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {
            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                String[] infos = ligne.split(";");

                if (infos.length < 7) {
                    continue;
                }

                int id = Integer.parseInt(infos[0]);

                if (id == idRevetement) {
                    return Color.web(infos[6].trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Color.LIGHTGRAY;
    }
}