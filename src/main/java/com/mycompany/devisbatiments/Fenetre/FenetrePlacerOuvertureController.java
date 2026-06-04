package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Maison;
import com.mycompany.devisbatiments.elements.Ouverture;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FenetrePlacerOuvertureController {

    private static final double PANE_W = FenetrePlacerOuverture.PANE_W;
    private static final double PANE_H = FenetrePlacerOuverture.PANE_H;
    private static final double MARGE = 40;

    public enum Mur { HAUT, DROITE, BAS, GAUCHE }

    static class FenetreItem {
        Mur mur;
        double x;
        double y;

        FenetreItem(Mur mur, double x, double y) {
            this.mur = mur;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Fenetre - " + mur.name()
                    + "  x=" + String.format("%.2f", x)
                    + " m  y=" + String.format("%.2f", y) + " m";
        }
    }

    static class PorteItem {
        Mur mur;
        double x;
        double y;

        PorteItem(Mur mur, double x, double y) {
            this.mur = mur;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Porte - " + mur.name()
                    + "  x=" + String.format("%.2f", x)
                    + " m  y=" + String.format("%.2f", y) + " m";
        }
    }

    static class TremieItem {
        double x, y, largeur, longueur;

        TremieItem(double x, double y, double largeur, double longueur) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.longueur = longueur;
        }

        @Override
        public String toString() {
            return "Tremie  x=" + String.format("%.2f", x)
                    + " m  y=" + String.format("%.2f", y)
                    + " m  " + String.format("%.2f", largeur)
                    + "x" + String.format("%.2f", longueur) + " m";
        }
    }

    private final FenetrePlacerOuverture vue;
    private final Batiments batiment;
    private final String nomEtage;
    private final String nomPiece;

    private double pieceX = 0;
    private double pieceY = 0;
    private double pieceLargeur = 4;
    private double pieceLongueur = 3;
    private double pieceHauteur = 2.5;

    private final List<FenetreItem> fenetres = new ArrayList<>();
    private final List<PorteItem> portes = new ArrayList<>();
    private final List<TremieItem> tremies = new ArrayList<>();
    private final PlanOuverture dessin;

    public FenetrePlacerOuvertureController(FenetrePlacerOuverture vue, Batiments batiment, String nomEtage, String nomPiece) {
        this.vue = vue;
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.nomPiece = nomPiece;
        this.dessin = new PlanOuverture(vue, this);
    }

    public void initialiserDonnees() {
        fenetres.clear();
        portes.clear();
        tremies.clear();
        chargerDimensionsPiece();
        chargerOuverturesDepuisPlan();
    }

    public String getTexteDimensionsPiece() {
        return "Piece : " + String.format("%.2f", pieceLargeur)
                + " m (largeur) x " + String.format("%.2f", pieceLongueur)
                + " m (longueur) x " + String.format("%.2f", pieceHauteur) + " m (hauteur)";
    }

    public void connecterActions(Stage stage) {
        vue.getBtnAjouterFen().setOnAction(e -> ajouterFenetre());
        vue.getBtnSupprimerFen().setOnAction(e -> supprimerFenetre());

        vue.getBtnAjouterPor().setOnAction(e -> ajouterPorte());
        vue.getBtnSupprimerPor().setOnAction(e -> supprimerPorte());

        vue.getBtnAjouterTr().setOnAction(e -> ajouterTremie());
        vue.getBtnSupprimerTr().setOnAction(e -> supprimerTremie());

        vue.getBtnRetour().setOnAction(e -> new FenetrePiece(
                batiment,
                nomEtage,
                nomPiece,
                pieceLargeur * pieceLongueur,
                new ArrayList<>()
        ).afficher(stage));

        vue.getBtnVoirPlan().setOnAction(e -> {
            PlanVisualisation pv = new PlanVisualisation();
            pv.afficher();
        });
    }

    public void actualiserToutesLesListes() {
        vue.actualiserListe(vue.getListeFenetres(), fenetres);
        vue.actualiserListe(vue.getListePortes(), portes);
        vue.actualiserListe(vue.getListeTremies(), tremies);
    }

    private void ajouterFenetre() {
        try {
            Mur mur = murDepuis(vue.getComboMurFen().getValue());
            double x = parse(vue.getTfFenX());
            double y = parse(vue.getTfFenY());
            validerOffsetFenetre(mur, x, y);

            fenetres.add(new FenetreItem(mur, x, y));
            sauvegarderFenetrePlan(mur, x, y);
            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        } catch (Exception ex) {
            alerte("Fenetre invalide", ex.getMessage());
        }
    }

    private void supprimerFenetre() {
        int idx = vue.getListeFenetres().getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            fenetres.remove(idx);
            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        }
    }

    private void ajouterPorte() {
        try {
            Mur mur = murDepuis(vue.getComboMurPor().getValue());
            double x = parse(vue.getTfPorX());
            validerOffsetPorte(mur, x);

            portes.add(new PorteItem(mur, x, 0));
            sauvegarderPortePlan(mur, x);
            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        } catch (Exception ex) {
            alerte("Porte invalide", ex.getMessage());
        }
    }

    private void supprimerPorte() {
        int idx = vue.getListePortes().getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            portes.remove(idx);
            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        }
    }

    private void ajouterTremie() {
        try {
            double x = parse(vue.getTfTrX());
            double y = parse(vue.getTfTrY());
            double l = parse(vue.getTfTrL());
            double lo = parse(vue.getTfTrLo());
            validerTremie(x, y, l, lo);

            tremies.add(new TremieItem(x, y, l, lo));
            sauvegarderTremiePlan(x, y, l, lo);
            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        } catch (Exception ex) {
            alerte("Tremie invalide", ex.getMessage());
        }
    }

    private void supprimerTremie() {
        int idx = vue.getListeTremies().getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            tremies.remove(idx);

            reecrireTremiesPieceDansPlan();

            mettreAJourInfosPieceDepuisListes();
            actualiserToutesLesListes();
            dessinerPlan();
        }
    }

    public void dessinerPlan() {
        dessin.dessinerPlan();
    }

    double[] dimensionsEtage() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Projets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 10) continue;
                if (p[0].trim().equalsIgnoreCase(batiment.getId())) {
                    return new double[]{
                            Double.parseDouble(p[8].trim().replace(",", ".")),
                            Double.parseDouble(p[9].trim().replace(",", "."))
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0, 0};
    }

    Batiments getBatiment() { return batiment; }
    String getNomEtage() { return nomEtage; }
    String getNomPiece() { return nomPiece; }
    double getPieceX() { return pieceX; }
    double getPieceY() { return pieceY; }
    double getPieceLargeur() { return pieceLargeur; }
    double getPieceLongueur() { return pieceLongueur; }
    List<FenetreItem> getFenetres() { return fenetres; }
    List<PorteItem> getPortes() { return portes; }
    List<TremieItem> getTremies() { return tremies; }

    public boolean isMurHorizontal(Mur mur) {
        return mur == Mur.HAUT || mur == Mur.BAS;
    }

    private void validerOffsetFenetre(Mur mur, double x, double y) {
        double longueurMur = isMurHorizontal(mur) ? pieceLargeur : pieceLongueur;
        if (x < 0 || x + 1.2 > longueurMur) {
            throw new IllegalArgumentException(
                    "La distance x=" + x + " m depasse le mur (" + longueurMur + " m).");
        }
        if (y < 0 || y + 1.0 > pieceHauteur) {
            throw new IllegalArgumentException(
                    "La hauteur y=" + y + " m depasse la hauteur de la piece (" + pieceHauteur + " m).");
        }
    }

    private void validerOffsetPorte(Mur mur, double x) {
        double longueurMur = isMurHorizontal(mur) ? pieceLargeur : pieceLongueur;
        if (x < 0 || x + 0.9 > longueurMur) {
            throw new IllegalArgumentException(
                    "La porte en " + x + " m depasse le mur (" + longueurMur + " m).");
        }
    }

    private void validerTremie(double x, double y, double l, double lo) {
        if (x < 0 || x + l > pieceLargeur) {
            throw new IllegalArgumentException("La tremie depasse la largeur de la piece.");
        }
        if (y < 0 || y + lo > pieceLongueur) {
            throw new IllegalArgumentException("La tremie depasse la longueur de la piece.");
        }
        if (l <= 0 || lo <= 0) {
            throw new IllegalArgumentException("Les dimensions de la tremie doivent etre positives.");
        }
    }

    private void chargerDimensionsPiece() {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(nomEtage))) continue;
                if (!p[2].trim().equalsIgnoreCase(nomPiece)) continue;

                pieceX = Double.parseDouble(p[3].trim().replace(",", "."));
                pieceY = Double.parseDouble(p[4].trim().replace(",", "."));
                pieceLargeur = Double.parseDouble(p[5].trim().replace(",", "."));
                pieceLongueur = Double.parseDouble(p[6].trim().replace(",", "."));
                if (p.length > 7) {
                    try {
                        pieceHauteur = Double.parseDouble(p[7].trim().replace(",", "."));
                    } catch (Exception ignored) {
                    }
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge les fenêtres, portes et trémies déjà présentes dans PlanProjets.txt.
     *
     * Cela permet, pour un projet existant, de revoir dans les listes les
     * ouvertures déjà créées. Les listes affichent uniquement les coordonnées
     * utiles : mur + x/y pour fenêtres et portes, x/y + taille pour trémies.
     */
    private void chargerOuverturesDepuisPlan() {
        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 9) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(nomEtage))) continue;

                String nomElement = p[2].trim();
                String nomNormalise = normaliser(nomElement);
                String prefixe = normaliser(nomPiece + "_");
                if (!nomNormalise.startsWith(prefixe)) continue;

                double xPlan = Double.parseDouble(p[3].trim().replace(",", "."));
                double yPlan = Double.parseDouble(p[4].trim().replace(",", "."));
                double largeur = Double.parseDouble(p[5].trim().replace(",", "."));
                double longueur = Double.parseDouble(p[6].trim().replace(",", "."));
                double hauteurOuY = 0;
                try {
                    hauteurOuY = Double.parseDouble(p[7].trim().replace(",", "."));
                } catch (Exception ignored) {
                }

                if (nomNormalise.contains("FENETRE")) {
                    Mur mur = retrouverMurDepuisPlan(xPlan, yPlan, largeur, longueur);
                    double offset = retrouverOffsetDepuisPlan(mur, xPlan, yPlan);
                    fenetres.add(new FenetreItem(mur, offset, hauteurOuY));
                } else if (nomNormalise.contains("PORTE")) {
                    Mur mur = retrouverMurDepuisPlan(xPlan, yPlan, largeur, longueur);
                    double offset = retrouverOffsetDepuisPlan(mur, xPlan, yPlan);
                    portes.add(new PorteItem(mur, offset, 0));
                } else if (nomNormalise.contains("TREMIE")) {
                    tremies.add(new TremieItem(xPlan - pieceX, yPlan - pieceY, largeur, longueur));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Mur retrouverMurDepuisPlan(double x, double y, double largeur, double longueur) {
        double eps = 0.20;
        if (Math.abs(y - pieceY) < eps) return Mur.HAUT;
        if (Math.abs(y - (pieceY + pieceLongueur)) < eps) return Mur.BAS;
        if (Math.abs(x - pieceX) < eps) return Mur.GAUCHE;
        if (Math.abs(x - (pieceX + pieceLargeur)) < eps) return Mur.DROITE;
        return largeur >= longueur ? Mur.HAUT : Mur.GAUCHE;
    }

    private double retrouverOffsetDepuisPlan(Mur mur, double x, double y) {
        switch (mur) {
            case HAUT:
            case BAS:
                return x - pieceX;
            case GAUCHE:
            case DROITE:
                return y - pieceY;
            default:
                return 0;
        }
    }

    private void sauvegarderFenetrePlan(Mur mur, double offset, double hauteurDepuisSol) {
        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Fenetre" + fenetres.size(),
                pieceX + calculerXPlan(mur, offset),
                pieceY + calculerYPlan(mur, offset),
                isMurHorizontal(mur) ? Ouverture.LARGEUR_FENETRE_STANDARD : 0.10,
                isMurHorizontal(mur) ? Ouverture.LARGEUR_PORTE_STANDARD : 1.2,
                hauteurDepuisSol,
                10
        );
    }

    private void sauvegarderPortePlan(Mur mur, double offset) {
        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Porte" + portes.size(),
                pieceX + calculerXPlan(mur, offset),
                pieceY + calculerYPlan(mur, offset),
                isMurHorizontal(mur) ? Ouverture.LARGEUR_PORTE_STANDARD : 0.10,
                isMurHorizontal(mur) ? 0.10 : 0.9,
                Ouverture.HAUTEUR_PORTE_STANDARD,
                13
        );
    }

    private void sauvegarderTremiePlan(double x, double y, double largeur, double longueur) {
        double xPlan = pieceX + x;
        double yPlan = pieceY + y;

        SauvegardeProjet.sauvegarderElementPlan(
                batiment.getId(),
                nomEtage,
                nomPiece + "_Tremie" + tremies.size(),
                xPlan,
                yPlan,
                largeur,
                longueur,
                0,
                14
        );

        // Pour les maisons uniquement : une trémie placée dans une pièce
        // crée aussi l'ouverture correspondante au même emplacement sur
        // l'étage du dessus, afin que les surfaces et le devis de la pièce
        // supérieure soient recalculés avec cette déduction.
        if (batiment instanceof Maison && !estVueAppartementOuInterne()) {
            SauvegardeProjet.sauvegarderTremieEtageDessusMaison(
                    batiment.getId(),
                    nomEtage,
                    nomPiece,
                    xPlan,
                    yPlan,
                    largeur,
                    longueur,
                    14
            );
        }
    }

    private boolean estVueAppartementOuInterne() {
        return nomEtage != null && nomEtage.contains("_");
    }

    private void reecrireTremiesPieceDansPlan() {
        supprimerTremiesPieceDansPlan();

        int index = 1;
        for (TremieItem t : tremies) {
            double xPlan = pieceX + t.x;
            double yPlan = pieceY + t.y;

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(),
                    nomEtage,
                    nomPiece + "_Tremie" + index,
                    xPlan,
                    yPlan,
                    t.largeur,
                    t.longueur,
                    0,
                    14
            );

            if (batiment instanceof Maison && !estVueAppartementOuInterne()) {
                SauvegardeProjet.sauvegarderTremieEtageDessusMaison(
                        batiment.getId(),
                        nomEtage,
                        nomPiece,
                        xPlan,
                        yPlan,
                        t.largeur,
                        t.longueur,
                        14
                );
            }

            index++;
        }
    }

    private void supprimerTremiesPieceDansPlan() {
        try {
            Path path = Paths.get("PlanProjets.txt");
            if (!Files.exists(path)) {
                return;
            }

            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();
            String prefixeTremie = normaliser(nomPiece + "_Tremie");
            String prefixeTremieAuto = "TREMIEAUTO_" + nettoyerNomPourPlan(nomPiece);

            for (String ligne : lignes) {
                if (ligne.trim().isEmpty()) {
                    nouvellesLignes.add(ligne);
                    continue;
                }

                String[] p = ligne.split(";");
                if (p.length < 3) {
                    nouvellesLignes.add(ligne);
                    continue;
                }

                boolean memeProjet = normaliser(p[0]).equals(normaliser(batiment.getId()));
                boolean memeEtage = normaliser(p[1]).equals(normaliser(nomEtage));
                boolean tremiePieceCourante = normaliser(p[2]).startsWith(prefixeTremie);

                // Pour les maisons, les trémies automatiques de l'étage du dessus sont
                // nommées avec "TremieAuto_<pieceOrigine>". On les enlève aussi avant
                // de les recréer pour les trémies restantes.
                boolean tremieAutoLiee = normaliser(p[2]).contains(prefixeTremieAuto);

                if (memeProjet && ((memeEtage && tremiePieceCourante) || tremieAutoLiee)) {
                    continue;
                }

                nouvellesLignes.add(ligne);
            }

            Files.write(path, nouvellesLignes);
        } catch (Exception e) {
            e.printStackTrace();
            alerte("Suppression tremie", "Erreur pendant la mise a jour de PlanProjets.txt.");
        }
    }

    private String nettoyerNomPourPlan(String texte) {
        return normaliser(texte).replaceAll("[^A-Z0-9]", "");
    }

    private void mettreAJourInfosPieceDepuisListes() {
        double largeurTremie = 0;
        double longueurTremie = 0;
        if (!tremies.isEmpty()) {
            TremieItem derniere = tremies.get(tremies.size() - 1);
            largeurTremie = derniere.largeur;
            longueurTremie = derniere.longueur;
        }
        SauvegardeProjet.mettreAJourInfosOuverturesPiece(
                batiment.getId(),
                nomEtage,
                nomPiece,
                fenetres.size(),
                portes.size(),
                tremies.size(),
                largeurTremie,
                longueurTremie
        );
    }

    private double calculerXPlan(Mur mur, double offset) {
        switch (mur) {
            case HAUT:
            case BAS:
                return offset;
            case GAUCHE:
                return 0;
            case DROITE:
                return pieceLargeur;
            default:
                return 0;
        }
    }

    private double calculerYPlan(Mur mur, double offset) {
        switch (mur) {
            case HAUT:
                return 0;
            case BAS:
                return pieceLongueur;
            case GAUCHE:
            case DROITE:
                return offset;
            default:
                return 0;
        }
    }

    private Mur murDepuis(String val) {
        switch (val.toUpperCase()) {
            case "HAUT":
                return Mur.HAUT;
            case "BAS":
                return Mur.BAS;
            case "GAUCHE":
                return Mur.GAUCHE;
            case "DROITE":
                return Mur.DROITE;
            default:
                throw new IllegalArgumentException("Mur inconnu : " + val);
        }
    }

    private double parse(TextField f) {
        String txt = f.getText().trim().replace(",", ".");
        if (txt.isEmpty()) throw new IllegalArgumentException("Un champ est vide.");
        return Double.parseDouble(txt);
    }

    private void alerte(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.setContentText(msg);
        a.showAndWait();
    }



    boolean estVueAppartement() {
        if (nomEtage == null || !nomEtage.contains("_")) {
            return false;
        }
        String nomAppartement = extraireNomAppartement(nomEtage);
        return estNomAppartement(nomAppartement);
    }

    boolean estBlocAppartementCourant() {
        return !(batiment instanceof Maison) && estNomAppartement(nomPiece);
    }

    double[] dimensionsZonePlan() {
        if (estVueAppartement()) {
            double[] dimsAppartement = dimensionsAppartementParent();
            if (dimsAppartement[0] > 0 && dimsAppartement[1] > 0) {
                return new double[]{dimsAppartement[0], dimsAppartement[1]};
            }
        }

        if (estBlocAppartementCourant()) {
            return new double[]{pieceLargeur, pieceLongueur};
        }

        return dimensionsEtage();
    }

    private double[] dimensionsAppartementParent() {
        String etageParent = extraireEtageParent(nomEtage);
        String nomAppartement = extraireNomAppartement(nomEtage);

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 7) continue;
                if (!p[0].trim().equalsIgnoreCase(batiment.getId())) continue;
                if (!normaliser(p[1].trim()).equals(normaliser(etageParent))) continue;
                if (!normaliser(p[2].trim()).equals(normaliser(nomAppartement))) continue;

                return new double[]{
                        Double.parseDouble(p[5].trim().replace(",", ".")),
                        Double.parseDouble(p[6].trim().replace(",", "."))
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0, 0};
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

    private boolean estNomAppartement(String nom) {
        String n = normaliser(nom);
        return n.startsWith("APPARTEMENT") || n.startsWith("APPART");
    }

    String normaliser(String s) {
        if (s == null) return "";
        return s.trim().toUpperCase()
                .replace("\u00c9", "E").replace("\u00c8", "E").replace("\u00ca", "E")
                .replace("\u00c0", "A").replace("\u00c2", "A")
                .replace(" ", "");
    }
}
