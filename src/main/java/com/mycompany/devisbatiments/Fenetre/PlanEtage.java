package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;

public class PlanEtage {

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;

    private Stage stage;
    private Label lblCouloir;
    private Label lblEscalier;
    private Label lblEtat;
    private Label lblEtatEscalier;
    private ComboBox<Revetement> choixRevetementCouloir;
    private Slider sliderCouloir;
    private Slider sliderEscalier;
    private VBox centre;

    public PlanEtage(Batiments batiment, String nomEtage,
                     double surfaceEtage,
                     HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {
        this.stage = stage;

        PlanEtageController controller = new PlanEtageController(this, batiment, nomEtage, surfaceEtage, nbAppartsParEtage);

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        String styleValider = "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("PLAN DE L'ÉTAGE — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));

        Label info = new Label(controller.creerTexteInfo());
        info.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F056B;");

        sliderCouloir = new Slider(0, controller.getYCouloirMax(), controller.getYCouloirInitial());
        sliderCouloir.setShowTickLabels(true);
        sliderCouloir.setShowTickMarks(true);
        sliderCouloir.setMajorTickUnit(1);
        sliderCouloir.setBlockIncrement(0.25);
        sliderCouloir.setPrefWidth(420);

        lblCouloir = new Label();
        lblCouloir.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F056B;");

        lblEtat = new Label(controller.getTexteEtatCouloirInitial());
        lblEtat.setStyle(controller.getStyleEtatCouloirInitial());

        choixRevetementCouloir = new ComboBox<>();
        choixRevetementCouloir.getItems().addAll(Revetement.getRevetementsSol());
        choixRevetementCouloir.setPromptText("Revêtement du couloir");
        controller.initialiserChoixRevetement(choixRevetementCouloir);

        Button btnValiderCouloir = new Button("VALIDER COULOIR + ESCALIER");
        btnValiderCouloir.setStyle(styleValider);

        sliderEscalier = new Slider(0, controller.getXEscalierMax(), controller.getXEscalierInitial());
        sliderEscalier.setShowTickLabels(true);
        sliderEscalier.setShowTickMarks(true);
        sliderEscalier.setMajorTickUnit(1);
        sliderEscalier.setBlockIncrement(0.25);
        sliderEscalier.setPrefWidth(320);

        lblEscalier = new Label();
        lblEscalier.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F056B;");

        lblEtatEscalier = new Label(controller.getTexteEtatEscalierInitial());
        lblEtatEscalier.setStyle(controller.getStyleEtatEscalierInitial());

        HBox ligneChoixCouloir = new HBox(
                15,
                new Label("Position Y du couloir :"),
                sliderCouloir,
                lblCouloir,
                new Label("Revêtement :"),
                choixRevetementCouloir,
                lblEtat
        );
        ligneChoixCouloir.setAlignment(Pos.CENTER);

        HBox ligneChoixEscalier = new HBox(
                15,
                new Label("Position X escalier/trémie :"),
                sliderEscalier,
                lblEscalier,
                btnValiderCouloir,
                lblEtatEscalier
        );
        ligneChoixEscalier.setAlignment(Pos.CENTER);

        Pane dessin = controller.creerDessinActuel();
        lblCouloir.setText(String.format("%.2f m", sliderCouloir.getValue()));
        lblEscalier.setText(String.format("%.2f m", sliderEscalier.getValue()));

        centre = new VBox(15, info, ligneChoixCouloir, ligneChoixEscalier, dessin);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(15));

        sliderCouloir.valueProperty().addListener((obs, oldValue, newValue) -> controller.actualiserDepuisCouloir(newValue.doubleValue()));
        sliderEscalier.valueProperty().addListener((obs, oldValue, newValue) -> controller.actualiserDepuisEscalier(newValue.doubleValue()));
        choixRevetementCouloir.setOnAction(e -> controller.actualiserDessin());
        btnValiderCouloir.setOnAction(e -> controller.validerCouloirEtEscalier());

        Button btnRetour = new Button("RETOUR ÉTAGES");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        Button btnMenuPrincipal = new Button("MENU PRINCIPAL");
        btnMenuPrincipal.setStyle(styleBouton);
        btnMenuPrincipal.setOnAction(e -> new FenetreProjet().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnMenuPrincipal);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1250, 720);
        stage.setTitle("Plan étage");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    void remplacerDessin(Pane nouveauDessin) {
        centre.getChildren().set(3, nouveauDessin);
    }

    public Label getLblCouloir() { return lblCouloir; }
    public Label getLblEscalier() { return lblEscalier; }
    public Label getLblEtat() { return lblEtat; }
    public Label getLblEtatEscalier() { return lblEtatEscalier; }
    public ComboBox<Revetement> getChoixRevetementCouloir() { return choixRevetementCouloir; }
    public Slider getSliderCouloir() { return sliderCouloir; }
    public Slider getSliderEscalier() { return sliderEscalier; }
    public Stage getStage() { return stage; }
}
