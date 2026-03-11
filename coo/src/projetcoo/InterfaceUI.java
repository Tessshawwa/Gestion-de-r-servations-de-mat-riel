package projetcoo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InterfaceUI extends JFrame {

    // La base de données centrale partagée entre les deux onglets
    private List<Reservation> reservationsList = new ArrayList<>();

    public InterfaceUI() {
        setTitle("Gestion des Réservations Universitaires");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // CREATION DU CONTENEUR D'ONGLETS
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 1. Création de l'onglet Statistiques (On le crée en premier car l'onglet Gestion en a besoin)
        VueStatistiques ongletStatistiques = new VueStatistiques();
        
        // 2. Création de l'onglet Gestion (On lui passe la liste de données et l'onglet Stats)
        VueGestion ongletGestion = new VueGestion(reservationsList, ongletStatistiques);
        
        //  AJOUT DES ONGLETS A LA FENETRE 
        tabbedPane.addTab("Gestion des Réservations", ongletGestion);
        tabbedPane.addTab("Statistiques d'usage", ongletStatistiques);
        
        // Ajouter les onglets à la fenêtre principale
        add(tabbedPane);
        
        // Initialiser l'affichage des statistiques (vide au départ/quand il n'y a pas de données)
        ongletStatistiques.rafraichir(reservationsList);
    }
}