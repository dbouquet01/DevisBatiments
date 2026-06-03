package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Immeuble;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class FenetreAttributsImmeubleController {

    private final FenetreAttributsImmeuble vue;

    public FenetreAttributsImmeubleController(FenetreAttributsImmeuble vue) {
        this.vue = vue;
    }

    public void retourProjet(Stage stage) {
        new FenetreProjet().afficher(stage);
    }

    public void validerAttributs(Stage stage, HashMap<String, Integer> nbAppartsPreRemplis) {
        String id = vue.fieldId.getText().trim();
        String designation = vue.fieldDesignation.getText().trim();
        String txtLargeur = vue.fieldLargeur.getText().trim().replace(",", ".");
        String txtLongueur = vue.fieldLongueur.getText().trim().replace(",", ".");
        String txtEtage = vue.fieldEtage.getText().trim();
        String txtHauteur = vue.fieldHauteur.getText().trim().replace(",", ".");

        Revetement facade = vue.comboFacade.getValue();
        Revetement isolation = vue.comboIsolation.getValue();

        if (id.isEmpty() || designation.isEmpty() || txtLargeur.isEmpty()
                || txtLongueur.isEmpty() || txtEtage.isEmpty() || txtHauteur.isEmpty()
                || facade == null || isolation == null) {
            vue.lblErreur.setText("Veuillez remplir tous les champs et faire vos sélections de revêtements.");
            return;
        }

        try {
            double largeur = Double.parseDouble(txtLargeur);
            double longueur = Double.parseDouble(txtLongueur);
            int nbEtages = Integer.parseInt(txtEtage);
            double hauteurEtage = Double.parseDouble(txtHauteur);

            if (largeur <= 0 || longueur <= 0 || nbEtages < 0 || hauteurEtage <= 0) {
                vue.lblErreur.setText("Les dimensions doivent être positives.");
                return;
            }

            double hauteurTotale = hauteurEtage * (nbEtages + 1);
            double perimetre = 2 * (largeur + longueur);
            double surfaceFacade = perimetre * hauteurTotale;
            double surfaceIsolation = perimetre * hauteurTotale;
            double coutFacade = facade.getPrixUnitaire() * surfaceFacade;
            double coutIsolation = isolation.getPrixUnitaire() * surfaceIsolation;

            SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Facade", surfaceFacade, 0, 0, coutFacade);
            SauvegardeProjet.sauvegarderDevis("D_" + id, id, "Isolation", surfaceIsolation, 0, 0, coutIsolation);

            vue.afficherSaisieAppartements(
                    stage, id, designation, largeur, longueur, nbEtages,
                    hauteurEtage,
                    nbAppartsPreRemplis,
                    facade.getIdRevetement(),
                    isolation.getIdRevetement()
            );

        } catch (NumberFormatException ex) {
            vue.lblErreur.setText("Les valeurs numériques sont invalides.");
        }
    }

    public void validerAppartements(Stage stage, String id, String designation,
                                    double largeur, double longueur, int nbEtages,
                                    double hauteurEtage, int idFacade, int idIsolation) {
        HashMap<String, Integer> nbAppartsParEtage = new HashMap<>();
        int totalAppartements = 0;

        try {
            for (int i = 0; i <= nbEtages; i++) {
                String etage = nomEtage(i);
                String txt = vue.champsApparts.get(i).getText().trim();

                if (txt.isEmpty()) {
                    vue.lblErreurAppartements.setText("Veuillez remplir le nombre d'appartements pour " + etage + ".");
                    return;
                }

                int nbApparts = Integer.parseInt(txt);

                if (nbApparts < 0) {
                    vue.lblErreurAppartements.setText("Le nombre d'appartements doit être positif pour " + etage + ".");
                    return;
                }

                nbAppartsParEtage.put(etage, nbApparts);
                totalAppartements += nbApparts;
            }

            Immeuble immeuble = new Immeuble(id, designation, largeur, longueur, nbEtages);

            String idDevis = "D_" + id;
            double hauteurTotale = hauteurEtage * (nbEtages + 1);
            double surfaceEtage = largeur * longueur;
            double surfaceTotale = surfaceEtage * (nbEtages + 1);

            SauvegardeProjet.sauvegarderProjet(
                    id, designation, "IMMEUBLE",
                    nbEtages, hauteurTotale, surfaceTotale,
                    totalAppartements, idDevis, largeur, longueur,
                    idFacade, idIsolation
            );

            for (int i = 0; i <= nbEtages; i++) {
                String nomEtage = nomEtage(i);
                int nbApparts = nbAppartsParEtage.get(nomEtage);

                SauvegardeProjet.sauvegarderEtage(id, nomEtage, nbApparts, surfaceEtage);
            }

            new FenetreEtage(immeuble, nbAppartsParEtage).afficher(stage);

        } catch (NumberFormatException ex) {
            vue.lblErreurAppartements.setText("Veuillez entrer uniquement des nombres entiers.");
        }
    }

    public void preRemplirInfosProjet(String idProjet, int nbEtagesExistant) {
        String[] projet = SauvegardeProjet.chargerProjet(idProjet);
        if (projet == null) return;

        try {
            if (projet.length >= 5 && nbEtagesExistant >= 0) {
                double hauteurTotale = Double.parseDouble(projet[4].trim().replace(",", "."));
                double hauteurParEtage = hauteurTotale / (nbEtagesExistant + 1);

                if (hauteurParEtage > 0) {
                    vue.fieldHauteur.setText(String.valueOf(hauteurParEtage));
                }
            }

            if (projet.length >= 12) {
                int idFacade = Integer.parseInt(projet[10].trim());
                int idIsolation = Integer.parseInt(projet[11].trim());

                selectionnerRevetement(vue.comboFacade, idFacade);
                selectionnerRevetement(vue.comboIsolation, idIsolation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectionnerRevetement(ComboBox<Revetement> combo, int idRevetement) {
        for (Revetement r : combo.getItems()) {
            if (r.getIdRevetement() == idRevetement) {
                combo.setValue(r);
                return;
            }
        }
    }

    public HashMap<String, Integer> chargerNbAppartementsDepuisEtage(String idProjet) {
        HashMap<String, Integer> nbApparts = new HashMap<>();
        if (idProjet == null || idProjet.trim().isEmpty()) return nbApparts;

        try (BufferedReader reader = new BufferedReader(new FileReader("Etage.txt"))) {
            String ligne;
            reader.readLine();
            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;
                String[] p = ligne.split(";");
                if (p.length < 4) continue;

                String idProjetLigne = p[1].trim();
                String nomEtage = p[2].trim();
                int nbAppartements = Integer.parseInt(p[3].trim());

                if (idProjetLigne.equalsIgnoreCase(idProjet)) {
                    nbApparts.put(nomEtage, nbAppartements);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbApparts;
    }

    public String nomEtage(int index) {
        return (index == 0) ? "RDC" : "Etage " + index;
    }
}
