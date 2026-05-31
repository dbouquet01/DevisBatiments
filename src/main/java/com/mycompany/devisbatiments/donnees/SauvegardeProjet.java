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
    private static final String FICHIER_OUVERTURES = "Ouvertures.txt";
    private static final String HEADER_OUVERTURES = "idProjet;nomEtage;nomPiece;type;mur;position;largeur;hauteur";

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
                                     String idDevis, double largeur, double longueur,
                                     int idRevetementFacade, int idRevetementIsolation) {

    String ligne = idProjet + ";" + designation + ";" + type + ";" + nombreEtages + ";"
            + hauteurTotale + ";" + surfaceTotale + ";" + nombreAppartements + ";"
            + idDevis + ";" + largeur + ";" + longueur + ";"
            + idRevetementFacade + ";" + idRevetementIsolation;

        upsert(FICHIER_PROJETS, HEADER_PROJETS, ligne, parts ->
                parts.length > 0 && normaliser(parts[0]).equals(normaliser(idProjet))
        );
    }

    public static void sauvegarderEtage(String idProjet, String nomEtage,
                                        int nbAppartements, double surfaceTotale) {
        String idEtage = idProjet + "_" + nomEtage.replace(" ", "").toUpperCase();

        String ligne = idEtage + ";" + idProjet + ";" + nomEtage + ";" + nbAppartements
                + ";0;false;false;" + surfaceTotale + ";0";

        upsert(FICHIER_ETAGE, HEADER_ETAGE, ligne, parts ->
                parts.length > 0 && normaliser(parts[0]).equals(normaliser(idEtage))
        );
    }

    public static void sauvegarderElementPlan(String idProjet, String vue, String nomElement,
                                              double x, double y,
                                              double largeur, double longueur,
                                              double hauteur,
                                              int idRevetement) {
        String ligne = idProjet + ";" + vue + ";" + nomElement + ";" + x + ";" + y + ";"
                + largeur + ";" + longueur + ";" + hauteur + ";" + idRevetement;

        upsert(FICHIER_PLAN, HEADER_PLAN, ligne, parts ->
                parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))
                        && normaliser(parts[2]).equals(normaliser(nomElement))
        );
    }

    public static void sauvegarderDevis(String idDevis, String idProjet, String element,
                                        double coutMurs, double coutSol,
                                        double coutPlafond, double total) {
        String ligne = idDevis + ";" + idProjet + ";" + element + ";"
                + coutMurs + ";" + coutSol + ";" + coutPlafond + ";" + total;

        upsert(FICHIER_DEVIS, HEADER_DEVIS, ligne, parts ->
                parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idDevis))
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[2]).equals(normaliser(element))
        );
    }

    public static void sauvegarderPiece(String idProjet, String nomEtage, String nomPiece,
                                        double x, double y,
                                        double largeur, double longueur, double hauteur,
                                        int idRevetementMur, int idRevetementSol, int idRevetementPlaf,
                                        double coutMurs, double coutSol,
                                        double coutPlafond, double total) {
        String idEtage = idProjet + "_" + nomEtage.replace(" ", "").toUpperCase();
        String idPiece = idEtage + "_" + nomPiece.replace(" ", "");

        double surfaceSol = largeur * longueur;
        double surfaceMurs = 2 * (largeur + longueur) * hauteur;
        double surfacePlafond = largeur * longueur;

        String ligne = idPiece + ";" + idProjet + ";" + idEtage + ";" + nomPiece + ";" + nomEtage + ";"
                + x + ";" + y + ";" + largeur + ";" + longueur + ";"
                + largeur + ";" + longueur + ";" + hauteur + ";"
                + surfaceSol + ";" + surfaceMurs + ";" + surfacePlafond + ";"
                + idRevetementMur + ";" + idRevetementSol + ";" + idRevetementPlaf + ";"
                + coutMurs + ";" + coutSol + ";" + coutPlafond + ";" + total;

        upsert(FICHIER_PIECE, HEADER_PIECE, ligne, parts ->
                parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(nomEtage))
                        && normaliser(parts[3]).equals(normaliser(nomPiece))
        );
    }

    public static String[] chargerPiece(String idProjet, String vue, String nomPiece) {
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
                        && normaliser(parts[4]).equals(normaliser(vue))
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
        ArrayList<String> noms = new ArrayList<>();

        try {
            Path path = Paths.get(FICHIER_PIECE);

            if (!Files.exists(path)) {
                return noms;
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
                        && normaliser(parts[4]).equals(normaliser(vue))) {

                    String nomPiece = parts[3].trim();

                    if (!nomPiece.isEmpty() && !contient(noms, nomPiece)) {
                        noms.add(nomPiece);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return noms;
    }

    public static void supprimerPiece(String idProjet, String vue, String nomPiece) {
        supprimerDansFichier(FICHIER_PIECE, HEADER_PIECE, parts ->
                parts.length >= 5
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[4]).equals(normaliser(vue))
                        && normaliser(parts[3]).equals(normaliser(nomPiece))
        );

        supprimerDansFichier(FICHIER_PLAN, HEADER_PLAN, parts ->
                parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))
                        && normaliser(parts[2]).equals(normaliser(nomPiece))
        );

        supprimerDansFichier(FICHIER_DEVIS, HEADER_DEVIS, parts ->
                parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser("D_" + idProjet))
                        && normaliser(parts[1]).equals(normaliser(idProjet))
                        && normaliser(parts[2]).equals(normaliser(nomPiece))
        );
    }

    public static void supprimerAppartementsAutoEtage(String idProjet, String nomEtage) {
        supprimerDansFichier(FICHIER_PLAN, HEADER_PLAN, parts ->
                parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(nomEtage))
                        && estAppartement(parts[2])
        );
    }

    public static void sauvegarderAppartementAuto(String idProjet, String nomEtage,
                                                  int numeroAppartement,
                                                  double x, double y,
                                                  double largeur, double longueur,
                                                  double hauteur,
                                                  int idRevetement) {
        sauvegarderElementPlan(
                idProjet,
                nomEtage,
                "Appartement" + numeroAppartement,
                x,
                y,
                largeur,
                longueur,
                hauteur,
                idRevetement
        );
    }

    public static void sauvegarderAppartementsAutoEtage(String idProjet, String nomEtage,
                                                        int nbAppartements,
                                                        double largeurBatiment,
                                                        double longueurBatiment,
                                                        double yCouloir,
                                                        double largeurCouloir,
                                                        int nbPiecesCommunes,
                                                        int idRevetementAppartement) {
        supprimerAppartementsAutoEtage(idProjet, nomEtage);

        if (nbAppartements <= 0 || largeurBatiment <= 0 || longueurBatiment <= 0) {
            return;
        }

        double haut = Math.max(0, yCouloir);
        double bas = Math.max(0, longueurBatiment - (yCouloir + largeurCouloir));

        double yZone = bas >= haut ? yCouloir + largeurCouloir : 0;
        double longueurZone = bas >= haut ? bas : haut;

        int nbBlocs = nbAppartements + Math.max(0, nbPiecesCommunes);

        if (nbBlocs <= 0 || longueurZone <= 0) {
            return;
        }

        double largeurBloc = largeurBatiment / nbBlocs;

        for (int i = 1; i <= nbAppartements; i++) {
            sauvegarderAppartementAuto(
                    idProjet,
                    nomEtage,
                    i,
                    (i - 1) * largeurBloc,
                    yZone,
                    largeurBloc,
                    longueurZone,
                    3.0,
                    idRevetementAppartement
            );
        }
    }


    public static ArrayList<String> chargerNomsElementsPlan(String idProjet, String vue) {
        ArrayList<String> noms = new ArrayList<>();

        try {
            Path path = Paths.get(FICHIER_PLAN);

            if (!Files.exists(path)) {
                return noms;
            }

            List<String> lignes = Files.readAllLines(path);

            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 3
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))) {

                    String nomElement = parts[2].trim();

                    if (!nomElement.isEmpty() && !contient(noms, nomElement)) {
                        noms.add(nomElement);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return noms;
    }

    public static String[] chargerElementPlan(String idProjet, String vue, String nomElement) {
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

                if (parts.length >= 9
                        && normaliser(parts[0]).equals(normaliser(idProjet))
                        && normaliser(parts[1]).equals(normaliser(vue))
                        && normaliser(parts[2]).equals(normaliser(nomElement))) {
                    return parts;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private interface ConditionLigne {
        boolean correspond(String[] parts);
    }

    private static void upsert(String fichier, String header, String nouvelleLigne, ConditionLigne condition) {
        try {
            verifierFichier(fichier, header);

            Path path = Paths.get(fichier);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();
            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? header : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (condition.correspond(parts)) {
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
    
    public static void sauvegarderOuverture(String idProjet, String nomEtage, String nomPiece, 
                                        String type, String mur, double position, double largeur, double hauteur) {
    Path path = Paths.get(FICHIER_OUVERTURES);
    try {
        List<String> lignes = new ArrayList<>();
        if (!Files.exists(path)) {
            lignes.add(HEADER_OUVERTURES);
        } else {
            lignes = Files.readAllLines(path);
        }

        // On crée la nouvelle ligne de données
        String nouvelleLigne = String.format("%s;%s;%s;%s;%s;%.2f;%.2f;%.2f", 
                idProjet, nomEtage, nomPiece, type, mur, position, largeur, hauteur);
        lignes.add(nouvelleLigne);

        Files.write(path, lignes);
    } catch (IOException e) {
        System.out.println("Erreur lors de la sauvegarde de l'ouverture : " + e.getMessage());
    }
}


public static List<String[]> chargerOuverturesPiece(String idProjet, String nomEtage, String nomPiece) {
    List<String[]> ouvertures = new ArrayList<>();
    Path path = Paths.get(FICHIER_OUVERTURES);
    
    if (!Files.exists(path)) return ouvertures;

    try {
        List<String> lignes = Files.readAllLines(path);
        for (int i = 1; i < lignes.size(); i++) { // On saute l'en-tête
            String ligne = lignes.get(i).trim();
            if (ligne.isEmpty()) continue;

            String[] parts = ligne.split(";");
            if (parts.length >= 8) {
                // On vérifie si l'ouverture appartient bien à ce projet, cet étage et cette pièce
                if (parts[0].trim().equalsIgnoreCase(idProjet.trim()) &&
                    parts[1].trim().equalsIgnoreCase(nomEtage.trim()) &&
                    parts[2].trim().equalsIgnoreCase(nomPiece.trim())) {
                    ouvertures.add(parts);
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Erreur lors du chargement des ouvertures : " + e.getMessage());
    }
    return ouvertures;
}

    private static void supprimerDansFichier(String fichier, String header, ConditionLigne condition) {
        try {
            verifierFichier(fichier, header);

            Path path = Paths.get(fichier);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? header : ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (!condition.correspond(parts)) {
                    nouvellesLignes.add(ligne);
                }
            }

            Files.write(path, nouvellesLignes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean contient(ArrayList<String> liste, String valeur) {
        for (String item : liste) {
            if (normaliser(item).equals(normaliser(valeur))) {
                return true;
            }
        }
        return false;
    }

    private static boolean estAppartement(String texte) {
        String n = normaliser(texte);
        return n.startsWith("appartement") || n.startsWith("appart");
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

