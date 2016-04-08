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
}
