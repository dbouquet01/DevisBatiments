/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

import java.util.ArrayList;

public class Piece {

    private String nom;

    private ArrayList<Coin> coins;
    private ArrayList<Mur> murs;

    public Piece(String nom) {

        this.nom = nom;

        coins = new ArrayList<>();
        murs = new ArrayList<>();
    }

    public void ajouterCoin(Coin coin) {
        coins.add(coin);
    }

    public void construireMurs() {

        murs.clear();

        for (int i = 0; i < coins.size(); i++) {

            Coin debut = coins.get(i);

            Coin fin;

            if (i == coins.size() - 1) {
                fin = coins.get(0);
            } else {
                fin = coins.get(i + 1);
            }

            murs.add(new Mur(debut, fin));
        }
    }

    public ArrayList<Coin> obtenirCoins() {
        return coins;
    }

    public String obtenirNom() {
        return nom;
    }
}
