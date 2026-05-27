package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;

public class FenetreEtage {

    private final Batiments batiment;
    private final HashMap<String, Integer> nbAppartsParEtage;

    public FenetreEtage(Batiments batiment, HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        Label titre = new Label("ÉTAGES DU BÂTIMENT : " + batiment.getId());
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label aide = new Label("Pour un immeuble : visualise d'abord l'étage pour placer le couloir, puis ajoute les pièces communes.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox topBox = new VBox(8, titre, aide);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        VBox listeEtages = new VBox(15);
        listeEtages.setAlignment(Pos.CENTER);
        listeEtages.setPadding(new Insets(20));

        String styleLigne = "-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10;";

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        String styleBoutonVert = "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        double surfaceEtage = batiment.getLargeur() * batiment.getLongueur();

        for (int i = 0; i <= batiment.getNbEtage(); i++) {

            String nomEtage = (i == 0) ? "RDC" : "Etage " + i;

            Label lblEtage = new Label(nomEtage);
            lblEtage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblEtage.setMinWidth(130);

            Label lblInfo = new Label("");
            lblInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B;");
            lblInfo.setMinWidth(170);

            if (!(batiment instanceof Maison)) {
                int nbApparts = nbAppartsParEtage.getOrDefault(nomEtage, 0);
                lblInfo.setText(nbApparts + " appartement(s)");
            }

            Button btnEntrer = new Button("Entrer →");
            btnEntrer.setStyle(styleBouton);

            Button btnVisualiser = new Button("Visualiser / placer couloir");
            btnVisualiser.setStyle(styleBouton);

            Button btnAjouterPiece = new Button("+ Pièce commune");
            btnAjouterPiece.setStyle(styleBoutonVert);
            btnAjouterPiece.setVisible(!(batiment instanceof Maison));
            btnAjouterPiece.setManaged(!(batiment instanceof Maison));

            final String nomEtageCapture = nomEtage;

            btnEntrer.setOnAction(e -> {
                if (batiment instanceof Maison) {
                    new FenetreListePieces(batiment, nomEtageCapture).afficher(stage);
                } else {
                    int nbApparts = nbAppartsParEtage.getOrDefault(nomEtageCapture, 0);

                    new FenetreAppartement(
                            batiment,
                            nomEtageCapture,
                            surfaceEtage,
                            nbApparts,
                            nbAppartsParEtage
                    ).afficher(stage);
                }
            });

            btnVisualiser.setOnAction(e -> {
                if (batiment instanceof Maison) {
                    PlanVisualisation plan = new PlanVisualisation(batiment.getId());
                    plan.afficher();
                } else {
                    new PlanEtage(
                            batiment,
                            nomEtageCapture,
                            surfaceEtage,
                            nbAppartsParEtage
                    ).afficher(stage);
                }
            });

            btnAjouterPiece.setOnAction(e -> {
                if (!GestionCouloirEtage.couloirExiste(batiment.getId(), nomEtageCapture)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Couloir obligatoire");
                    alert.setHeaderText("Place d'abord le couloir.");
                    alert.setContentText("Clique sur « Visualiser / placer couloir », choisis la position du couloir, puis valide le couloir avant d'ajouter une pièce commune.");
                    alert.showAndWait();
                    return;
                }

                new FenetrePieceCommuneEtage(
                        batiment,
                        nomEtageCapture,
                        nbAppartsParEtage
                ).afficher(stage);
            });

            HBox ligne = new HBox(18, lblEtage, lblInfo, btnEntrer, btnVisualiser, btnAjouterPiece);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle(styleLigne);
            ligne.setPadding(new Insets(10, 20, 10, 20));

            listeEtages.getChildren().add(ligne);
        }

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);

        btnRetour.setOnAction(e -> {
            if (batiment instanceof Maison) {
                new FenetreAttributsMaison().afficher(
                        stage,
                        batiment.getId(),
                        batiment.getDesignation(),
                        batiment.getLargeur(),
                        batiment.getLongueur(),
                        batiment.getNbEtage()
                );
            } else {
                new FenetreAttributsImmeuble().afficher(
                        stage,
                        batiment.getId(),
                        batiment.getDesignation(),
                        batiment.getLargeur(),
                        batiment.getLongueur(),
                        batiment.getNbEtage(),
                        nbAppartsParEtage
                );
            }
        });

        Button btnMenuPrincipal = new Button("MENU PRINCIPAL");
        btnMenuPrincipal.setStyle(styleBouton);
        btnMenuPrincipal.setOnAction(e -> new FenetreProjet().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnMenuPrincipal);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(listeEtages);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Étages");
        stage.setScene(scene);
        stage.show();
    }
}
