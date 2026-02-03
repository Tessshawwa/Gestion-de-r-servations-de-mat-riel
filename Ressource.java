public class Ressource {
    private String id;          // Ex: "PC-001"
    private String domaine;     // Ex: "Informatique"
    private String description; // Ex: "PC Dell avec souris"

    public Ressource(String id, String domaine, String description) {
        this.id = id;
        this.domaine = domaine;
        this.description = description;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getDomaine() { return domaine; }
    public String getDescription() { return description; }

    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setDomaine(String domaine) { this.domaine = domaine; }
    public void setDescription(String description) { this.description = description; }
}