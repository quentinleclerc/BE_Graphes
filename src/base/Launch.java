package base ;

/*
 * Ce programme propose de lancer divers algorithmes sur les graphes
 * a partir d'un menu texte, ou a partir de la ligne de commande (ou des deux).
 *
 * A chaque question posee par le programme (par exemple, le nom de la carte), 
 * la reponse est d'abord cherchee sur la ligne de commande.
 *
 * Pour executer en ligne de commande, ecrire les donnees dans l'ordre. Par exemple
 *   "java base.Launch insa 1 1 /tmp/sortie 0"
 * ce qui signifie : charge la carte "insa", calcule les composantes connexes avec une sortie graphique,
 * ecrit le resultat dans le fichier '/tmp/sortie', puis quitte le programme.
 */

import core.* ;

import javax.swing.*;
import java.io.* ;

public class Launch {

	private final Readarg readarg ;

	public Launch(String[] args) {
		this.readarg = new Readarg(args) ;
	}

	public void afficherMenu () {
		System.out.println () ;
		System.out.println ("MENU") ;
		System.out.println () ;
		System.out.println ("0 - Quitter") ;
		System.out.println ("1 - Composantes Connexes") ;
		System.out.println ("2 - Plus court chemin standard") ;
		System.out.println ("3 - Plus court chemin A-star") ;
		System.out.println ("4 - Cliquer sur la carte pour obtenir un numero de sommet.") ;
		System.out.println ("5 - Charger un fichier de chemin (.path) et le verifier.") ;

		System.out.println () ;
	}

	public static void main(String[] args) {
		Launch launch = new Launch(args) ;
		launch.go () ;
	}

	public void go() {

		try {
			System.out.println ("**") ;
			System.out.println ("** Programme de test des algorithmes de graphe.");
			System.out.println ("**") ;
			System.out.println () ;


			// Affichage d'une fenêtre pour choisir le fichier de la carte
			File maps = new File("./maps") ;
			JOptionPane choixCarte = new JOptionPane() ;
			String nomcarte = (String)choixCarte.showInputDialog(null,"Veuillez choisir votre carte","Choix de la carte", JOptionPane.QUESTION_MESSAGE, null, maps.list(),maps.list()[0]) ;

			DataInputStream mapdata = Openfile.open (nomcarte) ;


			// Choix de l'affichage ou non
			JOptionPane choixAffichage = new JOptionPane() ;
			String[] ouinon = {"Non", "Oui"} ;
			boolean display =  (1 == choixAffichage.showOptionDialog(null,"Voulez-vous afficher la carte ?","Choix de la sortie", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,ouinon,ouinon[1])) ;

			Dessin dessin = (display) ? new DessinVisible(800,600) : new DessinInvisible() ;

			Graphe graphe = new Graphe(nomcarte, mapdata, dessin) ;

			// Boucle principale : le menu est accessible
			// jusqu'a ce que l'on quitte.
			boolean continuer = true ;
			int choix = 0 ;

			while (continuer) {

				JOptionPane choixMenu = new JOptionPane() ;
				String[] menu = {"Quitter",
								"Composantes Connexes",
								"Plus court chemin standard",
								"Plus court chemin A-star",
								"Cliquer sur la carte pour obtenir un numero de sommet",
								"Charger un fichier de chemin (.path) et le verifier" } ;
				String menuChoisi = (String) choixMenu.showInputDialog(null, "Menu", "Menu", JOptionPane.QUESTION_MESSAGE, null, menu, menu[0]);

				// Attribution du choix selon le string
				if (menuChoisi.equals("Quitter")) {	choix = 0 ; }
				else if (menuChoisi.equals("Composantes Connexes")) { choix = 1 ; }
				else if (menuChoisi.equals("Plus court chemin standard")) {	choix = 2 ;	}
				else if (menuChoisi.equals("Plus court chemin A-star")) { choix = 3 ; }
				else if (menuChoisi.equals("Cliquer sur la carte pour obtenir un numero de sommet")) { choix = 4 ; }
				else if (menuChoisi.equals("Charger un fichier de chemin (.path) et le verifier")) { choix = 5 ; }


				// Algorithme a executer
				Algo algo = null ;

				// Le choix correspond au numero du menu.
				switch (choix) {
					case 0 : continuer = false ; break ;

					case 1 : algo = new Connexite(graphe, this.fichierSortie (), this.readarg) ; break ;

					case 2 : algo = new Pcc(graphe, this.fichierSortie (), this.readarg, display) ; break ;

					case 3 : algo = new PccStar(graphe, this.fichierSortie (), this.readarg, display) ; break ;

					case 4 : graphe.situerClick() ; break ;

					case 5 :
						String nom_chemin = this.readarg.lireString ("Nom du fichier .path contenant le chemin ? ") ;
						graphe.verifierChemin(Openfile.open (nom_chemin), nom_chemin) ;
						break ;

					default:
						System.out.println ("Choix de menu incorrect : " + choix) ;
						System.exit(1) ;
				}

				if (algo != null) { algo.run() ; }
			}

			System.out.println ("Programme terminé.") ;
			System.exit(0) ;


		} catch (Throwable t) {
			t.printStackTrace() ;
			System.exit(1) ;
		}
	}

	// Ouvre un fichier de sortie pour ecrire les reponses
	public PrintStream fichierSortie () {
		PrintStream result = System.out ;

		// Ouvre un panneau pour choisir le nom du fichier de sortie
		JOptionPane choixSortie = new JOptionPane() ;
		String nom = choixSortie.showInputDialog(null, "Veuillez choisir le nom du fichier de sortie", "Choix du fichier de sortie", JOptionPane.QUESTION_MESSAGE);

		if ("".equals(nom)) { nom = "/dev/null" ; }

		try { result = new PrintStream(nom) ; }
		catch (Exception e) {
			System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
			System.exit(1) ;
		}

		return result ;
	}

}
