package projetcoo;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Appliquer le style natif du système d'exploitation (Windows/Mac)
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Lancer l'interface graphique de manière sécurisée
        SwingUtilities.invokeLater(() -> {
            InterfaceUI fenetre = new InterfaceUI();
            fenetre.setVisible(true);
        });
    }
}
