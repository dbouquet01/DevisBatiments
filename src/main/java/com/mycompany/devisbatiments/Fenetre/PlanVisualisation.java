/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.elements.Coin;
import com.mycompany.devisbatiments.elements.Piece;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

/**
 *
 * @author delph
 */
public class PlanVisualisation {
    
    public static void dessinerPiece(
            Pane panneau,
            Piece piece) {

        Polygon polygone = new Polygon();

        for (Coin coin : piece.obtenirCoins()) {

            polygone.getPoints().addAll(
                    coin.obtenirX(),
                    coin.obtenirY()
            );
        }

        polygone.setFill(Color.BEIGE);
        polygone.setStroke(Color.BLACK);
        polygone.setStrokeWidth(3);

        panneau.getChildren().add(polygone);

        Coin premierCoin =
                piece.obtenirCoins().get(0);

        Text texte = new Text(
                premierCoin.obtenirX() + 20,
                premierCoin.obtenirY() + 20,
                piece.obtenirNom()
        );

        panneau.getChildren().add(texte);
    }
}
