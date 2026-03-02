package projetcoo;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap; 
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Comparator;

// Imports JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class VueStatistiques extends JPanel {

    private JPanel statsPanelContainer;
    private JLabel lblUtilisateurActif;

    public VueStatistiques() {
        setLayout(new BorderLayout());
        
        // En-tête avec l'utilisateur le plus actif
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 240, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        lblUtilisateurActif = new JLabel("Utilisateur le plus actif : (Aucune donnée)");
        lblUtilisateurActif.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(lblUtilisateurActif);
        add(headerPanel, BorderLayout.NORTH);

        // Conteneur pour les graphiques : Grille 2 lignes, 2 colonnes (4 cases)
        statsPanelContainer = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanelContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statsPanelContainer, BorderLayout.CENTER);
    }

    // --- METHODES DE CALCUL INTEGREES ---

    private String calculerUtilisateurLePlusActif(List<Reservation> reservationsList) {
        Map<String, Integer> compteurs = new HashMap<>();
        for (Reservation r : reservationsList) {
            String idUser = r.getUtilisateur().getId(); 
            compteurs.put(idUser, compteurs.getOrDefault(idUser, 0) + 1);
        }
        String meilleurUser = "Aucun";
        int maxReservations = 0;
        for (Map.Entry<String, Integer> entry : compteurs.entrySet()) {
            if (entry.getValue() > maxReservations) {
                maxReservations = entry.getValue();
                meilleurUser = entry.getKey();
            }
        }
        if (maxReservations > 0) {
            return meilleurUser + " (" + maxReservations + " réservations)";
        }
        return "Aucun";
    }

    private Map<String, Integer> calculerRepartitionParType(List<Reservation> reservationsList) {
        Map<String, Integer> compteurs = new HashMap<>();
        for (Reservation r : reservationsList) {
            String type = r.getType();
            compteurs.put(type, compteurs.getOrDefault(type, 0) + 1);
        }
        return compteurs;
    }

    private Map<String, Integer> calculerTop5Ressources(List<Reservation> reservationsList) {
        Map<String, Integer> compteurs = new HashMap<>();
        for (Reservation r : reservationsList) {
            String idRes = r.getRessource().getId();
            compteurs.put(idRes, compteurs.getOrDefault(idRes, 0) + 1);
        }
        List<Map.Entry<String, Integer>> listeTriee = new ArrayList<>(compteurs.entrySet());
        listeTriee.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<String, Integer> top5 = new LinkedHashMap<>(); 
        int limite = Math.min(5, listeTriee.size()); 
        for (int i = 0; i < limite; i++) {
            top5.put(listeTriee.get(i).getKey(), listeTriee.get(i).getValue());
        }
        return top5;
    }

    private Map<String, Integer> calculerEvolutionDansLeTemps(List<Reservation> reservationsList) {
        Map<String, Integer> compteurs = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM"); 

        for (Reservation r : reservationsList) {
            if (r.getDateHeureDebut() != null) {
                String moisAnnee = r.getDateHeureDebut().format(formatter);
                compteurs.put(moisAnnee, compteurs.getOrDefault(moisAnnee, 0) + 1);
            }
        }
        return compteurs;
    }

    // Calcul par Fonction (Étudiant, Enseignant...)
    private Map<String, Integer> calculerRepartitionParFonction(List<Reservation> reservationsList) {
        Map<String, Integer> compteurs = new HashMap<>();
        for (Reservation r : reservationsList) {
            String fonction = r.getUtilisateur().getFonction();
            // Au cas où la fonction serait vide ou nulle dans le CSV
            if (fonction == null || fonction.trim().isEmpty()) {
                fonction = "Non renseigné";
            }
            compteurs.put(fonction, compteurs.getOrDefault(fonction, 0) + 1);
        }
        return compteurs;
    }


    // MISE A JOUR DES GRAPHIQUES

    public void rafraichir(List<Reservation> reservationsList) {
        statsPanelContainer.removeAll(); 

        if (reservationsList.isEmpty()) {
            lblUtilisateurActif.setText("Utilisateur le plus actif : (Aucune donnée)");
            statsPanelContainer.revalidate();
            statsPanelContainer.repaint();
            return;
        }

        // 1. UTILISATEUR LE PLUS ACTIF
        String userActif = calculerUtilisateurLePlusActif(reservationsList);
        lblUtilisateurActif.setText("Utilisateur le plus actif : " + userActif);

        // 2. GRAPHIQUE 1 : Répartition par Type (Camembert)
        DefaultPieDataset pieDatasetType = new DefaultPieDataset();
        Map<String, Integer> typeCounts = calculerRepartitionParType(reservationsList); 
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            pieDatasetType.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart pieChartType = ChartFactory.createPieChart("Répartition par Type d'emprunt", pieDatasetType, true, true, false);
        statsPanelContainer.add(new ChartPanel(pieChartType));

        // 3. GRAPHIQUE 2 : Top 5 des Ressources (Barres)
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        Map<String, Integer> top5Counts = calculerTop5Ressources(reservationsList); 
        for (Map.Entry<String, Integer> entry : top5Counts.entrySet()) {
            barDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }
        JFreeChart barChart = ChartFactory.createBarChart("Top 5 des Ressources", "Ressource", "Nombre d'emprunts", barDataset);
        statsPanelContainer.add(new ChartPanel(barChart));

        // GRAPHIQUE 3 : Évolution temporelle (Ligne)
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        Map<String, Integer> evolutionCounts = calculerEvolutionDansLeTemps(reservationsList);
        for (Map.Entry<String, Integer> entry : evolutionCounts.entrySet()) {
            lineDataset.addValue(entry.getValue(), "Réservations", entry.getKey());
        }
        JFreeChart lineChart = ChartFactory.createLineChart("Évolution des emprunts dans le temps", "Mois", "Nombre d'emprunts", lineDataset);
        
        // Inclinaison des labels de l'axe X à 45 degrés pour la lisibilité
        org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);
        
        statsPanelContainer.add(new ChartPanel(lineChart));

        // GRAPHIQUE 4 : Répartition par Fonction (Anneau/Ring)
        DefaultPieDataset pieDatasetFonction = new DefaultPieDataset();
        Map<String, Integer> fonctionCounts = calculerRepartitionParFonction(reservationsList);
        for (Map.Entry<String, Integer> entry : fonctionCounts.entrySet()) {
            pieDatasetFonction.setValue(entry.getKey(), entry.getValue());
        }
        // Utilisation d'un RingChart (Graphique en anneau) 
        JFreeChart ringChartFonction = ChartFactory.createRingChart("Répartition par Fonction", pieDatasetFonction, true, true, false);
        statsPanelContainer.add(new ChartPanel(ringChartFonction));
        // -------------------------------------------------------------------------

        // Actualiser l'affichage
        statsPanelContainer.revalidate();
        statsPanelContainer.repaint();
    }
}