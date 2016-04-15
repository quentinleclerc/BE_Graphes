package core;


public class Chemin { //On suppose un chemin entièrement inclus dans une même zone

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


	
	// Retourne le cout métrique (en metres)
	public double calculCoutDistance(){
		int dist_totale = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			dist_totale+=noeuds[i].routePlusCourte(noeuds[i+1]).getDist() ;
		}
		return dist_totale;
	}
	
	// Retourne le cout horaire (en minutes)
	public double calculCoutTemps(){
		double temps_total = 0;
		for(int i=0; i<nb_noeuds-1; i++){
			temps_total+=noeuds[i].routePlusRapide(noeuds[i+1]).getTemps() ;
		}
		return temps_total;
	}
}
