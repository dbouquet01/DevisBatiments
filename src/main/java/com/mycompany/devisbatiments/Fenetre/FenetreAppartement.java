/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetreAppartement {

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;

    public FenetreAppartement(Batiments batiment, String nomEtage, double surfaceEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
    }

    public void afficher(Stage stage) {

        String styleLabel  = "-fx-font-size: 15px; -fx-font-weight: bold;";
        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

     
        Label titre = new Label("APPARTEMENTS — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));

      
        Label lblNb = new Label("Nombre d'appartements dans cet étage :");
        lblNb.setStyle(styleLabel);

        TextField fieldNbApparts = new TextField();
        fieldNbApparts.setMaxWidth(120);

        Button btnValider = new Button("Valider");
        btnValider.setStyle(styleBouton);

        Label lblErreur = new Label("");
        lblErreur.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        HBox ligneNb = new HBox(15, lblNb, fieldNbApparts, btnValider);
        ligneNb.setAlignment(Pos.CENTER);

        
        Label lblSurfaceInfo = new Label("");
        lblSurfaceInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #0F056B; -fx-font-weight: bold;");

        // --- LISTE DES APPARTEMENTS (cachée jusqu'à validation) ---
        VBox listeApparts = new VBox(10);
        listeApparts.setAlignment(Pos.CENTER);
        listeApparts.setPadding(new Insets(20));
        listeApparts.setVisible(false);

        btnValider.setOnAction(e -> {
            String txt = fieldNbApparts.getText().trim();
            if (txt.isEmpty()) {
                lblErreur.setText("Veuillez entrer un nombre d'appartements.");
                return;
            }
            try {
                int nbApparts = Integer.parseInt(txt);
                if (nbApparts <= 0) {
                    lblErreur.setText("Le nombre d'appartements doit être positif.");
                    return;
                }

                double surfaceParAppart = surfaceEtage / nbApparts;

                lblErreur.setText("");
                lblSurfaceInfo.setText("Surface de l'étage : " + surfaceEtage + " m²  —  "
                        + "Surface par appartement : " + String.format("%.2f", surfaceParAppart) + " m²");

                fieldNbApparts.setDisable(true);
                btnValider.setDisable(true);
                listeApparts.setVisible(true);
                listeApparts.getChildren().clear();

                // On génère la liste des appartements
                for (int i = 1; i <= nbApparts; i++) {
                    Label lblAppart = new Label("Appartement " + i);
                    lblAppart.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                    lblAppart.setMinWidth(250);

                    Button btnEntrer = new Button("Entrer →");
                    btnEntrer.setStyle(styleBouton);

                    final int numAppart = i;
                    final double surfaceCapture = surfaceParAppart;

                    btnEntrer.setOnAction(ev -> {
                        new FenetreListePieces(batiment, nomEtage, numAppart, surfaceCapture).afficher(stage);
                    });

                    HBox ligne = new HBox(20, lblAppart, btnEntrer);
                    ligne.setAlignment(Pos.CENTER_LEFT);
                    ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                            + "-fx-border-width: 1; -fx-padding: 10 20;");
                    listeApparts.getChildren().add(ligne);
                }

            } catch (NumberFormatException ex) {
                lblErreur.setText("Veuillez entrer un nombre valide.");
            }
        });

       
        Button btnRetour = new Button("RETOUR");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment).afficher(stage));

        HBox bottomBox = new HBox(btnRetour);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        
        VBox centre = new VBox(20, ligneNb, lblErreur, lblSurfaceInfo, listeApparts);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Appartement — " + nomEtage);
        stage.setScene(scene);
        stage.show();
    }
}