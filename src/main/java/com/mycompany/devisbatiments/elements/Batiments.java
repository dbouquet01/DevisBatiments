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
public class Batiments {
    String IdBatiment;
    int nbNiveau; 
    private ArrayList<Niveau> niveaux;

 
    
    public String getIdBat() {
        return IdBatiment;
    }

    public void setIdBat(String IdBat) {
        this.IdBatiment = IdBat;
    }

    public int getNbNiveau() {
        return nbNiveau;
    }

    public void setNbNiveau(int nbNiveau) {
        this.nbNiveau = nbNiveau;
    }
    
  
    
    
    
    public void afficher() {
        System.out.println("Bâtiment : " + IdBatiment);
        System.out.println("Nombre de niveaux : " + nbNiveau);
    }
    
    public double devisBatiment() {
    double total = 0;
    for (Niveau n : niveaux) {
        total += n.devisNiveau();
    }
    return total;
}
   
    
    


    
    //il manque la methode dessiner mais je sais pas comment l'ecrire 

    
}
    

