/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Revetement {

    private int idRevetement;
    private String designation;
    private boolean pourMur;
    private boolean pourSol;
    private boolean pourPlafond;
    private double prixUnitaire;
    private String couleur;

    public Revetement(int idRevetement, String designation,
                      boolean pourMur, boolean pourSol, boolean pourPlafond,
                      double prixUnitaire, String couleur) {
        this.idRevetement = idRevetement;
        this.designation = designation;
        this.pourMur = pourMur;
        this.pourSol = pourSol;
        this.pourPlafond = pourPlafond;
        this.prixUnitaire = prixUnitaire;
        this.couleur = couleur;
    }

    public static ArrayList<Revetement> chargerDepuisFichier() {
        ArrayList<Revetement> liste = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {

            reader.readLine();

            String ligne;

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) {
                    continue;
                }

                String[] parts = ligne.split(";");

                if (parts.length < 7) {
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String designation = parts[1].trim();
                boolean pourMur = parts[2].trim().equals("1");
                boolean pourSol = parts[3].trim().equals("1");
                boolean pourPlafond = parts[4].trim().equals("1");
                double prixUnitaire = Double.parseDouble(parts[5].trim());
                String couleur = parts[6].trim();

                liste.add(new Revetement(id, designation, pourMur, pourSol,
                        pourPlafond, prixUnitaire, couleur));
            }

        } catch (IOException e) {
            System.out.println("Erreur : impossible de lire CatalogueRevetements.txt");
            e.printStackTrace();
        }

        return liste;
    }

    public static ArrayList<Revetement> getRevetementsMur() {
        ArrayList<Revetement> resultat = new ArrayList<>();

        for (Revetement r : chargerDepuisFichier()) {
            if (r.isPourMur()) {
                resultat.add(r);
            }
        }

        return resultat;
    }

    public static ArrayList<Revetement> getRevetementsSol() {
        ArrayList<Revetement> resultat = new ArrayList<>();

        for (Revetement r : chargerDepuisFichier()) {
            if (r.isPourSol()) {
                resultat.add(r);
            }
        }

        return resultat;
    }

    public static ArrayList<Revetement> getRevetementsPlafond() {
        ArrayList<Revetement> resultat = new ArrayList<>();

        for (Revetement r : chargerDepuisFichier()) {
            if (r.isPourPlafond()) {
                resultat.add(r);
            }
        }

        return resultat;
    }

    public double calculerPrix(double surface) {
        return surface * prixUnitaire;
    }

    public int getIdRevetement() {
        return idRevetement;
    }

    public String getDesignation() {
        return designation;
    }

    public boolean isPourMur() {
        return pourMur;
    }

    public boolean isPourSol() {
        return pourSol;
    }

    public boolean isPourPlafond() {
        return pourPlafond;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public String getCouleur() {
        return couleur;
    }

    @Override
    public String toString() {
        return designation + " (" + prixUnitaire + " €/m²)";
    }
}