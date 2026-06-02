package com.mycompany.devisbatiments.elements;

public class Sol {

    private Coin c1;
    private Coin c2;
    private Coin c3;
    private Coin c4;
    private Revetement revetement;

    public Sol(Coin c1, Coin c2, Coin c3, Coin c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
    }

    public double calculerSurface() {
        double longueur = c1.distanceAvec(c2);
        double largeur = c2.distanceAvec(c3);
        return longueur * largeur;
    }

    public void setRevetement(Revetement revetement) {
        if (revetement != null && !revetement.isPourSol()) {
            throw new IllegalArgumentException("Ce revêtement n'est pas compatible avec le sol.");
        }

        this.revetement = revetement;
    }

    public double calculerPrix() {
        if (revetement == null) {
            return 0;
        }

        return revetement.calculerPrix(calculerSurface());
    }

    public Revetement getRevetement() {
        return revetement;
    }
}