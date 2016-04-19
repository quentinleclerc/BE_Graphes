package core;

import java.util.ArrayList;

public class Chemin { //On suppose un chemin entièrement inclus dans une même zone

	private int id_carte ;
	
	private int nb_noeuds ; 
	
	private int premier_noeud ;
	
	private int dernier_noeud ; 
	
	private ArrayList<Noeud> noeuds ;

	public Chemin (int id_carte, int premier_noeud, int dernier_noeud) {
		this.id_carte = id_carte ;
		this.nb_noeuds = 2 ;
		this.premier_noeud = premier_noeud ;
		this.dernier_noeud = dernier_noeud ;
		this.noeuds = new ArrayList<Noeud>() ;
	}
	
	public void addNoeud (Noeud node, int index) {
		 noeuds.add(index, node) ;
	}

	
	// Retourne le cout métrique (en metres)
	public double calculCoutDistance(){
		int dist_totale = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			dist_totale+=noeuds.get(i).routePlusCourte(noeuds.get(i+1)).getDist() ;
		}
		return dist_totale;
	}
	
	// Retourne le cout horaire (en minutes)
	public double calculCoutTemps(){
		double temps_total = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			temps_total+=noeuds.get(i).routePlusRapide(noeuds.get(i+1)).getTemps() ;
		}
		return temps_total;
	}

	public void printChemin() {
		for (Noeud n : noeuds) {
			System.out.println(n.getId()) ;
		}
	}
}
