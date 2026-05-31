/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FenetrePlacerOuverture {

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private double pieceX        = 0;
    private double pieceY        = 0;
    private double pieceLargeur  = 4;
    private double pieceLongueur = 3;
    private double pieceHauteur  = 2.5;
    private final List<Fenetre> fenetres = new ArrayList<>();
    private final List<Porte>   portes   = new ArrayList<>();
    private final List<Tremie>  tremies  = new ArrayList<>();
    private Pane zoneDessin;
    private static final double PANE_W = 700;
    private static final double PANE_H = 520;
    private static final double MARGE  = 40;

    public enum Mur { HAUT, DROITE, BAS, GAUCHE }

    private static class Fenetre {
        Mur mur;
        double x;
        double y;
        Fenetre(Mur mur, double x, double y) { this.mur = mur; this.x = x; this.y = y; }
        public String toString() {
            return "Fenetre - " + mur.name()
                    + "  x=" + String.format("%.2f", x)
                    + "  y=" + String.format("%.2f", y) + " m";
        }
    }

    private static class Porte {
        Mur mur;
        double x;
        Porte(Mur mur, double x) { this.mur = mur; this.x = x; }
        public String toString() {
            return "Porte - " + mur.name() + "  x=" + String.format("%.2f", x) + " m";
        }
    }

    private static class Tremie {
        double x, y, largeur, longueur;
        Tremie(double x, double y, double largeur, double longueur) {
            this.x = x; this.y = y; this.largeur = largeur; this.longueur = longueur;
        }
        public String toString() {
            return "Tremie  x=" + String.format("%.2f", x)
                    + "  y=" + String.format("%.2f", y)
                    + "  " + String.format("%.2f", largeur)
                    + "x" + String.format("%.2f", longueur) + " m";
        }
    }

    public FenetrePlacerOuverture(Batiments batiment, String nomEtage, String nomPiece) {
        this.batiment  = batiment;
        this.nomEtage  = nomEtage;
        this.nomPiece  = nomPiece;
        chargerDimensionsPiece();
        chargerOuverturesDepuisPlan(); 
    }

    public void afficher(Stage stage) {

        final String STYLE_BTN =
                "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        final String STYLE_ADD =
                "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        final String STYLE_DEL =
                "-fx-background-color: #B00020; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;";

        Label titre = new Label("PLACER LES OUVERTURES - " + nomPiece + " / " + nomEtage);
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label sousTitre = new Label(
                "Piece : " + String.format("%.2f", pieceLargeur)
                + " m (largeur) x " + String.format("%.2f", pieceLongueur)
                + " m (longueur) x " + String.format("%.2f", pieceHauteur) + " m (hauteur)"
        );
        sousTitre.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F056B;");

        VBox topBox = new VBox(5, titre, sousTitre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(18));

        zoneDessin = new Pane();
        zoneDessin.setPrefSize(PANE_W, PANE_H);
        zoneDessin.setStyle("-fx-background-color: white; -fx-border-color: #0F056B; -fx-border-width: 2;");
        dessinerPlan();

        VBox panneauGauche = new VBox(18);
        panneauGauche.setPadding(new Insets(10, 15, 10, 10));
        panneauGauche.setPrefWidth(420);

        ComboBox<String> comboMurFen = murCombo();
        TextField tfFenX = tf("distance x sur le mur (m)");
        TextField tfFenY = tf("hauteur depuis le sol (m)");
        ListView<String> listeFenetres = listeView();
        actualiserListe(listeFenetres, fenetres); // Affiche les fenêtres chargées

        Button btnAjouterFen = new Button("+ Ajouter fenetre");
        btnAjouterFen.setStyle(STYLE_ADD);
        btnAjouterFen.setOnAction(e -> {
            try {
                Mur mur  = murDepuis(comboMurFen.getValue());
                double x = parse(tfFenX);
                double y = parse(tfFenY);
                validerOffsetFenetre(mur, x, y);
                
                fenetres.add(new Fenetre(mur, x, y));
                sauvegarderFenetrePlan(mur, x, y);
                mettreAJourInfosPieceDepuisListes();

                actualiserListe(listeFenetres, fenetres);
                dessinerPlan();
            } catch (Exception ex) {
                alerte("Fenetre invalide", ex.getMessage());
            }
        });

        Button btnSupprimerFen = new Button("Supprimer");
        btnSupprimerFen.setStyle(STYLE_DEL);
        btnSupprimerFen.setOnAction(e -> {
            int idx = listeFenetres.getSelectionModel().getSelectedIndex();
            if (idx >= 0) { fenetres.remove(idx); mettreAJourInfosPieceDepuisListes(); actualiserListe(listeFenetres, fenetres); dessinerPlan(); }
        });

        VBox sectionFen = section("Fenetres",
                ligne("Mur :", comboMurFen),
                ligne("Distance du mur x (m) :", tfFenX),
                ligne("Hauteur y (m) :", tfFenY),
                new HBox(10, btnAjouterFen, btnSupprimerFen),
                listeFenetres
        );

        ComboBox<String> comboMurPor = murCombo();
        TextField tfPorX = tf("Distance x sur le mur (m)");
        ListView<String> listePortes = listeView();
        actualiserListe(listePortes, portes);

        Button btnAjouterPor = new Button("+ Ajouter porte");
        btnAjouterPor.setStyle(STYLE_ADD);
        btnAjouterPor.setOnAction(e -> {
            try {
                Mur mur  = murDepuis(comboMurPor.getValue());
                double x = parse(tfPorX);
                validerOffsetPorte(mur, x);
                
                portes.add(new Porte(mur, x));
                sauvegarderPortePlan(mur, x);
                mettreAJourInfosPieceDepuisListes();

                actualiserListe(listePortes, portes);
                dessinerPlan();
            } catch (Exception ex) {
                alerte("Porte invalide", ex.getMessage());
            }
        });

        Button btnSupprimerPor = new Button("Supprimer");
        btnSupprimerPor.setStyle(STYLE_DEL);
        btnSupprimerPor.setOnAction(e -> {
            int idx = listePortes.getSelectionModel().getSelectedIndex();
            if (idx >= 0) { portes.remove(idx); mettreAJourInfosPieceDepuisListes(); actualiserListe(listePortes, portes); dessinerPlan(); }
        });

        VBox sectionPor = section("Portes (touchent le sol)",
                ligne("Mur :", comboMurPor),
                ligne("Distance avec le mur (m) :", tfPorX),
                new HBox(10, btnAjouterPor, btnSupprimerPor),
                listePortes
        );

        TextField tfTrX  = tf("x depuis coin haut-gauche (m)");
        TextField tfTrY  = tf("y depuis coin haut-gauche (m)");
        TextField tfTrL  = tf("largeur (m)");
        TextField tfTrLo = tf("longueur (m)");
        ListView<String> listeTremies = listeView();
        actualiserListe(listeTremies, tremies);

        Button btnAjouterTr = new Button("+ Ajouter tremie");
        btnAjouterTr.setStyle(STYLE_ADD);
        btnAjouterTr.setOnAction(e -> {
            try {
                double x  = parse(tfTrX);
                double y  = parse(tfTrY);
                double l  = parse(tfTrL);
                double lo = parse(tfTrLo);
                validerTremie(x, y, l, lo);
                
                tremies.add(new Tremie(x, y, l, lo));
                sauvegarderTremiePlan(x, y, l, lo);
                mettreAJourInfosPieceDepuisListes();

                actualiserListe(listeTremies, tremies);
                dessinerPlan();
            } catch (Exception ex) {
                alerte("Tremie invalide", ex.getMessage());
            }
        });

        Button btnSupprimerTr = new Button("Supprimer");
        btnSupprimerTr.setStyle(STYLE_DEL);
        btnSupprimerTr.setOnAction(e -> {
            int idx = listeTremies.getSelectionModel().getSelectedIndex();
            if (idx >= 0) { tremies.remove(idx); mettreAJourInfosPieceDepuisListes(); actualiserListe(listeTremies, tremies); dessinerPlan(); }
        });

        VBox sectionTr = section("Tremies / Escaliers",
                ligne("x (m) :", tfTrX),
                ligne("y (m) :", tfTrY),
                ligne("Largeur (m) :", tfTrL),
                ligne("Longueur (m) :", tfTrLo),
                new HBox(10, btnAjouterTr, btnSupprimerTr),
                listeTremies
        );

        panneauGauche.getChildren().addAll(sectionFen, sectionPor, sectionTr);

        ScrollPane scrollGauche = new ScrollPane(panneauGauche);
        scrollGauche.setFitToWidth(true);
        scrollGauche.setStyle("-fx-background-color: transparent;");

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(STYLE_BTN);
        btnRetour.setOnAction(e -> {
            new FenetrePiece(
                batiment,
                nomEtage,
                nomPiece,
                pieceLargeur * pieceLongueur,
                new ArrayList<>()
            ).afficher(stage);
        });
        
        Button btnVoirPlan = new Button("VOIR LE PLAN");
        btnVoirPlan.setStyle(STYLE_BTN);

        btnVoirPlan.setOnAction(e -> {
            PlanVisualisation pv = new PlanVisualisation();
            pv.afficher();
        });
        
        Label aide = new Label(
                "Repere : (0, 0) = coin haut-gauche de la piece. "
                + "Murs HAUT/BAS : x = offset horizontal. "
                + "Murs GAUCHE/DROITE : x = offset vertical."
        );
        aide.setStyle("-fx-font-size: 11px; -fx-text-fill: grey;");
        aide.setWrapText(true);

        VBox bottomBox = new VBox(8, aide, btnRetour,btnVoirPlan);
        bottomBox.setPadding(new Insets(12, 20, 12, 20));

        HBox centre = new HBox(15, scrollGauche, zoneDessin);
        centre.setPadding(new Insets(10));
        HBox.setHgrow(zoneDessin, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);
        root.setStyle("-fx-background-color: #F5F5F5;");

        Scene scene = new Scene(root);
        stage.setTitle("Placer les ouvertures - " + nomPiece);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void dessinerPlan() {
        zoneDessin.getChildren().clear();

        double[] dims = dimensionsEtage();
        double largeurEtage  = dims[0];
        double longueurEtage = dims[1];

        if (largeurEtage <= 0 || longueurEtage <= 0) {
            zoneDessin.getChildren().add(new Text(20, 40, "Impossible de lire les dimensions de l'etage."));
            return;
        }

        double zoneL  = PANE_W - 2 * MARGE;
        double zoneH  = PANE_H - 2 * MARGE;
        double echelle = Math.min(zoneL / largeurEtage, zoneH / longueurEtage);

        double origX = (PANE_W - largeurEtage * echelle) / 2;
        double origY = (PANE_H - longueurEtage * echelle) / 2;

        Rectangle contourEtage = new Rectangle(origX, origY,
                largeurEtage * echelle, longueurEtage * echelle);
        contourEtage.setFill(Color.TRANSPARENT);
        contourEtage.setStroke(Color.BLACK);
        contourEtage.setStrokeWidth(3);
        zoneDessin.getChildren().add(contourEtage);

        dessinerPiecesEtage(origX, origY, echelle);
        dessinerPieceCourante(origX, origY, echelle);
        dessinerOuvertures(origX, origY, echelle);

        Text legende = new Text(origX, origY + longueurEtage * echelle + 20,
                "H=Haut  B=Bas  G=Gauche  D=Droite     [piece surlignée = " + nomPiece + "]");
        legende.setStyle("-fx-font-size: 11px;");
        zoneDessin.getChildren().add(legende);
    }

    private void dessinerPiecesEtage(double origX, double origY, double echelle) {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(nomEtage))) continue;

                double x  = Double.parseDouble(p[3].trim());
                double y  = Double.parseDouble(p[4].trim());
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
                zoneDessin.getChildren().add(rect);
                Text t = new Text(origX + x * echelle + 4, origY + y * echelle + 16, nom);
                t.setStyle("-fx-font-size: 11px;");
                zoneDessin.getChildren().add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dessinerPieceCourante(double origX, double origY, double echelle) {
        Rectangle surligné = new Rectangle(
                origX + pieceX * echelle, origY + pieceY * echelle,
                pieceLargeur * echelle, pieceLongueur * echelle);
        surligné.setFill(Color.TRANSPARENT);
        surligné.setStroke(Color.web("#0F056B"));
        surligné.setStrokeWidth(3.5);
        zoneDessin.getChildren().add(surligné);
        double cx = origX + (pieceX + pieceLargeur / 2) * echelle;
        addLabel(cx - 8,  origY + pieceY * echelle - 6, "H");
        addLabel(cx - 8,  origY + (pieceY + pieceLongueur) * echelle + 12, "B");
        addLabel(origX + pieceX * echelle - 14,
                 origY + (pieceY + pieceLongueur / 2) * echelle + 4, "G");
        addLabel(origX + (pieceX + pieceLargeur) * echelle + 4,
                 origY + (pieceY + pieceLongueur / 2) * echelle + 4, "D");
    }

    private void addLabel(double x, double y, String texte) {
        Text t = new Text(x, y, texte);
        t.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        t.setFill(Color.web("#0F056B"));
        zoneDessin.getChildren().add(t);
    }

    private void dessinerOuvertures(double origX, double origY, double echelle) {
        double ep = 6;
        for (Fenetre f : fenetres) {
            double[] seg = segmentMur(f.mur, f.x, 1.2);
            boolean horiz = isMurHorizontal(f.mur);
            Rectangle r = new Rectangle(
                    origX + seg[0] * echelle - (horiz ? 0 : ep / 2),
                    origY + seg[1] * echelle - (horiz ? ep / 2 : 0),
                    horiz ? seg[2] * echelle : ep,
                    horiz ? ep : seg[2] * echelle);
            r.setFill(Color.LIGHTBLUE);
            r.setStroke(Color.BLUE);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(f.toString()));
            zoneDessin.getChildren().add(r);
        }

        for (Porte p : portes) {
            double[] seg = segmentMur(p.mur, p.x, 0.9);
            boolean horiz = isMurHorizontal(p.mur);
            Rectangle r = new Rectangle(
                    origX + seg[0] * echelle - (horiz ? 0 : ep / 2),
                    origY + seg[1] * echelle - (horiz ? ep / 2 : 0),
                    horiz ? seg[2] * echelle : ep,
                    horiz ? ep : seg[2] * echelle);
            r.setFill(Color.SANDYBROWN);
            r.setStroke(Color.SADDLEBROWN);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(p.toString()));
            zoneDessin.getChildren().add(r);
            dessinerArcPorte(origX, origY, echelle, p);
        }

        for (Tremie t : tremies) {
            Rectangle r = new Rectangle(
                    origX + (pieceX + t.x) * echelle,
                    origY + (pieceY + t.y) * echelle,
                    t.largeur * echelle,
                    t.longueur * echelle);
            r.setFill(new Color(0.8, 0.8, 0.8, 0.7));
            r.setStroke(Color.DARKGRAY);
            r.setStrokeWidth(1.5);
            Tooltip.install(r, new Tooltip(t.toString()));
            zoneDessin.getChildren().add(r);

            Line d1 = new Line(
                    origX + (pieceX + t.x) * echelle,
                    origY + (pieceY + t.y) * echelle,
                    origX + (pieceX + t.x + t.largeur) * echelle,
                    origY + (pieceY + t.y + t.longueur) * echelle);
            Line d2 = new Line(
                    origX + (pieceX + t.x + t.largeur) * echelle,
                    origY + (pieceY + t.y) * echelle,
                    origX + (pieceX + t.x) * echelle,
                    origY + (pieceY + t.y + t.longueur) * echelle);
            d1.setStroke(Color.GRAY);
            d2.setStroke(Color.GRAY);
            zoneDessin.getChildren().addAll(d1, d2);
        }
    }

    private double[] segmentMur(Mur mur, double offset, double ouv) {
        switch (mur) {
            case HAUT:
                return new double[]{ pieceX + offset, pieceY, ouv };
            case BAS:
                return new double[]{ pieceX + offset, pieceY + pieceLongueur, ouv };
            case GAUCHE:
                return new double[]{ pieceX, pieceY + offset, ouv };
            case DROITE:
                return new double[]{ pieceX + pieceLargeur, pieceY + offset, ouv };
            default:
                return new double[]{ pieceX, pieceY, ouv };
        }
    }

    private boolean isMurHorizontal(Mur mur) {
        return mur == Mur.HAUT || mur == Mur.BAS;
    }

    private void dessinerArcPorte(double origX, double origY, double echelle, Porte p) {
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
        zoneDessin.getChildren().add(arc);
    }

    private void validerOffsetFenetre(Mur mur, double x, double y) {
        double longueurMur = isMurHorizontal(mur) ? pieceLargeur : pieceLongueur;
        if (x < 0 || x + 1.2 > longueurMur) {
            throw new IllegalArgumentException(
                    "La distance x=" + x + " m depasse le mur (" + longueurMur + " m).");
        }
        if (y < 0 || y + 1.0 > pieceHauteur) {
            throw new IllegalArgumentException(
                    "La hauteur y=" + y + " m depasse la hauteur de la piece (" + pieceHauteur + " m).");
        }
    }

    private void validerOffsetPorte(Mur mur, double x) {
        double longueurMur = isMurHorizontal(mur) ? pieceLargeur : pieceLongueur;
        if (x < 0 || x + 0.9 > longueurMur) {
            throw new IllegalArgumentException(
                    "La porte en " + x + " m depasse le mur (" + longueurMur + " m).");
        }
    }

    private void validerTremie(double x, double y, double l, double lo) {
        if (x < 0 || x + l > pieceLargeur)
            throw new IllegalArgumentException("La tremie depasse la largeur de la piece.");
        if (y < 0 || y + lo > pieceLongueur)
            throw new IllegalArgumentException("La tremie depasse la longueur de la piece.");
        if (l <= 0 || lo <= 0)
            throw new IllegalArgumentException("Les dimensions de la tremie doivent etre positives.");
    }

    private void chargerDimensionsPiece() {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(nomEtage))) continue;
                if (!p[2].trim().equalsIgnoreCase(nomPiece)) continue;
                pieceX        = Double.parseDouble(p[3].trim());
                pieceY        = Double.parseDouble(p[4].trim());
                pieceLargeur  = Double.parseDouble(p[5].trim());
                pieceLongueur = Double.parseDouble(p[6].trim());
                if (p.length > 7) {
                    try { pieceHauteur = Double.parseDouble(p[7].trim()); } catch (Exception ignored) {}
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double[] dimensionsEtage() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 10) continue;
                if (p[0].trim().equalsIgnoreCase(batiment.getId())) {
                    return new double[]{
                            Double.parseDouble(p[8].trim()),
                            Double.parseDouble(p[9].trim())
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0, 0};
    }
    
    private void chargerOuverturesDepuisPlan() {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(nomEtage))) continue;
                String nomElement = p[2].trim();
                String n = normaliser(nomElement);
                String prefixe = normaliser(nomPiece + "_");
                if (!n.startsWith(prefixe)) continue;
                double x = Double.parseDouble(p[3].trim().replace(",", "."));
                double y = Double.parseDouble(p[4].trim().replace(",", "."));
                double largeur = Double.parseDouble(p[5].trim().replace(",", "."));
                double longueur = Double.parseDouble(p[6].trim().replace(",", "."));
                if (n.contains("fenetre")) {
                    Mur mur = retrouverMurDepuisPlan(x, y, largeur, longueur);
                    fenetres.add(new Fenetre(mur, retrouverOffsetDepuisPlan(mur, x, y), 0));
                } else if (n.contains("porte")) {
                    Mur mur = retrouverMurDepuisPlan(x, y, largeur, longueur);
                    portes.add(new Porte(mur, retrouverOffsetDepuisPlan(mur, x, y)));
                } else if (n.contains("tremie")) {
                    tremies.add(new Tremie(x - pieceX, y - pieceY, largeur, longueur));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Mur retrouverMurDepuisPlan(double x, double y, double largeur, double longueur) {
        double eps = 0.20;
        if (Math.abs(y - pieceY) < eps) return Mur.HAUT;
        if (Math.abs(y - (pieceY + pieceLongueur)) < eps) return Mur.BAS;
        if (Math.abs(x - pieceX) < eps) return Mur.GAUCHE;
        if (Math.abs(x - (pieceX + pieceLargeur)) < eps) return Mur.DROITE;
        return largeur >= longueur ? Mur.HAUT : Mur.GAUCHE;
    }

    private double retrouverOffsetDepuisPlan(Mur mur, double x, double y) {
        switch (mur) {
            case HAUT:
            case BAS:
                return x - pieceX;
            case GAUCHE:
            case DROITE:
                return y - pieceY;
            default:
                return 0;
        }
    }

    private void sauvegarderFenetrePlan(Mur mur, double offset, double hauteurDepuisSol) {
        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Fenetre" + fenetres.size(),
                pieceX + calculerXPlan(mur, offset),
                pieceY + calculerYPlan(mur, offset),
                isMurHorizontal(mur) ? 1.2 : 0.10,
                isMurHorizontal(mur) ? 0.10 : 1.2,
                hauteurDepuisSol,
                10
        );
    }

    private void sauvegarderPortePlan(Mur mur, double offset) {
        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Porte" + portes.size(),
                pieceX + calculerXPlan(mur, offset),
                pieceY + calculerYPlan(mur, offset),
                isMurHorizontal(mur) ? 0.9 : 0.10,
                isMurHorizontal(mur) ? 0.10 : 0.9,
                2.1,
                13
        );
    }

    private void sauvegarderTremiePlan(double x, double y, double largeur, double longueur) {
        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Tremie" + tremies.size(),
                pieceX + x,
                pieceY + y,
                largeur,
                longueur,
                0,
                14
        );
    }

    private void mettreAJourInfosPieceDepuisListes() {
        double largeurTremie = 0;
        double longueurTremie = 0;
        if (!tremies.isEmpty()) {
            Tremie derniere = tremies.get(tremies.size() - 1);
            largeurTremie = derniere.largeur;
            longueurTremie = derniere.longueur;
        }
        SauvegardeProjet.mettreAJourInfosOuverturesPiece(
                batiment.getId(),
                nomEtage,
                nomPiece,
                fenetres.size(),
                portes.size(),
                tremies.size(),
                largeurTremie,
                longueurTremie
        );
    }

    private double calculerXPlan(Mur mur, double offset) {
        switch (mur) {
            case HAUT:
            case BAS:
                return offset;
            case GAUCHE:
                return 0;
            case DROITE:
                return pieceLargeur;
            default:
                return 0;
        }
    }

    private double calculerYPlan(Mur mur, double offset) {
        switch (mur) {
            case HAUT:
                return 0;
            case BAS:
                return pieceLongueur;
            case GAUCHE:
            case DROITE:
                return offset;
            default:
                return 0;
        }
    }

    private ComboBox<String> murCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll("HAUT", "DROITE", "BAS", "GAUCHE");
        cb.setValue("HAUT");
        cb.setPrefWidth(140);
        return cb;
    }

    private TextField tf(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(160);
        return f;
    }

    private ListView<String> listeView() {
        ListView<String> lv = new ListView<>();
        lv.setPrefHeight(80);
        return lv;
    }

    private <T> void actualiserListe(ListView<String> lv, List<T> items) {
        lv.getItems().clear();
        for (T it : items) {
            lv.getItems().add(it.toString());
        }
    }

    private HBox ligne(String label, javafx.scene.Node champ) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setStyle("-fx-font-size: 13px;");
        HBox hb = new HBox(10, lbl, champ);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    private VBox section(String titreSection, javafx.scene.Node... noeuds) {
        Label lbl = new Label(titreSection);
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox box = new VBox(8);
        box.getChildren().add(lbl);
        for (javafx.scene.Node n : noeuds) {
            box.getChildren().add(n);
        }
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: white; -fx-border-color: #0F056B; -fx-border-width: 1;");
        return box;
    }

    private Mur murDepuis(String val) {
        switch (val.toUpperCase()) {
            case "HAUT":   return Mur.HAUT;
            case "BAS":    return Mur.BAS;
            case "GAUCHE": return Mur.GAUCHE;
            case "DROITE": return Mur.DROITE;
            default: throw new IllegalArgumentException("Mur inconnu : " + val);
        }
    }

    private double parse(TextField f) {
        String txt = f.getText().trim().replace(",", ".");
        if (txt.isEmpty()) throw new IllegalArgumentException("Un champ est vide.");
        return Double.parseDouble(txt);
    }

    private void alerte(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.setContentText(msg);
        a.showAndWait();
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

    private String normaliser(String s) {
        if (s == null) return "";
        return s.trim().toUpperCase()
                .replace("\u00c9", "E").replace("\u00c8", "E").replace("\u00ca", "E")
                .replace("\u00c0", "A").replace("\u00c2", "A")
                .replace(" ", "");
    }
}