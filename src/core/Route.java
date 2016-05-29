package core;

import base.* ;

public class Route {
	
	private Noeud noeudDest ;

	private Descripteur descript ;
	
	private int longueur ; 
	
	private double temps ;
	
	public Route(Noeud noeudDest, Descripteur descript, int longueur) {
		this.noeudDest = noeudDest ;
		this.descript = descript ;
		this.longueur = longueur ;
	}
	
	private Noeud getNoeudDest() {
		return this.noeudDest ;
	}

	private Descripteur getDescript() {
		return this.descript ;
	}
	
	public Noeud getDest() {
		return this.noeudDest ; 
	}
	
	public Descripteur getDescr() {
		return this.descript ; 
	}
	
	public int getDist() {
		return this.longueur ;
	}
	
	public double getTemps(int vitMax) {
		if (vitMax > this.descript.vitesseMax() || vitMax == 0) {
			temps = (double)longueur / ((double)descript.vitesseMax()*1000.0) * 60.0 ;
		}
		else {
			temps = (double)longueur / ((double)vitMax*1000.0) * 60.0 ;
		}
		return this.temps ;
	}


}
