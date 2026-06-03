package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Maison;
import com.mycompany.devisbatiments.elements.Ouverture;
import com.mycompany.devisbatiments.elements.Piece;
import com.mycompany.devisbatiments.elements.Revetement;
import com.mycompany.devisbatiments.elements.Tremie;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;


public class FenetrePieceController {

    private final FenetrePiece vue;

    public FenetrePieceController(FenetrePiece vue) {
        this.vue = vue;
    }

    public void initialiserActions(Stage stage) {
        vue.btnCalculer.setOnAction(e -> calculer());

        vue.btnEnregistrer.setOnAction(e -> {
            enregistrer();
            actualiserPlan(vue.plan);
        });

        vue.btnRetour.setOnAction(e -> retour(stage));
        vue.btnMenu.setOnAction(e -> new FenetreAccueil().afficher(stage));

        vue.btnPlacerOuverture.setOnAction(e -> new FenetrePlacerOuverture(
                vue.batiment,
                vue.nomEtage,
                vue.nomPiece
        ).afficher(stage));
    }

    public void chargerDonnees() {
        String[] plan = SauvegardeProjet.chargerElementPlan(vue.batiment.getId(), vue.nomEtage, vue.nomPiece);

        if (plan != null && plan.length >= 8) {
            vue.fieldX.setText(plan[3]);
            vue.fieldY.setText(plan[4]);
            vue.fieldLargeur.setText(plan[5]);
            vue.fieldLongueur.setText(plan[6]);
            vue.fieldHauteur.setText(plan[7]);
        }

        String[] piece = SauvegardeProjet.chargerPiece(vue.batiment.getId(), vue.nomEtage, vue.nomPiece);

        if (piece != null && piece.length >= 18) {
            selectionner(vue.comboMur, piece[15]);
            selectionner(vue.comboSol, piece[16]);
            selectionner(vue.comboPlafond, piece[17]);
        }

        if (piece != null && piece.length >= 27) {
            vue.fieldNbFenetre.setText(piece[22]);
            vue.fieldNbPorte.setText(piece[23]);
            vue.fieldNbTremie.setText(piece[24]);
            vue.fieldLargeurTremie.setText(piece[25]);
            vue.fieldLongueurTremie.setText(piece[26]);
        } else {
            int[] compteurs = SauvegardeProjet.compterElementsPlanPiece(
                    vue.batiment.getId(), vue.nomEtage, vue.nomPiece
            );
            double[] dimTremie = SauvegardeProjet.chargerDerniereTremiePiece(
                    vue.batiment.getId(), vue.nomEtage, vue.nomPiece
            );

            vue.fieldNbFenetre.setText(String.valueOf(compteurs[0]));
            vue.fieldNbPorte.setText(String.valueOf(compteurs[1]));
            vue.fieldNbTremie.setText(String.valueOf(compteurs[2]));

            if (dimTremie[0] > 0) {
                vue.fieldLargeurTremie.setText(String.valueOf(dimTremie[0]));
            }
            if (dimTremie[1] > 0) {
                vue.fieldLongueurTremie.setText(String.valueOf(dimTremie[1]));
            }
        }
    }

    public Node creerPlan() {
        if (vue.batiment instanceof Maison) {
            return new PlanDessin(vue.batiment, vue.nomEtage);
        }

        if (estVueAppartement()) {
            DimensionsAppartement dim = chargerDimensionsAppartement();
            return new PlanDessin(vue.batiment, vue.nomEtage, dim.largeur, dim.longueur);
        }

        return new PlanBloc(vue.batiment, vue.nomEtage, vue.nomPiece);
    }

    public String getTitrePlan() {
        if (estVueAppartement()) {
            return "Plan de l'appartement";
        }
        return vue.batiment instanceof Maison ? "Plan de l'étage" : "Plan du bloc";
    }

    private void retour(Stage stage) {
        if (vue.batiment instanceof Maison) {
            new FenetreListePieces(
                    vue.batiment,
                    vue.nomEtage,
                    vue.nomsPieces
            ).afficher(stage);
            return;
        }

        HashMap<String, Integer> nbAppartsParEtage = chargerNbAppartsParEtage();

        if (estVueAppartement()) {
            String etageParent = extraireEtageParent(vue.nomEtage);

            new FenetreListePieces(
                    vue.batiment,
                    vue.nomEtage,
                    etageParent,
                    nbAppartsParEtage
            ).afficher(stage);
            return;
        }

        new FenetreAppartement(
                vue.batiment,
                vue.nomEtage,
                vue.batiment.getLargeur() * vue.batiment.getLongueur(),
                nbAppartsParEtage.getOrDefault(vue.nomEtage, 0),
                nbAppartsParEtage
        ).afficher(stage);
    }

    private void actualiserPlan(Node plan) {
        if (plan instanceof PlanDessin) {
            ((PlanDessin) plan).actualiser();
        } else if (plan instanceof PlanBloc) {
            ((PlanBloc) plan).actualiser();
        }
    }

    private void selectionner(ComboBox<Revetement> combo, String idTexte) {
        try {
            int id = Integer.parseInt(idTexte.trim());

            for (Revetement r : combo.getItems()) {
                if (r.getIdRevetement() == id) {
                    combo.setValue(r);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void calculer() {
        try {
            Resultat r = faireCalcul();

            vue.lblSurfaceMur.setText(String.format("Surface murs : %.2f m²", r.surfaceMurs));
            vue.lblSurfaceSol.setText(String.format("Surface sol : %.2f m²", r.surfaceSol));
            vue.lblSurfacePlafond.setText(String.format("Surface plafond : %.2f m²", r.surfacePlafond));
            vue.lblSurfaceTremie.setText(String.format("Surface escalier : %.2f m²", r.surfaceTremie));

            vue.lblPrixMur.setText(String.format("Prix murs : %.2f €", r.coutMurs));
            vue.lblPrixSol.setText(String.format("Prix sol : %.2f €", r.coutSol));
            vue.lblPrixPlafond.setText(String.format("Prix plafond : %.2f €", r.coutPlafond));
            vue.lblPrixTremie.setText(String.format("Prix escalier : %.2f €", r.coutTremie));
            vue.lblPrixTotal.setText(String.format("TOTAL : %.2f €", r.total));

            vue.lblMessage.setText("");
        } catch (Exception e) {
            vue.lblMessage.setStyle("-fx-text-fill: red;");
            vue.lblMessage.setText("Erreur : vérifie les champs.");
        }
    }

    private void enregistrer() {
        try {
            Resultat r = faireCalcul();

            SauvegardeProjet.sauvegarderElementPlan(
                    vue.batiment.getId(), vue.nomEtage, vue.nomPiece,
                    r.x, r.y, r.largeur, r.longueur, r.hauteur,
                    vue.comboSol.getValue().getIdRevetement()
            );

            SauvegardeProjet.sauvegarderPiece(
                    vue.batiment.getId(), vue.nomEtage, vue.nomPiece,
                    r.x, r.y, r.largeur, r.longueur, r.hauteur,
                    vue.comboMur.getValue().getIdRevetement(),
                    vue.comboSol.getValue().getIdRevetement(),
                    vue.comboPlafond.getValue().getIdRevetement(),
                    r.coutMurs, r.coutSol, r.coutPlafond, r.total,
                    (int) parseZero(vue.fieldNbFenetre),
                    (int) parseZero(vue.fieldNbPorte),
                    (int) parseZero(vue.fieldNbTremie),
                    parseZero(vue.fieldLargeurTremie),
                    parseZero(vue.fieldLongueurTremie)
            );

            SauvegardeProjet.sauvegarderDevis(
                    "D_" + vue.batiment.getId(), vue.batiment.getId(), vue.nomPiece,
                    r.coutMurs, r.coutSol, r.coutPlafond, r.total
            );

            vue.lblMessage.setStyle("-fx-text-fill: green;");
            vue.lblMessage.setText("Pièce enregistrée.");
        } catch (Exception e) {
            vue.lblMessage.setStyle("-fx-text-fill: red;");
            vue.lblMessage.setText("Impossible d'enregistrer.");
        }
    }

    private Resultat faireCalcul() {
        if (vue.comboMur.getValue() == null
                || vue.comboSol.getValue() == null
                || vue.comboPlafond.getValue() == null
                || vue.comboTremie.getValue() == null) {
            throw new IllegalArgumentException();
        }

        double x = parse(vue.fieldX);
        double y = parse(vue.fieldY);
        double largeur = parse(vue.fieldLargeur);
        double longueur = parse(vue.fieldLongueur);
        double hauteur = parse(vue.fieldHauteur);

        verifierDimensions(x, y, largeur, longueur, hauteur);

        Piece piece = new Piece(vue.nomPiece, x, y, largeur, longueur, hauteur);

        Ouverture fenetres = new Ouverture("Fenetre", (int) parseZero(vue.fieldNbFenetre));
        Ouverture portes = new Ouverture("Porte", (int) parseZero(vue.fieldNbPorte));

        double ouverturesMurs = fenetres.calculerSurfaceTotale() + portes.calculerSurfaceTotale();
        double nbTremie = parseZero(vue.fieldNbTremie);
        double largeurTremie = parseZero(vue.fieldLargeurTremie);
        double longueurTremie = parseZero(vue.fieldLongueurTremie);

        Tremie tremie = new Tremie(0, 0, largeurTremie, longueurTremie);
        tremie.setRevetement(vue.comboTremie.getValue());

        double surfaceTremieAuSol = nbTremie * tremie.calculerSurfaceAuSol();
        double surfaceMurs = Math.max(0, piece.calculerSurfaceMurs() - ouverturesMurs);
        double surfaceSol = Math.max(0, piece.calculerSurfaceSol() - surfaceTremieAuSol);
        double surfacePlafond = Math.max(0, piece.calculerSurfacePlafond() - surfaceTremieAuSol);
        double surfaceTremie = nbTremie * tremie.calculerSurfaceRevetement();

        double coutMurs = vue.comboMur.getValue().calculerPrix(surfaceMurs);
        double coutSol = vue.comboSol.getValue().calculerPrix(surfaceSol);
        double coutPlafond = vue.comboPlafond.getValue().calculerPrix(surfacePlafond);
        double coutTremie = vue.comboTremie.getValue().calculerPrix(surfaceTremie);

        return new Resultat(x, y, largeur, longueur, hauteur,
                surfaceMurs, surfaceSol, surfacePlafond, surfaceTremie,
                coutMurs, coutSol, coutPlafond, coutTremie);
    }

    private void verifierDimensions(double x, double y, double largeur, double longueur, double hauteur) {
        if (largeur <= 0 || longueur <= 0 || hauteur <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }

        if (estVueAppartement()) {
            DimensionsAppartement dim = chargerDimensionsAppartement();

            if (x + largeur > dim.largeur || y + longueur > dim.longueur) {
                throw new IllegalArgumentException();
            }
        }
    }

    private double parse(TextField field) {
        return Double.parseDouble(field.getText().trim().replace(",", "."));
    }

    private double parseZero(TextField field) {
        try {
            String texte = field.getText().trim().replace(",", ".");
            return texte.isEmpty() ? 0 : Double.parseDouble(texte);
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean estVueAppartement() {
        if (vue.nomEtage == null || !vue.nomEtage.contains("_")) {
            return false;
        }

        String nomAppartement = extraireNomAppartement(vue.nomEtage);
        String n = normaliser(nomAppartement);
        return n.startsWith("appartement") || n.startsWith("appart");
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

    private DimensionsAppartement chargerDimensionsAppartement() {
        String etageParent = extraireEtageParent(vue.nomEtage);
        String nomAppartement = extraireNomAppartement(vue.nomEtage);

        String[] bloc = SauvegardeProjet.chargerElementPlan(
                vue.batiment.getId(),
                etageParent,
                nomAppartement
        );

        if (bloc != null && bloc.length >= 7) {
            try {
                double largeur = Double.parseDouble(bloc[5].trim().replace(",", "."));
                double longueur = Double.parseDouble(bloc[6].trim().replace(",", "."));

                if (largeur > 0 && longueur > 0) {
                    return new DimensionsAppartement(largeur, longueur);
                }
            } catch (Exception ignored) {
            }
        }

        return new DimensionsAppartement(vue.batiment.getLargeur(), vue.batiment.getLongueur());
    }

    private HashMap<String, Integer> chargerNbAppartsParEtage() {
        HashMap<String, Integer> nbAppartsParEtage = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Etage.txt"))) {
            reader.readLine();

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] p = ligne.split(";");

                if (p.length >= 4 && p[1].trim().equalsIgnoreCase(vue.batiment.getId())) {
                    nbAppartsParEtage.put(
                            p[2].trim(),
                            Integer.parseInt(p[3].trim())
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nbAppartsParEtage;
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

    private static class DimensionsAppartement {
        double largeur;
        double longueur;

        DimensionsAppartement(double largeur, double longueur) {
            this.largeur = largeur;
            this.longueur = longueur;
        }
    }

    private static class Resultat {
        double x, y, largeur, longueur, hauteur;
        double surfaceMurs, surfaceSol, surfacePlafond, surfaceTremie;
        double coutMurs, coutSol, coutPlafond, coutTremie, total;

        Resultat(double x, double y, double largeur, double longueur, double hauteur,
                 double surfaceMurs, double surfaceSol, double surfacePlafond, double surfaceTremie,
                 double coutMurs, double coutSol, double coutPlafond, double coutTremie) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.longueur = longueur;
            this.hauteur = hauteur;
            this.surfaceMurs = surfaceMurs;
            this.surfaceSol = surfaceSol;
            this.surfacePlafond = surfacePlafond;
            this.surfaceTremie = surfaceTremie;
            this.coutMurs = coutMurs;
            this.coutSol = coutSol;
            this.coutPlafond = coutPlafond;
            this.coutTremie = coutTremie;
            this.total = coutMurs + coutSol + coutPlafond + coutTremie;
        }
    }
}
