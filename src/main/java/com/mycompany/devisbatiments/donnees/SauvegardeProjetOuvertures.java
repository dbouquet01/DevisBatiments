package com.mycompany.devisbatiments.donnees;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class SauvegardeProjetOuvertures {

    private static final String FICHIER_PLAN = "PlanProjets.txt";
    private static final String FICHIER_PIECE = "Piece.txt";
    private static final String FICHIER_CATALOGUE = "CatalogueRevetements.txt";
    private static final String HEADER_PIECE =
            "idPiece;idProjet;idEtage;nomPiece;vue;xPlan;yPlan;largeurPlan;hauteurPlan;"
                    + "largeurMetres;longueurMetres;hauteurMetres;surfaceSol;surfaceMurs;surfacePlafond;"
                    + "idRevetementMur;idRevetementSol;idRevetementPlafond;devisMurs;devisSol;devisPlafond;devisTotal;"
                    + "nbFenetre;nbPorte;nbTremie;largeurTremie;longueurTremie";

    private static void verifierFichier(String nomFichier, String header) throws java.io.IOException {
        Path path = Paths.get(nomFichier);
        if (!Files.exists(path)) {
            List<String> lignes = new ArrayList<>();
            lignes.add(header);
            Files.write(path, lignes);
        }
    }

    /**
     * Maison uniquement : quand une trémie/escalier est placée dans une pièce,
     * on ajoute automatiquement la trémie au même emplacement dans la pièce de
     * l'étage du dessus, puis on recalcule les surfaces et le devis de cette
     * pièce supérieure.
     */
    public static void sauvegarderTremieEtageDessusMaison(String idProjet,
                                                          String nomEtage,
                                                          String nomPieceOrigine,
                                                          double xPlan,
                                                          double yPlan,
                                                          double largeur,
                                                          double longueur,
                                                          int idRevetementTremie) {
        String etageDessus = getEtageDessus(nomEtage);
        if (etageDessus == null) {
            return;
        }

        String[] projet = SauvegardeProjet.chargerProjet(idProjet);
        if (projet == null || projet.length < 4 || !normaliser(projet[2]).equals("maison")) {
            return;
        }

        int nbEtages;
        try {
            nbEtages = Integer.parseInt(projet[3].trim());
        } catch (Exception e) {
            return;
        }

        int indiceEtageActuel = getIndiceEtageMaison(nomEtage);
        if (indiceEtageActuel < 0 || indiceEtageActuel >= nbEtages) {
            return;
        }

        String[] pieceDessus = chercherPieceContenantZone(idProjet, etageDessus, xPlan, yPlan, largeur, longueur);
        if (pieceDessus == null || pieceDessus.length < 9) {
            return;
        }

        String nomPieceDessus = pieceDessus[2].trim();
        String nomElement = nomPieceDessus + "_TremieAuto_" + nettoyerNom(nomPieceOrigine)
                + "_" + coordNom(xPlan) + "_" + coordNom(yPlan);

        SauvegardeProjet.sauvegarderElementPlan(
                idProjet,
                etageDessus,
                nomElement,
                xPlan,
                yPlan,
                largeur,
                longueur,
                0,
                idRevetementTremie
        );

        recalculerPieceAvecTremies(idProjet, etageDessus, nomPieceDessus);
    }

    private static String getEtageDessus(String nomEtage) {
        if (nomEtage == null) {
            return null;
        }

        String n = normaliser(nomEtage).replace(" ", "");
        if (n.equals("rdc")) {
            return "Etage 1";
        }

        if (n.startsWith("etage")) {
            try {
                int numero = Integer.parseInt(n.substring("etage".length()));
                return "Etage " + (numero + 1);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static int getIndiceEtageMaison(String nomEtage) {
        if (nomEtage == null) {
            return -1;
        }

        String n = normaliser(nomEtage).replace(" ", "");
        if (n.equals("rdc")) {
            return 0;
        }

        if (n.startsWith("etage")) {
            try {
                return Integer.parseInt(n.substring("etage".length()));
            } catch (Exception ignored) {
            }
        }

        return -1;
    }

    private static String[] chercherPieceContenantZone(String idProjet, String vue,
                                                       double x, double y,
                                                       double largeur, double longueur) {
        try {
            Path path = Paths.get(FICHIER_PLAN);
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
                if (parts.length < 9
                        || !normaliser(parts[0]).equals(normaliser(idProjet))
                        || !normaliser(parts[1]).equals(normaliser(vue))) {
                    continue;
                }

                String nom = normaliser(parts[2]);
                if (nom.contains("fenetre") || nom.contains("porte") || nom.contains("tremie")
                        || nom.contains("escalier") || nom.contains("couloir")) {
                    continue;
                }

                double px = Double.parseDouble(parts[3].trim().replace(",", "."));
                double py = Double.parseDouble(parts[4].trim().replace(",", "."));
                double pl = Double.parseDouble(parts[5].trim().replace(",", "."));
                double plo = Double.parseDouble(parts[6].trim().replace(",", "."));
                double eps = 0.0001;

                if (x + eps >= px && y + eps >= py
                        && x + largeur <= px + pl + eps
                        && y + longueur <= py + plo + eps) {
                    return parts;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void recalculerPieceAvecTremies(String idProjet, String vue, String nomPiece) {
        try {
            verifierFichier(FICHIER_PIECE, HEADER_PIECE);
            Path path = Paths.get(FICHIER_PIECE);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (i == 0) {
                    nouvellesLignes.add(HEADER_PIECE);
                    continue;
                }
                if (ligne.trim().isEmpty()) {
                    nouvellesLignes.add(ligne);
                    continue;
                }

                String[] parts = ligne.split(";");
                if (parts.length >= 22
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(vue))
                        && normaliser(parts[3]).equals(normaliser(nomPiece))) {

                    ArrayList<String> colonnes = new ArrayList<>();
                    for (String part : parts) {
                        colonnes.add(part);
                    }
                    while (colonnes.size() < 27) {
                        colonnes.add("0");
                    }

                    double largeurPiece = Double.parseDouble(colonnes.get(9).trim().replace(",", "."));
                    double longueurPiece = Double.parseDouble(colonnes.get(10).trim().replace(",", "."));
                    double hauteurPiece = Double.parseDouble(colonnes.get(11).trim().replace(",", "."));
                    int idMur = Integer.parseInt(colonnes.get(15).trim());
                    int idSol = Integer.parseInt(colonnes.get(16).trim());
                    int idPlafond = Integer.parseInt(colonnes.get(17).trim());

                    double surfaceTremies = calculerSurfaceTremiesPiece(idProjet, vue, nomPiece);
                    int nbTremies = SauvegardeProjet.compterElementsPlanPiece(idProjet, vue, nomPiece)[2];
                    double[] derniereTremie = SauvegardeProjet.chargerDerniereTremiePiece(idProjet, vue, nomPiece);

                    double surfaceSol = Math.max(0, largeurPiece * longueurPiece - surfaceTremies);
                    double surfacePlafond = Math.max(0, largeurPiece * longueurPiece - surfaceTremies);
                    double surfaceMurs = 2 * (largeurPiece + longueurPiece) * hauteurPiece;

                    double coutMurs = surfaceMurs * prixUnitaireRevetement(idMur);
                    double coutSol = surfaceSol * prixUnitaireRevetement(idSol);
                    double coutPlafond = surfacePlafond * prixUnitaireRevetement(idPlafond);
                    double total = coutMurs + coutSol + coutPlafond;

                    colonnes.set(12, String.valueOf(surfaceSol));
                    colonnes.set(13, String.valueOf(surfaceMurs));
                    colonnes.set(14, String.valueOf(surfacePlafond));
                    colonnes.set(18, String.format(java.util.Locale.US, "%.2f", coutMurs));
                    colonnes.set(19, String.format(java.util.Locale.US, "%.2f", coutSol));
                    colonnes.set(20, String.format(java.util.Locale.US, "%.2f", coutPlafond));
                    colonnes.set(21, String.format(java.util.Locale.US, "%.2f", total));
                    colonnes.set(24, String.valueOf(nbTremies));
                    colonnes.set(25, String.valueOf(derniereTremie[0]));
                    colonnes.set(26, String.valueOf(derniereTremie[1]));

                    nouvellesLignes.add(String.join(";", colonnes));

                    SauvegardeProjet.sauvegarderDevis("D_" + idProjet, idProjet, nomPiece,
                            coutMurs, coutSol, coutPlafond, total);
                } else {
                    nouvellesLignes.add(ligne);
                }
            }

            Files.write(path, nouvellesLignes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calculerSurfaceTremiesPiece(String idProjet, String vue, String nomPiece) {
        double total = 0;
        try {
            Path path = Paths.get(FICHIER_PLAN);
            if (!Files.exists(path)) {
                return 0;
            }

            List<String> lignes = Files.readAllLines(path);
            String prefixe = normaliser(nomPiece + "_Tremie");
            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");
                if (parts.length >= 7
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))
                        && normaliser(parts[2]).startsWith(prefixe)) {
                    double largeur = Double.parseDouble(parts[5].trim().replace(",", "."));
                    double longueur = Double.parseDouble(parts[6].trim().replace(",", "."));
                    total += largeur * longueur;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    private static double prixUnitaireRevetement(int idRevetement) {
        try {
            Path path = Paths.get(FICHIER_CATALOGUE);
            if (!Files.exists(path)) {
                return 0;
            }

            List<String> lignes = Files.readAllLines(path);
            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);
                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");
                if (parts.length >= 6 && Integer.parseInt(parts[0].trim()) == idRevetement) {
                    return Double.parseDouble(parts[5].trim().replace(",", "."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private static String nettoyerNom(String texte) {
        return normaliser(texte).replaceAll("[^a-z0-9]", "");
    }

    private static String coordNom(double valeur) {
        return String.valueOf(Math.round(valeur * 100));
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
