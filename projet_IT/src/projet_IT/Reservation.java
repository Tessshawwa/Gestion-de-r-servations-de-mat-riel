package projet_IT;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Reservation {
	    private String type; // Emprunt, Cours, Maintenance
	    private Date dateDebut;
	    private String duree; // durée en minutes, par exemple
	    private Utilisateur utilisateur;
	    private Ressource ressource;
	    private String HeureDuree;
	    
	    public Reservation(String type, Date dateDebut, String duree,
	                       Utilisateur utilisateur, Ressource ressource) {
	        this.type = type;
	        this.dateDebut = dateDebut;
	        this.duree = duree;
	        this.utilisateur = utilisateur;
	        this.ressource = ressource;
	        this.HeureDuree = dateDebut+duree;
	    }



		public String getType() {
	        return type;
	    }

	    public Date getDateDebut() {
	        return dateDebut;
	    }

	    public String getDuree() {
	        return duree;
	    }

	    public Utilisateur getUtilisateur() {
	        return utilisateur;
	    }

	    public Ressource getRessource() {
	        return ressource;
	    }
	    	   
	    public String getHeureDuree() {
	        // Format yyyy-MM-dd HH:mm + durée
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        return sdf.format(dateDebut) + " " + duree;
	    }

		public void setHeureDuree(String heureDuree) {
			HeureDuree = heureDuree;
		}
	   
}
	


