/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.devisbatiments.elements;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
/**
 *
 * @author eglan
 */
public class Revetement {
    
    protected int idRevetement;

    public Revetement(int idRevetement) {
        this.idRevetement = idRevetement;
    }

    public int getIdRevetment() {
        return idRevetement;
    }

    public void setIdRevetment(int idRevetement) {
        this.idRevetement = idRevetement;
    }
    
    
    public String chercheRevetement(String typeSupport) {
        

    try {

        List<String> lignes =
                Files.readAllLines(Paths.get("revetements.txt"));

        for(String ligne : lignes) {

            String[] parts = ligne.split(";");

            int id = Integer.parseInt(parts[0]);

            if(id == idRevetement) {

                boolean compatible = false;

                switch(typeSupport) {

                    case "MUR":
                        compatible =
                                Integer.parseInt(parts[2]) == 1;
                        break;

                    case "SOL":
                        compatible =
                                Integer.parseInt(parts[3]) == 1;
                        break;

                    case "PLAFOND":
                        compatible =
                                Integer.parseInt(parts[4]) == 1;
                        break;
                }

                if(compatible) {

                    return parts[1];
                }
                else {

                    return "Revêtement incompatible";
                }
            }
        }

    } catch(IOException e) {

        e.printStackTrace();
    }

    return "Revêtement introuvable";
    }
   
}
