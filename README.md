# Projet COO - Gestion des Réservations Universitaires

## Description du projet
Cette application permet la gestion et la visualisation des réservations de matériel au sein d'un établissement. L'application est divisée en deux parties principales : une interface de gestion des données (CRUD) et un tableau de bord statistique.

## Architecture des Données (Modèle)
Le code repose sur 4 classes principales :
* **Utilisateur** : `id`, `nom`, `prénom`, `fonction` (étudiant, professionnel ou enseignant).
* **Ressource** : `id`, `domaine`, `description`.
* **Reservation** : `id`, `date-heure de début`, `type`, `dernière mise à jour`.
* **FichierCSV** : Classe utilitaire ( sans attributs) dédiée à la lecture et l'écriture de la base de données. Elle intègre un algorithme de détection et de blocage des doublons lors de l'importation.

## Fonctionnalités Principales

### 1. Interface de Gestion (Vue Gestion)
* **Importation** : Chargement d'un fichier.csv via la classe FichierCSV qui nettoie automatiquement les doublons avant l'affichage.
* **Visualisation** : Affichage des données sous forme de tableau interactif (lignes et colonnes).
* **Ajout d'une réservation** : Formulaire permettant de renseigner les informations via :
  * Des champs de texte (ex: `id` : "mono1", description).
  * Des listes déroulantes (type d'emprunt, domaine, ressource, heure).
  * Un calendrier visuel (JSpinner) pour la sélection de la date.
* **Horodatage automatique** : L'application enregistre le temps exact de l'ajout ou de la modification pour mettre à jour l'attribut *Dernière mise à jour* (format `jj/MM/aaaa - HH:mm:ss`).
* **Modification et Suppression** : En sélectionnant une ligne spécifique du tableau, l'utilisateur peut supprimer la réservation ou modifier l'ensemble de ses attributs.
* **Exportation** : Sauvegarde et téléchargement de la base de données mise à jour au format `.csv` directement sur le bureau de l'ordinateur.

### 2. Interface de Statistiques (Vue Statistiques)
Un second onglet génère automatiquement des graphiques interactifs (via JFreeChart) pour analyser l'utilisation du matériel :
* Le Top 5 des ressources les plus empruntées.
* L'utilisateur le plus actif.
* La proportion Emprunt / Cours / Maintenance.
* La répartition par fonction (Étudiant, Professionnel, etc.).
* L'évolution du taux d'emprunt dans le temps (courbe chronologique).
