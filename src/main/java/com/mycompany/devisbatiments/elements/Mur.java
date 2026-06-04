package com.mycompany.devisbatiments.elements;

public class Mur {

    private Coin debut;
    private Coin fin;
    private double hauteur;
    private Revetement revetement;

    public Mur(Coin debut, Coin fin, double hauteur) {
        this.debut = debut;
        this.fin = fin;
        this.hauteur = hauteur;
    }

    public double calculerLongueur() {
        return debut.distanceAvec(fin);
    }

    public double calculerSurface() {
        return calculerLongueur() * hauteur;
    }

    public void setRevetement(Revetement revetement) {
        if (revetement != null && !revetement.isPourMur()) {
            throw new IllegalArgumentException("Ce revêtement n'est pas compatible avec les murs.");
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

    public Coin getDebut() {
        return debut;
    }

    public Coin getFin() {
        return fin;
    }
}