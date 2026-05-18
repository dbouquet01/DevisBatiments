/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.donnees;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SauvegardeProjet {

    private static final String FICHIER_PROJETS = "Projets.txt";
    private static final String FICHIER_PLAN = "PlanProjets.txt";
    private static final String FICHIER_DEVIS = "Devis.txt";

    private static final String HEADER_PROJETS =
            "idProjet;designation;type;nombreEtages;hauteurTotale;surfaceTotale;nombreAppartements;idDevis";

    private static final String HEADER_PLAN =
            "idProjet;vue;piece;x;y;largeur;longueur;hauteur;idRevetement";

    private static final String HEADER_DEVIS =
            "idDevis;idProjet;element;coutMurs;coutSol;coutPlafond;total";

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
                                         String idDevis) {
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
                    idDevis;

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(idProjet)) {
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

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 3
                        && parts[0].trim().equalsIgnoreCase(idProjet)
                        && parts[1].trim().equalsIgnoreCase(vue)
                        && parts[2].trim().equalsIgnoreCase(nomElement)) {

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
                    nouvellesLignes.add(ligne);
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length >= 3
                        && parts[0].trim().equalsIgnoreCase(idDevis)
                        && parts[1].trim().equalsIgnoreCase(idProjet)
                        && parts[2].trim().equalsIgnoreCase(element)) {

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
}