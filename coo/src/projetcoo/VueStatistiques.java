package projetcoo;

import javax.swing.*;
/** 
 * On importe fonctionnalité par fonctionnalité de la librairie awt
 * car awt.List entre en conflit avec java.util.List
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

// Importation de la bibliothèque JFreeChart pour créer les graphiques
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Classe VueStatistiques
 * -----------------------
 * Cette classe représente la partie interface graphique qui affiche
 * différentes statistiques sur les réservations d’outils.
 *
 * Les graphiques sont générés grâce à la bibliothèque JFreeChart.
 *
 * Les statistiques affichées sont :
 * - L’utilisateur ayant effectué le plus de réservations
 * - La répartition des réservations par type d’outil
 * - Les 5 ressources les plus réservées
 * - L’évolution du nombre de réservations dans le temps
 * - La répartition des réservations par fonction
 *
 * Cette classe fait uniquement l'affichage : les calculs statistiques
 * sont réalisés dans la classe Reservation (côté modèle).
 */
public class VueStatistiques extends JPanel {

    // Conteneur principal qui va accueillir les différents graphiques
    private JPanel statsPanelContainer;     

    // Label qui affiche l’utilisateur ayant effectué le plus de réservations
    private JLabel lblUtilisateurActif;     

    /**
     * Constructeur de la vue statistiques
     * Initialise l’organisation de l’interface graphique
     */
    public VueStatistiques() {

        // Layout principal : organisation en haut (titre) et centre (graphiques)
        setLayout(new BorderLayout());

        // Panel du haut qui contient le texte indiquant l'utilisateur le plus actif
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 240, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Création du label
        lblUtilisateurActif = new JLabel("Utilisateur le plus actif : (Aucune donnée)");
        lblUtilisateurActif.setFont(new Font("Arial", Font.BOLD, 16));

        // Ajout du label dans le panel
        headerPanel.add(lblUtilisateurActif);

        // Ajout du panel dans la partie nord de la fenêtre
        add(headerPanel, BorderLayout.NORTH);

        // Panel qui contiendra les graphiques (organisation en grille)
        statsPanelContainer = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanelContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ajout du conteneur de statistiques au centre
        add(statsPanelContainer, BorderLayout.CENTER);
    }

    /**
     * Méthode qui met à jour l’affichage des statistiques.
     *
     * Elle récupère les données de réservations puis crée
     * les graphiques correspondants.
     *
     * @param reservationsList Liste des réservations actuellement enregistrées
     */
    public void rafraichir(List<Reservation> reservationsList) {

        // On supprime les anciens graphiques avant de les recréer
        statsPanelContainer.removeAll(); 

        // Si aucune réservation n'existe, on affiche simplement un message
        if (reservationsList.isEmpty()) {
            lblUtilisateurActif.setText("Utilisateur le plus actif : (Aucune donnée)");
            statsPanelContainer.revalidate();
            statsPanelContainer.repaint();
            return;
        }


        // 1) UTILISATEUR LE PLUS ACTIF

        // On appelle une méthode de la classe Reservation qui calcule
        // l'utilisateur ayant effectué le plus de réservations
        lblUtilisateurActif.setText(
            "Utilisateur le plus actif : " + 
            Reservation.calculerUtilisateurLePlusActif(reservationsList)
        );


        // 2) DIAGRAMME CIRCULAIRE (CAMEMBERT)
        // Répartition des réservations par type d’outil

        DefaultPieDataset pieDataset = new DefaultPieDataset();

        // Récupération des données calculées dans la classe Reservation
        Map<String, Integer> typeCounts =
                Reservation.calculerRepartitionParType(reservationsList);

        // Ajout des valeurs dans le dataset
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        // Création du graphique de type pieChart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Répartition par Type",
                pieDataset,
                true,
                true,
                false
        );

        // Personnalisation de l’affichage des étiquettes (nom + pourcentage)
        org.jfree.chart.plot.PiePlot piePlot =
                (org.jfree.chart.plot.PiePlot) pieChart.getPlot();

        piePlot.setLabelGenerator(
                new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                        "{0} : {2}",
                        new java.text.DecimalFormat("0"),
                        new java.text.DecimalFormat("0.0%")
                )
        );

        // Ajout du graphique dans l’interface
        statsPanelContainer.add(new ChartPanel(pieChart));


        // 3) DIAGRAMME EN BARRES
        // Top 5 des ressources les plus réservées
        
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        Map<String, Integer> top5 =
                Reservation.calculerTop5Ressources(reservationsList);

        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            barDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }

        // Création du graphique en barres
        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 5 Ressources",
                "Ressource",
                "Nombre",
                barDataset
        );

        statsPanelContainer.add(new ChartPanel(barChart));

    
        // 4) GRAPHIQUE EN LIGNE
        // Évolution du nombre de réservations dans le temps
        
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();

        Map<String, Integer> evolution =
                Reservation.calculerEvolutionDansLeTemps(reservationsList);

        for (Map.Entry<String, Integer> entry : evolution.entrySet()) {
            lineDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Évolution dans le temps",
                "Mois",
                "Nombre",
                lineDataset
        );

        // Inclinaison des labels pour éviter qu'ils se chevauchent
        org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.UP_45
        );

        statsPanelContainer.add(new ChartPanel(lineChart));

     
        // 5) DIAGRAMME EN ANNEAU
        // Répartition des réservations par fonction
     
        DefaultPieDataset ringDataset = new DefaultPieDataset();

        Map<String, Integer> fonctions =
                Reservation.calculerRepartitionParFonction(reservationsList);

        for (Map.Entry<String, Integer> entry : fonctions.entrySet()) {
            ringDataset.setValue(entry.getKey(), entry.getValue());
        }

        // Création du graphique en anneau
        JFreeChart ringChart = ChartFactory.createRingChart(
                "Répartition par Fonction",
                ringDataset,
                true,
                true,
                false
        );

        statsPanelContainer.add(new ChartPanel(ringChart));

        // Rafraîchissement automatique de l’interface graphique
        statsPanelContainer.revalidate();
        statsPanelContainer.repaint();
    }
}