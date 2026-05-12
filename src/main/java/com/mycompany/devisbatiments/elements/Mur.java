/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

/**
 *
 * @author eglan
 */
public class Mur {

    private Coin debut;
    private Coin fin;

    public Mur(Coin debut, Coin fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public Coin obtenirDebut() {
        return debut;
    }

    public Coin obtenirFin() {
        return fin;
    }
}
