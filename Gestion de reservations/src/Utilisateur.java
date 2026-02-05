public class Utilisateur {
    private String id;
    private String nom;
    private String prenom;
    private String fonction; 

    public Utilisateur(String id, String nom, String prenom, String fonction) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
    }

    // Getters 
    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getFonction() { return fonction; }
    
    // Helper pour afficher le nom complet
    public String getNomComplet() { return nom + " " + prenom; }

    // Setters
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setFonction(String fonction) { this.fonction = fonction; }
}