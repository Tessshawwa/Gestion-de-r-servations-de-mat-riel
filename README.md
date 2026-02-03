# Gestion-de-r-servations-de-mat-riel
Projet COO

Le code va avoir 3 classes prinicipales: 
Utilisateurs avec les attribus: id, nom, prénom, fonction (étudiant ou professionnel) 
Ressources avec les attribus: id, domaine, description
Réservation avec les attribus: id, date-heure de debut, type, dernière mise à jour





L'application doit afficher une fenetre qui a une option de charger un fichier csv (le fichier qui est donné par a  prof) en suite, l'application va contenir l'option de visualiser le ficher (une table des lignes et colonnes), l'application doit avoir l'option de ajouter des lignes (des reservations) donc ici la prof doit renseigner le nom (ex: mono1) le type d'emprunt (liste deroulante), le domaine (liste), la ressources (liste derolante aussi ) et la description (remplise manuellement). en suite, il faut qu'elle ajoute la date donc une petite fenetre doit afficher (visuel de calendrier) et l'heure aussi (liste deroulante). l'application doit enregistrer le temps exact de l'ajout d'une ligne pour avoir la date de Dernière mise à jour (jour/ mois/ année - heure-min-sec)
l'application doit avoir une option de supprimer une ligne et modifier une ligne existante (c'est-à-dire, en accèdant à une ligne specific "une réervation unique avec un id" elle peut modifer touts les attribus des 3 listes (utilisateurs, ressources, réservations). 

à la fin, l'application va expoter le fichier csv et le télécharger dans le desktop. 


Dans un autre panel dans l'application, il y aure une option "Statistiques" ici la prof va pouvoir afficher et calculer: 
• le top 5 des ressources les plus empruntées,
• l’utilisateur le plus actif,
• la proportion Emprunt / Cours / Maintenance par domaine,
• un taux d’emprunt sur une période donnée,
• un taux d’emprunt pour un domaine spécifique,
• une évolution du taux d’emprunt dans le temps
