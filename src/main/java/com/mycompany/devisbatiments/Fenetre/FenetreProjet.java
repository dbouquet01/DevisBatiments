package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;
import com.mycompany.devisbatiments.elements.Immeuble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FenetreProjet {

    public void afficher(Stage stage) {

        final String[] idTrouve = {null};
        final String[] designationTrouvee = {null};
        final String[] typeTrouve = {null};
        final int[] nbEtagesTrouve = {0};
        final double[] surfaceTrouvee = {0};
        final double[] largeurTrouvee = {0};
        final double[] longueurTrouvee = {0};

        Label titre = new Label("RECHERCHER UN PROJET");
        titre.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;");

        VBox topContainer = new VBox(titre);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(30));

        TextField barreRecherche = new TextField();
        barreRecherche.setPromptText("Entrez le id du projet...");
        barreRecherche.setPrefWidth(300);

        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");

        HBox searchBar = new HBox(10, barreRecherche, btnRechercher);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setPadding(new Insets(20));

        VBox zoneResultats = new VBox(15);
        zoneResultats.setAlignment(Pos.TOP_LEFT);
        zoneResultats.setPadding(new Insets(20));
        zoneResultats.setStyle("-fx-border-color: #0F056B; -fx-border-width: 2; -fx-background-color: #F4F4F4;");
        zoneResultats.setVisible(false);

        Label lblInfos = new Label("Caractéristiques du bâtiment :");
        lblInfos.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Text detailsText = new Text();
        zoneResultats.getChildren().addAll(lblInfos, detailsText);

        btnRechercher.setOnAction(e -> {

            String nomCherche = barreRecherche.getText().trim();

            try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {

                String ligne;
                boolean trouve = false;

                reader.readLine();

                while ((ligne = reader.readLine()) != null) {

                    if (ligne.trim().isEmpty()) {
                        continue;
                    }

                    String[] parties = ligne.split(";");

                    if (parties.length < 8) {
                        continue;
                    }

                    String id = parties[0].trim();
                    String designation = parties[1].trim();
                    String type = parties[2].trim();
                    String etages = parties[3].trim();
                    String hauteur = parties[4].trim();
                    String surface = parties[5].trim();
                    String appartements = parties[6].trim();
                    String devis = parties[7].trim();
                    String largeur = parties[8].trim();
                    String longueur = parties[9].trim();
                   

                    if (id.equalsIgnoreCase(nomCherche)) {

                        idTrouve[0] = id;
                        designationTrouvee[0] = designation;
                        typeTrouve[0] = type;
                        nbEtagesTrouve[0] = Integer.parseInt(etages);
                        surfaceTrouvee[0] = Double.parseDouble(surface);
                        largeurTrouvee[0] = Double.parseDouble(largeur);
                        longueurTrouvee[0] = Double.parseDouble(longueur);
                       

                        detailsText.setText(
                                "ID Projet : " + id + "\n" +
                                "Nom : " + designation + "\n" +
                                "Type : " + type + "\n" +
                                "Nombre d'étages : " + etages + "\n" +
                                "Hauteur totale : " + hauteur + " m\n" +
                                "Surface totale : " + surface + " m²\n" +
                                "Nombre d'appartements : " + appartements + "\n" +
                                "ID Devis : " + devis + "\n"
                        );

                        zoneResultats.setVisible(true);
                        trouve = true;
                        break;
                    }
                }

                if (!trouve) {
                    idTrouve[0] = null;
                    detailsText.setText("Projet introuvable.");
                    zoneResultats.setVisible(true);
                }

            } catch (IOException ex) {
                detailsText.setText("Erreur : fichier Projets.txt introuvable.");
                zoneResultats.setVisible(true);
                ex.printStackTrace();
            }
        });

        Button retour = new Button("Retour");
        retour.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");
        retour.setOnAction(e -> new FenetreAccueil().afficher(stage));

        Button btnPlans = new Button("Voir les plans");
        btnPlans.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");

        btnPlans.setOnAction(e -> {
            if (idTrouve[0] == null) {
                detailsText.setText("Veuillez d'abord rechercher un projet.");
                zoneResultats.setVisible(true);
                return;
            }

            PlanVisualisation fenetrePlans = new PlanVisualisation(idTrouve[0]);
            fenetrePlans.afficher();
        });

        Button btnModification = new Button("Modifier");
        btnModification.setStyle("-fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #0F056B;-fx-text-fill: white;-fx-cursor: hand;");

        btnModification.setOnAction(e -> {
            if (idTrouve[0] == null) {
                detailsText.setText("Veuillez d'abord rechercher un projet.");
                zoneResultats.setVisible(true);
                return;
            }

            if (typeTrouve[0].equalsIgnoreCase("MAISON")) {
                new FenetreAttributsMaison().afficher(
                        stage,
                        idTrouve[0],
                        designationTrouvee[0],
                        largeurTrouvee[0],
                        longueurTrouvee[0],
                        nbEtagesTrouve[0]
                );
            } else if (typeTrouve[0].equalsIgnoreCase("IMMEUBLE")) {
                new FenetreAttributsImmeuble().afficher(
                        stage,
                        idTrouve[0],
                        designationTrouvee[0],
                        largeurTrouvee[0],
                        longueurTrouvee[0],
                        nbEtagesTrouve[0]
                );
            }
        });
        
        Button btnDevis = new Button("VOIR LE DEVIS");
        btnDevis.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        
        
        btnDevis.setOnAction(e -> {
            if (idTrouve[0] == null) {
                detailsText.setText("Veuillez d'abord rechercher un projet.");
                zoneResultats.setVisible(true);
                return;
            }

            Batiments batiment;

            if (typeTrouve[0].equalsIgnoreCase("MAISON")) {
                batiment = new Maison(
                        idTrouve[0],
                        designationTrouvee[0],
                        largeurTrouvee[0],
                        longueurTrouvee[0],
                        nbEtagesTrouve[0]
                );
            } else {
                batiment = new Immeuble(
                        idTrouve[0],
                        designationTrouvee[0],
                        largeurTrouvee[0],
                        longueurTrouvee[0],
                        nbEtagesTrouve[0]
                );
            }

            new FenetreRecapitulatif(batiment, "Tous les étages").afficher(stage);
        });
        HBox bottomContainer = new HBox();
        bottomContainer.setPadding(new Insets(15));
        bottomContainer.setSpacing(20);
        bottomContainer.setAlignment(Pos.CENTER_LEFT);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        bottomContainer.getChildren().addAll(
                retour,
                espace,
                btnPlans,
                btnDevis,
                btnModification
        );

        VBox layoutCentre = new VBox(20, searchBar, zoneResultats);
        layoutCentre.setPadding(new Insets(0, 50, 0, 50));

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(layoutCentre);
        root.setBottom(bottomContainer);

        Scene scene = new Scene(root);
        stage.setTitle("Recherche de Projet");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
