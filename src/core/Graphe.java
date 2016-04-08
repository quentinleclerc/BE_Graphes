package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.io.* ;
import base.* ;

public class Graphe {

	// Nom de la carte utilisee pour construire ce graphe
	private final String nomCarte ;

	// Fenetre graphique
	private final Dessin dessin ;

	// Version du format MAP utilise'.
	private static final int version_map = 4 ;
	private static final int magic_number_map = 0xbacaff ;

	// Version du format PATH.
	private static final int version_path = 1 ;
	private static final int magic_number_path = 0xdecafe ;

	// Identifiant de la carte
	private int idcarte ;

	// Numero de zone de la carte
	private int numzone ;

	// Tableau de noeuds 
	private Noeud[] noeuds ;

	// Deux + un malheureux getters.
	public Dessin getDessin() { return dessin ; }
	public int getZone() { return numzone ; }
	public Noeud[] getNoeuds() { return noeuds ; }

	// Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
	public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

		this.nomCarte = nomCarte ;
		this.dessin = dessin ;
		Utils.calibrer(nomCarte, dessin) ;

		// Lecture du fichier MAP. 
		// Voir le fichier "FORMAT" pour le detail du format binaire.
		try {

			// Nombre d'aretes
			int edges = 0 ;

			// Verification du magic number et de la version du format du fichier .map
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

			// Lecture de l'identifiant de carte et du numero de zone, 
			this.idcarte = dis.readInt () ;
			this.numzone = dis.readInt () ;

			// Lecture du nombre de descripteurs, nombre de noeuds.
			int nb_descripteurs = dis.readInt () ;
			int nb_nodes = dis.readInt () ;

			// Création du tableau de noeuds et de nb_successeurs
			noeuds = new Noeud[nb_nodes] ;
			int[] nb_succ = new int[nb_nodes] ;
			
			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node (longitude, latitude, id, nbRoutes)
				float longi = (float)dis.readInt()  / 1E6f ;
				float lati = (float)dis.readInt () / 1E6f ;
				
				nb_succ[num_node] = dis.readUnsignedByte() ; 
	
				noeuds[num_node] = new Noeud(lati, longi,num_node) ;
			}

			Utils.checkByte(255, dis) ; // Verification de l'octet à 255

			
			// Lecture des descripteurs
			Descripteur[] descripteurs = new Descripteur[nb_descripteurs] ; 

			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				descripteurs[num_descr] = new Descripteur(dis) ;

				// On affiche quelques descripteurs parmi tous.
				if (0 == num_descr % (1 + nb_descripteurs / 400))
					System.out.println("Descripteur " + num_descr + " = " + descripteurs[num_descr]) ;
			}

			Utils.checkByte(254, dis) ;
			
			// Lecture des successeurs
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture de tous les successeurs du noeud num_node
				for (int num_succ = 0 ; num_succ < nb_succ[num_node] ; num_succ++) {
					// zone du successeur
					int succ_zone = dis.readUnsignedByte() ;

					// numero de noeud du successeur
					int dest_node = Utils.read24bits(dis) ;

					// descripteur de l'arete
					int descr_num = Utils.read24bits(dis) ;

					// longueur de l'arete en metres
					int longueur  = dis.readUnsignedShort() ;

					// Nombre de segments constituant l'arete
					int nb_segm   = dis.readUnsignedShort() ;
					
					edges++ ;
					
					 
					Couleur.set(dessin, descripteurs[descr_num].getType()) ;

					float current_long = noeuds[num_node].getLongi() ;
					float current_lat  = noeuds[num_node].getLat() ;
					
					// Chaque segment est dessiné
					for (int i = 0 ; i < nb_segm ; i++) {
						float delta_lon = (dis.readShort()) / 2.0E5f ;
						float delta_lat = (dis.readShort()) / 2.0E5f ;
						dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
						
						current_long += delta_lon ;
						current_lat  += delta_lat ;
					}

					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) {
						
						Route road = new Route(noeuds[dest_node], descripteurs[descr_num], longueur ) ;
						noeuds[num_node].addRoute(road) ;

						// ajout de l'arc réciproque si route à double sens 
						if (!descripteurs[descr_num].isSensUnique()) { 
							noeuds[dest_node].addRoute(new Route(noeuds[num_node], descripteurs[descr_num], longueur));
						}
						
						dessin.drawLine(current_long, current_lat, noeuds[dest_node].getLongi(), noeuds[dest_node].getLat()) ;
					}
				}
			}

			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
					+ nb_descripteurs + " descripteurs.") ;

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

	// Rayon de la terre en metres
	private static final double rayon_terre = 6378137.0 ;

	/**
	 *  Calcule de la distance orthodromique - plus court chemin entre deux points à la surface d'une sphère
	 *  @param long1 longitude du premier point.
	 *  @param lat1 latitude du premier point.
	 *  @param long2 longitude du second point.
	 *  @param lat2 latitude du second point.
	 *  @return la distance entre les deux points en metres.
	 *  Methode Ã©crite par Thomas Thiebaud, mai 2013
	 */
	public static double distance(double long1, double lat1, double long2, double lat2) {
		double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
		double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
		double cosLong = Math.cos(Math.toRadians(long2-long1));
		return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
	}

	/**
	 *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
	 *  A n'utiliser que pour faire du debug ou des tests ponctuels.
	 *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
	 */
	public void situerClick() {

		System.out.println("Allez-y, cliquez donc.") ;

		if (dessin.waitClick()) {
			float lon = dessin.getClickLon() ;
			float lat = dessin.getClickLat() ;

			System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

			// On cherche le noeud le plus proche. O(n)
			float minDist = Float.MAX_VALUE ;
			int   noeud   = 0 ;

			for (int num_node = 0 ; num_node < noeuds.length ; num_node++) {
				float londiff = (noeuds[num_node].getLongi() - lon) ;
				float latdiff = (noeuds[num_node].getLat() - lat) ;
				float dist = londiff*londiff + latdiff*latdiff ;
				if (dist < minDist) {
					noeud = num_node ;
					minDist = dist ;
				}
			}

			System.out.println("Noeud le plus proche : " + noeud) ;
			System.out.println() ;
			dessin.setColor(java.awt.Color.red) ;
			dessin.drawPoint(noeuds[noeud].getLongi(),noeuds[noeud].getLat(), 5) ;
		}
	}

	/**
	 *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
	 *  Verifie que le chemin est empruntable et calcule le temps de trajet.
	 */
	public void verifierChemin(DataInputStream dis, String nom_chemin) {

		try {

			// Verification du magic number et de la version du format du fichier .path
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

			// Lecture de l'identifiant de carte
			int path_carte = dis.readInt () ;

			if (path_carte != this.idcarte) {
				System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
				System.exit(1) ;
			}

			int nb_noeuds = dis.readInt () ;

			// Origine du chemin
			int first_zone = dis.readUnsignedByte() ;
			int first_node = Utils.read24bits(dis) ;

			// Destination du chemin
			int last_zone  = dis.readUnsignedByte() ;
			int last_node = Utils.read24bits(dis) ;
			
			Chemin monChem = new Chemin(path_carte, nb_noeuds, first_node, last_node);
 
			System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

			int current_zone = 0 ;
			int current_node = 0 ;

			// Tous les noeuds du chemin
			for (int i = 0 ; i < nb_noeuds ; i++) {
				current_zone = dis.readUnsignedByte() ;
				current_node = Utils.read24bits(dis) ;
				System.out.println(" --> " + current_zone + ":" + current_node) ;
				monChem.addNoeud(noeuds[current_node], i);
			}

			if ((current_zone != last_zone) || (current_node != last_node)) {
				System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
				System.exit(1) ;
			}
			
			System.out.println("Cout en temps (min) = "+monChem.calculCoutTemps());
			System.out.println("Cout en distance (mètres) = "+monChem.calculCoutDistance());			

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

}
