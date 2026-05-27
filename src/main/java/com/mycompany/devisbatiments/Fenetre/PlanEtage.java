/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package com.mycompany.devisbatiments.Fenetre;

import com.mycompany.devisbatiments.donnees.SauvegardeProjet;
import com.mycompany.devisbatiments.elements.Batiments;
import com.mycompany.devisbatiments.elements.Revetement;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class PlanEtage {

    private final Batiments batiment;
    private final String nomEtage;
    private final double surfaceEtage;
    private final HashMap<String, Integer> nbAppartsParEtage;

    private static final double LARGEUR_COULOIR_METRES = 1.50;

    private static class ZoneCommune {
        String nom;
        double largeur;
        double longueur;
        int idRevetement;

        ZoneCommune(String nom, double largeur, double longueur, int idRevetement) {
            this.nom = nom;
            this.largeur = largeur;
            this.longueur = longueur;
            this.idRevetement = idRevetement;
        }

        double getSurface() {
            return largeur * longueur;
        }
    }

    private static class BlocPlan {
        String nom;
        boolean appartement;
        int idRevetement;
        double surfaceReelle;

        BlocPlan(String nom, boolean appartement, int idRevetement, double surfaceReelle) {
            this.nom = nom;
            this.appartement = appartement;
            this.idRevetement = idRevetement;
            this.surfaceReelle = surfaceReelle;
        }
    }

    public PlanEtage(Batiments batiment, String nomEtage,
                     double surfaceEtage,
                     HashMap<String, Integer> nbAppartsParEtage) {
        this.batiment = batiment;
        this.nomEtage = nomEtage;
        this.surfaceEtage = surfaceEtage;
        this.nbAppartsParEtage = nbAppartsParEtage;
    }

    public void afficher(Stage stage) {

        String styleBouton = "-fx-background-color: #0F056B; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        String styleValider = "-fx-background-color: #28A745; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;";

        Label titre = new Label("PLAN DE L'ÉTAGE — " + nomEtage);
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox topBox = new VBox(titre);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));

        int nbApparts = nbAppartsParEtage.getOrDefault(nomEtage, 0);
        ArrayList<ZoneCommune> zonesCommunes = chargerZonesCommunes();

        double surfaceCouloir = batiment.getLargeur() * LARGEUR_COULOIR_METRES;

        double surfaceZonesCommunes = 0;
        for (ZoneCommune z : zonesCommunes) {
            surfaceZonesCommunes += z.getSurface();
        }

        double surfaceHabitable = Math.max(0, surfaceEtage - surfaceCouloir - surfaceZonesCommunes);
        double surfaceParAppart = nbApparts > 0 ? surfaceHabitable / nbApparts : 0;

        Label info = new Label(
                nomEtage + " — " + nbApparts + " appartement(s)"
                        + " — Pièces communes : " + zonesCommunes.size()
                        + " — Couloir : " + String.format("%.2f", surfaceCouloir) + " m²"
                        + " — Appart moyen : " + String.format("%.2f", surfaceParAppart) + " m²"
        );
        info.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F056B;");

        GestionCouloirEtage.CouloirInfo couloirExistant =
                GestionCouloirEtage.chargerCouloir(batiment.getId(), nomEtage);

        double valeurInitiale = couloirExistant != null
                ? couloirExistant.yCouloir
                : Math.max(0, (batiment.getLongueur() - LARGEUR_COULOIR_METRES) / 2.0);

        Slider sliderCouloir = new Slider(
                0,
                Math.max(0, batiment.getLongueur() - LARGEUR_COULOIR_METRES),
                valeurInitiale
        );

        sliderCouloir.setShowTickLabels(true);
        sliderCouloir.setShowTickMarks(true);
        sliderCouloir.setMajorTickUnit(1);
        sliderCouloir.setBlockIncrement(0.25);
        sliderCouloir.setPrefWidth(420);

        Label lblCouloir = new Label();
        lblCouloir.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F056B;");

        Label lblEtat = new Label(couloirExistant == null
                ? "Couloir non validé"
                : "Couloir déjà validé");
        lblEtat.setStyle(couloirExistant == null
                ? "-fx-text-fill: #B00020; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");

        ComboBox<Revetement> choixRevetementCouloir = new ComboBox<>();
        choixRevetementCouloir.getItems().addAll(Revetement.getRevetementsSol());
        choixRevetementCouloir.setPromptText("Revêtement du couloir");

        if (!choixRevetementCouloir.getItems().isEmpty()) {
            choixRevetementCouloir.setValue(choixRevetementCouloir.getItems().get(0));

            if (couloirExistant != null) {
                for (Revetement r : choixRevetementCouloir.getItems()) {
                    if (r.getIdRevetement() == couloirExistant.idRevetement) {
                        choixRevetementCouloir.setValue(r);
                        break;
                    }
                }
            }
        }

        Button btnValiderCouloir = new Button("VALIDER LE COULOIR");
        btnValiderCouloir.setStyle(styleValider);

        HBox ligneChoix = new HBox(
                15,
                new Label("Position Y du couloir :"),
                sliderCouloir,
                lblCouloir,
                new Label("Revêtement :"),
                choixRevetementCouloir,
                btnValiderCouloir,
                lblEtat
        );
        ligneChoix.setAlignment(Pos.CENTER);

        Pane dessin = creerDessinEtage(
                nbApparts,
                surfaceParAppart,
                sliderCouloir.getValue(),
                choixRevetementCouloir.getValue(),
                zonesCommunes
        );

        lblCouloir.setText(String.format("%.2f m", sliderCouloir.getValue()));

        VBox centre = new VBox(15, info, ligneChoix, dessin);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(15));

        sliderCouloir.valueProperty().addListener((obs, oldValue, newValue) -> {
            double yCouloir = newValue.doubleValue();
            lblCouloir.setText(String.format("%.2f m", yCouloir));

            Pane nouveauDessin = creerDessinEtage(
                    nbApparts,
                    surfaceParAppart,
                    yCouloir,
                    choixRevetementCouloir.getValue(),
                    zonesCommunes
            );

            centre.getChildren().set(2, nouveauDessin);
        });

        choixRevetementCouloir.setOnAction(e -> {
            Pane nouveauDessin = creerDessinEtage(
                    nbApparts,
                    surfaceParAppart,
                    sliderCouloir.getValue(),
                    choixRevetementCouloir.getValue(),
                    zonesCommunes
            );

            centre.getChildren().set(2, nouveauDessin);
        });

        btnValiderCouloir.setOnAction(e -> {
            Revetement rev = choixRevetementCouloir.getValue();

            if (rev == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Revêtement manquant");
                alert.setHeaderText("Choisis un revêtement pour le couloir.");
                alert.showAndWait();
                return;
            }

            double yCouloir = sliderCouloir.getValue();

            GestionCouloirEtage.sauvegarderCouloir(
                    batiment.getId(),
                    nomEtage,
                    yCouloir,
                    LARGEUR_COULOIR_METRES,
                    rev.getIdRevetement()
            );

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(),
                    nomEtage,
                    "Couloir",
                    0,
                    yCouloir,
                    batiment.getLargeur(),
                    LARGEUR_COULOIR_METRES,
                    3.0,
                    rev.getIdRevetement()
            );

            lblEtat.setText("Couloir validé");
            lblEtat.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            /*
             * La surface réelle des blocs ne change pas quand on déplace le couloir :
             * surface couloir = largeur bâtiment x 1,50 m.
             * Donc seule la répartition visuelle change, pas les surfaces théoriques.
             */
        });

        Button btnRetour = new Button("RETOUR ÉTAGES");
        btnRetour.setStyle(styleBouton);
        btnRetour.setOnAction(e -> new FenetreEtage(batiment, nbAppartsParEtage).afficher(stage));

        Button btnMenuPrincipal = new Button("MENU PRINCIPAL");
        btnMenuPrincipal.setStyle(styleBouton);
        btnMenuPrincipal.setOnAction(e -> new FenetreProjet().afficher(stage));

        HBox bottomBox = new HBox(20, btnRetour, btnMenuPrincipal);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centre);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1250, 720);
        stage.setTitle("Plan étage");
        stage.setScene(scene);
        stage.show();
    }

    private Pane creerDessinEtage(int nbApparts,
                                  double surfaceParAppart,
                                  double yCouloirMetres,
                                  Revetement revetementCouloir,
                                  ArrayList<ZoneCommune> zonesCommunes) {

        Pane dessin = new Pane();
        dessin.setPrefSize(800, 430);
        dessin.setStyle("-fx-background-color: white; -fx-border-color: #0F056B;");

        if (batiment.getLargeur() <= 0 || batiment.getLongueur() <= 0) {
            return dessin;
        }

        double marge = 35;
        double largeurTotale = 700;
        double hauteurTotale = 350;

        double echelleY = hauteurTotale / batiment.getLongueur();

        double xDepart = marge;
        double yDepart = marge;

        Rectangle contour = new Rectangle(xDepart, yDepart, largeurTotale, hauteurTotale);
        contour.setFill(Color.TRANSPARENT);
        contour.setStroke(Color.BLACK);
        contour.setStrokeWidth(3);
        dessin.getChildren().add(contour);

        double yCouloirPixels = yDepart + yCouloirMetres * echelleY;
        double hauteurCouloirPixels = LARGEUR_COULOIR_METRES * echelleY;

        if (yCouloirPixels < yDepart) {
            yCouloirPixels = yDepart;
        }

        if (yCouloirPixels + hauteurCouloirPixels > yDepart + hauteurTotale) {
            yCouloirPixels = yDepart + hauteurTotale - hauteurCouloirPixels;
        }

        ArrayList<BlocPlan> blocs = construireBlocs(nbApparts, surfaceParAppart, zonesCommunes);

        double hauteurZoneHaut = Math.max(0, yCouloirPixels - yDepart);
        double hauteurZoneBas = Math.max(0, (yDepart + hauteurTotale) - (yCouloirPixels + hauteurCouloirPixels));

        int totalBlocs = blocs.size();

        int nbHaut = calculerNombreBlocsHaut(totalBlocs, hauteurZoneHaut, hauteurZoneBas);
        int nbBas = totalBlocs - nbHaut;

        ArrayList<BlocPlan> blocsHaut = new ArrayList<>();
        ArrayList<BlocPlan> blocsBas = new ArrayList<>();

        for (int i = 0; i < blocs.size(); i++) {
            if (i < nbHaut) {
                blocsHaut.add(blocs.get(i));
            } else {
                blocsBas.add(blocs.get(i));
            }
        }

        dessinerLigneBlocs(dessin, blocsHaut, xDepart, yDepart, largeurTotale, hauteurZoneHaut);

        dessinerCouloir(dessin, xDepart, yCouloirPixels, largeurTotale,
                hauteurCouloirPixels, revetementCouloir);

        dessinerLigneBlocs(dessin, blocsBas, xDepart,
                yCouloirPixels + hauteurCouloirPixels, largeurTotale, hauteurZoneBas);

        return dessin;
    }

    private ArrayList<BlocPlan> construireBlocs(int nbApparts,
                                                double surfaceParAppart,
                                                ArrayList<ZoneCommune> zonesCommunes) {
        ArrayList<BlocPlan> blocs = new ArrayList<>();

        for (ZoneCommune z : zonesCommunes) {
            blocs.add(new BlocPlan(z.nom, false, z.idRevetement, z.getSurface()));
        }

        for (int i = 1; i <= nbApparts; i++) {
            blocs.add(new BlocPlan("Appart " + i, true, 0, surfaceParAppart));
        }

        return blocs;
    }

    private int calculerNombreBlocsHaut(int totalBlocs,
                                        double hauteurZoneHaut,
                                        double hauteurZoneBas) {
        if (totalBlocs <= 0) return 0;
        if (hauteurZoneHaut <= 5 && hauteurZoneBas > 5) return 0;
        if (hauteurZoneBas <= 5 && hauteurZoneHaut > 5) return totalBlocs;

        double hauteurTotaleDisponible = hauteurZoneHaut + hauteurZoneBas;

        if (hauteurTotaleDisponible <= 0) return 0;

        int nbHaut = (int) Math.round(totalBlocs * (hauteurZoneHaut / hauteurTotaleDisponible));

        if (nbHaut < 0) nbHaut = 0;
        if (nbHaut > totalBlocs) nbHaut = totalBlocs;

        if (totalBlocs > 1 && hauteurZoneHaut > 30 && hauteurZoneBas > 30) {
            if (nbHaut == 0) nbHaut = 1;
            if (nbHaut == totalBlocs) nbHaut = totalBlocs - 1;
        }

        return nbHaut;
    }

    private void dessinerLigneBlocs(Pane dessin,
                                    ArrayList<BlocPlan> blocs,
                                    double x,
                                    double y,
                                    double largeur,
                                    double hauteur) {
        if (blocs.isEmpty() || hauteur <= 5) return;

        double largeurBloc = largeur / blocs.size();

        for (int i = 0; i < blocs.size(); i++) {
            BlocPlan bloc = blocs.get(i);

            double bx = x + i * largeurBloc;
            double by = y;

            Rectangle rect = new Rectangle(bx, by, largeurBloc, hauteur);

            if (bloc.appartement) {
                rect.setFill(Color.web("#F8F8F8"));
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(2);
            } else {
                rect.setFill(getCouleurDepuisCatalogue(bloc.idRevetement));
                rect.setStroke(Color.web("#B00020"));
                rect.setStrokeWidth(2.5);
            }

            Text titre = new Text(bx + 8, by + 22, bloc.nom);
            titre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

            Text surface = new Text(bx + 8, by + 42,
                    String.format("%.2f m²", bloc.surfaceReelle));
            surface.setStyle("-fx-font-size: 12px;");

            dessin.getChildren().addAll(rect, titre, surface);
        }
    }

    private void dessinerCouloir(Pane dessin,
                                 double x,
                                 double y,
                                 double largeur,
                                 double hauteur,
                                 Revetement revetementCouloir) {

        Color couleur = Color.web("#E8E8E8");

        if (revetementCouloir != null) {
            couleur = getCouleurDepuisCatalogue(revetementCouloir.getIdRevetement());
        }

        Rectangle couloir = new Rectangle(x, y, largeur, hauteur);
        couloir.setFill(couleur);
        couloir.setStroke(Color.BLACK);
        couloir.setStrokeWidth(2);

        Text texte = new Text(
                x + largeur / 2 - 70,
                y + hauteur / 2 + 5,
                "Couloir 1,50 m"
        );
        texte.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        dessin.getChildren().addAll(couloir, texte);
    }

    private ArrayList<ZoneCommune> chargerZonesCommunes() {
        ArrayList<ZoneCommune> zones = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("PlanProjets.txt"))) {

            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.trim().isEmpty()) continue;

                String[] p = ligne.split(";");

                if (p.length < 9) continue;

                String idProjet = p[0].trim();
                String vue = p[1].trim();
                String nom = p[2].trim();

                if (!idProjet.equalsIgnoreCase(batiment.getId())
                        || !vue.equalsIgnoreCase(nomEtage)) {
                    continue;
                }

                if (nom.equalsIgnoreCase("Couloir")
                        || nom.toLowerCase().startsWith("appart")
                        || nom.toLowerCase().startsWith("appartement")) {
                    continue;
                }

                zones.add(new ZoneCommune(
                        nom,
                        Double.parseDouble(p[5].trim()),
                        Double.parseDouble(p[6].trim()),
                        Integer.parseInt(p[8].trim())
                ));
            }

        } catch (Exception e) {
            // aucune zone commune
        }

        return zones;
    }

    private Color getCouleurDepuisCatalogue(int idRevetement) {

        try (BufferedReader reader = new BufferedReader(new FileReader("CatalogueRevetements.txt"))) {

            String ligne;
            reader.readLine();

            while ((ligne = reader.readLine()) != null) {

                String[] infos = ligne.split(";");

                if (infos.length < 7) continue;

                int id = Integer.parseInt(infos[0].trim());

                if (id == idRevetement) {
                    return Color.web(infos[6].trim());
                }
            }

        } catch (Exception e) {
            // couleur par défaut
        }

        return Color.LIGHTGRAY;
    }
}

