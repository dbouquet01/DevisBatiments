package com.mycompany.devisbatiments.Fenetre;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;

public class PlanVisualisation {

    private Pane zoneDessin;
    private String projetInitial;

    public PlanVisualisation() {
        this.projetInitial = "";
    }

    public PlanVisualisation(String projetInitial) {
        this.projetInitial = projetInitial;
    }

    public void afficher() {
        Stage stage = new Stage();

        Label lblProjet = new Label("PROJET :");
        lblProjet.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField champProjet = new TextField(projetInitial);
        champProjet.setPromptText("Ex : P1");

        Label lblVue = new Label("VUE :");
        lblVue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField champVue = new TextField();
        champVue.setPromptText("Ex : RDC / ETAGE1 / FACE");

        Button btnAfficher = new Button("AFFICHER");
        btnAfficher.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #0F056B;" +
                "-fx-text-fill: white;" +
                "-fx-cursor: hand;"
        );

        zoneDessin = new Pane();
        zoneDessin.setPrefSize(850, 600);
        zoneDessin.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #0F056B;" +
                "-fx-border-width: 2;"
        );

        btnAfficher.setOnAction(e -> afficherPlan(
                champProjet.getText().trim(),
                champVue.getText().trim()
        ));

        HBox topBar = new HBox(10, lblProjet, champProjet, lblVue, champVue, btnAfficher);
        topBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(zoneDessin);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Visualisation des Plans");
        stage.show();
    }

    public void afficherPlan(String projetRecherche, String vueRecherche) {
        zoneDessin.getChildren().clear();

        double[] dimensionsPlan = calculerDimensionsDepuisPlan(projetRecherche, vueRecherche);

        double largeurProjet = chercherLargeurProjet(projetRecherche);
        double longueurProjet = chercherLongueurProjet(projetRecherche);

        if (dimensionsPlan[0] > largeurProjet) {
            largeurProjet = dimensionsPlan[0];
        }

        if (dimensionsPlan[1] > longueurProjet) {
            longueurProjet = dimensionsPlan[1];
        }

        if (largeurProjet <= 0 || longueurProjet <= 0) {
            zoneDessin.getChildren().add(
                    new Text(40, 40, "Impossible de retrouver les dimensions du projet.")
            );
            return;
        }

        double marge = 50;
        double largeurZone = zoneDessin.getPrefWidth() - 2 * marge;
        double hauteurZone = zoneDessin.getPrefHeight() - 2 * marge;

        double echelleX = largeurZone / largeurProjet;
        double echelleY = hauteurZone / longueurProjet;
        double echelle = Math.min(echelleX, echelleY);

        double largeurDessinProjet = largeurProjet * echelle;
        double longueurDessinProjet = longueurProjet * echelle;

        double origineX = (zoneDessin.getPrefWidth() - largeurDessinProjet) / 2;
        double origineY = (zoneDessin.getPrefHeight() - longueurDessinProjet) / 2;

        Rectangle surfaceTotale = new Rectangle(
                origineX,
                origineY,
                largeurDessinProjet,
                longueurDessinProjet
        );

        surfaceTotale.setFill(Color.TRANSPARENT);
        surfaceTotale.setStroke(Color.BLACK);
        surfaceTotale.setStrokeWidth(4);

        zoneDessin.getChildren().add(surfaceTotale);

        Text titre = new Text(
                origineX,
                Math.max(25, origineY - 12),
                "Surface totale de l'appartement : "
                        + String.format("%.2f", largeurProjet)
                        + " x "
                        + String.format("%.2f", longueurProjet)
        );

        titre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        zoneDessin.getChildren().add(titre);

        boolean pieceTrouvee = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {

            String ligne;

            while ((ligne = reader.readLine()) != null) {

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                if (ligne.toLowerCase().startsWith("idprojet")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 9) {
                    continue;
                }

                String projet = infos[0].trim();
                String vue = infos[1].trim();
                String nomPiece = infos[2].trim();

                if (!projet.equalsIgnoreCase(projetRecherche.trim())
                        || !normaliserVue(vue).equals(normaliserVue(vueRecherche))) {
                    continue;
                }

                double x = Double.parseDouble(infos[3].trim());
                double y = Double.parseDouble(infos[4].trim());
                double largeur = Double.parseDouble(infos[5].trim());
                double longueur = Double.parseDouble(infos[6].trim());
                int idRevetement = Integer.parseInt(infos[8].trim());

                Rectangle rectPiece = new Rectangle(
                        origineX + x * echelle,
                        origineY + y * echelle,
                        largeur * echelle,
                        longueur * echelle
                );

                rectPiece.setFill(getCouleurDepuisCatalogue(idRevetement));
                rectPiece.setStroke(Color.BLACK);
                rectPiece.setStrokeWidth(1.5);

                Text textePiece = new Text(nomPiece);
                textePiece.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                textePiece.setX(origineX + x * echelle + 8);
                textePiece.setY(origineY + y * echelle + 22);

                zoneDessin.getChildren().addAll(rectPiece, textePiece);

                pieceTrouvee = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            zoneDessin.getChildren().add(
                    new Text(40, 70, "Erreur pendant la lecture de PlanProjets.txt.")
            );
        }

        if (!pieceTrouvee) {
            zoneDessin.getChildren().add(
                    new Text(
                            origineX + 20,
                            origineY + 40,
                            "Aucune pièce trouvée pour " + projetRecherche + " / " + vueRecherche
                    )
            );
        }
    }

    private double[] calculerDimensionsDepuisPlan(String projetRecherche, String vueRecherche) {
        double maxX = 0;
        double maxY = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {

            String ligne;

            while ((ligne = reader.readLine()) != null) {

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                if (ligne.toLowerCase().startsWith("idprojet")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 9) {
                    continue;
                }

                String projet = infos[0].trim();
                String vue = infos[1].trim();

                if (!projet.equalsIgnoreCase(projetRecherche.trim())
                        || !normaliserVue(vue).equals(normaliserVue(vueRecherche))) {
                    continue;
                }

                double x = Double.parseDouble(infos[3].trim());
                double y = Double.parseDouble(infos[4].trim());
                double largeur = Double.parseDouble(infos[5].trim());
                double longueur = Double.parseDouble(infos[6].trim());

                maxX = Math.max(maxX, x + largeur);
                maxY = Math.max(maxY, y + longueur);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[]{maxX, maxY};
    }

    private double chercherLargeurProjet(String idProjet) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {

            String ligne;

            while ((ligne = reader.readLine()) != null) {

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                if (ligne.toLowerCase().startsWith("idprojet")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 10) {
                    continue;
                }

                if (infos[0].trim().equalsIgnoreCase(idProjet.trim())) {
                    return Double.parseDouble(infos[8].trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private double chercherLongueurProjet(String idProjet) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {

            String ligne;

            while ((ligne = reader.readLine()) != null) {

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                if (ligne.toLowerCase().startsWith("idprojet")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 10) {
                    continue;
                }

                if (infos[0].trim().equalsIgnoreCase(idProjet.trim())) {
                    return Double.parseDouble(infos[9].trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Color getCouleurDepuisCatalogue(int idRevetement) {
        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {

            String ligne;

            while ((ligne = reader.readLine()) != null) {

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                if (ligne.toLowerCase().startsWith("id")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 7) {
                    continue;
                }

                int id = Integer.parseInt(infos[0].trim());

                if (id == idRevetement) {
                    return Color.web(infos[6].trim());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Color.LIGHTGRAY;
    }

    private String normaliserVue(String vue) {
        return vue
                .trim()
                .toUpperCase()
                .replace(" ", "")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E");
    }
}