package projetcoo;

import javax.swing.*;
/** On importe fonctionnalité par fonctionnalité de la librairie awt car awt.List entre en conflit avec java.util.List */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Classe VueStatistiques
 * -----------------------
 * Cette classe affiche différents graphiques statistiques
 * à partir d'une liste de réservations.
 * * Elle utilise la bibliothèque JFreeChart pour générer :
 * - un diagramme circulaire (camembert)
 * - un diagramme en barres
 * - un graphique en ligne
 * - un diagramme en anneau
 */
public class VueStatistiques extends JPanel {

    private JPanel statsPanelContainer;     
    private JLabel lblUtilisateurActif;     

    /**
     * Constructeur
     * Initialise la structure graphique de la vue
     */
    public VueStatistiques() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 240, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblUtilisateurActif = new JLabel("Utilisateur le plus actif : (Aucune donnée)");
        lblUtilisateurActif.setFont(new Font("Arial", Font.BOLD, 16));

        headerPanel.add(lblUtilisateurActif);
        add(headerPanel, BorderLayout.NORTH);

        statsPanelContainer = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanelContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statsPanelContainer, BorderLayout.CENTER);
    }

    /**
     * Méthode principale de mise à jour des graphiques
     * Récupère les calculs depuis le modèle (Reservation) et génère l'affichage.
     * * @param reservationsList La liste des réservations actuelles en mémoire
     */
    public void rafraichir(List<Reservation> reservationsList) {
        statsPanelContainer.removeAll(); 

        if (reservationsList.isEmpty()) {
            lblUtilisateurActif.setText("Utilisateur le plus actif : (Aucune donnée)");
            statsPanelContainer.revalidate();
            statsPanelContainer.repaint();
            return;
        }

        // 1) UTILISATEUR LE PLUS ACTIF 
        lblUtilisateurActif.setText("Utilisateur le plus actif : " + Reservation.calculerUtilisateurLePlusActif(reservationsList));

        // 2) CAMEMBERT : Répartition par type
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Integer> typeCounts = Reservation.calculerRepartitionParType(reservationsList);

        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart("Répartition par Type", pieDataset, true, true, false);
        org.jfree.chart.plot.PiePlot piePlot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        piePlot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0} : {2}", new java.text.DecimalFormat("0"), new java.text.DecimalFormat("0.0%")));
        statsPanelContainer.add(new ChartPanel(pieChart));

        // 3) DIAGRAMME EN BARRES : Top 5 ressources
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        Map<String, Integer> top5 = Reservation.calculerTop5Ressources(reservationsList);

        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            barDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart("Top 5 Ressources", "Ressource", "Nombre", barDataset);
        statsPanelContainer.add(new ChartPanel(barChart));

        // 4) GRAPHIQUE EN LIGNE : Evolution mensuelle
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        Map<String, Integer> evolution = Reservation.calculerEvolutionDansLeTemps(reservationsList);

        for (Map.Entry<String, Integer> entry : evolution.entrySet()) {
            lineDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }

        JFreeChart lineChart = ChartFactory.createLineChart("Évolution dans le temps", "Mois", "Nombre", lineDataset);
        org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        statsPanelContainer.add(new ChartPanel(lineChart));

        // 5) ANNEAU : Répartition par fonction
        DefaultPieDataset ringDataset = new DefaultPieDataset();
        Map<String, Integer> fonctions = Reservation.calculerRepartitionParFonction(reservationsList);

        for (Map.Entry<String, Integer> entry : fonctions.entrySet()) {
            ringDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart ringChart = ChartFactory.createRingChart("Répartition par Fonction", ringDataset, true, true, false);
        statsPanelContainer.add(new ChartPanel(ringChart));

        statsPanelContainer.revalidate();
        statsPanelContainer.repaint();
    }
}