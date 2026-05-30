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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class FenetreEtage {

    private final Batiments batiment;
    private final HashMap<String, Integer> nbAppartsParEtage;

    public FenetreEtage(Batiments batiment, HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nbAppartsParEtage = nbAppartsParEtage == null ? new HashMap<>() : nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        boolean estMaison = batiment instanceof Maison;
        double surfaceEtage = batiment.getLargeur() * batiment.getLongueur();

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        String styleVert = "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";
        String styleLigne = "-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10;";

        Label titre = new Label("ÉTAGES DU BÂTIMENT : " + batiment.getId());
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label aide = new Label(estMaison
                ? "Sélectionne un étage pour gérer ses pièces."
                : "Pour un immeuble : visualise d'abord l'étage pour placer le couloir, puis ajoute les pièces communes.");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        VBox topBox = new VBox(8, titre, aide);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

        VBox listeEtages = new VBox(15);
        listeEtages.setAlignment(Pos.CENTER);
        listeEtages.setPadding(new Insets(20));

        for (int i = 0; i <= batiment.getNbEtage(); i++) {
            String nomEtage = i == 0 ? "RDC" : "Etage " + i;
            final String nomEtageCapture = nomEtage;

            Label lblEtage = new Label(nomEtage);
            lblEtage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            lblEtage.setMinWidth(130);

            Label lblInfo = new Label("");
            lblInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B;");
            lblInfo.setMinWidth(170);

            if (!estMaison) {
                lblInfo.setText(getNbApparts(nomEtage) + " appartement(s)");
            }

            Button btnEntrer = bouton("Entrer →", styleBouton);
            btnEntrer.setOnAction(e -> {
                if (estMaison) {
                    new FenetreListePieces(batiment, nomEtageCapture, nbAppartsParEtage).afficher(stage);
                } else {
                    new FenetreAppartement(
                            batiment,
                            nomEtageCapture,
                            surfaceEtage,
                            getNbApparts(nomEtageCapture),
                            nbAppartsParEtage
                    ).afficher(stage);
                }
            });

            HBox ligne;

            if (estMaison) {
                ligne = new HBox(18, lblEtage, lblInfo, btnEntrer);
            } else {
                Button btnVisualiser = bouton("Visualiser / placer couloir", styleBouton);
                Button btnAjouterPiece = bouton("+ Pièce commune", styleVert);

                btnVisualiser.setOnAction(e -> new PlanEtage(
                        batiment,
                        nomEtageCapture,
                        surfaceEtage,
                        nbAppartsParEtage
                ).afficher(stage));

                btnAjouterPiece.setOnAction(e -> ouvrirPieceCommune(stage, nomEtageCapture));

                ligne = new HBox(18, lblEtage, lblInfo, btnEntrer, btnVisualiser, btnAjouterPiece);
            }

            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle(styleLigne);
            ligne.setPadding(new Insets(10, 20, 10, 20));
            listeEtages.getChildren().add(ligne);
        }

        Button btnRetour = bouton("RETOUR", styleBouton);
        btnRetour.setOnAction(e -> retourAttributs(stage, estMaison));

        Button btnMenuPrincipal = bouton("MENU PRINCIPAL", styleBouton);
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
        stage.setFullScreen(true);
        stage.show();
    }

    private int getNbApparts(String nomEtage) {
        if (nbAppartsParEtage.containsKey(nomEtage)) {
            return nbAppartsParEtage.get(nomEtage);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("Etage.txt"))) {
            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] p = ligne.split(";");

                if (p.length >= 4
                        && p[1].trim().equalsIgnoreCase(batiment.getId())
                        && p[2].trim().equalsIgnoreCase(nomEtage)) {

                    int nb = Integer.parseInt(p[3].trim());
                    nbAppartsParEtage.put(nomEtage, nb);
                    return nb;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void ouvrirPieceCommune(Stage stage, String nomEtage) {
        if (!GestionCouloirEtage.couloirExiste(batiment.getId(), nomEtage)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Couloir obligatoire");
            alert.setHeaderText("Place d'abord le couloir.");
            alert.setContentText("Clique sur « Visualiser / placer couloir », puis valide le couloir.");
            alert.showAndWait();
            return;
        }

        new FenetrePieceCommuneEtage(batiment, nomEtage, nbAppartsParEtage).afficher(stage);
    }

    private void retourAttributs(Stage stage, boolean estMaison) {
        if (estMaison) {
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
    }

    private Button bouton(String texte, String style) {
        Button bouton = new Button(texte);
        bouton.setStyle(style);
        return bouton;
    }
}

