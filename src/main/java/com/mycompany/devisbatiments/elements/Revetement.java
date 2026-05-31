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
    private boolean facade;
    private boolean isolation;

    public Revetement(int idRevetement, String designation,
                      boolean pourMur, boolean pourSol, boolean pourPlafond,
                      double prixUnitaire, String couleur,
                      boolean facade, boolean isolation) {
        this.idRevetement = idRevetement;
        this.designation = designation;
        this.pourMur = pourMur;
        this.pourSol = pourSol;
        this.pourPlafond = pourPlafond;
        this.prixUnitaire = prixUnitaire;
        this.couleur = couleur;
        this.facade = facade;
        this.isolation = isolation;
    }

    
    public static ArrayList<Revetement> chargerDepuisFichier() {
        ArrayList<Revetement> liste = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {

            reader.readLine(); // en-tête

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] parts = ligne.split(";");
                if (parts.length < 7) continue;

                int id             = Integer.parseInt(parts[0].trim());
                String desig       = parts[1].trim();
                boolean pourMur    = parts[2].trim().equals("1");
                boolean pourSol    = parts[3].trim().equals("1");
                boolean pourPlafond= parts[4].trim().equals("1");
                double prix        = Double.parseDouble(parts[5].trim());
                String couleur     = parts[6].trim();

                // Colonnes optionnelles facade (7) et isolation (8)
                boolean facadeVal    = parts.length > 7 && parts[7].trim().equals("1");
                boolean isolationVal = parts.length > 8 && parts[8].trim().equals("1");

                liste.add(new Revetement(id, desig, pourMur, pourSol, pourPlafond,
                        prix, couleur, facadeVal, isolationVal));
            }

        } catch (IOException e) {
            System.out.println("Erreur : impossible de lire CatalogueRevetements.txt");
            e.printStackTrace();
        }

        return liste;
    }

    
    public static ArrayList<Revetement> getRevetementsMur() {
        ArrayList<Revetement> r = new ArrayList<>();
        for (Revetement rev : chargerDepuisFichier())
            if (rev.isPourMur()) r.add(rev);
        return r;
    }

    public static ArrayList<Revetement> getRevetementsSol() {
        ArrayList<Revetement> r = new ArrayList<>();
        for (Revetement rev : chargerDepuisFichier())
            if (rev.isPourSol()) r.add(rev);
        return r;
    }

    public static ArrayList<Revetement> getRevetementsPlafond() {
        ArrayList<Revetement> r = new ArrayList<>();
        for (Revetement rev : chargerDepuisFichier())
            if (rev.isPourPlafond()) r.add(rev);
        return r;
    }

    public static ArrayList<Revetement> getRevetementsFacade() {
        ArrayList<Revetement> r = new ArrayList<>();
        for (Revetement rev : chargerDepuisFichier())
            if (rev.isFacade()) r.add(rev);
        return r;
    }

    public static ArrayList<Revetement> getRevetementsIsolation() {
        ArrayList<Revetement> r = new ArrayList<>();
        for (Revetement rev : chargerDepuisFichier())
            if (rev.isIsolation()) r.add(rev);
        return r;
    }


    public double calculerPrix(double surface) {
        return surface * prixUnitaire;
    }

    public int getIdRevetement()   { return idRevetement; }
    public String getDesignation() { return designation; }
    public boolean isPourMur()     { return pourMur; }
    public boolean isPourSol()     { return pourSol; }
    public boolean isPourPlafond() { return pourPlafond; }
    public double getPrixUnitaire(){ return prixUnitaire; }
    public String getCouleur()     { return couleur; }
    public boolean isFacade()      { return facade; }
    public boolean isIsolation()   { return isolation; }

    @Override
    public String toString() {
        return designation + " (" + prixUnitaire + " €/m²)";
    }
}