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
	    private Date DerMAJ;
	    
	    public Reservation() {};

	    public Reservation(String type, Date dateDebut, String duree,
	                       Utilisateur utilisateur, Ressource ressource, Date DerMAJ) {
	        this.type = type;
	        this.dateDebut = dateDebut;
	        this.duree = duree;
	        this.utilisateur = utilisateur;
	        this.ressource = ressource;
	        this.HeureDuree = dateDebut+duree;
	        this.DerMAJ = DerMAJ;
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
	    	  
	    //Chgmt format des dates
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    
	    public String getHeureDuree() {
	        // Format yyyy-MM-dd HH:mm + durée
	     return sdf.format(dateDebut) + " " + duree;
	    }

		public void setHeureDuree(String heureDuree) {
			HeureDuree = heureDuree;
		}



		public String getDerMAJ() {
			 return sdf.format(DerMAJ);
		}



		public void setDerMAJ(Date derMAJ) {
			DerMAJ = derMAJ;
		}
	   
}
	


