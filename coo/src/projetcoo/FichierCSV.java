package projetcoo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Classe FichiersCSV qui:
 * Gère l'importation et l'exportation des données de réservation.
 */
public class FichierCSV {

    private static final DateTimeFormatter formatterLecture = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy HH:mm:ss", Locale.FRENCH);
    private static final DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter dtfUpdate = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    /**
     * Lit un fichier CSV, filtre les doublons et renvoie une liste propre de réservations.
     * @param fichier Le fichier CSV à lire
     * @return La liste des réservations extraites
     */
    public static List<Reservation> charger(File fichier) throws Exception {
        List<Reservation> liste = new ArrayList<>();
        
        // Utilisation d'un Set pour repérer et bloquer les doublons dans la base
        Set<String> clesExistantes = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fichier), "Windows-1252"))) {
            String line; 
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] data = line.split(";"); 
                
                if (data.length >= 6) {
                    String idUser = data[0].trim(); 
                    String domaine = data[1].trim();
                    String ressourceId = data[2].trim();
                    String description = data[3].trim();
                    String dateEtDuree = data[4].trim();
                    String type = data[5].trim();
                    
                    LocalDateTime dateExacte = LocalDateTime.now();
                    try {
                        String dateBrute = dateEtDuree.split(" - ")[0].trim(); 
                        dateExacte = LocalDateTime.parse(dateBrute, formatterLecture);
                    } catch (Exception e) {
                        System.out.println("Format de date ignoré : " + dateEtDuree);
                    }

                    // Création d'une clé unique pour détecter les doublons
                    // (Ex: si la même personne réserve la même chose à la même heure)
                    String cleDoublon = idUser + "_" + ressourceId + "_" + dateExacte.toString();

                    // Si cette réservation n'existe pas encore, on l'ajoute !
                    if (!clesExistantes.contains(cleDoublon)) {
                        Utilisateur u = new Utilisateur(idUser, "", "", "Etudiant");
                        Ressource r = new Ressource(ressourceId, domaine, description);
                        Reservation res = new Reservation(UUID.randomUUID().toString(), u, r, dateExacte, type);
                        
                        liste.add(res);
                        clesExistantes.add(cleDoublon);
                    } else {
                        System.out.println("Doublon détecté et ignoré dans la base : " + cleDoublon);
                    }
                }
            }
        }
        return liste;
    }

    /**
     * Exporte la liste actuelle des réservations vers un fichier CSV.
     * @param liste La liste des réservations à sauvegarder
     * @param fichier Le fichier de destination
     */
    public static void exporter(List<Reservation> liste, File fichier) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fichier))) {
            pw.println("ID_Reservation;ID_Utilisateur;Nom;Prenom;Fonction;Domaine;Ressource_ID;Description;Date_Debut;Type;Last_Update");
            for (Reservation r : liste) {
                pw.println(r.getId() + ";" + r.getUtilisateur().getId() + ";" + r.getUtilisateur().getNom() + ";" + r.getUtilisateur().getPrenom() + ";" +
                           r.getUtilisateur().getFonction() + ";" + r.getRessource().getDomaine() + ";" +
                           r.getRessource().getId() + ";" + r.getRessource().getDescription() + ";" +
                           r.getDateHeureDebut().format(dtfDisplay) + ";" + r.getType() + ";" +
                           r.getDerniereMiseAJour().format(dtfUpdate));
            }
        }
    }
}