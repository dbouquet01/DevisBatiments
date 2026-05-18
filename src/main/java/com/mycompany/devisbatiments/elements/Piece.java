/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

import java.util.ArrayList;

public class Piece {

    private String nom;

    private double x;
    private double y;
    private double largeur;
    private double longueur;
    private double hauteur;

    private Coin c1;
    private Coin c2;
    private Coin c3;
    private Coin c4;

    private ArrayList<Mur> murs;
    private Sol sol;
    private Plafond plafond;

    public Piece(String nom, double x, double y,
                 double largeur, double longueur, double hauteur) {
        this.nom = nom;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.longueur = longueur;
        this.hauteur = hauteur;

        creerGeometrie();
    }

    private void creerGeometrie() {
        c1 = new Coin(x, y);
        c2 = new Coin(x + longueur, y);
        c3 = new Coin(x + longueur, y + largeur);
        c4 = new Coin(x, y + largeur);

        murs = new ArrayList<>();

        murs.add(new Mur(c1, c2, hauteur));
        murs.add(new Mur(c2, c3, hauteur));
        murs.add(new Mur(c3, c4, hauteur));
        murs.add(new Mur(c4, c1, hauteur));

        sol = new Sol(c1, c2, c3, c4);
        plafond = new Plafond(c1, c2, c3, c4);
    }

    public double calculerSurfaceMurs() {
        double total = 0;

        for (Mur mur : murs) {
            total += mur.calculerSurface();
        }

        return total;
    }

    public double calculerSurfaceSol() {
        return sol.calculerSurface();
    }

    public double calculerSurfacePlafond() {
        return plafond.calculerSurface();
    }

    public void appliquerRevetementMurs(Revetement revetement) {
        for (Mur mur : murs) {
            mur.setRevetement(revetement);
        }
    }

    public void appliquerRevetementSol(Revetement revetement) {
        sol.setRevetement(revetement);
    }

    public void appliquerRevetementPlafond(Revetement revetement) {
        plafond.setRevetement(revetement);
    }

    public double calculerPrixMurs() {
        double total = 0;

        for (Mur mur : murs) {
            total += mur.calculerPrix();
        }

        return total;
    }

    public double calculerPrixSol() {
        return sol.calculerPrix();
    }

    public double calculerPrixPlafond() {
        return plafond.calculerPrix();
    }

    public double calculerPrixTotal() {
        return calculerPrixMurs()
                + calculerPrixSol()
                + calculerPrixPlafond();
    }

    public String getNom() {
        return nom;
    }

    public ArrayList<Mur> getMurs() {
        return murs;
    }

    public Sol getSol() {
        return sol;
    }

    public Plafond getPlafond() {
        return plafond;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLargeur() {
        return largeur;
    }

    public double getLongueur() {
        return longueur;
    }

    public double getHauteur() {
        return hauteur;
    }
}