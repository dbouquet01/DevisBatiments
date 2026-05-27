package com.mycompany.devisbatiments.donnees;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SauvegardeProjet {

    private static final String FICHIER_PROJETS = "Projets.txt";
    private static final String FICHIER_PLAN = "PlanProjets.txt";
    private static final String FICHIER_DEVIS = "Devis.txt";
    private static final String FICHIER_ETAGE = "Etage.txt";
    private static final String FICHIER_PIECE = "Piece.txt";

    private static final String HEADER_PROJETS =
            "idProjet;designation;type;nombreEtages;hauteurTotale;surfaceTotale;nombreAppartements;idDevis;largeur;longueur";

    private static final String HEADER_PLAN =
            "idProjet;vue;piece;x;y;largeur;longueur;hauteur;idRevetement";

    private static final String HEADER_DEVIS =
            "idDevis;idProjet;element;coutMurs;coutSol;coutPlafond;total";

    private static final String HEADER_ETAGE =
            "idEtage;idProjet;nomEtage;nbAppartements;nbPieces;presenceEscalier;presenceAscenseur;surfaceTotale;devis";

    private static final String HEADER_PIECE =
            "idPiece;idProjet;idEtage;nomPiece;vue;xPlan;yPlan;largeurPlan;hauteurPlan;"
                    + "largeurMetres;longueurMetres;hauteurMetres;surfaceSol;surfaceMurs;surfacePlafond;"
                    + "idRevetementMur;idRevetementSol;idRevetementPlafond;devisMurs;devisSol;devisPlafond;devisTotal";

    private static void verifierFichier(String nomFichier, String header) throws IOException {
        Path path = Paths.get(nomFichier);

        if (!Files.exists(path)) {
            List<String> lignes = new ArrayList<>();
            lignes.add(header);
            Files.write(path, lignes);
        }
    }

    public static void sauvegarderProjet(String idProjet, String designation, String type,
                                         int nombreEtages, double hauteurTotale,
                                         double surfaceTotale, int nombreAppartements,
                                         String idDevis, double largeur, double longueur) {
        try {
            verifierFichier(FICHIER_PROJETS, HEADER_PROJETS);

            Path path = Paths.get(FICHIER_PROJETS);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            String nouvelleLigne =
                    idProjet + ";" +
                    designation + ";" +
                    type + ";" +
                    nombreEtages + ";" +
                    hauteurTotale + ";" +
                    surfaceTotale + ";" +
                    nombreAppartements + ";" +
                    idDevis + ";" +
                    largeur + ";" +
                    longueur;

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_PROJETS : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length > 0 && normaliser(parts[0]).equals(normaliser(idProjet))) {
                    nouvellesLignes.add(nouvelleLigne);
                    remplace = true;
                } else {
                    nouvellesLignes.add(ligne);
                }
            }

            if (!remplace) {
                nouvellesLignes.add(nouvelleLigne);
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderEtage(String idProjet, String nomEtage,
                                        int nbAppartements, double surfaceTotale) {
        try {
            verifierFichier(FICHIER_ETAGE, HEADER_ETAGE);

            Path path = Paths.get(FICHIER_ETAGE);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            String idEtage = idProjet + "_" + nomEtage.replace(" ", "").toUpperCase();

            String nouvelleLigne =
                    idEtage + ";" +
                    idProjet + ";" +
                    nomEtage + ";" +
                    nbAppartements + ";" +
                    0 + ";" +
                    false + ";" +
                    false + ";" +
                    surfaceTotale + ";" +
                    0;

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_ETAGE : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length > 0 && normaliser(parts[0]).equals(normaliser(idEtage))) {
                    nouvellesLignes.add(nouvelleLigne);
                    remplace = true;
                } else {
                    nouvellesLignes.add(ligne);
                }
            }

            if (!remplace) {
                nouvellesLignes.add(nouvelleLigne);
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderElementPlan(String idProjet, String vue, String nomElement,
                                              double x, double y,
                                              double largeur, double longueur,
                                              double hauteur,
                                              int idRevetement) {
        try {
            verifierFichier(FICHIER_PLAN, HEADER_PLAN);

            Path path = Paths.get(FICHIER_PLAN);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            String nouvelleLigne =
                    idProjet + ";" +
                    vue + ";" +
                    nomElement + ";" +
                    x + ";" +
                    y + ";" +
                    largeur + ";" +
                    longueur + ";" +
                    hauteur + ";" +
                    idRevetement;

            boolean remplace = false;

            String idProjetNormalise = normaliser(idProjet);
            String vueNormalisee = normaliser(vue);
            String nomElementNormalise = normaliser(nomElement);

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_PLAN : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 3) {
                    String idExistant = normaliser(parts[0]);
                    String vueExistante = normaliser(parts[1]);
                    String nomExistant = normaliser(parts[2]);

                    if (idExistant.equals(idProjetNormalise)
                            && vueExistante.equals(vueNormalisee)
                            && nomExistant.equals(nomElementNormalise)) {

                        nouvellesLignes.add(nouvelleLigne);
                        remplace = true;
                        continue;
                    }
                }

                nouvellesLignes.add(ligne);
            }

            if (!remplace) {
                nouvellesLignes.add(nouvelleLigne);
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderDevis(String idDevis, String idProjet, String element,
                                        double coutMurs, double coutSol,
                                        double coutPlafond, double total) {
        try {
            verifierFichier(FICHIER_DEVIS, HEADER_DEVIS);

            Path path = Paths.get(FICHIER_DEVIS);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            String nouvelleLigne =
                    idDevis + ";" +
                    idProjet + ";" +
                    element + ";" +
                    coutMurs + ";" +
                    coutSol + ";" +
                    coutPlafond + ";" +
                    total;

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_DEVIS : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idDevis))
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[2]).equals(normaliser(element))) {

                    nouvellesLignes.add(nouvelleLigne);
                    remplace = true;

                } else {
                    nouvellesLignes.add(ligne);
                }
            }

            if (!remplace) {
                nouvellesLignes.add(nouvelleLigne);
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderPiece(String idProjet, String nomEtage, String nomPiece,
                                        double x, double y,
                                        double largeur, double longueur, double hauteur,
                                        int idRevetementMur, int idRevetementSol, int idRevetementPlaf,
                                        double coutMurs, double coutSol,
                                        double coutPlafond, double total) {
        try {
            verifierFichier(FICHIER_PIECE, HEADER_PIECE);

            String idEtage = idProjet + "_" + nomEtage.replace(" ", "").toUpperCase();
            String idPiece = idEtage + "_" + nomPiece.replace(" ", "");

            double surfaceSol = largeur * longueur;
            double surfaceMurs = 2 * (largeur + longueur) * hauteur;
            double surfacePlafond = largeur * longueur;

            String nouvelleLigne =
                    idPiece + ";" +
                    idProjet + ";" +
                    idEtage + ";" +
                    nomPiece + ";" +
                    nomEtage + ";" +
                    x + ";" +
                    y + ";" +
                    largeur + ";" +
                    longueur + ";" +
                    largeur + ";" +
                    longueur + ";" +
                    hauteur + ";" +
                    surfaceSol + ";" +
                    surfaceMurs + ";" +
                    surfacePlafond + ";" +
                    idRevetementMur + ";" +
                    idRevetementSol + ";" +
                    idRevetementPlaf + ";" +
                    coutMurs + ";" +
                    coutSol + ";" +
                    coutPlafond + ";" +
                    total;

            Path path = Paths.get(FICHIER_PIECE);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_PIECE : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(nomEtage))
                        && normaliser(parts[3]).equals(normaliser(nomPiece))) {

                    nouvellesLignes.add(nouvelleLigne);
                    remplace = true;

                } else {
                    nouvellesLignes.add(ligne);
                }
            }

            if (!remplace) {
                nouvellesLignes.add(nouvelleLigne);
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] chargerPiece(String idProjet, String nomEtage, String nomPiece) {
        try {
            Path path = Paths.get(FICHIER_PIECE);

            if (!Files.exists(path)) {
                return null;
            }

            List<String> lignes = Files.readAllLines(path);

            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(nomEtage))
                        && normaliser(parts[3]).equals(normaliser(nomPiece))) {

                    return parts;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static ArrayList<String> chargerNomsPieces(String idProjet, String vue) {
        ArrayList<String> nomsPieces = new ArrayList<>();

        try {
            verifierFichier(FICHIER_PIECE, HEADER_PIECE);

            Path path = Paths.get(FICHIER_PIECE);
            List<String> lignes = Files.readAllLines(path);

            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(vue))) {

                    String nomPiece = parts[3];

                    if (!contientNomPiece(nomsPieces, nomPiece)) {
                        nomsPieces.add(nomPiece);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return nomsPieces;
    }

    public static void supprimerPiece(String idProjet, String vue, String nomPiece) {
        supprimerPieceDansFichierPiece(idProjet, vue, nomPiece);
        supprimerPieceDansPlan(idProjet, vue, nomPiece);
        supprimerPieceDansDevis(idProjet, nomPiece);
    }

    private static void supprimerPieceDansFichierPiece(String idProjet, String vue, String nomPiece) {
        try {
            verifierFichier(FICHIER_PIECE, HEADER_PIECE);

            Path path = Paths.get(FICHIER_PIECE);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_PIECE : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                boolean estPieceASupprimer = parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(vue))
                        && normaliser(parts[3]).equals(normaliser(nomPiece));

                if (!estPieceASupprimer) {
                    nouvellesLignes.add(ligne);
                }
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void supprimerPieceDansPlan(String idProjet, String vue, String nomPiece) {
        try {
            verifierFichier(FICHIER_PLAN, HEADER_PLAN);

            Path path = Paths.get(FICHIER_PLAN);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_PLAN : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                boolean estPieceASupprimer = parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))
                        && normaliser(parts[2]).equals(normaliser(nomPiece));

                if (!estPieceASupprimer) {
                    nouvellesLignes.add(ligne);
                }
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void supprimerPieceDansDevis(String idProjet, String nomPiece) {
        try {
            verifierFichier(FICHIER_DEVIS, HEADER_DEVIS);

            Path path = Paths.get(FICHIER_DEVIS);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER_DEVIS : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                boolean estDevisASupprimer = parts.length >= 3
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[2]).equals(normaliser(nomPiece));

                if (!estDevisASupprimer) {
                    nouvellesLignes.add(ligne);
                }
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean contientNomPiece(ArrayList<String> nomsPieces, String nomPiece) {
        for (String nom : nomsPieces) {
            if (normaliser(nom).equals(normaliser(nomPiece))) {
                return true;
            }
        }
        return false;
    }

    private static String normaliser(String texte) {
        if (texte == null) {
            return "";
        }

        return texte
                .trim()
                .toLowerCase()
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