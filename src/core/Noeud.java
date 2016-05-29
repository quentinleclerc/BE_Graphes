package core;

import java.util.* ;



public class Noeud {

	/* Coordonnées du noeud */ 
	private float latitude ;
	private float longitude ;
	
	private int id ; 
	
	// Nb de routes sortantes 
	private int nbRoutes = 0 ;
	
	// Liste des routes sortantes
	private ArrayList<Route> routes ;
	
	public Noeud(float lat, float longi, int id) {
		this.latitude = lat ;
		this.longitude = longi ; 
		this.id = id ;
		this.routes = new ArrayList<Route>() ;
	}
	
	public int getNbRoutes() {
		return this.nbRoutes ; 
	}
	
	public int getId() {
		return this.id ;
	}

	public float getLongi() {
		return this.longitude ; 
	}
	
	public float getLat() {
		return this.latitude ; 
	}
	
	public ArrayList<Route> getRoutes() {
		return this.routes ; 
	}
	
	public void setNbRoutes(int nb_routes){
		this.nbRoutes = nb_routes ; 
	}
	
	public void addRoute (Route road) {
		this.routes.add(road) ;
		this.nbRoutes++ ; 
	}

	//Retourne la route la plus courte entre 2 nodes
	public Route routePlusCourte (Noeud node2) {
		int dist_min = Integer.MAX_VALUE ;
		Route routeCourte = null ;
		for(Route r : this.getRoutes()) {
			if(r.getDest() == node2 && r.getDist() < dist_min) {
				dist_min = r.getDist()	;
				routeCourte = r ;
			}
		}
		return routeCourte ;
	}

	//Retourne la route la plus rapide entre 2 nodes succéssifs :
	public Route routePlusRapide ( Noeud node2) {
		double temps_min = Double.MAX_VALUE ;
		Route routeRapide = null ;
		for(Route r : this.getRoutes()) {
			if(r.getDest() == node2 && r.getTemps(0) < temps_min) {
				temps_min = r.getTemps(0) ;
				routeRapide = r ;
			}
		}
		return routeRapide ;
	}

}
