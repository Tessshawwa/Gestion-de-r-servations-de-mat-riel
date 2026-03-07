package projet_IT;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSV {

    private static final String sep = ";";

    /**
     * Export toutes les réservations
     */
    public static void exporterToutes(List<Reservation> reservations, String cheminFichier) {
        exporterFiltre(reservations, cheminFichier, null, null);
    }

    /**
     * Export filtré par utilisateur ou ressource
     */
    public static void exporterFiltre(List<Reservation> reservations, String cheminFichier,
                                      String nomUtilisateurFiltre, String nomRessourceFiltre) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cheminFichier))) {
            // Écrire l'en-tête
            bw.write("nomUtilisateur;domaine;nomRessource;HeureDuree|type;Dernière modification");
            bw.newLine();

            // Parcours simple de toutes les réservations
            for (Reservation r : reservations) {
                boolean inclure = true;

                // Filtre utilisateur
                if (nomUtilisateurFiltre != null && !r.getUtilisateur().getNom().equals(nomUtilisateurFiltre)) {
                    inclure = false;
                }

                // Filtre ressource
                if (nomRessourceFiltre != null && !r.getRessource().getNom().equals(nomRessourceFiltre)) {
                    inclure = false;
                }

                // Écrire la ligne si elle correspond
                if (inclure) {
                    String ligne = r.getUtilisateur().getNom() + sep +
                                   r.getRessource().getDomaine() + sep +
                                   r.getRessource().getNom() + sep +
                                   r.getHeureDuree() + sep +
                                   r.getType()+ sep +
                                   r.getDerMAJ();
                    bw.write(ligne);
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur lors de l'export CSV : " + e.getMessage());
        }
    }
}
