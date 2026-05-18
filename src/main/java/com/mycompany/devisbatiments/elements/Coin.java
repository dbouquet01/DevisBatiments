/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

public class Coin {

    private double x;
    private double y;

    public Coin(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceAvec(Coin autre) {
        double dx = autre.x - this.x;
        double dy = autre.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}