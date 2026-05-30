/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

public class Tremie {

    private double x;
    private double y;
    private double largeur;
    private double longueur;
    private Revetement revetement;

    public Tremie(double x, double y, double largeur, double longueur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.longueur = longueur;
    }

    public double calculerSurfaceAuSol() {
        return largeur * longueur;
    }

    public double calculerSurfaceRevetement() {
        return 2 * calculerSurfaceAuSol();
    }

    public double calculerPrix() {
        if (revetement == null) {
            return 0;
        }
        return revetement.calculerPrix(calculerSurfaceRevetement());
    }

    public void setRevetement(Revetement revetement) {
        this.revetement = revetement;
    }

    public Revetement getRevetement() {
        return revetement;
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
}