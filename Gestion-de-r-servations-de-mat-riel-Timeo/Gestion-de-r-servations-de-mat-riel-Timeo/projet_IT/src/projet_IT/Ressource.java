package projet_IT;

import java.util.ArrayList;
import java.util.List;

public class Ressource {
    private String nom;
    private String domaine;
    private String description;
    private List<Reservation> reservations = new ArrayList<>();

    public Ressource(String nom, String domaine, String description) {
        this.nom = nom;
        this.domaine = domaine;
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void ajouterReservation(Reservation r) {
        reservations.add(r);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

   
}
