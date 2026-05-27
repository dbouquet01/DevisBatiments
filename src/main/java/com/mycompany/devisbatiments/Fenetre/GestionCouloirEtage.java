/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class GestionCouloirEtage {

    private static final String FICHIER_COULOIRS = "CouloirsEtages.txt";
    private static final String HEADER = "idProjet;nomEtage;yCouloir;largeurCouloir;idRevetement";

    public static class CouloirInfo {
        public String idProjet;
        public String nomEtage;
        public double yCouloir;
        public double largeurCouloir;
        public int idRevetement;

        public CouloirInfo(String idProjet, String nomEtage,
                           double yCouloir, double largeurCouloir, int idRevetement) {
            this.idProjet = idProjet;
            this.nomEtage = nomEtage;
            this.yCouloir = yCouloir;
            this.largeurCouloir = largeurCouloir;
            this.idRevetement = idRevetement;
        }
    }

    public static void sauvegarderCouloir(String idProjet, String nomEtage,
                                          double yCouloir, double largeurCouloir,
                                          int idRevetement) {
        try {
            verifierFichier();

            Path path = Paths.get(FICHIER_COULOIRS);
            List<String> lignes = Files.readAllLines(path);
            List<String> nouvellesLignes = new ArrayList<>();

            String nouvelleLigne = idProjet + ";"
                    + nomEtage + ";"
                    + yCouloir + ";"
                    + largeurCouloir + ";"
                    + idRevetement;

            boolean remplace = false;

            for (int i = 0; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (i == 0 || ligne.trim().isEmpty()) {
                    nouvellesLignes.add(i == 0 ? HEADER : ligne);
                    continue;
                }

                String[] p = ligne.split(";");

                if (p.length >= 2
                        && p[0].trim().equalsIgnoreCase(idProjet)
                        && p[1].trim().equalsIgnoreCase(nomEtage)) {
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

    public static CouloirInfo chargerCouloir(String idProjet, String nomEtage) {
        try {
            Path path = Paths.get(FICHIER_COULOIRS);

            if (!Files.exists(path)) {
                return null;
            }

            List<String> lignes = Files.readAllLines(path);

            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i);

                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] p = ligne.split(";");

                if (p.length < 5) {
                    continue;
                }

                if (p[0].trim().equalsIgnoreCase(idProjet)
                        && p[1].trim().equalsIgnoreCase(nomEtage)) {
                    return new CouloirInfo(
                            p[0].trim(),
                            p[1].trim(),
                            Double.parseDouble(p[2].trim()),
                            Double.parseDouble(p[3].trim()),
                            Integer.parseInt(p[4].trim())
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean couloirExiste(String idProjet, String nomEtage) {
        return chargerCouloir(idProjet, nomEtage) != null;
    }

    private static void verifierFichier() throws IOException {
        Path path = Paths.get(FICHIER_COULOIRS);

        if (!Files.exists(path)) {
            List<String> lignes = new ArrayList<>();
            lignes.add(HEADER);
            Files.write(path, lignes);
        }
    }
}
