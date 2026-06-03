package com.mycompany.devisbatiments.Fenetre;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;

public class PlanOuverture {

    private static final double PANE_W = FenetrePlacerOuverture.PANE_W;
    private static final double PANE_H = FenetrePlacerOuverture.PANE_H;
    private static final double MARGE = 40;

    private final FenetrePlacerOuverture vue;
    private final FenetrePlacerOuvertureController controller;

    public PlanOuverture(FenetrePlacerOuverture vue,
                                        FenetrePlacerOuvertureController controller) {
        this.vue = vue;
        this.controller = controller;
    }

    public void dessinerPlan() {
        Pane zoneDessin = vue.getZoneDessin();
        zoneDessin.getChildren().clear();

        double[] dims = controller.dimensionsEtage();
        double largeurEtage = dims[0];
        double longueurEtage = dims[1];

        if (largeurEtage <= 0 || longueurEtage <= 0) {
            zoneDessin.getChildren().add(new Text(20, 40, "Impossible de lire les dimensions de l'etage."));
            return;
        }

        double zoneL = PANE_W - 2 * MARGE;
        double zoneH = PANE_H - 2 * MARGE;
        double echelle = Math.min(zoneL / largeurEtage, zoneH / longueurEtage);

        double origX = (PANE_W - largeurEtage * echelle) / 2;
        double origY = (PANE_H - longueurEtage * echelle) / 2;

        Rectangle contourEtage = new Rectangle(origX, origY,
                largeurEtage * echelle, longueurEtage * echelle);
        contourEtage.setFill(Color.TRANSPARENT);
        contourEtage.setStroke(Color.BLACK);
        contourEtage.setStrokeWidth(3);
        zoneDessin.getChildren().add(contourEtage);

        dessinerGraduations(origX, origY, echelle, largeurEtage, longueurEtage);
        dessinerPiecesEtage(origX, origY, echelle);
        dessinerPieceCourante(origX, origY, echelle);
        dessinerOuvertures(origX, origY, echelle);

        Text legende = new Text(origX, origY + longueurEtage * echelle + 20,
                "H=Haut  B=Bas  G=Gauche  D=Droite     [piece surlignee = " + controller.getNomPiece() + "]");
        legende.setStyle("-fx-font-size: 11px;");
        zoneDessin.getChildren().add(legende);
    }

    private void dessinerGraduations(double origineX, double origineY, double echelle,
                                     double largeurProjet, double longueurProjet) {
        Pane zoneDessin = vue.getZoneDessin();

        for (int i = 0; i <= Math.floor(largeurProjet); i++) {
            double x = origineX + i * echelle;

            Line graduation = new Line(x, origineY - 6, x, origineY);
            graduation.setStroke(Color.BLACK);
            zoneDessin.getChildren().add(graduation);

            Text texte = new Text(x - 4, origineY - 10, i + "m");
            texte.setStyle("-fx-font-size: 10px;");
            zoneDessin.getChildren().add(texte);
        }

        for (int i = 0; i <= Math.floor(longueurProjet); i++) {
            double y = origineY + i * echelle;

            Line graduation = new Line(origineX - 6, y, origineX, y);
            graduation.setStroke(Color.BLACK);
            zoneDessin.getChildren().add(graduation);

            Text texte = new Text(origineX - 30, y + 4, i + "m");
            texte.setStyle("-fx-font-size: 10px;");
            zoneDessin.getChildren().add(texte);
        }
    }

    private void dessinerPiecesEtage(double origX, double origY, double echelle) {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(controller.getBatiment().getId())) continue;
                if (!controller.normaliser(p[1].trim()).equals(controller.normaliser(controller.getNomEtage()))) continue;

                double x = Double.parseDouble(p[3].trim());
                double y = Double.parseDouble(p[4].trim());
                double lx = Double.parseDouble(p[5].trim());
                double ly = Double.parseDouble(p[6].trim());
                int idRev = Integer.parseInt(p[8].trim());
                String nom = p[2].trim();

                Rectangle rect = new Rectangle(
                        origX + x * echelle, origY + y * echelle,
                        lx * echelle, ly * echelle);
                Color c = getCouleur(idRev);
                rect.setFill(new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.5));
                rect.setStroke(Color.DARKGRAY);
                rect.setStrokeWidth(1);
                vue.getZoneDessin().getChildren().add(rect);

                Text t = new Text(origX + x * echelle + 4, origY + y * echelle + 16, nom);
                t.setStyle("-fx-font-size: 11px;");
                vue.getZoneDessin().getChildren().add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dessinerPieceCourante(double origX, double origY, double echelle) {
        Rectangle surligne = new Rectangle(
                origX + controller.getPieceX() * echelle, origY + controller.getPieceY() * echelle,
                controller.getPieceLargeur() * echelle, controller.getPieceLongueur() * echelle);
        surligne.setFill(Color.TRANSPARENT);
        surligne.setStroke(Color.web("#0F056B"));
        surligne.setStrokeWidth(3.5);
        vue.getZoneDessin().getChildren().add(surligne);

        double cx = origX + (controller.getPieceX() + controller.getPieceLargeur() / 2) * echelle;
        addLabel(cx - 8, origY + controller.getPieceY() * echelle - 6, "H");
        addLabel(cx - 8, origY + (controller.getPieceY() + controller.getPieceLongueur()) * echelle + 12, "B");
        addLabel(origX + controller.getPieceX() * echelle - 14,
                origY + (controller.getPieceY() + controller.getPieceLongueur() / 2) * echelle + 4, "G");
        addLabel(origX + (controller.getPieceX() + controller.getPieceLargeur()) * echelle + 4,
                origY + (controller.getPieceY() + controller.getPieceLongueur() / 2) * echelle + 4, "D");
    }

    private void addLabel(double x, double y, String texte) {
        Text t = new Text(x, y, texte);
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        t.setFill(Color.web("#0F056B"));
        vue.getZoneDessin().getChildren().add(t);
    }

    private void dessinerOuvertures(double origX, double origY, double echelle) {
        double ep = 6;
        for (FenetrePlacerOuvertureController.FenetreItem f : controller.getFenetres()) {
            double[] seg = segmentMur(f.mur, f.x, 1.2);
            boolean horiz = controller.isMurHorizontal(f.mur);
            Rectangle r = new Rectangle(
                    origX + seg[0] * echelle - (horiz ? 0 : ep / 2),
                    origY + seg[1] * echelle - (horiz ? ep / 2 : 0),
                    horiz ? seg[2] * echelle : ep,
                    horiz ? ep : seg[2] * echelle);
            r.setFill(Color.LIGHTBLUE);
            r.setStroke(Color.BLUE);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(f.toString()));
            vue.getZoneDessin().getChildren().add(r);
        }

        for (FenetrePlacerOuvertureController.PorteItem p : controller.getPortes()) {
            double[] seg = segmentMur(p.mur, p.x, 0.9);
            boolean horiz = controller.isMurHorizontal(p.mur);
            Rectangle r = new Rectangle(
                    origX + seg[0] * echelle - (horiz ? 0 : ep / 2),
                    origY + seg[1] * echelle - (horiz ? ep / 2 : 0),
                    horiz ? seg[2] * echelle : ep,
                    horiz ? ep : seg[2] * echelle);
            r.setFill(Color.SANDYBROWN);
            r.setStroke(Color.SADDLEBROWN);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(p.toString()));
            vue.getZoneDessin().getChildren().add(r);
            dessinerArcPorte(origX, origY, echelle, p);
        }

        for (FenetrePlacerOuvertureController.TremieItem t : controller.getTremies()) {
            Rectangle r = new Rectangle(
                    origX + (controller.getPieceX() + t.x) * echelle,
                    origY + (controller.getPieceY() + t.y) * echelle,
                    t.largeur * echelle,
                    t.longueur * echelle);
            r.setFill(new Color(0.8, 0.8, 0.8, 0.7));
            r.setStroke(Color.DARKGRAY);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(t.toString()));
            vue.getZoneDessin().getChildren().add(r);

            Line d1 = new Line(
                    origX + (controller.getPieceX() + t.x) * echelle,
                    origY + (controller.getPieceY() + t.y) * echelle,
                    origX + (controller.getPieceX() + t.x + t.largeur) * echelle,
                    origY + (controller.getPieceY() + t.y + t.longueur) * echelle);
            Line d2 = new Line(
                    origX + (controller.getPieceX() + t.x + t.largeur) * echelle,
                    origY + (controller.getPieceY() + t.y) * echelle,
                    origX + (controller.getPieceX() + t.x) * echelle,
                    origY + (controller.getPieceY() + t.y + t.longueur) * echelle);
            d1.setStroke(Color.GRAY);
            d2.setStroke(Color.GRAY);
            vue.getZoneDessin().getChildren().addAll(d1, d2);
        }
    }

    private double[] segmentMur(FenetrePlacerOuvertureController.Mur mur, double offset, double ouv) {
        switch (mur) {
            case HAUT:
                return new double[]{controller.getPieceX() + offset, controller.getPieceY(), ouv};
            case BAS:
                return new double[]{controller.getPieceX() + offset, controller.getPieceY() + controller.getPieceLongueur(), ouv};
            case GAUCHE:
                return new double[]{controller.getPieceX(), controller.getPieceY() + offset, ouv};
            case DROITE:
                return new double[]{controller.getPieceX() + controller.getPieceLargeur(), controller.getPieceY() + offset, ouv};
            default:
                return new double[]{controller.getPieceX(), controller.getPieceY(), ouv};
        }
    }

    private void dessinerArcPorte(double origX, double origY, double echelle,
                                  FenetrePlacerOuvertureController.PorteItem p) {
        double[] seg = segmentMur(p.mur, p.x, 0.9);
        double px = origX + seg[0] * echelle;
        double py = origY + seg[1] * echelle;
        double taille = 0.9 * echelle;
        Line arc = new Line();
        arc.setStroke(Color.SADDLEBROWN);
        arc.setStrokeWidth(1);

        switch (p.mur) {
            case HAUT:
                arc.setStartX(px); arc.setStartY(py);
                arc.setEndX(px + taille); arc.setEndY(py + taille);
                break;
            case BAS:
                arc.setStartX(px); arc.setStartY(py);
                arc.setEndX(px + taille); arc.setEndY(py - taille);
                break;
            case GAUCHE:
                arc.setStartX(px); arc.setStartY(py);
                arc.setEndX(px + taille); arc.setEndY(py + taille);
                break;
            case DROITE:
                arc.setStartX(px); arc.setStartY(py);
                arc.setEndX(px - taille); arc.setEndY(py + taille);
                break;
            default:
                break;
        }
        vue.getZoneDessin().getChildren().add(arc);
    }

    private Color getCouleur(int idRevetement) {
        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                String[] p = ligne.split(";");
                if (p.length < 7) continue;
                if (Integer.parseInt(p[0].trim()) == idRevetement) {
                    return Color.web(p[6].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.LIGHTGRAY;
    }
}
