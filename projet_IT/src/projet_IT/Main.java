package projet_IT;


import java.util.*;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {

        try {
            // Format pour les dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // 
            // 1️ Créer un utilisateur
            // 
            Utilisateur alice = new Utilisateur("Alice");

            // 
            // 2️ Créer une ressource
            // 
            Ressource pc1 = new Ressource("PC B CD 1", "PC Courte Duree", "Ordinateur portable pour cours");

            // 
            // 3️ Créer une réservation
            // 
            Date dateDebut = sdf.parse("2026-02-03 10:00");
            String duree = "2 heures"; // minutes
            String type = "Emprunt";

            Reservation reservation1 = new Reservation(type, dateDebut, duree, alice, pc1);

            // Ajouter la réservation aux listes
            alice.ajouterReservation(reservation1);
            pc1.ajouterReservation(reservation1);

            // 
            // 4️ Liste globale de réservations
            // 
            List<Reservation> reservations = new ArrayList<>();
            reservations.add(reservation1);

            // 
            // 5 ️Export CSV
            // 
            // Export toutes les réservations
            CSV.exporterFiltre(reservations, "export_toutes.csv", null, null);

            // Export filtré par utilisateur
            CSV.exporterFiltre(reservations, "export_alice.csv", "Alice", null);

            // Export filtré par ressource
            CSV.exporterFiltre(reservations, "export_pc1.csv", null, "PC B CD 1");

            System.out.println("Export terminé !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

	


