package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;

public class PlanDessin extends Pane {

    private final Batiments batiment;
    private final String vueFiltre;
    private final double largeurAffichee;
    private final double longueurAffichee;

    // Maison / étage entier
    public PlanDessin(Batiments batiment, String vueFiltre) {
        this.batiment = batiment;
        this.vueFiltre = vueFiltre;
        this.largeurAffichee = batiment.getLargeur();
        this.longueurAffichee = batiment.getLongueur();

        initialiser();
    }

    // Appartement individuel
    public PlanDessin(Batiments batiment, String vueFiltre,
                      double largeurAffichee, double longueurAffichee) {
        this.batiment = batiment;
        this.vueFiltre = vueFiltre;
        this.largeurAffichee = largeurAffichee;
        this.longueurAffichee = longueurAffichee;

        initialiser();
    }

    private void initialiser() {
        setPrefSize(650, 600);

        setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #0F056B;" +
                "-fx-border-width: 2;"
        );

        widthProperty().addListener((obs, oldVal, newVal) -> actualiser());
        heightProperty().addListener((obs, oldVal, newVal) -> actualiser());

        actualiser();
    }

    public void actualiser() {

        getChildren().clear();

        double largeurPane = getWidth() > 0 ? getWidth() : getPrefWidth();
        double hauteurPane = getHeight() > 0 ? getHeight() : getPrefHeight();

        double marge = 70;

        double largeurPlan = largeurAffichee;
        double longueurPlan = longueurAffichee;

        if (largeurPlan <= 0 || longueurPlan <= 0) {
            return;
        }

        double zoneLargeur = largeurPane - 2 * marge;
        double zoneHauteur = hauteurPane - 2 * marge;

        double echelleX = zoneLargeur / largeurPlan;
        double echelleY = zoneHauteur / longueurPlan;
        double echelle = Math.min(echelleX, echelleY);

        double largeurDessinee = largeurPlan * echelle;
        double longueurDessinee = longueurPlan * echelle;

        double origineX = (largeurPane - largeurDessinee) / 2;
        double origineY = (hauteurPane - longueurDessinee) / 2;

        Rectangle surfaceTotale = new Rectangle(
                origineX,
                origineY,
                largeurDessinee,
                longueurDessinee
        );

        surfaceTotale.setFill(Color.TRANSPARENT);
        surfaceTotale.setStroke(Color.BLACK);
        surfaceTotale.setStrokeWidth(4);

        getChildren().add(surfaceTotale);

        Text titre = new Text(
                origineX,
                origineY - 40,
                "Surface : " + String.format("%.2f", largeurPlan)
                        + " m x " + String.format("%.2f", longueurPlan) + " m"
        );

        getChildren().add(titre);
        
        dessinerGraduations(
        origineX,
        origineY,
        echelle,
        largeurPlan,
        longueurPlan
);

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

                String idProjet = infos[0].trim();
                String vue = infos[1].trim();

                if (!idProjet.equalsIgnoreCase(batiment.getId())) {
                    continue;
                }

                if (vueFiltre != null && !vue.equalsIgnoreCase(vueFiltre)) {
                    continue;
                }

                String nomPiece = infos[2].trim();

                double x = Double.parseDouble(infos[3]);
                double y = Double.parseDouble(infos[4]);
                double largeur = Double.parseDouble(infos[5]);
                double longueur = Double.parseDouble(infos[6]);

                int idRevetement = Integer.parseInt(infos[8]);

                String nom = nomPiece.toLowerCase()
                    .replace("é", "e")
                    .replace("è", "e")
                    .replace("ê", "e");

                double posX = origineX + x * echelle;
                double posY = origineY + y * echelle;

                double largeurAffichage = largeur * echelle;
                double longueurAffichage = longueur * echelle;

                if (nom.contains("fenetre")) {
                    double epaisseur = 4;

                    if (largeur >= longueur) {
                        longueurAffichage = epaisseur;
                    } else {
                        largeurAffichage = epaisseur;
                    }
                }

                Rectangle piece = new Rectangle(
                        posX,
                        posY,
                        largeurAffichage,
                        longueurAffichage
                );

                if (nom.contains("fenetre")) {
                    piece.setFill(Color.LIGHTGRAY);
                    piece.setStroke(Color.DARKGRAY);
                    piece.setStrokeWidth(1);
                } else {
                    piece.setFill(getCouleurDepuisCatalogue(idRevetement));
                    piece.setStroke(Color.BLACK);
                }

                Text texte = new Text(nomPiece);
                texte.setStyle("-fx-font-size: 11px;");

                double centreX = origineX + x * echelle + (largeur * echelle) / 2;
                double centreY = origineY + y * echelle + (longueur * echelle) / 2;

                texte.setX(centreX - nomPiece.length() * 3);
                texte.setY(centreY);

                getChildren().add(piece);

                if (!nom.contains("fenetre") && !nom.contains("porte")) {
                    getChildren().add(texte);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void dessinerGraduations(double origineX, double origineY,
                                 double echelle,
                                 double largeurPlan,
                                 double longueurPlan) {

        for (int i = 0; i <= (int) largeurPlan; i += 2) {

            double x = origineX + i * echelle;

            javafx.scene.shape.Line trait =
                    new javafx.scene.shape.Line(
                            x, origineY - 8,
                            x, origineY
                    );

            Text texte = new Text(
                    x - 8,
                    origineY - 15,
                    i + "m"
            );

            texte.setStyle("-fx-font-size: 10px;");

            getChildren().addAll(trait, texte);
        }

        for (int i = 0; i <= (int) longueurPlan; i += 2) {

            double y = origineY + i * echelle;

            javafx.scene.shape.Line trait =
                    new javafx.scene.shape.Line(
                            origineX - 8, y,
                            origineX, y
                    );

            Text texte = new Text(
                    origineX - 35,
                    y + 4,
                    i + "m"
            );

            texte.setStyle("-fx-font-size: 10px;");

            getChildren().addAll(trait, texte);
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