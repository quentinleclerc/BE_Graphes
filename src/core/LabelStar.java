package core;

public class LabelStar extends Label {
	//Le cout d'un labelStar est son le cout d'un noeud à sa destination PLUS l'estimation
	public LabelStar(Noeud n, Noeud dest) {
		super(n);
    double deltaLat= n.getLat() - dest.getLat() ;
    double deltaLong= n.getLongi() - dest.getLongi() ;
    //Estimation calculée en temps de trajet (min) à la vitesse maximale : 130 km/h
    estimation= (60*Math.sqrt(deltaLat*deltaLat + deltaLong*deltaLong)) / (130 * 1000) ;
    this.setCout(this.getCout()+estimation);
	}

  public LabelStar(boolean marque, double coast, Noeud father, Noeud current, Noeud dest) {
    super(marque, coast, father, current);
    double deltaLat = current.getLat() - dest.getLat() ;
    double deltaLong = current.getLongi() - dest.getLongi() ;
    //Estimation calculée en temps de trajet (min) à la vitesse maximale : 130 km/h
    estimation = (60*Math.sqrt(deltaLat*deltaLat + deltaLong*deltaLong)) / (130 * 1000) ;
    this.setCout(this.getCout()+estimation);
  }
  
  public int compareTo(LabelStar label) {
  	double coutLab= this.getCout();
  	double coutAutre = label.getCout();
    if (coutLab - coutAutre > 0 ) {
        return 100 ;
    }
    else if (coutLab == coutAutre) {
        return 0 ;
    }
    else {
        return - 100 ;
    }
}

}
