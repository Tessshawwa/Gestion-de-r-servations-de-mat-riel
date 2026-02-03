package projet_IT;
import java.util.ArrayList;
import java.util.List;
public class Utilisateur {


	    private String nom;
	    private List<Reservation> reservations = new ArrayList<>();

	    public Utilisateur(String nom) {
	        this.nom = nom;
	    }

	    public String getNom() {
	        return nom;
	    }

	    public void setNom(String nom) {
	        this.nom = nom;
	    }

	    public void ajouterReservation(Reservation r) {
	        reservations.add(r);
	    }

	    public List<Reservation> getReservations() {
	        return reservations;
	    }


}
