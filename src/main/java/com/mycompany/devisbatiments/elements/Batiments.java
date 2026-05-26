/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

import java.util.ArrayList;

/**
 *
 * @author eglan
 */
public abstract class Batiments {
   
    protected String Id;
    protected String Designation;
    protected double largeur;
    protected double longueur;
    protected int nbEtage;
   
public Batiments (String Id, double largeur, double longueur, int nbEtage){
    this.Id = Id;
    this.Designation = Designation;
    this.largeur=largeur;
    this.longueur=longueur;
    this.nbEtage= nbEtage; }

    public String getId() {
        return Id;
    }


    public void setId(String Id) {
        this.Id = Id;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String Designation) {
        this.Designation = Designation;
    }
    
    

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public int getNbEtage() {
        return nbEtage;
    }

    public void setNbEtage(int nbEtage) {
        this.nbEtage = nbEtage;
    }
   
public double calculerSuperficie() {
    return this.largeur * this.longueur * (this.nbEtage + 1);
}
   
}

   

