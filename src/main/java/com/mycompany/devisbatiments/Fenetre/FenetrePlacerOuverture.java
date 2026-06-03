package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vue de la fenêtre de placement des ouvertures.
 *
 * Cette classe garde uniquement la construction graphique JavaFX : labels,
 * champs, boutons, listes et disposition. Toute la logique d'action, de
 * sauvegarde, de chargement et de dessin du plan est dans
 * FenetrePlacerOuvertureController.
 */
public class FenetrePlacerOuverture {

    static final double PANE_W = 700;
    static final double PANE_H = 520;

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private final FenetrePlacerOuvertureController controller;

    private Pane zoneDessin;

    private ComboBox<String> comboMurFen;
    private TextField tfFenX;
    private TextField tfFenY;
    private ListView<String> listeFenetres;
    private Button btnAjouterFen;
    private Button btnSupprimerFen;

    private ComboBox<String> comboMurPor;
    private TextField tfPorX;
    private ListView<String> listePortes;
    private Button btnAjouterPor;
    private Button btnSupprimerPor;

    private TextField tfTrX;
    private TextField tfTrY;
    private TextField tfTrL;
    private TextField tfTrLo;
    private ListView<String> listeTremies;
    private Button btnAjouterTr;
    private Button btnSupprimerTr;

    private Button btnRetour;
    private Button btnVoirPlan;

    public FenetrePlacerOuverture(Batiments batiment, String nomEtage, String nomPiece) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.controller = new FenetrePlacerOuvertureController(this, batiment, nomEtage, nomPiece);
    }

    public void afficher(Stage stage) {
        controller.initialiserDonnees();

        final String styleBtn =
                "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        final String styleAdd =
                "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        final String styleDel =
                "-fx-background-color: #B00020; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;";

        Label titre = new Label("PLACER LES OUVERTURES - " + nomPiece + " / " + nomEtage);
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label sousTitre = new Label(controller.getTexteDimensionsPiece());
        sousTitre.setStyle("-fx-font-size: 13px; -fx-text-fill: #0F056B;");

        VBox topBox = new VBox(5, titre, sousTitre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(18));

        zoneDessin = new Pane();
        zoneDessin.setPrefSize(PANE_W, PANE_H);
        zoneDessin.setStyle("-fx-background-color: white; -fx-border-color: #0F056B; -fx-border-width: 2;");

        VBox panneauGauche = new VBox(18);
        panneauGauche.setPadding(new Insets(10, 15, 10, 10));
        panneauGauche.setPrefWidth(420);

        panneauGauche.getChildren().addAll(creerSectionFenetres(styleAdd, styleDel),
                creerSectionPortes(styleAdd, styleDel),
                creerSectionTremies(styleAdd, styleDel));

        ScrollPane scrollGauche = new ScrollPane(panneauGauche);
        scrollGauche.setFitToWidth(true);
        scrollGauche.setStyle("-fx-background-color: transparent;");

        btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBtn);

        btnVoirPlan = new Button("VOIR LE PLAN");
        btnVoirPlan.setStyle(styleBtn);

        Label aide = new Label(
                "Repere : (0, 0) = coin haut-gauche de la piece. "
                + "Murs HAUT/BAS : x = offset horizontal. "
                + "Murs GAUCHE/DROITE : x = offset vertical."
        );
        aide.setStyle("-fx-font-size: 11px; -fx-text-fill: grey;");
        aide.setWrapText(true);

        VBox bottomBox = new VBox(8, aide, btnRetour, btnVoirPlan);
        bottomBox.setPadding(new Insets(12, 20, 12, 20));

        HBox centre = new HBox(15, scrollGauche, zoneDessin);
        centre.setPadding(new Insets(10));
        HBox.setHgrow(zoneDessin, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);
        root.setStyle("-fx-background-color: #F5F5F5;");

        controller.connecterActions(stage);
        controller.actualiserToutesLesListes();
        controller.dessinerPlan();

        Scene scene = new Scene(root);
        stage.setTitle("Placer les ouvertures - " + nomPiece);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    private VBox creerSectionFenetres(String styleAdd, String styleDel) {
        comboMurFen = murCombo();
        tfFenX = tf("distance x sur le mur (m)");
        tfFenY = tf("hauteur depuis le sol (m)");
        listeFenetres = listeView();

        btnAjouterFen = new Button("+ Ajouter fenetre");
        btnAjouterFen.setStyle(styleAdd);
        btnSupprimerFen = new Button("Supprimer");
        btnSupprimerFen.setStyle(styleDel);

        return section("Fenetres",
                ligne("Mur :", comboMurFen),
                ligne("Distance du mur x (m) :", tfFenX),
                ligne("Hauteur y (m) :", tfFenY),
                new HBox(10, btnAjouterFen, btnSupprimerFen),
                listeFenetres
        );
    }

    private VBox creerSectionPortes(String styleAdd, String styleDel) {
        comboMurPor = murCombo();
        tfPorX = tf("Distance x sur le mur (m)");
        listePortes = listeView();

        btnAjouterPor = new Button("+ Ajouter porte");
        btnAjouterPor.setStyle(styleAdd);
        btnSupprimerPor = new Button("Supprimer");
        btnSupprimerPor.setStyle(styleDel);

        return section("Portes (touchent le sol)",
                ligne("Mur :", comboMurPor),
                ligne("Distance avec le mur (m) :", tfPorX),
                new HBox(10, btnAjouterPor, btnSupprimerPor),
                listePortes
        );
    }

    private VBox creerSectionTremies(String styleAdd, String styleDel) {
        tfTrX = tf("x depuis coin haut-gauche (m)");
        tfTrY = tf("y depuis coin haut-gauche (m)");
        tfTrL = tf("largeur (m)");
        tfTrLo = tf("longueur (m)");
        listeTremies = listeView();

        btnAjouterTr = new Button("+ Ajouter tremie");
        btnAjouterTr.setStyle(styleAdd);
        btnSupprimerTr = new Button("Supprimer");
        btnSupprimerTr.setStyle(styleDel);

        return section("Tremies / Escaliers",
                ligne("x (m) :", tfTrX),
                ligne("y (m) :", tfTrY),
                ligne("Largeur (m) :", tfTrL),
                ligne("Longueur (m) :", tfTrLo),
                new HBox(10, btnAjouterTr, btnSupprimerTr),
                listeTremies
        );
    }

    ComboBox<String> murCombo() {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll("HAUT", "DROITE", "BAS", "GAUCHE");
        cb.setValue("HAUT");
        cb.setPrefWidth(140);
        return cb;
    }

    TextField tf(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(160);
        return f;
    }

    ListView<String> listeView() {
        ListView<String> lv = new ListView<>();
        lv.setPrefHeight(80);
        return lv;
    }

    void actualiserListe(ListView<String> lv, java.util.List<?> items) {
        lv.getItems().clear();
        for (Object item : items) {
            lv.getItems().add(item.toString());
        }
    }

    HBox ligne(String label, javafx.scene.Node champ) {
        Label lbl = new Label(label);
        lbl.setMinWidth(120);
        lbl.setStyle("-fx-font-size: 13px;");
        HBox hb = new HBox(10, lbl, champ);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    VBox section(String titreSection, javafx.scene.Node... noeuds) {
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

    Pane getZoneDessin() { return zoneDessin; }
    ComboBox<String> getComboMurFen() { return comboMurFen; }
    TextField getTfFenX() { return tfFenX; }
    TextField getTfFenY() { return tfFenY; }
    ListView<String> getListeFenetres() { return listeFenetres; }
    Button getBtnAjouterFen() { return btnAjouterFen; }
    Button getBtnSupprimerFen() { return btnSupprimerFen; }

    ComboBox<String> getComboMurPor() { return comboMurPor; }
    TextField getTfPorX() { return tfPorX; }
    ListView<String> getListePortes() { return listePortes; }
    Button getBtnAjouterPor() { return btnAjouterPor; }
    Button getBtnSupprimerPor() { return btnSupprimerPor; }

    TextField getTfTrX() { return tfTrX; }
    TextField getTfTrY() { return tfTrY; }
    TextField getTfTrL() { return tfTrL; }
    TextField getTfTrLo() { return tfTrLo; }
    ListView<String> getListeTremies() { return listeTremies; }
    Button getBtnAjouterTr() { return btnAjouterTr; }
    Button getBtnSupprimerTr() { return btnSupprimerTr; }

    Button getBtnRetour() { return btnRetour; }
    Button getBtnVoirPlan() { return btnVoirPlan; }
}
