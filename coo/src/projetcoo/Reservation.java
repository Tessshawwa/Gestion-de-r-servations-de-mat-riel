package projetcoo;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Classe Reservation
 * -------------------
 * Représente une réservation liant un Utilisateur à une Ressource.
 * Contient également toute la logique métier et les calculs statistiques (Architecture MVC).
 */
public class Reservation {

    // attributs
    private String id;
    private Utilisateur utilisateur;
    private Ressource ressource;
    private LocalDateTime dateHeureDebut;
    private String type; // "Emprunt", "Cours", "Maintenance"
    private LocalDateTime derniereMiseAJour;

    /**
     * Constructeur de la Réservation
     */
    public Reservation(String id, Utilisateur utilisateur, Ressource ressource, LocalDateTime dateHeureDebut, String type) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.ressource = ressource;
        this.dateHeureDebut = dateHeureDebut;
        this.type = type;
        this.derniereMiseAJour = LocalDateTime.now(); // Date actuelle à la création
    }

    // Getters 
    public String getId() { return id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public Ressource getRessource() { return ressource; }
    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public String getType() { return type; }
    public LocalDateTime getDerniereMiseAJour() { return derniereMiseAJour; }

    // Setters avec Mise à jour automatique de la date
    public void setUtilisateur(Utilisateur u) { this.utilisateur = u; updateTimestamp(); }
    public void setRessource(Ressource r) { this.ressource = r; updateTimestamp(); }
    public void setDateHeureDebut(LocalDateTime d) { this.dateHeureDebut = d; updateTimestamp(); }
    public void setType(String t) { this.type = t; updateTimestamp(); }
    
    private void updateTimestamp() { this.derniereMiseAJour = LocalDateTime.now(); }

    //
    // MÉTHODES DE CALCUL MÉTIER 
    // 

    /**
     * Calcule la durée écoulée depuis le début de la réservation.
     * @return Une chaîne de caractères formatée (ex: "2 j 5 h") ou "À venir".
     */
    public String getDureeEcoulee() {
        if (this.dateHeureDebut == null) return "0 j 0 h";
        Duration duration = Duration.between(this.dateHeureDebut, LocalDateTime.now());
        long jours = duration.toDays();
        long heures = duration.toHoursPart(); 
        if (jours < 0 || heures < 0) return "À venir"; 
        return jours + " j " + heures + " h";
    }

    /**
     * Trouve l'utilisateur ayant effectué le plus grand nombre de réservations.
     * @param list La liste complète des réservations
     * @return L'identifiant de l'utilisateur avec son nombre d'emprunts
     */
    public static String calculerUtilisateurLePlusActif(List<Reservation> list) {
        Map<String, Integer> compteur = new HashMap<>();
        for (Reservation r : list) {
            String id = r.getUtilisateur().getId();
            compteur.put(id, compteur.getOrDefault(id, 0) + 1);
        }
        String meilleur = "Aucun";
        int max = 0;
        for (Map.Entry<String, Integer> entry : compteur.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                meilleur = entry.getKey();
            }
        }
        return (max > 0) ? meilleur + " (" + max + " réservations)" : "Aucun";
    }

    /**
     * Calcule la répartition des réservations selon leur type (Emprunt, Maintenance...).
     * @param list La liste complète des réservations
     * @return Une map associant chaque type à son nombre d'occurrences
     */
    public static Map<String, Integer> calculerRepartitionParType(List<Reservation> list) {
        Map<String, Integer> compteur = new HashMap<>();
        for (Reservation r : list) {
            String type = r.getType();
            compteur.put(type, compteur.getOrDefault(type, 0) + 1);
        }
        return compteur;
    }

    /**
     * Détermine les 5 ressources les plus fréquemment réservées.
     * @param list La liste complète des réservations
     * @return Une map triée des 5 ressources les plus populaires
     */
    public static Map<String, Integer> calculerTop5Ressources(List<Reservation> list) {
        Map<String, Integer> compteur = new HashMap<>();
        for (Reservation r : list) {
            String id = r.getRessource().getId();
            compteur.put(id, compteur.getOrDefault(id, 0) + 1);
        }
        List<Map.Entry<String, Integer>> tri = new ArrayList<>(compteur.entrySet());
        tri.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> top5 = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(5, tri.size()); i++) {
            top5.put(tri.get(i).getKey(), tri.get(i).getValue());
        }
        return top5;
    }

    /**
     * Calcule l'évolution du nombre de réservations mois par mois.
     * @param list La liste complète des réservations
     * @return Une map triée chronologiquement (Année-Mois -> Nombre)
     */
    public static Map<String, Integer> calculerEvolutionDansLeTemps(List<Reservation> list) {
        Map<String, Integer> compteur = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (Reservation r : list) {
            if (r.getDateHeureDebut() != null) {
                String mois = r.getDateHeureDebut().format(formatter);
                compteur.put(mois, compteur.getOrDefault(mois, 0) + 1);
            }
        }
        return compteur;
    }

    /**
     * Calcule la répartition des réservations selon la fonction de l'utilisateur (Etudiant, etc.).
     * @param list La liste complète des réservations
     * @return Une map associant chaque fonction à son nombre d'occurrences
     */
    public static Map<String, Integer> calculerRepartitionParFonction(List<Reservation> list) {
        Map<String, Integer> compteur = new HashMap<>();
        for (Reservation r : list) {
            String fonction = r.getUtilisateur().getFonction();
            if (fonction == null || fonction.isBlank()) {
                fonction = "Non renseigné";
            }
            compteur.put(fonction, compteur.getOrDefault(fonction, 0) + 1);
        }
        return compteur;
    }
}