/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class FenetreListePieces {

    private final Batiments batiment;
    private final String vuePlan;
    private final String retourEtage;
    private final double surface;
    private final HashMap<String, Integer> nbAppartsParEtage;
    private final ArrayList<String> nomsPieces = new ArrayList<>();

    public FenetreListePieces(Batiments batiment, String vuePlan) {
        this(batiment, vuePlan, vuePlan, new HashMap<String, Integer>());
    }

    public FenetreListePieces(Batiments batiment, String vuePlan,
                              HashMap<String, Integer> nbAppartsParEtage) {
        this(batiment, vuePlan, vuePlan, nbAppartsParEtage);
    }

    public FenetreListePieces(Batiments batiment, String vuePlan,
                              ArrayList<String> nomsPieces) {
        this(batiment, vuePlan, vuePlan, new HashMap<String, Integer>());
        this.nomsPieces.addAll(nomsPieces);
    }

    public FenetreListePieces(Batiments batiment, String vuePlan,
                              String retourEtage,
                              HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.vuePlan = vuePlan;
        this.retourEtage = retourEtage;
        this.nbAppartsParEtage = nbAppartsParEtage == null ? new HashMap<>() : nbAppartsParEtage;
        this.surface = calculerSurfaceVue();
    }

    public void afficher(Stage stage) {
        chargerPiecesExistantes();

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("PIÈCES — " + vuePlan);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label aide = new Label(estVueAppartement()
                ? "Tu ajoutes ici les pièces propres à cet appartement."
                : "Tu ajoutes ici les pièces de cette vue.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox topBox = new VBox(6, titre, aide);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        Label lblSurface = new Label("Surface : " + String.format("%.2f", surface) + " m²");
        lblSurface.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        TextField fieldNomPiece = new TextField();
        fieldNomPiece.setPromptText("Nom de la pièce");
        fieldNomPiece.setMaxWidth(300);

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        VBox listePieces = new VBox(8);
        listePieces.setAlignment(Pos.TOP_CENTER);
        listePieces.setPadding(new Insets(10));

        Button btnAjouter = new Button("+ AJOUTER UNE PIÈCE");
        btnAjouter.setStyle(styleBouton);
        btnAjouter.setOnAction(e -> ajouterPiece(fieldNomPiece, lblErreur, stage, listePieces, styleBouton));

        HBox ligneAjout = new HBox(15, fieldNomPiece, btnAjouter);
        ligneAjout.setAlignment(Pos.CENTER);

        actualiserListePieces(stage, listePieces, styleBouton);

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> retour(stage));

        Button btnDevis = new Button("VOIR LE DEVIS");
        btnDevis.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        btnDevis.setOnAction(e -> {
        Stage fenetreActuelle =
                (Stage) btnDevis.getScene().getWindow();

        fenetreActuelle.hide();

        new FenetreRecapitulatif(
                batiment,
                vuePlan,
                fenetreActuelle
        ).afficher(new Stage());
    });
        Button btnMenu = new Button("MENU PRINCIPAL");
        btnMenu.setStyle(styleBouton);
        btnMenu.setOnAction(e -> new FenetreAccueil().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnDevis, btnMenu);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox centre = new VBox(18, lblSurface, ligneAjout, lblErreur, listePieces);
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
        stage.setTitle("Liste Pièces - " + vuePlan);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private void ajouterPiece(TextField fieldNomPiece, Label lblErreur,
                              Stage stage, VBox listePieces, String styleBouton) {
        String nomPiece = fieldNomPiece.getText().trim();

        if (nomPiece.isEmpty()) {
            lblErreur.setText("Veuillez donner un nom à la pièce.");
            return;
        }

        if (pieceExisteDeja(nomPiece)) {
            lblErreur.setText("Cette pièce existe déjà.");
            return;
        }

        lblErreur.setText("");
        nomsPieces.add(nomPiece);
        fieldNomPiece.clear();
        actualiserListePieces(stage, listePieces, styleBouton);
    }

    private void actualiserListePieces(Stage stage, VBox listePieces, String styleBouton) {
        listePieces.getChildren().clear();

        if (nomsPieces.isEmpty()) {
            Label lblVide = new Label("Aucune pièce enregistrée pour le moment.");
            lblVide.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");
            listePieces.getChildren().add(lblVide);
            return;
        }

        for (int i = 0; i < nomsPieces.size(); i++) {
            ajouterLignePiece(stage, listePieces, nomsPieces.get(i), i + 1, styleBouton);
        }
    }

    private void ajouterLignePiece(Stage stage, VBox listePieces, String nomPiece,
                                   int numero, String styleBouton) {
        Label lblNumero = new Label("Pièce " + numero);
        lblNumero.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");
        lblNumero.setMinWidth(90);

        Label lblNom = new Label(nomPiece);
        lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        lblNom.setMinWidth(300);

        Button btnEntrer = new Button("Entrer →");
        btnEntrer.setStyle(styleBouton);
        btnEntrer.setOnAction(e -> new FenetrePiece(
                batiment,
                vuePlan,
                nomPiece,
                surface,
                nomsPieces
        ).afficher(stage));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #B00020; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerPiece(stage, listePieces, nomPiece, styleBouton));

        HBox ligne = new HBox(20, lblNumero, lblNom, btnEntrer, btnSupprimer);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(750);
        ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10 20;");

        listePieces.getChildren().add(ligne);
    }

    private void supprimerPiece(Stage stage, VBox listePieces, String nomPiece, String styleBouton) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer la pièce");
        confirmation.setHeaderText("Supprimer " + nomPiece + " ?");
        confirmation.setContentText("La pièce sera supprimée de la liste, du plan et du devis enregistré.");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                SauvegardeProjet.supprimerPiece(batiment.getId(), vuePlan, nomPiece);
                nomsPieces.remove(nomPiece);
                actualiserListePieces(stage, listePieces, styleBouton);
            }
        });
    }

    private void retour(Stage stage) {
        if (batiment instanceof Maison) {
            new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage);
            return;
        }

        if (estVueAppartement()) {
            String etageParent = extraireEtageParent(vuePlan);

            new FenetreAppartement(
                    batiment,
                    etageParent,
                    batiment.getLargeur() * batiment.getLongueur(),
                    nbAppartsParEtage.getOrDefault(etageParent, 0),
                    nbAppartsParEtage
            ).afficher(stage);
            return;
        }

        new FenetreAppartement(
                batiment,
                retourEtage,
                batiment.getLargeur() * batiment.getLongueur(),
                nbAppartsParEtage.getOrDefault(retourEtage, 0),
                nbAppartsParEtage
        ).afficher(stage);
    }

    private void chargerPiecesExistantes() {
        ArrayList<String> piecesSauvegardees = SauvegardeProjet.chargerNomsPieces(
                batiment.getId(),
                vuePlan
        );

        for (String piece : piecesSauvegardees) {
            if (!pieceExisteDeja(piece)) {
                nomsPieces.add(piece);
            }
        }
    }

    private double calculerSurfaceVue() {
        if (batiment instanceof Maison || !estVueAppartement()) {
            return batiment.getLargeur() * batiment.getLongueur();
        }

        String etageParent = extraireEtageParent(vuePlan);
        String nomAppartement = extraireNomAppartement(vuePlan);

        String[] bloc = SauvegardeProjet.chargerElementPlan(
                batiment.getId(),
                etageParent,
                nomAppartement
        );

        if (bloc != null && bloc.length >= 7) {
            try {
                return Double.parseDouble(bloc[5].trim().replace(",", "."))
                        * Double.parseDouble(bloc[6].trim().replace(",", "."));
            } catch (Exception ignored) {
            }
        }

        return batiment.getLargeur() * batiment.getLongueur();
    }

    private boolean estVueAppartement() {
        if (vuePlan == null || !vuePlan.contains("_")) {
            return false;
        }

        String nom = extraireNomAppartement(vuePlan);
        return normaliser(nom).startsWith("appartement") || normaliser(nom).startsWith("appart");
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

    private boolean pieceExisteDeja(String nomPiece) {
        for (String nom : nomsPieces) {
            if (normaliser(nom).equals(normaliser(nomPiece))) {
                return true;
            }
        }
        return false;
    }

    private String normaliser(String texte) {
        if (texte == null) return "";

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
