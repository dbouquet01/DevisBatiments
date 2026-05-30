/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;
import com.mycompany.devisbatiments.elements.Piece;
import com.mycompany.devisbatiments.elements.Revetement;
import com.mycompany.devisbatiments.elements.Tremie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FenetrePiece {

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private final double surfacePiece;
    private final ArrayList<String> nomsPieces;

    public FenetrePiece(Batiments batiment, String nomEtage,
                        String nomPiece, double surfacePiece,
                        ArrayList<String> nomsPieces) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.surfacePiece = surfacePiece;
        this.nomsPieces = nomsPieces == null ? new ArrayList<>() : nomsPieces;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 24; -fx-cursor: hand;";

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F5;");

        Label titre = new Label("CONFIGURATION PIÈCE — " + nomPiece + " / " + nomEtage);
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        HBox top = new HBox(titre);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        root.setTop(top);

        TextField fieldX = new TextField();
        TextField fieldY = new TextField();
        TextField fieldLargeur = new TextField();
        TextField fieldLongueur = new TextField();
        TextField fieldHauteur = new TextField();

        TextField fieldNbFenetre = new TextField("0");
        TextField fieldNbPorte = new TextField("0");

        TextField fieldNbTremie = new TextField("0");
        TextField fieldLargeurTremie = new TextField("1.0");
        TextField fieldLongueurTremie = new TextField("2.5");

        ComboBox<Revetement> comboMur = new ComboBox<>();
        ComboBox<Revetement> comboSol = new ComboBox<>();
        ComboBox<Revetement> comboPlafond = new ComboBox<>();
        ComboBox<Revetement> comboTremie = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());
        comboTremie.getItems().addAll(Revetement.getRevetementsSol());

        if (!comboMur.getItems().isEmpty()) comboMur.setValue(comboMur.getItems().get(0));
        if (!comboSol.getItems().isEmpty()) comboSol.setValue(comboSol.getItems().get(0));
        if (!comboPlafond.getItems().isEmpty()) comboPlafond.setValue(comboPlafond.getItems().get(0));
        if (!comboTremie.getItems().isEmpty()) comboTremie.setValue(comboTremie.getItems().get(0));

        chargerDonnees(fieldX, fieldY, fieldLargeur, fieldLongueur, fieldHauteur,
                comboMur, comboSol, comboPlafond);

        VBox boxDim = creerBox("1. Dimensions / position",
                ligne("X origine :", fieldX, 140, 220),
                ligne("Y origine :", fieldY, 140, 220),
                ligne("Largeur (m) :", fieldLargeur, 140, 220),
                ligne("Longueur (m) :", fieldLongueur, 140, 220),
                ligne("Hauteur (m) :", fieldHauteur, 140, 220)
        );

        VBox boxOuv = creerBox("2. Ouvertures / escalier",
                ligne("Fenêtres :", fieldNbFenetre, 160, 180),
                ligne("Portes :", fieldNbPorte, 160, 180),
                ligne("Nombre escaliers :", fieldNbTremie, 160, 180),
                ligne("Largeur escalier :", fieldLargeurTremie, 160, 180),
                ligne("Longueur escalier :", fieldLongueurTremie, 160, 180)
        );

        VBox boxRev = creerBox("3. Revêtements",
                ligne("Murs :", comboMur, 140, 260),
                ligne("Sol :", comboSol, 140, 260),
                ligne("Plafond :", comboPlafond, 140, 260),
                ligne("Escalier :", comboTremie, 140, 260)
        );

        Label lblSurfaceMur = new Label("Surface murs : -");
        Label lblSurfaceSol = new Label("Surface sol : -");
        Label lblSurfacePlafond = new Label("Surface plafond : -");
        Label lblSurfaceTremie = new Label("Surface escalier : -");

        Label lblPrixMur = new Label("Prix murs : -");
        Label lblPrixSol = new Label("Prix sol : -");
        Label lblPrixPlafond = new Label("Prix plafond : -");
        Label lblPrixTremie = new Label("Prix escalier : -");
        Label lblPrixTotal = new Label("TOTAL : -");

        VBox resultatsCalcul = new VBox(8,
                lblSurfaceMur,
                lblSurfaceSol,
                lblSurfacePlafond,
                lblSurfaceTremie,
                lblPrixMur,
                lblPrixSol,
                lblPrixPlafond,
                lblPrixTremie,
                lblPrixTotal
        );

        VBox boxCalcul = creerBox("4. Calcul", resultatsCalcul);

        Label lblMessage = new Label("");
        lblMessage.setStyle("-fx-font-size: 13px;");

        HBox ligneHaut = new HBox(15, boxDim, boxOuv);
        HBox ligneBas = new HBox(15, boxRev, boxCalcul);

        HBox.setHgrow(boxDim, Priority.ALWAYS);
        HBox.setHgrow(boxOuv, Priority.ALWAYS);
        HBox.setHgrow(boxRev, Priority.ALWAYS);
        HBox.setHgrow(boxCalcul, Priority.ALWAYS);

        VBox panneauGauche = new VBox(12, ligneHaut, ligneBas, lblMessage);
        panneauGauche.setPadding(new Insets(0, 15, 0, 25));
        panneauGauche.setMaxWidth(Double.MAX_VALUE);

        Node plan = creerPlan();
        if (plan instanceof Region) {
            ((Region) plan).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        Label titrePlan = new Label(estVueAppartement()
                ? "Plan de l'appartement"
                : (batiment instanceof Maison ? "Plan de l'étage" : "Plan du bloc"));
        VBox panneauDroit = new VBox(8, titrePlan, plan);
        panneauDroit.setAlignment(Pos.TOP_CENTER);
        panneauDroit.setPadding(new Insets(0, 25, 0, 15));
        panneauDroit.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(plan, Priority.ALWAYS);

        HBox centre = new HBox(10, panneauGauche, panneauDroit);
        centre.setPadding(new Insets(0, 10, 0, 10));

        HBox.setHgrow(panneauGauche, Priority.ALWAYS);
        HBox.setHgrow(panneauDroit, Priority.ALWAYS);

        panneauGauche.prefWidthProperty().bind(centre.widthProperty().multiply(0.55));
        panneauDroit.prefWidthProperty().bind(centre.widthProperty().multiply(0.45));

        root.setCenter(centre);

        Button btnRetour = new Button("RETOUR");
        Button btnCalculer = new Button("CALCULER");
        Button btnEnregistrer = new Button("ENREGISTRER");
        Button btnMenu = new Button("MENU PRINCIPAL");

        btnRetour.setStyle(styleBouton);
        btnCalculer.setStyle(styleBouton);
        btnEnregistrer.setStyle(styleBouton);
        btnMenu.setStyle(styleBouton);

        HBox bottom = new HBox(20, btnRetour, btnCalculer, btnEnregistrer, btnMenu);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(10, 30, 10, 30));
        bottom.setMinHeight(58);
        bottom.setPrefHeight(58);
        bottom.setStyle("-fx-background-color: #F5F5F5;");
        root.setBottom(bottom);

        btnCalculer.setOnAction(e -> calculer(
                fieldX, fieldY, fieldLargeur, fieldLongueur, fieldHauteur,
                fieldNbFenetre, fieldNbPorte,
                fieldNbTremie, fieldLargeurTremie, fieldLongueurTremie,
                comboMur, comboSol, comboPlafond, comboTremie,
                lblSurfaceMur, lblSurfaceSol, lblSurfacePlafond, lblSurfaceTremie,
                lblPrixMur, lblPrixSol, lblPrixPlafond, lblPrixTremie, lblPrixTotal,
                lblMessage
        ));

        btnEnregistrer.setOnAction(e -> {
            enregistrer(
                    fieldX, fieldY, fieldLargeur, fieldLongueur, fieldHauteur,
                    fieldNbFenetre, fieldNbPorte,
                    fieldNbTremie, fieldLargeurTremie, fieldLongueurTremie,
                    comboMur, comboSol, comboPlafond, comboTremie,
                    lblMessage
            );
            actualiserPlan(plan);
        });

        btnRetour.setOnAction(e -> retour(stage));

        btnMenu.setOnAction(e -> new FenetreAccueil().afficher(stage));

        Scene scene = new Scene(root, 1500, 850);
        stage.setTitle("Configuration pièce");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void retour(Stage stage) {
        if (batiment instanceof Maison) {
            new FenetreListePieces(
                    batiment,
                    nomEtage,
                    nomsPieces
            ).afficher(stage);
            return;
        }

        HashMap<String, Integer> nbAppartsParEtage = chargerNbAppartsParEtage();

        if (estVueAppartement()) {
            String etageParent = extraireEtageParent(nomEtage);

            new FenetreListePieces(
                    batiment,
                    nomEtage,
                    etageParent,
                    nbAppartsParEtage
            ).afficher(stage);
            return;
        }

        new FenetreAppartement(
                batiment,
                nomEtage,
                batiment.getLargeur() * batiment.getLongueur(),
                nbAppartsParEtage.getOrDefault(nomEtage, 0),
                nbAppartsParEtage
        ).afficher(stage);
    }

    private Node creerPlan() {
        if (batiment instanceof Maison) {
            return new PlanDessin(batiment, nomEtage);
        }

        if (estVueAppartement()) {
            DimensionsAppartement dim = chargerDimensionsAppartement();
            return new PlanDessin(batiment, nomEtage, dim.largeur, dim.longueur);
        }

        return new PlanBloc(batiment, nomEtage, nomPiece);
    }

    private void actualiserPlan(Node plan) {
        if (plan instanceof PlanDessin) {
            ((PlanDessin) plan).actualiser();
        } else if (plan instanceof PlanBloc) {
            ((PlanBloc) plan).actualiser();
        }
    }

    private VBox creerBox(String titre, Node... contenus) {
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-size: 15px;");

        VBox box = new VBox(10);
        box.getChildren().add(lblTitre);
        box.getChildren().addAll(contenus);
        box.setPadding(new Insets(12));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color: white; -fx-border-color: #0F056B; -fx-border-width: 1;");

        return box;
    }

    private HBox ligne(String texte, Node champ, double largeurLabel, double largeurChamp) {
        Label lbl = new Label(texte);
        lbl.setMinWidth(largeurLabel);

        if (champ instanceof Control) {
            ((Control) champ).setPrefWidth(largeurChamp);
        }

        HBox ligne = new HBox(10, lbl, champ);
        ligne.setAlignment(Pos.CENTER_LEFT);
        return ligne;
    }

    private void chargerDonnees(TextField x, TextField y, TextField largeur,
                                TextField longueur, TextField hauteur,
                                ComboBox<Revetement> mur,
                                ComboBox<Revetement> sol,
                                ComboBox<Revetement> plafond) {

        String[] plan = SauvegardeProjet.chargerElementPlan(batiment.getId(), nomEtage, nomPiece);

        if (plan != null && plan.length >= 8) {
            x.setText(plan[3]);
            y.setText(plan[4]);
            largeur.setText(plan[5]);
            longueur.setText(plan[6]);
            hauteur.setText(plan[7]);
        }

        String[] piece = SauvegardeProjet.chargerPiece(batiment.getId(), nomEtage, nomPiece);

        if (piece != null && piece.length >= 18) {
            selectionner(mur, piece[15]);
            selectionner(sol, piece[16]);
            selectionner(plafond, piece[17]);
        }
    }

    private void selectionner(ComboBox<Revetement> combo, String idTexte) {
        try {
            int id = Integer.parseInt(idTexte.trim());

            for (Revetement r : combo.getItems()) {
                if (r.getIdRevetement() == id) {
                    combo.setValue(r);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void calculer(TextField x, TextField y,
                          TextField largeur, TextField longueur, TextField hauteur,
                          TextField nbFenetre, TextField nbPorte,
                          TextField nbTremie,
                          TextField largeurTremie,
                          TextField longueurTremie,
                          ComboBox<Revetement> mur,
                          ComboBox<Revetement> sol,
                          ComboBox<Revetement> plafond,
                          ComboBox<Revetement> tremie,
                          Label lblSurfaceMur,
                          Label lblSurfaceSol,
                          Label lblSurfacePlafond,
                          Label lblSurfaceTremie,
                          Label lblPrixMur,
                          Label lblPrixSol,
                          Label lblPrixPlafond,
                          Label lblPrixTremie,
                          Label lblPrixTotal,
                          Label lblMessage) {

        try {
            Resultat r = faireCalcul(x, y, largeur, longueur, hauteur,
                    nbFenetre, nbPorte,
                    nbTremie, largeurTremie, longueurTremie,
                    mur, sol, plafond, tremie);

            lblSurfaceMur.setText(String.format("Surface murs : %.2f m²", r.surfaceMurs));
            lblSurfaceSol.setText(String.format("Surface sol : %.2f m²", r.surfaceSol));
            lblSurfacePlafond.setText(String.format("Surface plafond : %.2f m²", r.surfacePlafond));
            lblSurfaceTremie.setText(String.format("Surface escalier : %.2f m²", r.surfaceTremie));

            lblPrixMur.setText(String.format("Prix murs : %.2f €", r.coutMurs));
            lblPrixSol.setText(String.format("Prix sol : %.2f €", r.coutSol));
            lblPrixPlafond.setText(String.format("Prix plafond : %.2f €", r.coutPlafond));
            lblPrixTremie.setText(String.format("Prix escalier : %.2f €", r.coutTremie));
            lblPrixTotal.setText(String.format("TOTAL : %.2f €", r.total));

            lblMessage.setText("");
        } catch (Exception e) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Erreur : vérifie les champs.");
        }
    }

    private void enregistrer(TextField x, TextField y,
                             TextField largeur, TextField longueur, TextField hauteur,
                             TextField nbFenetre, TextField nbPorte,
                             TextField nbTremie,
                             TextField largeurTremie,
                             TextField longueurTremie,
                             ComboBox<Revetement> mur,
                             ComboBox<Revetement> sol,
                             ComboBox<Revetement> plafond,
                             ComboBox<Revetement> tremie,
                             Label lblMessage) {

        try {
            Resultat r = faireCalcul(x, y, largeur, longueur, hauteur,
                    nbFenetre, nbPorte,
                    nbTremie, largeurTremie, longueurTremie,
                    mur, sol, plafond, tremie);

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(), nomEtage, nomPiece,
                    r.x, r.y, r.largeur, r.longueur, r.hauteur,
                    sol.getValue().getIdRevetement()
            );

            SauvegardeProjet.sauvegarderPiece(
                    batiment.getId(), nomEtage, nomPiece,
                    r.x, r.y, r.largeur, r.longueur, r.hauteur,
                    mur.getValue().getIdRevetement(),
                    sol.getValue().getIdRevetement(),
                    plafond.getValue().getIdRevetement(),
                    r.coutMurs, r.coutSol, r.coutPlafond, r.total
            );

            SauvegardeProjet.sauvegarderDevis(
                    "D_" + batiment.getId(), batiment.getId(), nomPiece,
                    r.coutMurs, r.coutSol, r.coutPlafond, r.total
            );

            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Pièce enregistrée.");
        } catch (Exception e) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Impossible d'enregistrer.");
        }
    }

    private Resultat faireCalcul(TextField fieldX, TextField fieldY,
                                 TextField fieldLargeur, TextField fieldLongueur,
                                 TextField fieldHauteur,
                                 TextField fieldNbFenetre,
                                 TextField fieldNbPorte,
                                 TextField fieldNbTremie,
                                 TextField fieldLargeurTremie,
                                 TextField fieldLongueurTremie,
                                 ComboBox<Revetement> comboMur,
                                 ComboBox<Revetement> comboSol,
                                 ComboBox<Revetement> comboPlafond,
                                 ComboBox<Revetement> comboTremie) {

        if (comboMur.getValue() == null
                || comboSol.getValue() == null
                || comboPlafond.getValue() == null
                || comboTremie.getValue() == null) {
            throw new IllegalArgumentException();
        }

        double x = parse(fieldX);
        double y = parse(fieldY);
        double largeur = parse(fieldLargeur);
        double longueur = parse(fieldLongueur);
        double hauteur = parse(fieldHauteur);

        verifierDimensions(x, y, largeur, longueur, hauteur);

        Piece piece = new Piece(nomPiece, x, y, largeur, longueur, hauteur);

        double ouverturesMurs = parseZero(fieldNbFenetre) * 1.2 * 1.2
                + parseZero(fieldNbPorte) * 0.9 * 2.1;

        double nbTremie = parseZero(fieldNbTremie);
        double largeurTremie = parseZero(fieldLargeurTremie);
        double longueurTremie = parseZero(fieldLongueurTremie);

        Tremie tremie = new Tremie(0, 0, largeurTremie, longueurTremie);
        tremie.setRevetement(comboTremie.getValue());

       
        double surfaceTremieAuSol = nbTremie * tremie.calculerSurfaceAuSol();
        double surfaceMurs = Math.max(0, piece.calculerSurfaceMurs() - ouverturesMurs);
        double surfaceSol = Math.max(0, piece.calculerSurfaceSol() - surfaceTremieAuSol);
        double surfacePlafond = Math.max(0, piece.calculerSurfacePlafond() - surfaceTremieAuSol);
        double surfaceTremie = nbTremie * tremie.calculerSurfaceRevetement();

        double coutMurs = comboMur.getValue().calculerPrix(surfaceMurs);
        double coutSol = comboSol.getValue().calculerPrix(surfaceSol);
        double coutPlafond = comboPlafond.getValue().calculerPrix(surfacePlafond);
        double coutTremie = comboTremie.getValue().calculerPrix(surfaceTremie);

        return new Resultat(x, y, largeur, longueur, hauteur,
                surfaceMurs, surfaceSol, surfacePlafond, surfaceTremie,
                coutMurs, coutSol, coutPlafond, coutTremie);
    }

    private void verifierDimensions(double x, double y, double largeur, double longueur, double hauteur) {
        if (largeur <= 0 || longueur <= 0 || hauteur <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
    
        if (estVueAppartement()) {
            DimensionsAppartement dim = chargerDimensionsAppartement();

            if (x + largeur > dim.largeur || y + longueur > dim.longueur) {
                throw new IllegalArgumentException();
            }
        }
    }

    private double parse(TextField field) {
        return Double.parseDouble(field.getText().trim().replace(",", "."));
    }

    private double parseZero(TextField field) {
        try {
            String texte = field.getText().trim().replace(",", ".");
            return texte.isEmpty() ? 0 : Double.parseDouble(texte);
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean estVueAppartement() {
        if (nomEtage == null || !nomEtage.contains("_")) {
            return false;
        }

        String nomAppartement = extraireNomAppartement(nomEtage);
        String n = normaliser(nomAppartement);
        return n.startsWith("appartement") || n.startsWith("appart");
    }

    private String extraireEtageParent(String vueInterne) {
        String etage = vueInterne.substring(0, vueInterne.indexOf("_"));

        if (etage.equalsIgnoreCase("RDC")) {
            return "RDC";
        }

        if (etage.toLowerCase().startsWith("etage")) {
            return etage.replace("Etage", "Etage ");
        }

        return etage;
    }

    private String extraireNomAppartement(String vueInterne) {
        return vueInterne.substring(vueInterne.indexOf("_") + 1);
    }

    private DimensionsAppartement chargerDimensionsAppartement() {
        String etageParent = extraireEtageParent(nomEtage);
        String nomAppartement = extraireNomAppartement(nomEtage);

        String[] bloc = SauvegardeProjet.chargerElementPlan(
                batiment.getId(),
                etageParent,
                nomAppartement
        );

        if (bloc != null && bloc.length >= 7) {
            try {
                double largeur = Double.parseDouble(bloc[5].trim().replace(",", "."));
                double longueur = Double.parseDouble(bloc[6].trim().replace(",", "."));

                if (largeur > 0 && longueur > 0) {
                    return new DimensionsAppartement(largeur, longueur);
                }
            } catch (Exception ignored) {
            }
        }

        return new DimensionsAppartement(batiment.getLargeur(), batiment.getLongueur());
    }

    private HashMap<String, Integer> chargerNbAppartsParEtage() {
        HashMap<String, Integer> nbAppartsParEtage = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Etage.txt"))) {
            reader.readLine();

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] p = ligne.split(";");

                if (p.length >= 4 && p[1].trim().equalsIgnoreCase(batiment.getId())) {
                    nbAppartsParEtage.put(
                            p[2].trim(),
                            Integer.parseInt(p[3].trim())
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nbAppartsParEtage;
    }

    private String normaliser(String texte) {
        if (texte == null) {
            return "";
        }

        return texte.trim().toLowerCase()
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("ë", "e")
                .replace("à", "a")
                .replace("â", "a")
                .replace("ù", "u")
                .replace("û", "u")
                .replace("î", "i")
                .replace("ï", "i")
                .replace("ô", "o")
                .replace("ç", "c");
    }

    private static class DimensionsAppartement {
        double largeur;
        double longueur;

        DimensionsAppartement(double largeur, double longueur) {
            this.largeur = largeur;
            this.longueur = longueur;
        }
    }

    private static class Resultat {
        double x, y, largeur, longueur, hauteur;
        double surfaceMurs, surfaceSol, surfacePlafond, surfaceTremie;
        double coutMurs, coutSol, coutPlafond, coutTremie, total;

        Resultat(double x, double y, double largeur, double longueur, double hauteur,
                 double surfaceMurs, double surfaceSol, double surfacePlafond, double surfaceTremie,
                 double coutMurs, double coutSol, double coutPlafond, double coutTremie) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.longueur = longueur;
            this.hauteur = hauteur;
            this.surfaceMurs = surfaceMurs;
            this.surfaceSol = surfaceSol;
            this.surfacePlafond = surfacePlafond;
            this.surfaceTremie = surfaceTremie;
            this.coutMurs = coutMurs;
            this.coutSol = coutSol;
            this.coutPlafond = coutPlafond;
            this.coutTremie = coutTremie;
            this.total = coutMurs + coutSol + coutPlafond + coutTremie;
        }
    }
}
