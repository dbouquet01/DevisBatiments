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

public class Niveau {
    
        
    String IdNiveau; 
    private final ArrayList<Appartement> apparts;
    
     public String getIdNiveau() {
        return IdNiveau;
    }

    public void setIdNiveau(String idNiveau) {
        this.IdNiveau = idNiveau;
    }

    public Niveau() {
        this.apparts = new ArrayList<>();
    
   }
    
    public ArrayList<Appartement> getApparts() {
    return apparts;
}

public double devisNiveau() {
    double total = 0;
    for (Appartement a : apparts) {
        
    }
    return total;
}
    
}
