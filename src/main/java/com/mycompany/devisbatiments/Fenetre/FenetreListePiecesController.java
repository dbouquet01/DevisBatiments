package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FenetreListePiecesController {

    private final FenetreListePieces vue;

    public FenetreListePiecesController(FenetreListePieces vue) {
        this.vue = vue;
    }

    public void ajouterPiece(Stage stage) {
        TextField fieldNomPiece = vue.getFieldNomPiece();
        Label lblErreur = vue.getLblErreur();
        String nomPiece = fieldNomPiece.getText().trim();

        if (nomPiece.isEmpty()) {
            lblErreur.setText("Veuillez donner un nom à la pièce.");
            return;
        }

        if (pieceExisteDeja(nomPiece)) {
            lblErreur.setText("Cette pièce existe déjà.");
            return;
        }

        lblErreur.setText("");
        vue.getNomsPieces().add(nomPiece);
        fieldNomPiece.clear();
        actualiserListePieces(stage);
    }

    public void actualiserListePieces(Stage stage) {
        VBox listePieces = vue.getListePieces();
        String styleBouton = vue.getStyleBouton();
        listePieces.getChildren().clear();

        if (vue.getNomsPieces().isEmpty()) {
            Label lblVide = new Label("Aucune pièce enregistrée pour le moment.");
            lblVide.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");
            listePieces.getChildren().add(lblVide);
            return;
        }

        for (int i = 0; i < vue.getNomsPieces().size(); i++) {
            ajouterLignePiece(stage, listePieces, vue.getNomsPieces().get(i), i + 1, styleBouton);
        }
    }

    private void ajouterLignePiece(Stage stage, VBox listePieces, String nomPiece,
                                   int numero, String styleBouton) {
        Label lblNumero = new Label("Pièce " + numero);
        lblNumero.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");
        lblNumero.setMinWidth(90);

        Label lblNom = new Label(nomPiece);
        lblNom.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        lblNom.setMinWidth(300);

        Button btnEntrer = new Button("Entrer →");
        btnEntrer.setStyle(styleBouton);
        btnEntrer.setOnAction(e -> new FenetrePiece(
                vue.getBatiment(),
                vue.getVuePlan(),
                nomPiece,
                calculerSurfaceVue(),
                vue.getNomsPieces()
        ).afficher(stage));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #B00020; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> supprimerPiece(stage, nomPiece));

        HBox ligne = new HBox(20, lblNumero, lblNom, btnEntrer, btnSupprimer);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setMaxWidth(750);
        ligne.setStyle("-fx-background-color: #F4F4F4; -fx-border-color: #0F056B; "
                + "-fx-border-width: 1; -fx-padding: 10 20;");

        listePieces.getChildren().add(ligne);
    }

    private void supprimerPiece(Stage stage, String nomPiece) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer la pièce");
        confirmation.setHeaderText("Supprimer " + nomPiece + " ?");
        confirmation.setContentText("La pièce sera supprimée de la liste, du plan et du devis enregistré.");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                SauvegardeProjet.supprimerPiece(vue.getBatiment().getId(), vue.getVuePlan(), nomPiece);
                vue.getNomsPieces().remove(nomPiece);
                actualiserListePieces(stage);
            }
        });
    }

    public void retour(Stage stage) {
        Batiments batiment = vue.getBatiment();
        HashMap<String, Integer> nbAppartsParEtage = vue.getNbAppartsParEtage();

        if (batiment instanceof Maison) {
            new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage);
            return;
        }

        if (estVueAppartement()) {
            String etageParent = extraireEtageParent(vue.getVuePlan());

            new FenetreAppartement(
                    batiment,
                    etageParent,
                    batiment.getLargeur() * batiment.getLongueur(),
                    nbAppartsParEtage.getOrDefault(etageParent, 0),
                    nbAppartsParEtage
            ).afficher(stage);
            return;
        }

        new FenetreAppartement(
                batiment,
                vue.getRetourEtage(),
                batiment.getLargeur() * batiment.getLongueur(),
                nbAppartsParEtage.getOrDefault(vue.getRetourEtage(), 0),
                nbAppartsParEtage
        ).afficher(stage);
    }

    public void voirDevis(Button btnDevis) {
        Stage fenetreActuelle = (Stage) btnDevis.getScene().getWindow();
        fenetreActuelle.hide();

        new FenetreRecapitulatif(
                vue.getBatiment(),
                vue.getVuePlan(),
                fenetreActuelle
        ).afficher(new Stage());
    }

    public void chargerPiecesExistantes() {
        ArrayList<String> piecesSauvegardees = SauvegardeProjet.chargerNomsPieces(
                vue.getBatiment().getId(),
                vue.getVuePlan()
        );

        for (String piece : piecesSauvegardees) {
            if (!pieceExisteDeja(piece)) {
                vue.getNomsPieces().add(piece);
            }
        }
    }

    public double calculerSurfaceVue() {
        Batiments batiment = vue.getBatiment();

        if (batiment instanceof Maison || !estVueAppartement()) {
            return batiment.getLargeur() * batiment.getLongueur();
        }

        String etageParent = extraireEtageParent(vue.getVuePlan());
        String nomAppartement = extraireNomAppartement(vue.getVuePlan());

        String[] bloc = SauvegardeProjet.chargerElementPlan(
                batiment.getId(),
                etageParent,
                nomAppartement
        );

        if (bloc != null && bloc.length >= 7) {
            try {
                return Double.parseDouble(bloc[5].trim().replace(",", "."))
                        * Double.parseDouble(bloc[6].trim().replace(",", "."));
            } catch (Exception ignored) {
            }
        }

        return batiment.getLargeur() * batiment.getLongueur();
    }

    public boolean estVueAppartement() {
        if (vue.getVuePlan() == null || !vue.getVuePlan().contains("_")) {
            return false;
        }

        String nom = extraireNomAppartement(vue.getVuePlan());
        return normaliser(nom).startsWith("appartement") || normaliser(nom).startsWith("appart");
    }

    private String extraireEtageParent(String vueInterne) {
        String etage = vueInterne.substring(0, vueInterne.indexOf("_"));

        if (etage.equalsIgnoreCase("RDC")) {
            return "RDC";
        }

        if (etage.toLowerCase().startsWith("etage")) {
            return etage.replace("Etage", "Etage ");
        }

        return etage;
    }

    private String extraireNomAppartement(String vueInterne) {
        return vueInterne.substring(vueInterne.indexOf("_") + 1);
    }

    private boolean pieceExisteDeja(String nomPiece) {
        for (String nom : vue.getNomsPieces()) {
            if (normaliser(nom).equals(normaliser(nomPiece))) {
                return true;
            }
        }
        return false;
    }

    private String normaliser(String texte) {
        if (texte == null) {
            return "";
        }

        return texte.trim().toLowerCase()
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("ë", "e")
                .replace("à", "a")
                .replace("â", "a")
                .replace("ù", "u")
                .replace("û", "u")
                .replace("î", "i")
                .replace("ï", "i")
                .replace("ô", "o")
                .replace("ç", "c");
    }
}