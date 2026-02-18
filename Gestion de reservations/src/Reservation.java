import java.time.LocalDateTime;

public class Reservation {
    private String id;
    private Utilisateur utilisateur;
    private Ressource ressource;
    private LocalDateTime dateHeureDebut;
    private String type; // "Emprunt", "Cours", "Maintenance"
    private LocalDateTime derniereMiseAJour;

    public Reservation(String id, Utilisateur utilisateur, Ressource ressource, LocalDateTime dateHeureDebut, String type) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.ressource = ressource;
        this.dateHeureDebut = dateHeureDebut;
        this.type = type;
        this.derniereMiseAJour = LocalDateTime.now(); // Date actuelle à la création
    }

    // --- Getters ---
    public String getId() { return id; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public Ressource getRessource() { return ressource; }
    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public String getType() { return type; }
    public LocalDateTime getDerniereMiseAJour() { return derniereMiseAJour; }

    // --- Setters avec Mise à jour automatique de la date ---
    public void setUtilisateur(Utilisateur u) { 
        this.utilisateur = u; 
        updateTimestamp(); 
    }
    
    public void setRessource(Ressource r) { 
        this.ressource = r; 
        updateTimestamp(); 
    }
    
    public void setDateHeureDebut(LocalDateTime d) { 
        this.dateHeureDebut = d; 
        updateTimestamp(); 
    }
    
    public void setType(String t) { 
        this.type = t; 
        updateTimestamp(); 
    }
    
    // Méthode privée pour mettre à jour l'horodatage
    private void updateTimestamp() {
        this.derniereMiseAJour = LocalDateTime.now();
    }
}