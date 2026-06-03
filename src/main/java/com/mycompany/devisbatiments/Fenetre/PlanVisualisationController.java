package com.mycompany.devisbatiments.Fenetre;

import java.io.BufferedReader;
import java.io.FileReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class PlanVisualisationController {

    private final PlanVisualisation vue;

    public PlanVisualisationController(PlanVisualisation vue) {
        this.vue = vue;
    }

    public void afficherPlan(String projetRecherche, String vueRecherche) {
        Pane zoneDessin = vue.getZoneDessin();
        zoneDessin.getChildren().clear();

        double largeurProjet = chercherLargeurProjet(projetRecherche);
        double longueurProjet = chercherLongueurProjet(projetRecherche);

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

        dessinerGraduations(origineX, origineY, echelle, largeurProjet, longueurProjet);
        dessinerTitreDimensions(origineX, origineY, largeurProjet, longueurProjet, vueRecherche);
        dessinerPieces(projetRecherche, vueRecherche, origineX, origineY, echelle);
    }

    private void dessinerGraduations(double origineX, double origineY, double echelle,
                                     double largeurProjet, double longueurProjet) {
        Pane zoneDessin = vue.getZoneDessin();

        for (int i = 0; i <= largeurProjet; i++) {
            double x = origineX + i * echelle;

            zoneDessin.getChildren().add(new Line(
                    x, origineY - 6,
                    x, origineY
            ));

            Text t = new Text(x - 4, origineY - 10, i + "m");
            t.setStyle("-fx-font-size: 10px;");
            zoneDessin.getChildren().add(t);
        }

        for (int i = 0; i <= longueurProjet; i++) {
            double y = origineY + i * echelle;

            zoneDessin.getChildren().add(new Line(
                    origineX - 6, y,
                    origineX, y
            ));

            Text t = new Text(origineX - 30, y + 4, i + "m");
            t.setStyle("-fx-font-size: 10px;");
            zoneDessin.getChildren().add(t);
        }
    }

    private void dessinerTitreDimensions(double origineX, double origineY,
                                         double largeurProjet, double longueurProjet,
                                         String vueRecherche) {
        String vueNorm = normaliserVue(vueRecherche);
        String texteTitre;

        if (vueNorm.equals("FACE") || vueNorm.equals("ARRIERE")) {
            texteTitre = "Dimensions : "
                    + String.format("%.2f", largeurProjet)
                    + " m × "
                    + String.format("%.2f", longueurProjet)
                    + " m";
        } else if (vueNorm.equals("GAUCHE") || vueNorm.equals("DROITE")) {
            texteTitre = "Dimensions : "
                    + String.format("%.2f", longueurProjet)
                    + " m × "
                    + String.format("%.2f", largeurProjet)
                    + " m";
        } else {
            texteTitre = "Surface : "
                    + String.format("%.2f", largeurProjet)
                    + " m × "
                    + String.format("%.2f", longueurProjet)
                    + " m";
        }

        Text titre = new Text(
                origineX,
                Math.max(25, origineY - 37),
                texteTitre
        );

        titre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        vue.getZoneDessin().getChildren().add(titre);
    }

    private void dessinerPieces(String projetRecherche, String vueRecherche,
                                double origineX, double origineY, double echelle) {
        Pane zoneDessin = vue.getZoneDessin();
        boolean pieceTrouvee = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty() || ligne.toLowerCase().startsWith("idprojet")) {
                    continue;
                }

                String[] infos = ligne.split(";");

                if (infos.length < 9) {
                    continue;
                }

                String projet = infos[0].trim();
                String vuePlan = infos[1].trim();
                String nomPiece = infos[2].trim();

                if (!projet.equalsIgnoreCase(projetRecherche.trim())
                        || !normaliserVue(vuePlan).equals(normaliserVue(vueRecherche))) {
                    continue;
                }

                double x = Double.parseDouble(infos[3].trim());
                double y = Double.parseDouble(infos[4].trim());
                double largeur = Double.parseDouble(infos[5].trim());
                double longueur = Double.parseDouble(infos[6].trim());
                int idRevetement = Integer.parseInt(infos[8].trim());

                dessinerElementPlan(nomPiece, x, y, largeur, longueur, idRevetement,
                        origineX, origineY, echelle);
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

    private void dessinerElementPlan(String nomPiece, double x, double y,
                                     double largeur, double longueur, int idRevetement,
                                     double origineX, double origineY, double echelle) {
        Pane zoneDessin = vue.getZoneDessin();

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

        Rectangle rectPiece = new Rectangle(
                posX,
                posY,
                largeurAffichage,
                longueurAffichage
        );

        rectPiece.setFill(getCouleurDepuisCatalogue(idRevetement));
        rectPiece.setStroke(Color.BLACK);
        rectPiece.setStrokeWidth(1.5);

        Text textePiece = new Text(nomPiece);
        textePiece.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        double centreX = origineX + x * echelle + (largeur * echelle) / 2;
        double centreY = origineY + y * echelle + (longueur * echelle) / 2;

        textePiece.setX(centreX - nomPiece.length() * 3);
        textePiece.setY(centreY);

        zoneDessin.getChildren().add(rectPiece);

        if (!nom.contains("fenetre") && !nom.contains("porte")) {
            zoneDessin.getChildren().add(textePiece);
        }
    }

    private double chercherLargeurProjet(String idProjet) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {
            String ligne;

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty() || ligne.toLowerCase().startsWith("idprojet")) {
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
                if (ligne.trim().isEmpty() || ligne.toLowerCase().startsWith("idprojet")) {
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
                if (ligne.trim().isEmpty() || ligne.toLowerCase().startsWith("id")) {
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
