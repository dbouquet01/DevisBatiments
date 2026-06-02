/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;
 
import com.mycompany.devisbatiments.elements.Batiments;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
 
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class FenetreRecapitulatif {
 
    private final Batiments batiment;
    private final String nomEtage;
    private Stage fenetrePrecedente;
    
    
 
  
    private static class LigneDevis {
        String piece;
        double coutMurs;
        double coutSol;
        double coutPlafond;
        double total;
 
        LigneDevis(String piece, double coutMurs, double coutSol, double coutPlafond, double total) {
            this.piece = piece;
            this.coutMurs = coutMurs;
            this.coutSol = coutSol;
            this.coutPlafond = coutPlafond;
            this.total = total;
        }
    }

    public FenetreRecapitulatif(Batiments batiment, String nomEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
    }
   
    public FenetreRecapitulatif(Batiments batiment, String nomEtage, Stage fenetrePrecedente) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.fenetrePrecedente = fenetrePrecedente;
    }
 
    public void afficher(Stage stage) {
 
        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;";
        String styleEntete = "-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: white; -fx-background-color: #0F056B; -fx-padding: 8;";
        String styleCellule = "-fx-font-size: 13px; -fx-padding: 6 8; "
                + "-fx-border-color: #CCCCCC; -fx-border-width: 0 0 1 0;";
 
     
        Label titre = new Label("RÉCAPITULATIF DU DEVIS — " + batiment.getId());
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        Label sousTitre = new Label("Projet : " + batiment.getId()
                + "   |   Date : " + LocalDate.now());
        sousTitre.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");
 
        VBox topBox = new VBox(6, titre, sousTitre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(25));
 
     
        List<LigneDevis> lignesDevis = lireDevis();
 
        
        GridPane tableau = new GridPane();
        tableau.setHgap(0);
        tableau.setVgap(0);
        tableau.setPadding(new Insets(10));
        tableau.setStyle("-fx-background-color: white;");
 
        
        String[] entetes = {"Pièce", "Murs (€)", "Sol (€)", "Plafond (€)", "Total (€)"};
        for (int col = 0; col < entetes.length; col++) {
            Label lbl = new Label(entetes[col]);
            lbl.setStyle(styleEntete);
            lbl.setMinWidth(col == 0 ? 200 : 130);
            lbl.setMaxWidth(col == 0 ? 200 : 130);
            tableau.add(lbl, col, 0);
        }
 
      
        double totalMurs = 0, totalSol = 0, totalPlafond = 0, grandTotal = 0;
 
        for (int row = 0; row < lignesDevis.size(); row++) {
            LigneDevis ld = lignesDevis.get(row);
            String bgColor = (row % 2 == 0) ? "#F9F9F9" : "#FFFFFF";
 
            String[] valeurs = {
                ld.piece,
                String.format("%.2f", ld.coutMurs),
                String.format("%.2f", ld.coutSol),
                String.format("%.2f", ld.coutPlafond),
                String.format("%.2f", ld.total)
            };
 
            for (int col = 0; col < valeurs.length; col++) {
                Label cell = new Label(valeurs[col]);
                cell.setStyle(styleCellule + "-fx-background-color: " + bgColor + ";");
                cell.setMinWidth(col == 0 ? 200 : 130);
                cell.setMaxWidth(col == 0 ? 200 : 130);
                tableau.add(cell, col, row + 1);
            }
 
            totalMurs    += ld.coutMurs;
            totalSol     += ld.coutSol;
            totalPlafond += ld.coutPlafond;
            grandTotal   += ld.total;
        }
 
       
        int rowTotal = lignesDevis.size() + 1;
        String styleTotalCell = "-fx-font-weight: bold; -fx-font-size: 14px; "
                + "-fx-background-color: #E8E8FF; -fx-padding: 8; "
                + "-fx-border-color: #0F056B; -fx-border-width: 1 0 1 0;";
 
        String[] totaux = {
            "TOTAL",
            String.format("%.2f", totalMurs),
            String.format("%.2f", totalSol),
            String.format("%.2f", totalPlafond),
            String.format("%.2f", grandTotal)
        };
 
        for (int col = 0; col < totaux.length; col++) {
            Label cell = new Label(totaux[col]);
            cell.setStyle(styleTotalCell);
            cell.setMinWidth(col == 0 ? 200 : 130);
            tableau.add(cell, col, rowTotal);
        }
 
        ScrollPane scrollPane = new ScrollPane(tableau);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
 
        
        if (lignesDevis.isEmpty()) {
            Label lblVide = new Label("Aucune pièce enregistrée pour ce projet dans Devis.txt.");
            lblVide.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            scrollPane.setContent(lblVide);
        }
 
     
        Label lblMessage = new Label("");
        lblMessage.setStyle("-fx-font-size: 13px;");
 
        Button btnTelecharger = new Button("⬇  TÉLÉCHARGER LE DEVIS");
        btnTelecharger.setStyle(styleBouton);
 
        final double fTotalMurs    = totalMurs;
        final double fTotalSol     = totalSol;
        final double fTotalPlafond = totalPlafond;
        final double fGrandTotal   = grandTotal;
 
        btnTelecharger.setOnAction(e -> {
            try {
                String nomFichier = "Devis_" + batiment.getId() + "_" + LocalDate.now() + ".txt";
                String contenu = genererTexteDevis(lignesDevis,
                        fTotalMurs, fTotalSol, fTotalPlafond, fGrandTotal);
                Files.write(Paths.get(nomFichier), contenu.getBytes());
                lblMessage.setStyle("-fx-text-fill: green; -fx-font-size: 13px;");
                lblMessage.setText("✔ Fichier enregistré : " + nomFichier);
            } catch (IOException ex) {
                lblMessage.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
                lblMessage.setText("Erreur lors de l'enregistrement.");
                ex.printStackTrace();
            }
        });

 
        HBox bottomBox = new HBox(20, btnTelecharger, lblMessage);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
 
  
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F5;");
        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(bottomBox);
 
        Scene scene = new Scene(root);
        stage.setTitle("Récapitulatif Devis — " + batiment.getId());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnHidden(e -> {
            if (fenetrePrecedente != null) {
                fenetrePrecedente.show();
            }
        });
        stage.show();
    }
 
    
    private List<LigneDevis> lireDevis() {
        List<LigneDevis> liste = new ArrayList<>();
        String idDevis = "D_" + batiment.getId();
 
        try (BufferedReader reader = new BufferedReader(new FileReader("Devis.txt"))) {
            String ligne;
            reader.readLine(); // ignorer en-tête
 
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
 
                String[] parts = ligne.split(";");
                if (parts.length < 7) continue;
 
                String idD   = parts[0].trim();
                String idP   = parts[1].trim();
                String piece = parts[2].trim();
 
                
                if (!idD.equalsIgnoreCase(idDevis)
                        || !idP.equalsIgnoreCase(batiment.getId())) {
                    continue;
                }
 
                double coutMurs    = Double.parseDouble(parts[3].trim());
                double coutSol     = Double.parseDouble(parts[4].trim());
                double coutPlafond = Double.parseDouble(parts[5].trim());
                double total       = Double.parseDouble(parts[6].trim());
 
                liste.add(new LigneDevis(piece, coutMurs, coutSol, coutPlafond, total));
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return liste;
    }
 

    private String genererTexteDevis(List<LigneDevis> lignes,
                                     double totalMurs, double totalSol,
                                     double totalPlafond, double grandTotal) {
        StringBuilder sb = new StringBuilder();
        String sep = "=".repeat(70) + "\n";
        String ligne = "-".repeat(70) + "\n";
 
        sb.append(sep);
        sb.append("                        DEVIS BATIMENT \n");
        sb.append(sep);
        sb.append(String.format("  Projet       : %s%n", batiment.getId()));
        sb.append(String.format("  Date         : %s%n", LocalDate.now()));
        sb.append(String.format("  Surface bât. : %.2f m x %.2f m%n",
                batiment.getLargeur(), batiment.getLongueur()));
        sb.append(sep);
        sb.append("\n");
        sb.append(String.format("  %-22s %10s %10s %10s %10s%n",
                "PIÈCE", "MURS (€)", "SOL (€)", "PLAFOND (€)", "TOTAL (€)"));
        sb.append(ligne);
 
        for (LigneDevis ld : lignes) {
            sb.append(String.format("  %-22s %10.2f %10.2f %11.2f %10.2f%n",
                    ld.piece, ld.coutMurs, ld.coutSol, ld.coutPlafond, ld.total));
        }
 
        sb.append(ligne);
        sb.append(String.format("  %-22s %10.2f %10.2f %11.2f %10.2f%n",
                "TOTAL", totalMurs, totalSol, totalPlafond, grandTotal));
        sb.append(sep);
        sb.append(String.format("%n  MONTANT TOTAL DU DEVIS : %.2f €%n", grandTotal));
        sb.append(sep);
        sb.append("\n  Merci de votre confiance.\n");
 
        return sb.toString();
    }
}