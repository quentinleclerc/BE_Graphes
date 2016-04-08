package core;


public class Chemin {

	private int id_carte ;
	
	private int nb_noeuds ; 
	
	private int premier_noeud ;
	
	private int dernier_noeud ; 
	
	private Noeud[] noeuds ;

	public Chemin (int id_carte, int nb_noeuds, int premier_noeud, int dernier_noeud) {
		this.id_carte = id_carte ;
		this.nb_noeuds = nb_noeuds ;
		this.premier_noeud = premier_noeud ;
		this.dernier_noeud = dernier_noeud ;
		noeuds = new Noeud[nb_noeuds] ;
	}
	
	public void addNoeud (Noeud node, int index) {
		 noeuds[index]=node ; 
	}

	//Retourne la route la plus courte entre 2 nodes
	private Route routePlusCourte (Noeud node1, Noeud node2) {
		int dist_min = Integer.MAX_VALUE ;
		Route routeCourte = null ;
		for(Route r : node1.getRoutes()) {
			if(r.getDest() == node2 && r.getDist() < dist_min) {
				dist_min = r.getDist()	;
				routeCourte = r ; 
			}
		}
		return routeCourte ; 
	}
	
	//Retourne la route la plus rapide entre 2 nodes
	private Route routePlusRapide (Noeud node1, Noeud node2) {
		double temps_min = Double.MAX_VALUE ;
		Route routeRapide = null ;
		for(Route r : node1.getRoutes()) {
			if(r.getDest() == node2 && r.getTemps() < temps_min) {
				temps_min = r.getDist()	;
				routeRapide = r ; 
			}
		}
		return routeRapide ; 
	}
	
	
	// Retourne le cout métrique (en metres)
	public double calculCoutDistance(){
		int dist_totale = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			dist_totale+=routePlusCourte(noeuds[i],noeuds[i+1]).getDist() ;
		}
		return dist_totale;
	}
	
	// Retourne le cout horaire (en minutes)
	public double calculCoutTemps(){
		double temps_total = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			temps_total+=routePlusRapide(noeuds[i],noeuds[i+1]).getTemps() ;
		}
		return temps_total;
	}
}
