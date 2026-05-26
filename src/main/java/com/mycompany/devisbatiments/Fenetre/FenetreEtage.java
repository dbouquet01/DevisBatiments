package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30));

        VBox listeEtages = new VBox(15);
        listeEtages.setAlignment(Pos.CENTER);
        listeEtages.setPadding(new Insets(20));

        String styleLigne = "-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10;";

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        double surfaceEtage = batiment.getLargeur() * batiment.getLongueur();

        for (int i = 0; i <= batiment.getNbEtage(); i++) {

            String nomEtage = (i == 0) ? "RDC" : "Etage " + i;

            Label lblEtage = new Label(nomEtage);
            lblEtage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblEtage.setMinWidth(160);

            Label lblInfo = new Label("");
            lblInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B;");
            lblInfo.setMinWidth(180);

            if (!(batiment instanceof Maison)) {
                int nbApparts = nbAppartsParEtage.getOrDefault(nomEtage, 0);
                lblInfo.setText(nbApparts + " appartement(s)");
            }

            Button btnEntrer = new Button("Entrer →");
            btnEntrer.setStyle(styleBouton);

            Button btnVisualiser = new Button("Visualiser");
            btnVisualiser.setStyle(styleBouton);

            final String nomEtageCapture = nomEtage;

            btnEntrer.setOnAction(e -> {
                if (batiment instanceof Maison) {
                    new FenetreListePieces(batiment, nomEtageCapture).afficher(stage);
                } else {
                    int nbApparts = nbAppartsParEtage.get(nomEtageCapture);

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

            HBox ligne = new HBox(20, lblEtage, lblInfo, btnEntrer, btnVisualiser);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle(styleLigne);
            ligne.setPadding(new Insets(10, 20, 10, 20));

            listeEtages.getChildren().add(ligne);
        }

        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);

        btnRetour.setOnAction(e -> {
            new FenetreAttributsImmeuble().afficher(
                    stage,
                    batiment.getId(),
                    batiment.getDesignation(),
                    batiment.getLargeur(),
                    batiment.getLongueur(),
                    batiment.getNbEtage()
            );
        });

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(listeEtages);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Étages");
        stage.setScene(scene);
        stage.show();
    }
}