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
    private static final double LARGEUR_ESCALIER_METRES = 2.00;
    private static final double LONGUEUR_ESCALIER_METRES = LARGEUR_COULOIR_METRES;

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
        double surfaceTheorique;

        BlocPlan(String nom, boolean appartement, int idRevetement, double surfaceTheorique) {
            this.nom = nom;
            this.appartement = appartement;
            this.idRevetement = idRevetement;
            this.surfaceTheorique = surfaceTheorique;
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
        double surfaceMoyenne = nbApparts > 0 ? surfaceHabitable / nbApparts : 0;

        Label info = new Label(
                nomEtage + " — " + nbApparts + " appartement(s)"
                        + " — Pièces communes : " + zonesCommunes.size()
                        + " — Couloir : " + String.format("%.2f", surfaceCouloir) + " m²"
                        + " — Surface habitable restante : " + String.format("%.2f", surfaceHabitable) + " m²"
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

        Button btnValiderCouloir = new Button("VALIDER COULOIR + ESCALIER");
        btnValiderCouloir.setStyle(styleValider);

        String[] escalierExistant = SauvegardeProjet.chargerElementPlan(batiment.getId(), nomEtage, "Escalier");
        String[] tremieExistante = SauvegardeProjet.chargerElementPlan(batiment.getId(), nomEtage, "Tremie");
        String[] elementEscalierOuTremie = escalierExistant != null ? escalierExistant : tremieExistante;

        double xEscalierInitial = elementEscalierOuTremie != null
                ? Double.parseDouble(elementEscalierOuTremie[3].trim().replace(",", "."))
                : Math.max(0, (batiment.getLargeur() - LARGEUR_ESCALIER_METRES) / 2.0);

        Slider sliderEscalier = new Slider(
                0,
                Math.max(0, batiment.getLargeur() - LARGEUR_ESCALIER_METRES),
                xEscalierInitial
        );
        sliderEscalier.setShowTickLabels(true);
        sliderEscalier.setShowTickMarks(true);
        sliderEscalier.setMajorTickUnit(1);
        sliderEscalier.setBlockIncrement(0.25);
        sliderEscalier.setPrefWidth(320);

        Label lblEscalier = new Label();
        lblEscalier.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F056B;");

        boolean dernierEtage = estDernierEtage(nomEtage);
        Label lblEtatEscalier = new Label(elementEscalierOuTremie == null
                ? (dernierEtage ? "Trémie non validée" : "Escalier non validé")
                : (dernierEtage ? "Trémie déjà validée" : "Escalier déjà validé"));
        lblEtatEscalier.setStyle(elementEscalierOuTremie == null
                ? "-fx-text-fill: #B00020; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");

        HBox ligneChoixCouloir = new HBox(
                15,
                new Label("Position Y du couloir :"),
                sliderCouloir,
                lblCouloir,
                new Label("Revêtement :"),
                choixRevetementCouloir,
                lblEtat
        );
        ligneChoixCouloir.setAlignment(Pos.CENTER);

        HBox ligneChoixEscalier = new HBox(
                15,
                new Label("Position X escalier/trémie :"),
                sliderEscalier,
                lblEscalier,
                btnValiderCouloir,
                lblEtatEscalier
        );
        ligneChoixEscalier.setAlignment(Pos.CENTER);

        Pane dessin = creerDessinEtage(
                nbApparts,
                surfaceMoyenne,
                sliderCouloir.getValue(),
                choixRevetementCouloir.getValue(),
                sliderEscalier.getValue(),
                true,
                zonesCommunes
        );

        lblCouloir.setText(String.format("%.2f m", sliderCouloir.getValue()));
        lblEscalier.setText(String.format("%.2f m", sliderEscalier.getValue()));

        VBox centre = new VBox(15, info, ligneChoixCouloir, ligneChoixEscalier, dessin);
        centre.setAlignment(Pos.TOP_CENTER);
        centre.setPadding(new Insets(15));

        sliderCouloir.valueProperty().addListener((obs, oldValue, newValue) -> {
            double yCouloir = newValue.doubleValue();
            lblCouloir.setText(String.format("%.2f m", yCouloir));

            Pane nouveauDessin = creerDessinEtage(
                    nbApparts,
                    surfaceMoyenne,
                    yCouloir,
                    choixRevetementCouloir.getValue(),
                    sliderEscalier.getValue(),
                    true,
                    zonesCommunes
            );

            centre.getChildren().set(3, nouveauDessin);
        });

        sliderEscalier.valueProperty().addListener((obs, oldValue, newValue) -> {
            double xEscalier = newValue.doubleValue();
            lblEscalier.setText(String.format("%.2f m", xEscalier));

            Pane nouveauDessin = creerDessinEtage(
                    nbApparts,
                    surfaceMoyenne,
                    sliderCouloir.getValue(),
                    choixRevetementCouloir.getValue(),
                    xEscalier,
                    true,
                    zonesCommunes
            );

            centre.getChildren().set(3, nouveauDessin);
        });

        choixRevetementCouloir.setOnAction(e -> {
            Pane nouveauDessin = creerDessinEtage(
                    nbApparts,
                    surfaceMoyenne,
                    sliderCouloir.getValue(),
                    choixRevetementCouloir.getValue(),
                    sliderEscalier.getValue(),
                    true,
                    zonesCommunes
            );

            centre.getChildren().set(3, nouveauDessin);
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

            double xEscalier = sliderEscalier.getValue();

            GestionCouloirEtage.sauvegarderCouloirTousEtages(
                    batiment.getId(),
                    batiment.getNbEtage(),
                    yCouloir,
                    LARGEUR_COULOIR_METRES,
                    rev.getIdRevetement()
            );

            SauvegardeProjet.sauvegarderCouloirTousEtages(
                    batiment.getId(),
                    batiment.getNbEtage(),
                    batiment.getLargeur(),
                    yCouloir,
                    LARGEUR_COULOIR_METRES,
                    rev.getIdRevetement()
            );

            SauvegardeProjet.sauvegarderEscalierEtTremieTousEtages(
                    batiment.getId(),
                    batiment.getNbEtage(),
                    xEscalier,
                    yCouloir,
                    LARGEUR_ESCALIER_METRES,
                    LONGUEUR_ESCALIER_METRES,
                    3.0,
                    rev.getIdRevetement(),
                    rev.getIdRevetement()
            );

            sauvegarderAppartementsSelonAffichage(
                    nbApparts,
                    surfaceMoyenne,
                    yCouloir,
                    zonesCommunes,
                    rev.getIdRevetement()
            );

            lblEtat.setText("Couloir validé sur tous les étages");
            lblEtat.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            lblEtatEscalier.setText("Escalier/trémie validé sur tous les étages");
            lblEtatEscalier.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
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
        stage.setFullScreen(true);
        stage.show();
    }

    private Pane creerDessinEtage(int nbApparts,
                                  double surfaceMoyenne,
                                  double yCouloirMetres,
                                  Revetement revetementCouloir,
                                  double xEscalierMetres,
                                  boolean afficherEscalierTemporaire,
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
        double echelleX = largeurTotale / batiment.getLargeur();

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

        ArrayList<BlocPlan> blocs = construireBlocs(nbApparts, surfaceMoyenne, zonesCommunes);

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

        dessinerLigneBlocs(
                dessin,
                blocsHaut,
                xDepart,
                yDepart,
                largeurTotale,
                hauteurZoneHaut
        );

        dessinerCouloir(
                dessin,
                xDepart,
                yCouloirPixels,
                largeurTotale,
                hauteurCouloirPixels,
                revetementCouloir
        );

        dessinerEscalierOuTremie(
                dessin,
                xDepart + xEscalierMetres * echelleX,
                yCouloirPixels,
                LARGEUR_ESCALIER_METRES * echelleX,
                LONGUEUR_ESCALIER_METRES * echelleY,
                afficherEscalierTemporaire,
                estDernierEtage(nomEtage)
        );

        dessinerLigneBlocs(
                dessin,
                blocsBas,
                xDepart,
                yCouloirPixels + hauteurCouloirPixels,
                largeurTotale,
                hauteurZoneBas
        );

        return dessin;
    }

    private ArrayList<BlocPlan> construireBlocs(int nbApparts,
                                                double surfaceMoyenne,
                                                ArrayList<ZoneCommune> zonesCommunes) {
        ArrayList<BlocPlan> blocs = new ArrayList<>();

        for (ZoneCommune z : zonesCommunes) {
            blocs.add(new BlocPlan(z.nom, false, z.idRevetement, z.getSurface()));
        }

        for (int i = 1; i <= nbApparts; i++) {
            blocs.add(new BlocPlan("Appart " + i, true, 0, surfaceMoyenne));
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

        double largeurBlocPixels = largeur / blocs.size();

        for (int i = 0; i < blocs.size(); i++) {
            BlocPlan bloc = blocs.get(i);

            double bx = x + i * largeurBlocPixels;
            double by = y;

            Rectangle rect = new Rectangle(bx, by, largeurBlocPixels, hauteur);

            if (bloc.appartement) {
                rect.setFill(Color.web("#F8F8F8"));
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(2);
            } else {
                rect.setFill(getCouleurDepuisCatalogue(bloc.idRevetement));
                rect.setStroke(Color.web("#B00020"));
                rect.setStrokeWidth(2.5);
            }

            double largeurReelle = pixelsVersMetresLargeur(largeurBlocPixels);
            double longueurReelle = pixelsVersMetresLongueur(hauteur);
            double surfaceReelleAffichee = largeurReelle * longueurReelle;

            Text titre = new Text(bx + 8, by + 22, bloc.nom);
            titre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

            Text surface = new Text(bx + 8, by + 42,
                    String.format("%.2f m²", surfaceReelleAffichee));
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



    private void dessinerEscalierOuTremie(Pane dessin,
                                          double x,
                                          double y,
                                          double largeur,
                                          double hauteur,
                                          boolean afficher,
                                          boolean dernierEtage) {
        if (!afficher) {
            return;
        }

        Rectangle rect = new Rectangle(x, y, largeur, hauteur);
        rect.setFill(dernierEtage ? Color.web("#FFF3CD") : Color.web("#D6EAF8"));
        rect.setStroke(dernierEtage ? Color.web("#B8860B") : Color.web("#0F056B"));
        rect.setStrokeWidth(2.5);

        Text texte = new Text(
                x + 8,
                y + Math.max(18, hauteur / 2 + 5),
                dernierEtage ? "Trémie" : "Escalier"
        );
        texte.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        dessin.getChildren().addAll(rect, texte);
    }

    private boolean estDernierEtage(String nomEtageCourant) {
        return extraireNumeroEtage(nomEtageCourant) == batiment.getNbEtage();
    }

    private int extraireNumeroEtage(String nomEtageCourant) {
        if (nomEtageCourant == null || nomEtageCourant.equalsIgnoreCase("RDC")) {
            return 0;
        }

        String chiffres = nomEtageCourant.replaceAll("[^0-9]", "");

        if (chiffres.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(chiffres);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private void sauvegarderAppartementsSelonAffichage(int nbApparts,
                                                       double surfaceMoyenne,
                                                       double yCouloirMetres,
                                                       ArrayList<ZoneCommune> zonesCommunes,
                                                       int idRevetementAppartement) {
        /*
         * Cette méthode sauvegarde les appartements selon la même logique que l'affichage.
         * Elle permet à PlanProjets.txt d'avoir des dimensions cohérentes avec le plan affiché.
         */
        ArrayList<BlocPlan> blocs = construireBlocs(nbApparts, surfaceMoyenne, zonesCommunes);

        double hauteurZoneHaut = Math.max(0, yCouloirMetres);
        double hauteurZoneBas = Math.max(0, batiment.getLongueur() - (yCouloirMetres + LARGEUR_COULOIR_METRES));

        int nbHaut = calculerNombreBlocsHaut(blocs.size(), hauteurZoneHaut, hauteurZoneBas);

        ArrayList<BlocPlan> blocsHaut = new ArrayList<>();
        ArrayList<BlocPlan> blocsBas = new ArrayList<>();

        for (int i = 0; i < blocs.size(); i++) {
            if (i < nbHaut) {
                blocsHaut.add(blocs.get(i));
            } else {
                blocsBas.add(blocs.get(i));
            }
        }

        SauvegardeProjet.supprimerAppartementsAutoEtage(batiment.getId(), nomEtage);

        sauvegarderAppartementsDansLigne(blocsHaut, 0, hauteurZoneHaut, idRevetementAppartement);
        sauvegarderAppartementsDansLigne(blocsBas, yCouloirMetres + LARGEUR_COULOIR_METRES, hauteurZoneBas, idRevetementAppartement);
    }

    private void sauvegarderAppartementsDansLigne(ArrayList<BlocPlan> blocs,
                                                  double y,
                                                  double longueur,
                                                  int idRevetementAppartement) {
        if (blocs.isEmpty() || longueur <= 0) {
            return;
        }

        double largeurBloc = batiment.getLargeur() / blocs.size();

        int numeroAppartement = 1;

        for (int i = 0; i < blocs.size(); i++) {
            BlocPlan bloc = blocs.get(i);

            if (!bloc.appartement) {
                continue;
            }

            String nom = bloc.nom.replace(" ", "");

            SauvegardeProjet.sauvegarderElementPlan(
                    batiment.getId(),
                    nomEtage,
                    nom,
                    i * largeurBloc,
                    y,
                    largeurBloc,
                    longueur,
                    3.0,
                    idRevetementAppartement
            );

            numeroAppartement++;
        }
    }

    private double pixelsVersMetresLargeur(double largeurPixels) {
        return largeurPixels / 700.0 * batiment.getLargeur();
    }

    private double pixelsVersMetresLongueur(double hauteurPixels) {
        return hauteurPixels / 350.0 * batiment.getLongueur();
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
                        || nom.equalsIgnoreCase("Escalier")
                        || nom.equalsIgnoreCase("Tremie")
                        || nom.equalsIgnoreCase("Trémie")
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
