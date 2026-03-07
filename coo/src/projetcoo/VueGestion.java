package projetcoo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Classe VueGestion
 * -----------------
 * Cette classe représente l'interface principale permettant
 * de gérer les réservations d'outils.
 *
 * Elle permet à l'utilisateur de :
 * - charger un fichier CSV contenant des réservations
 * - ajouter une nouvelle réservation
 * - modifier une réservation existante
 * - supprimer une réservation
 * - exporter les réservations vers un fichier CSV
 *
 * Les données sont affichées dans un tableau (JTable) et
 * peuvent être modifiées à partir d'un formulaire situé
 * à droite de l'interface.
 *
 * Cette classe communique également avec VueStatistiques
 * afin de mettre à jour les graphiques après chaque modification.
 */
public class VueGestion extends JPanel {

    // Tableau graphique qui affiche les réservations
    private JTable table;

    // Modèle de données utilisé par le tableau
    private DefaultTableModel tableModel;
    
    // Liste contenant toutes les réservations en mémoire
    private List<Reservation> reservationsList;

    // Référence vers la vue des statistiques
    private VueStatistiques vueStats; 
    
    // Champs du formulaire
    private JTextField txtIdUser, txtNom, txtPrenom, txtDescription, txtResId;

    // Listes déroulantes pour certains choix
    private JComboBox<String> cbFonction, cbDomaine, cbType, cbHeure, cbMinute;

    // Sélecteur de date
    private JSpinner dateSpinner; 
    
    // Format d'affichage des dates
    private DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Format utilisé pour la dernière mise à jour
    private DateTimeFormatter dtfUpdate = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    /**
     * Constructeur de la vue de gestion
     * Initialise toute l'interface graphique
     */
    public VueGestion(List<Reservation> reservationsList, VueStatistiques vueStats) {

        this.reservationsList = reservationsList;
        this.vueStats = vueStats;
        
        // Organisation principale de l'interface
        setLayout(new BorderLayout());

        // -------------------------------------------------
        // PANEL DU HAUT : boutons de chargement et export
        // -------------------------------------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnLoad = new JButton("Charger CSV");
        JButton btnExport = new JButton("Exporter CSV (Desktop)");

        topPanel.add(btnLoad);
        topPanel.add(btnExport);

        add(topPanel, BorderLayout.NORTH);

        // -------------------------------------------------
        // TABLEAU CENTRAL : affichage des réservations
        // -------------------------------------------------

        String[] columnNames = {
                "ID Résa", "ID Utilisateur", "Nom", "Prénom", "Fonction",
                "Domaine", "Ressource", "Description",
                "Date Début", "Durée écoulée", "Type", "Dernière MàJ"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // -------------------------------------------------
        // PANEL DROIT : formulaire de création/modification
        // -------------------------------------------------

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails Réservation"));
        formPanel.setPreferredSize(new Dimension(260, 0));

        // Champ ID utilisateur
        formPanel.add(createLabel("ID Utilisateur :"));
        txtIdUser = new JTextField();
        fixerTailleMax(txtIdUser);
        formPanel.add(txtIdUser);

        // Nom
        formPanel.add(createLabel("Nom (optionnel) :"));
        txtNom = new JTextField();
        fixerTailleMax(txtNom);
        formPanel.add(txtNom);
        
        // Prénom
        formPanel.add(createLabel("Prénom (optionnel) :"));
        txtPrenom = new JTextField();
        fixerTailleMax(txtPrenom);
        formPanel.add(txtPrenom);
        
        // Fonction de l'utilisateur
        formPanel.add(createLabel("Fonction :"));
        cbFonction = new JComboBox<>(new String[]{
                "Etudiant", "Professionnel", "Enseignant"
        });

        fixerTailleMax(cbFonction);
        formPanel.add(cbFonction);

        formPanel.add(new JSeparator());

        // Domaine de la ressource
        formPanel.add(createLabel("Domaine :"));
        cbDomaine = new JComboBox<>(new String[]{
                "Appareil Photo", "Camescope", "Enceinte", "Enregistreur Numerique",
                "NOUVEAUX PC COURTE DUREE", "NOUVEAUX PC LONGUE DUREE",
                "PC Courte Duree", "PC Longue Duree", "Retroprojecteur",
                "Tablettes Ipad - Longue Durée", "Trepied Photo / Vidéo", "Videoprojecteur"
        });

        fixerTailleMax(cbDomaine);
        formPanel.add(cbDomaine);
        
        // Identifiant de la ressource
        formPanel.add(createLabel("Nom/ID Ressource :"));
        txtResId = new JTextField();
        fixerTailleMax(txtResId);
        formPanel.add(txtResId);
        
        // Description
        formPanel.add(createLabel("Description :"));
        txtDescription = new JTextField();
        fixerTailleMax(txtDescription);
        formPanel.add(txtDescription);

        formPanel.add(new JSeparator());

        // -------------------------------------------------
        // Sélection de la date
        // -------------------------------------------------
        formPanel.add(createLabel("Date (Calendrier) :"));

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        fixerTailleMax(dateSpinner);
        formPanel.add(dateSpinner);

        // -------------------------------------------------
        // Sélection de l'heure
        // -------------------------------------------------
        formPanel.add(createLabel("Heure de début :"));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fixerTailleMax(timePanel);

        String[] heures = new String[24];
        for(int i=0; i<24; i++)
            heures[i] = String.format("%02d", i);

        cbHeure = new JComboBox<>(heures);

        cbMinute = new JComboBox<>(new String[]{"00", "15", "30", "45"});

        timePanel.add(cbHeure);
        timePanel.add(new JLabel(" h "));
        timePanel.add(cbMinute);

        formPanel.add(timePanel);

        // -------------------------------------------------
        // Type de réservation
        // -------------------------------------------------
        formPanel.add(createLabel("Type d'emprunt :"));

        cbType = new JComboBox<>(new String[]{
                "Emprunt", "Cours", "Maintenance"
        });

        fixerTailleMax(cbType);
        formPanel.add(cbType);

        formPanel.add(Box.createVerticalStrut(20));

        // -------------------------------------------------
        // Boutons d'action
        // -------------------------------------------------

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));

        fixerTailleMax(btnPanel);

        JButton btnAdd = new JButton("Ajouter");
        btnAdd.setBackground(new Color(200, 255, 200));

        JButton btnUpdate = new JButton("Modifier");

        JButton btnDelete = new JButton("Supprimer");
        btnDelete.setBackground(new Color(255, 200, 200));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        formPanel.add(btnPanel);

        add(formPanel, BorderLayout.EAST);

        // -------------------------------------------------
        // ACTIONS DES BOUTONS
        // -------------------------------------------------

        btnLoad.addActionListener(e -> {
            chargerCSV();
            vueStats.rafraichir(reservationsList);
        });

        btnAdd.addActionListener(e -> {
            ajouterReservation();
            vueStats.rafraichir(reservationsList);
        });

        btnUpdate.addActionListener(e -> {
            modifierReservation();
            vueStats.rafraichir(reservationsList);
        });

        btnDelete.addActionListener(e -> {
            supprimerReservation();
            vueStats.rafraichir(reservationsList);
        });

        btnExport.addActionListener(e -> exporterCSV());

        // Lorsque l'utilisateur clique sur une ligne du tableau,
        // les informations sont chargées dans le formulaire
        table.getSelectionModel().addListSelectionListener(event -> {

            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {

                remplirFormulaireDepuisSelection(table.getSelectedRow());
            }
        });
    }

    /**
     * Fixe la taille maximale d’un composant afin d’éviter
     * qu’il s’étire trop dans le layout vertical.
     */
    private void fixerTailleMax(JComponent comp) {

        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); 
    }

    /**
     * Charge un fichier CSV contenant des réservations
     * et les affiche dans le tableau.
     */
    private void chargerCSV() {

        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            try {

                reservationsList.clear();
                tableModel.setRowCount(0);

                // Appel à la classe utilitaire de gestion CSV
                List<Reservation> nouvellesResas =
                        FichierCSV.charger(fileChooser.getSelectedFile());

                for(Reservation res : nouvellesResas) {

                    reservationsList.add(res);
                    ajouterLigneTable(res);
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Chargement terminé ! (" + nouvellesResas.size() + " lignes importées sans doublons)"
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Erreur CSV : " + ex.getMessage()
                );
            }
        }
    }