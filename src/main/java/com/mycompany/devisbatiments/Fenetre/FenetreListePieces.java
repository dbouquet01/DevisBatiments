package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FenetreListePieces {

    private final Batiments batiment;
    private final String vuePlan;
    private final String retourEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;
    private final ArrayList<String> nomsPieces = new ArrayList<>();
    private FenetreListePiecesController controller;

    private TextField fieldNomPiece;
    private Label lblErreur;
    private VBox listePieces;
    private String styleBouton;

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
    }

    public void afficher(Stage stage) {
        controller = new FenetreListePiecesController(this);
        controller.chargerPiecesExistantes();

        styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("PIÈCES — " + vuePlan);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label aide = new Label(controller.estVueAppartement()
                ? "Tu ajoutes ici les pièces propres à cet appartement."
                : "Tu ajoutes ici les pièces de cette vue.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox topBox = new VBox(6, titre, aide);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        Label lblSurface = new Label("Surface : " + String.format("%.2f", controller.calculerSurfaceVue()) + " m²");
        lblSurface.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        fieldNomPiece = new TextField();
        fieldNomPiece.setPromptText("Nom de la pièce");
        fieldNomPiece.setMaxWidth(300);

        lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        listePieces = new VBox(8);
        listePieces.setAlignment(Pos.TOP_CENTER);
        listePieces.setPadding(new Insets(10));

        Button btnAjouter = new Button("+ AJOUTER UNE PIÈCE");
        btnAjouter.setStyle(styleBouton);
        btnAjouter.setOnAction(e -> controller.ajouterPiece(stage));

        HBox ligneAjout = new HBox(15, fieldNomPiece, btnAjouter);
        ligneAjout.setAlignment(Pos.CENTER);

        controller.actualiserListePieces(stage);

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> controller.retour(stage));

        Button btnDevis = new Button("VOIR LE DEVIS");
        btnDevis.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        btnDevis.setOnAction(e -> controller.voirDevis(btnDevis));

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

    public Batiments getBatiment() {
        return batiment;
    }

    public String getVuePlan() {
        return vuePlan;
    }

    public String getRetourEtage() {
        return retourEtage;
    }

    public HashMap<String, Integer> getNbAppartsParEtage() {
        return nbAppartsParEtage;
    }

    public ArrayList<String> getNomsPieces() {
        return nomsPieces;
    }

    public TextField getFieldNomPiece() {
        return fieldNomPiece;
    }

    public Label getLblErreur() {
        return lblErreur;
    }

    public VBox getListePieces() {
        return listePieces;
    }

    public String getStyleBouton() {
        return styleBouton;
    }
}