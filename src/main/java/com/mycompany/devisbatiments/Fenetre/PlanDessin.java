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

    public PlanDessin(Batiments batiment) {

        this.batiment = batiment;

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

        double largeurPane =
                getWidth() > 0 ? getWidth() : getPrefWidth();

        double hauteurPane =
                getHeight() > 0 ? getHeight() : getPrefHeight();

        double marge = 40;

        double largeurBatiment =
                batiment.getLargeur();

        double longueurBatiment =
                batiment.getLongueur();

        if(largeurBatiment <= 0 || longueurBatiment <= 0) {
            return;
        }

        double zoneLargeur =
                largeurPane - 2 * marge;

        double zoneHauteur =
                hauteurPane - 2 * marge;

        double echelleX =
                zoneLargeur / largeurBatiment;

        double echelleY =
                zoneHauteur / longueurBatiment;

        double echelle =
                Math.min(echelleX, echelleY);

        double largeurDessinee =
                largeurBatiment * echelle;

        double longueurDessinee =
                longueurBatiment * echelle;

        double origineX =
                (largeurPane - largeurDessinee) / 2;

        double origineY =
                (hauteurPane - longueurDessinee) / 2;

        Rectangle surfaceTotale =
                new Rectangle(
                        origineX,
                        origineY,
                        largeurDessinee,
                        longueurDessinee
                );

        surfaceTotale.setFill(Color.TRANSPARENT);
        surfaceTotale.setStroke(Color.BLACK);
        surfaceTotale.setStrokeWidth(4);

        getChildren().add(surfaceTotale);

        Text titre =
                new Text(
                        origineX,
                        origineY - 10,
                        "Surface totale : "
                                + largeurBatiment
                                + " m x "
                                + longueurBatiment
                                + " m"
                );

        getChildren().add(titre);

        try(BufferedReader reader =
                    new BufferedReader(
                            new FileReader("PlanProjets.txt")
                    )) {

            String ligne;

            reader.readLine();

            while((ligne = reader.readLine()) != null) {

                if(ligne.trim().isEmpty()) {
                    continue;
                }

                String[] infos =
                        ligne.split(";");

                if(infos.length < 9) {
                    continue;
                }

                String idProjet =
                        infos[0].trim();

                if(!idProjet.equalsIgnoreCase(
                        batiment.getId()
                )) {
                    continue;
                }

                String nomPiece =
                        infos[2].trim();

                double x =
                        Double.parseDouble(infos[3]);

                double y =
                        Double.parseDouble(infos[4]);

                double largeur =
                        Double.parseDouble(infos[5]);

                double longueur =
                        Double.parseDouble(infos[6]);

                int idRevetement =
                        Integer.parseInt(infos[8]);

                Rectangle piece =
                        new Rectangle(
                                origineX + x * echelle,
                                origineY + y * echelle,
                                largeur * echelle,
                                longueur * echelle
                        );

                piece.setFill(
                        getCouleurDepuisCatalogue(idRevetement)
                );

                piece.setStroke(Color.BLACK);

                Text texte =
                        new Text(
                                origineX + x * echelle + 5,
                                origineY + y * echelle + 18,
                                nomPiece
                        );

                getChildren().addAll(piece, texte);
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    private Color getCouleurDepuisCatalogue(int idRevetement) {

        try(BufferedReader reader =
                    new BufferedReader(
                            new FileReader("CatalogueRevetements.txt")
                    )) {

            String ligne;

            reader.readLine();

            while((ligne = reader.readLine()) != null) {

                String[] infos =
                        ligne.split(";");

                if(infos.length < 7) {
                    continue;
                }

                int id =
                        Integer.parseInt(infos[0]);

                if(id == idRevetement) {

                    return Color.web(
                            infos[6].trim()
                    );
                }
            }

        } catch(Exception e) {

            e.printStackTrace();
        }

        return Color.LIGHTGRAY;
    }
}
