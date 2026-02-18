import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    // Composants UI
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Reservation> reservationsList = new ArrayList<>();
    
    // Champs du formulaire
    private JTextField txtNom, txtPrenom, txtDescription, txtResId;
    private JComboBox<String> cbFonction, cbDomaine, cbType, cbHeure, cbMinute;
    private JSpinner dateSpinner; // Pour le calendrier visuel
    
    // Formatters pour l'affichage des dates
    private DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DateTimeFormatter dtfUpdate = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    public Main() {
        setTitle("Gestion des Réservations Universitaires");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HAUT : BARRE D'OUTILS ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLoad = new JButton("Charger CSV");
        JButton btnExport = new JButton("Exporter CSV (Desktop)");
        topPanel.add(btnLoad);
        topPanel.add(btnExport);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTRE : TABLEAU ---
        // Colonnes du tableau
        String[] columnNames = {"ID", "Utilisateur", "Fonction", "Domaine", "Ressource", "Description", "Date Début", "Type", "Dernière MàJ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- DROITE : FORMULAIRE D'AJOUT/EDITION ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails Réservation"));
        formPanel.setPreferredSize(new Dimension(350, 0));

        // Ajout des champs
        formPanel.add(createLabel("Nom Utilisateur :"));
        txtNom = new JTextField();
        formPanel.add(txtNom);
        
        formPanel.add(createLabel("Prénom :"));
        txtPrenom = new JTextField();
        formPanel.add(txtPrenom);

        formPanel.add(createLabel("Fonction :"));
        cbFonction = new JComboBox<>(new String[]{"Etudiant", "Professionnel", "Enseignant"});
        formPanel.add(cbFonction);

        formPanel.add(new JSeparator());

        formPanel.add(createLabel("Domaine :"));
        cbDomaine = new JComboBox<>(new String[]{"Appareil Photo", "Camescope", "Enceinte", "Enregistreur Numerique", "Micro Cravate NON PRO", "Micro filaire", "NOUVEAUX PC COURTE DUREE", "NOUVEAUX PC LONGUE DUREE", "PC Courte Duree", "PC Longue Duree", "Retroprojecteur","Tablettes Ipad - Longue Durée", "Trepied Photo / Vidéo", "Videoprojecteur"
});
        formPanel.add(cbDomaine);

        formPanel.add(createLabel("Nom/ID Ressource :"));
        txtResId = new JTextField(); 
        formPanel.add(txtResId);

        formPanel.add(createLabel("Description :"));
        txtDescription = new JTextField();
        formPanel.add(txtDescription);

        formPanel.add(new JSeparator());

        formPanel.add(createLabel("Date (Calendrier) :"));
        // Utilisation de JSpinner pour simuler un Date Picker natif simple
        SpinnerDateModel model = new SpinnerDateModel();
        dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(editor);
        formPanel.add(dateSpinner);

        formPanel.add(createLabel("Heure de début :"));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        String[] heures = new String[24];
        for(int i=0; i<24; i++) heures[i] = String.format("%02d", i);
        cbHeure = new JComboBox<>(heures);
        
        String[] minutes = {"00", "15", "30", "45"};
        cbMinute = new JComboBox<>(minutes);
        
        timePanel.add(cbHeure);
        timePanel.add(new JLabel(" h "));
        timePanel.add(cbMinute);
        formPanel.add(timePanel);

        formPanel.add(createLabel("Type d'emprunt :"));
        cbType = new JComboBox<>(new String[]{"Emprunt", "Cours", "Maintenance"});
        formPanel.add(cbType);

        formPanel.add(Box.createVerticalStrut(20));

        // Boutons d'action
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        
        // Couleurs pour distinguer les boutons
        btnAdd.setBackground(new Color(200, 255, 200));
        btnDelete.setBackground(new Color(255, 200, 200));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        formPanel.add(btnPanel);

        add(formPanel, BorderLayout.EAST);

        // --- GESTION DES EVENEMENTS ---

        // 1. Charger CSV
        btnLoad.addActionListener(e -> chargerCSV());

        // 2. Ajouter
        btnAdd.addActionListener(e -> ajouterReservation());

        // 3. Selection dans la table -> Remplir le formulaire
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                remplirFormulaireDepuisSelection(table.getSelectedRow());
            }
        });

        // 4. Modifier
        btnUpdate.addActionListener(e -> modifierReservation());

        // 5. Supprimer
        btnDelete.addActionListener(e -> supprimerReservation());

        // 6. Exporter
        btnExport.addActionListener(e -> exporterCSV());
    }

    // --- LOGIQUE METIER ---

    private void chargerCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean header = true;
                reservationsList.clear();
                tableModel.setRowCount(0);

                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; }
                    String[] data = line.split(";"); // Sépérateur ";"
                    if (data.length >= 4) {
                        // Création simplifiée des objets depuis le CSV
                        Utilisateur u = new Utilisateur(UUID.randomUUID().toString(), data[0], "", "Etudiant");
                        Ressource r = new Ressource(data[2], data[1], "Importé");
                        
                        LocalDateTime date = LocalDateTime.now(); // Par défaut maintenant si erreur date
                        Reservation res = new Reservation(UUID.randomUUID().toString(), u, r, date, "Emprunt");
                        
                        reservationsList.add(res);
                        ajouterLigneTable(res);
                    }
                }
                JOptionPane.showMessageDialog(this, "Chargement terminé !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur de lecture CSV : " + ex.getMessage());
            }
        }
    }

    private void ajouterReservation() {
        // 1. Créer Utilisateur
        String nom = txtNom.getText();
        String prenom = txtPrenom.getText();
        String fonction = (String) cbFonction.getSelectedItem();
        Utilisateur u = new Utilisateur(UUID.randomUUID().toString(), nom, prenom, fonction);

        // 2. Créer Ressource
        String idRes = txtResId.getText();
        String domaine = (String) cbDomaine.getSelectedItem();
        String desc = txtDescription.getText();
        Ressource r = new Ressource(idRes, domaine, desc);

        // 3. Calculer Date
        Date dateJour = (Date) dateSpinner.getValue();
        int h = Integer.parseInt((String)cbHeure.getSelectedItem());
        int m = Integer.parseInt((String)cbMinute.getSelectedItem());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateJour);
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), java.time.ZoneId.systemDefault());

        String type = (String) cbType.getSelectedItem();

        // 4. Créer Réservation
        Reservation newRes = new Reservation(UUID.randomUUID().toString(), u, r, ldt, type);
        
        reservationsList.add(newRes);
        ajouterLigneTable(newRes);
        viderFormulaire();
    }

    private void modifierReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        Reservation res = reservationsList.get(selectedRow);

        // Mise à jour des attributs
        res.getUtilisateur().setNom(txtNom.getText());
        res.getUtilisateur().setPrenom(txtPrenom.getText());
        res.getUtilisateur().setFonction((String)cbFonction.getSelectedItem());

        res.getRessource().setId(txtResId.getText());
        res.getRessource().setDomaine((String)cbDomaine.getSelectedItem());
        res.getRessource().setDescription(txtDescription.getText());

        // Recalcul Date
        Date dateJour = (Date) dateSpinner.getValue();
        int h = Integer.parseInt((String)cbHeure.getSelectedItem());
        int m = Integer.parseInt((String)cbMinute.getSelectedItem());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateJour);
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), java.time.ZoneId.systemDefault());
        
        res.setDateHeureDebut(ldt);
        res.setType((String)cbType.getSelectedItem());
        // Note: La date de mise à jour se fait automatiquement dans la classe Reservation grâce aux Setters

        // Rafraîchir la table visuelle
        tableModel.setValueAt(res.getUtilisateur().getNomComplet(), selectedRow, 1);
        tableModel.setValueAt(res.getUtilisateur().getFonction(), selectedRow, 2);
        tableModel.setValueAt(res.getRessource().getDomaine(), selectedRow, 3);
        tableModel.setValueAt(res.getRessource().getId(), selectedRow, 4);
        tableModel.setValueAt(res.getRessource().getDescription(), selectedRow, 5);
        tableModel.setValueAt(res.getDateHeureDebut().format(dtfDisplay), selectedRow, 6);
        tableModel.setValueAt(res.getType(), selectedRow, 7);
        tableModel.setValueAt(res.getDerniereMiseAJour().format(dtfUpdate), selectedRow, 8);
    }

    private void supprimerReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            reservationsList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            viderFormulaire();
        }
    }

    private void exporterCSV() {
        String userHome = System.getProperty("user.home");
        File desktopFile = new File(userHome + "/Desktop/reservations_export.csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(desktopFile))) {
            pw.println("ID_Reservation;Nom;Prenom;Fonction;Domaine;Ressource_ID;Description;Date_Debut;Type;Last_Update");
            
            for (Reservation r : reservationsList) {
                // On utilise la concaténation simple pour éviter les erreurs de type "Object"
                String line = r.getId() + ";" +
                              r.getUtilisateur().getNom() + ";" +
                              r.getUtilisateur().getPrenom() + ";" +
                              r.getUtilisateur().getFonction() + ";" +
                              r.getRessource().getDomaine() + ";" +
                              r.getRessource().getId() + ";" +
                              r.getRessource().getDescription() + ";" +
                              r.getDateHeureDebut().format(dtfDisplay) + ";" +
                              r.getType() + ";" +
                              r.getDerniereMiseAJour().format(dtfUpdate);
                
                pw.println(line);
            
            }
            JOptionPane.showMessageDialog(this, "Fichier exporté sur le bureau :\n" + desktopFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur export : " + e.getMessage());
        }
    }

    // --- Helpers UI ---
    private void ajouterLigneTable(Reservation r) {
        tableModel.addRow(new Object[]{
            r.getId(),
            r.getUtilisateur().getNomComplet(),
            r.getUtilisateur().getFonction(),
            r.getRessource().getDomaine(),
            r.getRessource().getId(),
            r.getRessource().getDescription(),
            r.getDateHeureDebut().format(dtfDisplay),
            r.getType(),
            r.getDerniereMiseAJour().format(dtfUpdate)
        });
    }

    private void remplirFormulaireDepuisSelection(int row) {
        Reservation r = reservationsList.get(row);
        txtNom.setText(r.getUtilisateur().getNom());
        txtPrenom.setText(r.getUtilisateur().getNom());
        cbFonction.setSelectedItem(r.getUtilisateur().getFonction());
        
        txtResId.setText(r.getRessource().getId());
        cbDomaine.setSelectedItem(r.getRessource().getDomaine());
        txtDescription.setText(r.getRessource().getDescription());
        
        Date date = Date.from(r.getDateHeureDebut().atZone(java.time.ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(date);
        
        cbHeure.setSelectedItem(String.format("%02d", r.getDateHeureDebut().getHour()));
        
        // Approximation minute pour affichage
        String minStr = "00";
        int m = r.getDateHeureDebut().getMinute();
        if(m >= 45) minStr="45"; else if(m >= 30) minStr="30"; else if(m >= 15) minStr="15";
        cbMinute.setSelectedItem(minStr);
        
        cbType.setSelectedItem(r.getType());
    }

    private void viderFormulaire() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtResId.setText("");
        txtDescription.setText("");
        table.clearSelection();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}