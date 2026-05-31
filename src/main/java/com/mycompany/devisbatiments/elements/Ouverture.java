/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

public class Ouverture {

    public static final double LARGEUR_FENETRE_STANDARD = 1.20;
    public static final double HAUTEUR_FENETRE_STANDARD = 1.20;

    public static final double LARGEUR_PORTE_STANDARD = 0.90;
    public static final double HAUTEUR_PORTE_STANDARD = 2.10;

    private int nbOuverture;
    private String typeOuverture;

    public Ouverture(String typeOuverture, int nbOuverture) {
        this.typeOuverture = typeOuverture;
        this.nbOuverture = nbOuverture;
    }

    public int getNbOuverture() {
        return nbOuverture;
    }

    public void setNbOuverture(int nbOuverture) {
        this.nbOuverture = nbOuverture;
    }

    public String getTypeOuverture() {
        return typeOuverture;
    }

    public void setTypeOuverture(String typeOuverture) {
        this.typeOuverture = typeOuverture;
    }

    public double calculerSurfaceTotale() {

        if (typeOuverture.equalsIgnoreCase("Fenetre")) {
            return nbOuverture
                    * LARGEUR_FENETRE_STANDARD
                    * HAUTEUR_FENETRE_STANDARD;
        }

        if (typeOuverture.equalsIgnoreCase("Porte")) {
            return nbOuverture
                    * LARGEUR_PORTE_STANDARD
                    * HAUTEUR_PORTE_STANDARD;
        }

        return 0;
    }
}