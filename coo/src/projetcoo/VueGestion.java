package projetcoo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration; // Pour calculer la durée
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class VueGestion extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    
    private List<Reservation> reservationsList;
    private VueStatistiques vueStats; 
    
    // Champs du formulaire (Ajout de txtIdUser)
    private JTextField txtIdUser, txtNom, txtPrenom, txtDescription, txtResId;
    private JComboBox<String> cbFonction, cbDomaine, cbType, cbHeure, cbMinute;
    private JSpinner dateSpinner; 
    
    private DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DateTimeFormatter dtfUpdate = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    public VueGestion(List<Reservation> reservationsList, VueStatistiques vueStats) {
        this.reservationsList = reservationsList;
        this.vueStats = vueStats;
        
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLoad = new JButton("Charger CSV");
        JButton btnExport = new JButton("Exporter CSV (Desktop)");
        topPanel.add(btnLoad); topPanel.add(btnExport);
        add(topPanel, BorderLayout.NORTH);

        // Ajout de la colonne "Durée écoulée" et séparation ID/Nom/Prénom
        String[] columnNames = {"ID Résa", "ID User", "Nom", "Prénom", "Fonction", "Domaine", "Ressource", "Description", "Date Début", "Durée écoulée", "Type", "Dernière MàJ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- FORMULAIRE ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails Réservation"));
        formPanel.setPreferredSize(new Dimension(350, 0));

        // CHAMP d'ID Utilisateur
        formPanel.add(createLabel("ID Utilisateur :"));
        txtIdUser = new JTextField(); formPanel.add(txtIdUser);

        formPanel.add(createLabel("Nom (optionnel) :"));
        txtNom = new JTextField(); formPanel.add(txtNom);
        
        formPanel.add(createLabel("Prénom (optionnel) :"));
        txtPrenom = new JTextField(); formPanel.add(txtPrenom);
        
        formPanel.add(createLabel("Fonction :"));
        cbFonction = new JComboBox<>(new String[]{"Etudiant", "Professionnel", "Enseignant"});
        formPanel.add(cbFonction);
        formPanel.add(new JSeparator());

        formPanel.add(createLabel("Domaine :"));
        cbDomaine = new JComboBox<>(new String[]{"Appareil Photo", "Camescope", "Enceinte", "Enregistreur Numerique", "NOUVEAUX PC COURTE DUREE", "NOUVEAUX PC LONGUE DUREE", "PC Courte Duree", "PC Longue Duree", "Retroprojecteur", "Tablettes Ipad - Longue Durée", "Trepied Photo / Vidéo", "Videoprojecteur"});
        formPanel.add(cbDomaine);
        
        formPanel.add(createLabel("Nom/ID Ressource :"));
        txtResId = new JTextField(); formPanel.add(txtResId);
        
        formPanel.add(createLabel("Description :"));
        txtDescription = new JTextField(); formPanel.add(txtDescription);
        formPanel.add(new JSeparator());

        formPanel.add(createLabel("Date (Calendrier) :"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        formPanel.add(dateSpinner);

        formPanel.add(createLabel("Heure de début :"));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        String[] heures = new String[24]; for(int i=0; i<24; i++) heures[i] = String.format("%02d", i);
        cbHeure = new JComboBox<>(heures);
        cbMinute = new JComboBox<>(new String[]{"00", "15", "30", "45"});
        timePanel.add(cbHeure); timePanel.add(new JLabel(" h ")); timePanel.add(cbMinute);
        formPanel.add(timePanel);

        formPanel.add(createLabel("Type d'emprunt :"));
        cbType = new JComboBox<>(new String[]{"Emprunt", "Cours", "Maintenance"});
        formPanel.add(cbType);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnAdd = new JButton("Ajouter"); btnAdd.setBackground(new Color(200, 255, 200));
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer"); btnDelete.setBackground(new Color(255, 200, 200));
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        formPanel.add(btnPanel);
        add(formPanel, BorderLayout.EAST);

        // Fonctionnalités
        btnLoad.addActionListener(e -> { chargerCSV(); vueStats.rafraichir(reservationsList); });
        btnAdd.addActionListener(e -> { ajouterReservation(); vueStats.rafraichir(reservationsList); });
        btnUpdate.addActionListener(e -> { modifierReservation(); vueStats.rafraichir(reservationsList); });
        btnDelete.addActionListener(e -> { supprimerReservation(); vueStats.rafraichir(reservationsList); });
        btnExport.addActionListener(e -> exporterCSV());

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                remplirFormulaireDepuisSelection(table.getSelectedRow());
            }
        });
    }

    // Méthode pour calculer la durée
    private String calculerDureeEcoulee(LocalDateTime dateDebut) {
        if (dateDebut == null) return "0 j 0 h";
        
        // Calcule le temps entre la date de début et aujourd'hui
        Duration duration = Duration.between(dateDebut, LocalDateTime.now());
        long jours = duration.toDays();
        long heures = duration.toHoursPart(); // Reste des heures
        
        if (jours < 0 || heures < 0) {
            return "À venir"; // Si la date est dans le futur
        }
        return jours + " j " + heures + " h";
    }

    // --- LOGIQUE METIER ---
    private void chargerCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Utilisation de Windows-1252 pour éviter les "?" à la place des "°" car il y avait des problèmes d'encodage
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Windows-1252"))) {
                String line; boolean header = true;
                reservationsList.clear(); tableModel.setRowCount(0);
                
                // Formatter Français pour LIRE le fichier (mais pas pour l'afficher)
                DateTimeFormatter formatterLecture = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy HH:mm:ss", Locale.FRENCH);

                while ((line = br.readLine()) != null) {
                    if (header) { header = false; continue; }
                    String[] data = line.split(";"); 
                    if (data.length >= 6) {
                        String idUser = data[0].trim(); // Ex: "mono3"
                        String domaine = data[1].trim();
                        String ressourceId = data[2].trim();
                        String description = data[3].trim();
                        String dateEtDuree = data[4].trim();
                        String type = data[5].trim();
                        
                        LocalDateTime dateExacte = LocalDateTime.now();
                        try {
                            // On coupe le texte pour ne garder que la date (ex: le "mercredi 01 septembre 2021 08:30:00" dans le csv va devenir 01/09/2011)
                            String dateBrute = dateEtDuree.split(" - ")[0].trim(); 
                            dateExacte = LocalDateTime.parse(dateBrute, formatterLecture);
                        } catch (Exception e) {
                            System.out.println("Format de date ignoré : " + dateEtDuree);
                        }

                        // L'ID est "mono3", Nom et Prénom sont vides par défaut pour matcher le csv fournis
                        Utilisateur u = new Utilisateur(idUser, "", "", "Etudiant");
                        Ressource r = new Ressource(ressourceId, domaine, description);
                        Reservation res = new Reservation(UUID.randomUUID().toString(), u, r, dateExacte, type);
                        
                        reservationsList.add(res);
                        ajouterLigneTable(res);
                    }
                }
                JOptionPane.showMessageDialog(this, "Chargement terminé !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur CSV : " + ex.getMessage());
            }
        }
    }

    private void ajouterReservation() {
        Utilisateur u = new Utilisateur(txtIdUser.getText(), txtNom.getText(), txtPrenom.getText(), (String) cbFonction.getSelectedItem());
        Ressource r = new Ressource(txtResId.getText(), (String) cbDomaine.getSelectedItem(), txtDescription.getText());
        
        Calendar cal = Calendar.getInstance(); cal.setTime((Date) dateSpinner.getValue());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String)cbHeure.getSelectedItem()));
        cal.set(Calendar.MINUTE, Integer.parseInt((String)cbMinute.getSelectedItem()));
        LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), java.time.ZoneId.systemDefault());

        Reservation newRes = new Reservation(UUID.randomUUID().toString(), u, r, ldt, (String) cbType.getSelectedItem());
        reservationsList.add(newRes);
        ajouterLigneTable(newRes);
        viderFormulaire();
    }

    private void modifierReservation() {
        int row = table.getSelectedRow(); if (row == -1) return;
        Reservation res = reservationsList.get(row);
        
        res.getUtilisateur().setId(txtIdUser.getText());
        res.getUtilisateur().setNom(txtNom.getText());
        res.getUtilisateur().setPrenom(txtPrenom.getText());
        res.getUtilisateur().setFonction((String)cbFonction.getSelectedItem());
        
        res.getRessource().setId(txtResId.getText());
        res.getRessource().setDomaine((String)cbDomaine.getSelectedItem());
        res.getRessource().setDescription(txtDescription.getText());

        Calendar cal = Calendar.getInstance(); cal.setTime((Date) dateSpinner.getValue());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((String)cbHeure.getSelectedItem()));
        cal.set(Calendar.MINUTE, Integer.parseInt((String)cbMinute.getSelectedItem()));
        res.setDateHeureDebut(LocalDateTime.ofInstant(cal.toInstant(), java.time.ZoneId.systemDefault()));
        res.setType((String)cbType.getSelectedItem());

        // Mise à jour du tableau
        tableModel.setValueAt(res.getUtilisateur().getId(), row, 1);
        tableModel.setValueAt(res.getUtilisateur().getNom(), row, 2);
        tableModel.setValueAt(res.getUtilisateur().getPrenom(), row, 3);
        tableModel.setValueAt(res.getUtilisateur().getFonction(), row, 4);
        tableModel.setValueAt(res.getRessource().getDomaine(), row, 5);
        tableModel.setValueAt(res.getRessource().getId(), row, 6);
        tableModel.setValueAt(res.getRessource().getDescription(), row, 7);
        tableModel.setValueAt(res.getDateHeureDebut().format(dtfDisplay), row, 8);
        tableModel.setValueAt(calculerDureeEcoulee(res.getDateHeureDebut()), row, 9); // Recalcul de la durée
        tableModel.setValueAt(res.getType(), row, 10);
        tableModel.setValueAt(res.getDerniereMiseAJour().format(dtfUpdate), row, 11);
    }

    private void supprimerReservation() {
        int row = table.getSelectedRow();
        if (row != -1) {
            reservationsList.remove(row);
            tableModel.removeRow(row);
            viderFormulaire();
        }
    }

    private void exporterCSV() {
        File desktopFile = new File(System.getProperty("user.home") + "/Desktop/reservations_export.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(desktopFile))) {
            pw.println("ID_Reservation;ID_Utilisateur;Nom;Prenom;Fonction;Domaine;Ressource_ID;Description;Date_Debut;Type;Last_Update");
            for (Reservation r : reservationsList) {
                pw.println(r.getId() + ";" + r.getUtilisateur().getId() + ";" + r.getUtilisateur().getNom() + ";" + r.getUtilisateur().getPrenom() + ";" +
                           r.getUtilisateur().getFonction() + ";" + r.getRessource().getDomaine() + ";" +
                           r.getRessource().getId() + ";" + r.getRessource().getDescription() + ";" +
                           r.getDateHeureDebut().format(dtfDisplay) + ";" + r.getType() + ";" +
                           r.getDerniereMiseAJour().format(dtfUpdate));
            }
            JOptionPane.showMessageDialog(this, "Fichier exporté sur le bureau :\n" + desktopFile.getAbsolutePath());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void ajouterLigneTable(Reservation r) {
        String dureeCalc = calculerDureeEcoulee(r.getDateHeureDebut());
        tableModel.addRow(new Object[]{ 
            r.getId(), r.getUtilisateur().getId(), r.getUtilisateur().getNom(), r.getUtilisateur().getPrenom(), r.getUtilisateur().getFonction(),
            r.getRessource().getDomaine(), r.getRessource().getId(), r.getRessource().getDescription(),
            r.getDateHeureDebut().format(dtfDisplay), dureeCalc, r.getType(), r.getDerniereMiseAJour().format(dtfUpdate)
        });
    }

    private void remplirFormulaireDepuisSelection(int row) {
        Reservation r = reservationsList.get(row);
        txtIdUser.setText(r.getUtilisateur().getId());
        txtNom.setText(r.getUtilisateur().getNom()); 
        txtPrenom.setText(r.getUtilisateur().getPrenom()); 
        cbFonction.setSelectedItem(r.getUtilisateur().getFonction());
        
        txtResId.setText(r.getRessource().getId()); 
        cbDomaine.setSelectedItem(r.getRessource().getDomaine());
        txtDescription.setText(r.getRessource().getDescription());
        
        dateSpinner.setValue(Date.from(r.getDateHeureDebut().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        cbHeure.setSelectedItem(String.format("%02d", r.getDateHeureDebut().getHour()));
        int m = r.getDateHeureDebut().getMinute(); cbMinute.setSelectedItem(m >= 45 ? "45" : m >= 30 ? "30" : m >= 15 ? "15" : "00");
        cbType.setSelectedItem(r.getType());
    }

    private void viderFormulaire() {
        txtIdUser.setText(""); txtNom.setText(""); txtPrenom.setText(""); txtResId.setText(""); txtDescription.setText(""); table.clearSelection();
    }

    private JLabel createLabel(String text) { JLabel l = new JLabel(text); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
}