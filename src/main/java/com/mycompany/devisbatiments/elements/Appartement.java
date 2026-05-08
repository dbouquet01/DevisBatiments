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
public class Appartement {
   
    
    String IdAppartement;
    private ArrayList<Piece> pieces;

    public Appartement() {
        this.pieces = new ArrayList<>();
    }
    

    public String getIdAppartement() {
        return IdAppartement;
    }

    public void setIdAppartement(String IdAppartement) {
        this.IdAppartement = IdAppartement;
    }

 public ArrayList<Piece> getPieces() {
    return pieces;
}  
    
    
}
    

