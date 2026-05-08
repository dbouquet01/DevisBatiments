/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

/**
 *
 * @author eglan
 */
public class Piece {
    
      
    String IdPiece;
    private ArrayList<Coin> coins;
    private ArrayList<Mur> murs;

    public Piece() {
        this.coins = new ArrayList<>();
        this.murs = new ArrayList<>();
    }
    
    
    //il faut appeler les classes murs et sols et invoquer le catalogue 

    public String getIdPiece() {
        return IdPiece;
    }

    public void setIdPiece(String IdPiece) {
        this.IdPiece = IdPiece;
    }

   public double surface() {
    // calcul de la surface de la pièce
    // à compléter selon comment on calcule
    return 0;
}
    
}
