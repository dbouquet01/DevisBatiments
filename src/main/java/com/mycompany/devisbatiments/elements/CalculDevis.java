/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

public class CalculDevis {

    public static double calculerCout(double surface, Revetement revetement) {
        if (revetement == null) {
            return 0;
        }

        return revetement.calculerPrix(surface);
    }

    public static double calculerCoutTotal(double surfaceMur, Revetement revMur,
                                           double surfaceSol, Revetement revSol,
                                           double surfacePlafond, Revetement revPlafond) {
        return calculerCout(surfaceMur, revMur)
                + calculerCout(surfaceSol, revSol)
                + calculerCout(surfacePlafond, revPlafond);
    }
}