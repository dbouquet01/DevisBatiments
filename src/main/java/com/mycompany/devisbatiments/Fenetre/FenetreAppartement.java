package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class FenetreAppartement {

    private static final double LARGEUR_COULOIR_METRES = 1.50;

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;
    private final int nbApparts;
    private final HashMap<String, Integer> nbAppartsParEtage;

    public FenetreAppartement(Batiments batiment, String nomEtage, double surfaceEtage,
                              int nbApparts, HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
        this.nbApparts = nbApparts;
        this.nbAppartsParEtage = nbAppartsParEtage == null ? new HashMap<>() : nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("APPARTEMENTS / PIÈCES COMMUNES — " + nomEtage);
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label aide = new Label("Les appartements peuvent contenir leurs propres pièces. Les pièces communes restent gérées séparément.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox topBox = new VBox(6, titre, aide);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        double surfaceCouloir = calculerSurfaceCouloir();
        double surfaceHabitable = calculerSurfaceHabitable();
        double surfaceParAppart = nbApparts > 0 ? surfaceHabitable / nbApparts : 0;

        Label lblSurfaceInfo = new Label(
                "Surface étage : " + String.format("%.2f", surfaceEtage)
                        + " m² — Couloir théorique : " + String.format("%.2f", surfaceCouloir)
                        + " m² — Surface restante théorique : " + String.format("%.2f", surfaceHabitable)
                        + " m² — Surface/appartement théorique : " + String.format("%.2f", surfaceParAppart) + " m²"
        );
        lblSurfaceInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        VBox listeBlocs = new VBox(10);
        listeBlocs.setAlignment(Pos.TOP_CENTER);
        listeBlocs.setPadding(new Insets(20));

        ArrayList<String> blocsEtage = SauvegardeProjet.chargerNomsElementsPlan(batiment.getId(), nomEtage);

        boolean auMoinsUnBlocVisible = false;

        for (String nomBloc : blocsEtage) {
            if (estElementTechnique(nomBloc)) {
                continue;
            }

            auMoinsUnBlocVisible = true;
            ajouterLigneBloc(stage, listeBlocs, blocsEtage, nomBloc, styleBouton);
        }

        if (!auMoinsUnBlocVisible) {
            Label vide = new Label("Aucun appartement ou bloc enregistré sur cet étage. Va d'abord dans « Visualiser / placer couloir ».");
            vide.setStyle("-fx-font-size: 15px; -fx-text-fill: grey;");
            listeBlocs.getChildren().add(vide);
        }

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        Button btnMenu = new Button("MENU PRINCIPAL");
        btnMenu.setStyle(styleBouton);
        btnMenu.setOnAction(e -> new FenetreProjet().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnMenu);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.setStyle("-fx-background-color: #F5F5F5;");

        VBox centre = new VBox(20, lblSurfaceInfo, listeBlocs);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(centre);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root);
        stage.setTitle("Appartements / pièces communes — " + nomEtage);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void ajouterLigneBloc(Stage stage, VBox listeBlocs, ArrayList<String> blocsEtage,
                                  String nomBloc, String styleBouton) {

        boolean appartement = estAppartement(nomBloc);

        Label lblBloc = new Label(nomBloc + calculerSurfaceBlocTexte(nomBloc));
        lblBloc.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        lblBloc.setMinWidth(330);

        HBox ligne = new HBox(20);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(1100);
        ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10 20;");

        ligne.getChildren().add(lblBloc);

        if (appartement) {
            Button btnPieces = new Button("Gérer les pièces →");
            btnPieces.setStyle(styleBouton);
            btnPieces.setOnAction(e -> ouvrirPiecesAppartement(stage, nomBloc));

            Button btnModifierBloc = new Button("Modifier bloc");
            btnModifierBloc.setStyle(styleBouton);
            btnModifierBloc.setOnAction(e -> new FenetrePiece(
                    batiment,
                    nomEtage,
                    nomBloc,
                    surfaceEtage,
                    blocsEtage
            ).afficher(stage));

            ligne.getChildren().addAll(btnPieces, btnModifierBloc);
        } else {
            Button btnModifier = new Button("Modifier →");
            btnModifier.setStyle(styleBouton);
            btnModifier.setOnAction(e -> new FenetrePiece(
                    batiment,
                    nomEtage,
                    nomBloc,
                    surfaceEtage,
                    blocsEtage
            ).afficher(stage));

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #B00020; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
            btnSupprimer.setOnAction(e -> {
                SauvegardeProjet.supprimerPiece(batiment.getId(), nomEtage, nomBloc);
                afficher(stage);
            });

            ligne.getChildren().addAll(btnModifier, btnSupprimer);
        }

        listeBlocs.getChildren().add(ligne);
    }

    private void ouvrirPiecesAppartement(Stage stage, String nomAppartement) {
        String vueAppartement = creerVueAppartement(nomEtage, nomAppartement);

        new FenetreListePieces(
                batiment,
                vueAppartement,
                nomEtage,
                nbAppartsParEtage
        ).afficher(stage);
    }

    public static String creerVueAppartement(String nomEtage, String nomAppartement) {
        return nettoyerPourVue(nomEtage) + "_" + nettoyerPourVue(nomAppartement);
    }

    public static String nettoyerPourVue(String texte) {
        if (texte == null) {
            return "";
        }

        return texte.trim().replace(" ", "");
    }

    private String calculerSurfaceBlocTexte(String nomBloc) {
        String[] element = SauvegardeProjet.chargerElementPlan(batiment.getId(), nomEtage, nomBloc);

        if (element == null || element.length < 7) {
            return "";
        }

        try {
            double largeur = Double.parseDouble(element[5].trim().replace(",", "."));
            double longueur = Double.parseDouble(element[6].trim().replace(",", "."));
            return " — " + String.format("%.2f", largeur * longueur) + " m²";
        } catch (Exception e) {
            return "";
        }
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

    private boolean estAppartement(String nomBloc) {
        String n = normaliser(nomBloc);
        return n.startsWith("appartement") || n.startsWith("appart");
    }

    private boolean estElementTechnique(String nomBloc) {
        String n = normaliser(nomBloc);
        return n.equals("couloir")
                || n.equals("escalier")
                || n.equals("tremie")
                || n.equals("trémie");
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
}
