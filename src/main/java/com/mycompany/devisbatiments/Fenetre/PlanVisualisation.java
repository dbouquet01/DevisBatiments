package com.mycompany.devisbatiments.Fenetre;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PlanVisualisation {

    private Pane zoneDessin;
    private String projetInitial;
    private Stage fenetrePrecedente;
    private PlanVisualisationController controller;

    public PlanVisualisation() {
        this.projetInitial = "";
    }

    public PlanVisualisation(String projetInitial) {
        this.projetInitial = projetInitial;
    }

    public PlanVisualisation(String projetInitial, Stage fenetrePrecedente) {
        this.projetInitial = projetInitial;
        this.fenetrePrecedente = fenetrePrecedente;
    }

    public void afficher() {
        Stage stage = new Stage();
        controller = new PlanVisualisationController(this);

        stage.setOnHidden(e -> {
            if (fenetrePrecedente != null) {
                fenetrePrecedente.show();
            }
        });

        Label lblProjet = new Label("PROJET :");
        lblProjet.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField champProjet = new TextField(projetInitial);
        champProjet.setPromptText("Ex : P1");

        Label lblVue = new Label("VUE :");
        lblVue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField champVue = new TextField();
        champVue.setPromptText("Ex : RDC / ETAGE1 / FACE");

        Button btnAfficher = new Button("AFFICHER");
        btnAfficher.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #0F056B;" +
                "-fx-text-fill: white;" +
                "-fx-cursor: hand;"
        );

        zoneDessin = new Pane();
        zoneDessin.setPrefSize(1000, 500);
        zoneDessin.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #0F056B;" +
                "-fx-border-width: 2;"
        );

        btnAfficher.setOnAction(e -> controller.afficherPlan(
                champProjet.getText().trim(),
                champVue.getText().trim()
        ));

        HBox topBar = new HBox(10, lblProjet, champProjet, lblVue, champVue, btnAfficher);
        topBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(zoneDessin);

        Scene scene = new Scene(root, 1100, 600);
        stage.setScene(scene);
        stage.setTitle("Visualisation des Plans");
        stage.setMaximized(true);
        stage.setOnCloseRequest(e -> {
            e.consume();
            new FenetreProjet().afficher(stage);
        });
        stage.show();
    }

    public Pane getZoneDessin() {
        return zoneDessin;
    }
}
