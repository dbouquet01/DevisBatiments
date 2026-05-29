package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class FenetrePiece {

    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;
    private final double surfacePiece;
    private final ArrayList<String> nomsPieces;

    private Piece piece;

    public FenetrePiece(Batiments batiment, String nomEtage,
                        String nomPiece, double surfacePiece,
                        ArrayList<String> nomsPieces) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.surfacePiece = surfacePiece;
        this.nomsPieces = nomsPieces;
    }

    public void afficher(Stage stage) {

        String styleBouton =
                "-fx-background-color: #0F056B;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;";

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F5;");

        Label titre = new Label("CONFIGURATION PIÈCE — " + nomPiece + " / " + nomEtage);
        titre.setStyle("-fx-font-size: 22px;-fx-font-weight: bold;");

        HBox top = new HBox(titre);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(12));
        root.setTop(top);

        VBox panneauGauche = new VBox(12);
        panneauGauche.setPadding(new Insets(15));
        panneauGauche.setPrefWidth(760);

        // ── 1. Dimensions ──
        TextField fieldX       = new TextField();
        TextField fieldY       = new TextField();
        TextField fieldLargeur = new TextField();
        TextField fieldLongueur= new TextField();
        TextField fieldHauteur = new TextField();

        GridPane gridDim = new GridPane();
        gridDim.setHgap(10);
        gridDim.setVgap(10);
        gridDim.add(new Label("X origine :"),   0, 0); gridDim.add(fieldX,        1, 0);
        gridDim.add(new Label("Y origine :"),   0, 1); gridDim.add(fieldY,        1, 1);
        gridDim.add(new Label("Largeur (m) :"), 0, 2); gridDim.add(fieldLargeur,  1, 2);
        gridDim.add(new Label("Longueur (m) :"),0, 3); gridDim.add(fieldLongueur, 1, 3);
        gridDim.add(new Label("Hauteur (m) :"), 0, 4); gridDim.add(fieldHauteur,  1, 4);

        VBox boxDim = new VBox(10, new Label("1. Dimensions / position"), gridDim);
        boxDim.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );
        boxDim.setPrefWidth(250);

        // ── 2. Ouvertures (nombre seulement, surfaces standard utilisées pour le calcul) ──
        // Fenêtre standard : 1.2 m x 1.2 m = 1.44 m²
        // Porte standard   : 0.9 m x 2.1 m = 1.89 m²
        // Trémie standard  : 1.0 m x 2.5 m = 2.50 m²
        TextField fieldNbFenetre = new TextField(); fieldNbFenetre.setPromptText("0");
        TextField fieldNbPorte   = new TextField(); fieldNbPorte.setPromptText("0");
        TextField fieldNbTremie  = new TextField(); fieldNbTremie.setPromptText("0");

        GridPane gridOuv = new GridPane();
        gridOuv.setHgap(20);
        gridOuv.setVgap(20);
        gridOuv.add(new Label("Nombre de fenêtres :"),  0, 0); gridOuv.add(fieldNbFenetre, 1, 0);
        gridOuv.add(new Label("Nombre de portes :"),    0, 1); gridOuv.add(fieldNbPorte,   1, 1);
        gridOuv.add(new Label("Nombre de trémies :"),   0, 2); gridOuv.add(fieldNbTremie,  1, 2);

        VBox boxOuv = new VBox(10, new Label("2. Ouvertures"), gridOuv);
        boxOuv.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 20;" +
                "-fx-border-color: #0F056B;"
        );
        boxOuv.setPrefWidth(465);

        // ── 3. Revêtements ──
        ComboBox<Revetement> comboMur     = new ComboBox<>();
        ComboBox<Revetement> comboSol     = new ComboBox<>();
        ComboBox<Revetement> comboPlafond = new ComboBox<>();

        comboMur.getItems().addAll(Revetement.getRevetementsMur());
        comboSol.getItems().addAll(Revetement.getRevetementsSol());
        comboPlafond.getItems().addAll(Revetement.getRevetementsPlafond());

        GridPane gridRev = new GridPane();
        gridRev.setHgap(10);
        gridRev.setVgap(10);
        gridRev.add(new Label("Murs :"),    0, 0); gridRev.add(comboMur,     1, 0);
        gridRev.add(new Label("Sol :"),     0, 1); gridRev.add(comboSol,     1, 1);
        gridRev.add(new Label("Plafond :"), 0, 2); gridRev.add(comboPlafond, 1, 2);

        VBox boxRev = new VBox(10, new Label("3. Revêtements"), gridRev);
        boxRev.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );
        boxRev.setPrefWidth(715);

        // ── Chargement données existantes depuis Piece.txt ──
        String[] donnees = SauvegardeProjet.chargerPiece(batiment.getId(), nomEtage, nomPiece);
        if (donnees != null && donnees.length >= 18) {
            try {
                fieldX.setText(donnees[5].trim());
                fieldY.setText(donnees[6].trim());
                fieldLargeur.setText(donnees[9].trim());
                fieldLongueur.setText(donnees[10].trim());
                fieldHauteur.setText(donnees[11].trim());

                int idRevMur  = Integer.parseInt(donnees[15].trim());
                int idRevSol  = Integer.parseInt(donnees[16].trim());
                int idRevPlaf = Integer.parseInt(donnees[17].trim());

                for (Revetement r : comboMur.getItems())     { if (r.getIdRevetement() == idRevMur)  { comboMur.setValue(r);     break; } }
                for (Revetement r : comboSol.getItems())     { if (r.getIdRevetement() == idRevSol)  { comboSol.setValue(r);     break; } }
                for (Revetement r : comboPlafond.getItems()) { if (r.getIdRevetement() == idRevPlaf) { comboPlafond.setValue(r); break; } }
            } catch (Exception ex) {
                System.out.println("Impossible de charger les anciennes données de la pièce.");
            }
        }

        HBox ligneHaut = new HBox(15, boxDim, boxOuv);

        // ── 4. Calcul ──
        Label lblSurfaceMur     = new Label("Surface murs : -");
        Label lblSurfaceSol     = new Label("Surface sol : -");
        Label lblSurfacePlafond = new Label("Surface plafond : -");
        Label lblPrixMur        = new Label("Prix murs : -");
        Label lblPrixSol        = new Label("Prix sol : -");
        Label lblPrixPlafond    = new Label("Prix plafond : -");
        Label lblPrixTotal      = new Label("TOTAL : -");

        GridPane resultats = new GridPane();
        resultats.setHgap(30);
        resultats.setVgap(8);
        resultats.add(lblSurfaceMur,     0, 0); resultats.add(lblPrixMur,     1, 0);
        resultats.add(lblSurfaceSol,     0, 1); resultats.add(lblPrixSol,     1, 1);
        resultats.add(lblSurfacePlafond, 0, 2); resultats.add(lblPrixPlafond, 1, 2);
        resultats.add(lblPrixTotal,      0, 3);

        VBox boxCalcul = new VBox(10, new Label("4. Calcul"), resultats);
        boxCalcul.setStyle(
                "-fx-background-color: white;" +
                "-fx-padding: 12;" +
                "-fx-border-color: #0F056B;"
        );

        Label lblMessage = new Label("");

        Button btnCalculer    = new Button("CALCULER");    btnCalculer.setStyle(styleBouton);
        Button btnEnregistrer = new Button("ENREGISTRER"); btnEnregistrer.setStyle(styleBouton);
        Button btnRetour      = new Button("RETOUR");      btnRetour.setStyle(styleBouton);

        HBox boutons = new HBox(15, btnCalculer, btnEnregistrer, btnRetour);

        VBox zoneConfiguration = new VBox(15, ligneHaut, boxRev, boxCalcul);

        panneauGauche.getChildren().addAll(zoneConfiguration, lblMessage, boutons);

        PlanDessin plan = new PlanDessin(batiment, nomEtage);

        VBox panneauDessin = new VBox(10, new Label("Plan du projet"), plan);
        panneauDessin.setPadding(new Insets(15));

        root.setLeft(panneauGauche);
        root.setCenter(panneauDessin);

        // ── Action CALCULER ──
        btnCalculer.setOnAction(e -> {
            try {
                double x        = parseField(fieldX);
                double y        = parseField(fieldY);
                double largeur  = parseField(fieldLargeur);
                double longueur = parseField(fieldLongueur);
                double hauteur  = parseField(fieldHauteur);

                piece = new Piece(nomPiece, x, y, largeur, longueur, hauteur);

                Revetement revMur     = comboMur.getValue();
                Revetement revSol     = comboSol.getValue();
                Revetement revPlafond = comboPlafond.getValue();

                if (revMur == null || revSol == null || revPlafond == null) {
                    lblMessage.setStyle("-fx-text-fill: red;");
                    lblMessage.setText("Choisissez les 3 revêtements.");
                    return;
                }

                piece.appliquerRevetementMurs(revMur);
                piece.appliquerRevetementSol(revSol);
                piece.appliquerRevetementPlafond(revPlafond);

                // Surfaces standard : fenêtre 1.2x1.2, porte 0.9x2.1, trémie 1.0x2.5
                double nbFen = parseFieldOrZero(fieldNbFenetre);
                double nbPor = parseFieldOrZero(fieldNbPorte);
                double nbTre = parseFieldOrZero(fieldNbTremie);

                double soustractionMurs = (nbFen * 1.2 * 1.2) + (nbPor * 0.9 * 2.1);
                double soustractionSol  =  nbTre * 1.0 * 2.5;

                double surfMursReelle   = Math.max(0, piece.calculerSurfaceMurs()    - soustractionMurs);
                double surfSolReelle    = Math.max(0, piece.calculerSurfaceSol()     - soustractionSol);
                double surfPlafReelle   = Math.max(0, piece.calculerSurfacePlafond() - soustractionSol);

                double prixMurs   = revMur.calculerPrix(surfMursReelle);
                double prixSol    = revSol.calculerPrix(surfSolReelle);
                double prixPlaf   = revPlafond.calculerPrix(surfPlafReelle);
                double prixTotal  = prixMurs + prixSol + prixPlaf;

                lblSurfaceMur.setText(String.format(
                        "Surface murs réelle : %.2f m²  (brut %.2f - ouv. %.2f)",
                        surfMursReelle, piece.calculerSurfaceMurs(), soustractionMurs));
                lblSurfaceSol.setText(String.format(
                        "Surface sol réelle : %.2f m²", surfSolReelle));
                lblSurfacePlafond.setText(String.format(
                        "Surface plafond réelle : %.2f m²", surfPlafReelle));

                lblPrixMur.setText(String.format(    "Prix murs : %.2f €",    prixMurs));
                lblPrixSol.setText(String.format(    "Prix sol : %.2f €",     prixSol));
                lblPrixPlafond.setText(String.format("Prix plafond : %.2f €", prixPlaf));
                lblPrixTotal.setText(String.format(  "TOTAL : %.2f €",        prixTotal));

                lblMessage.setText("");

            } catch (Exception ex) {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Erreur : vérifiez les champs.");
                ex.printStackTrace();
            }
        });

        // ── Action ENREGISTRER ──
        btnEnregistrer.setOnAction(e -> {
            try {
                double x        = parseField(fieldX);
                double y        = parseField(fieldY);
                double largeur  = parseField(fieldLargeur);
                double longueur = parseField(fieldLongueur);
                double hauteur  = parseField(fieldHauteur);

                Revetement revMur     = comboMur.getValue();
                Revetement revSol     = comboSol.getValue();
                Revetement revPlafond = comboPlafond.getValue();

                if (revMur == null || revSol == null || revPlafond == null) {
                    lblMessage.setStyle("-fx-text-fill: red;");
                    lblMessage.setText("Choisissez les 3 revêtements.");
                    return;
                }

                piece = new Piece(nomPiece, x, y, largeur, longueur, hauteur);
                piece.appliquerRevetementMurs(revMur);
                piece.appliquerRevetementSol(revSol);
                piece.appliquerRevetementPlafond(revPlafond);

                double nbFen = parseFieldOrZero(fieldNbFenetre);
                double nbPor = parseFieldOrZero(fieldNbPorte);
                double nbTre = parseFieldOrZero(fieldNbTremie);

                double soustractionMurs = (nbFen * 1.2 * 1.2) + (nbPor * 0.9 * 2.1);
                double soustractionSol  =  nbTre * 1.0 * 2.5;

                double surfMursReelle   = Math.max(0, piece.calculerSurfaceMurs()    - soustractionMurs);
                double surfSolReelle    = Math.max(0, piece.calculerSurfaceSol()     - soustractionSol);
                double surfPlafReelle   = Math.max(0, piece.calculerSurfacePlafond() - soustractionSol);

                double prixMurs  = revMur.calculerPrix(surfMursReelle);
                double prixSol   = revSol.calculerPrix(surfSolReelle);
                double prixPlaf  = revPlafond.calculerPrix(surfPlafReelle);
                double prixTotal = prixMurs + prixSol + prixPlaf;

                SauvegardeProjet.sauvegarderElementPlan(
                        batiment.getId(), nomEtage, nomPiece,
                        x, y, largeur, longueur, hauteur,
                        revSol.getIdRevetement()
                );

                SauvegardeProjet.sauvegarderDevis(
                        "D_" + batiment.getId(), batiment.getId(), nomPiece,
                        prixMurs, prixSol, prixPlaf, prixTotal
                );

                SauvegardeProjet.sauvegarderPiece(
                        batiment.getId(), nomEtage, nomPiece,
                        x, y, largeur, longueur, hauteur,
                        revMur.getIdRevetement(),
                        revSol.getIdRevetement(),
                        revPlafond.getIdRevetement(),
                        prixMurs, prixSol, prixPlaf, prixTotal
                );

                plan.actualiser();

                lblMessage.setStyle("-fx-text-fill: green;");
                lblMessage.setText("Pièce enregistrée et plan mis à jour.");

            } catch (Exception ex) {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Impossible d'enregistrer la pièce.");
                ex.printStackTrace();
            }
        });

        btnRetour.setOnAction(e -> {
            new FenetreListePieces(batiment, nomEtage, nomsPieces).afficher(stage);
        });

        Scene scene = new Scene(root, 1450, 820);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    private double parseField(TextField f) {
        return Double.parseDouble(f.getText().trim().replace(",", "."));
    }

    private double parseFieldOrZero(TextField f) {
        try {
            String txt = f.getText().trim().replace(",", ".");
            if (txt.isEmpty()) return 0;
            return Double.parseDouble(txt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}