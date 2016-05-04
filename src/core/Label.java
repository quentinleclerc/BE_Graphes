package core ;

public class Label implements Comparable<Label> {
	
	// Estimation utile pour le LabelStar, nulle pour un simple label
	// Permet de traiter le méthode binaire CompareTo comme il le faut
    protected double estimation; //Donnée en mètres
		
    // Vrai si le sommet est définitivement fixé par l'algorithme
    private boolean marquage;

    // Valeur courante du chemin le plus court
    private double cout;

    // Sommet précédent correponsant au chemin le plus court
    private Noeud pere;

    // Sommet associé à ce label
    private Noeud courant;


    //Constructeur par défaut
    public Label(Noeud n) {
        marquage = false;
        cout = Double.MAX_VALUE ;
        pere = null;
        courant = n;
        estimation=0;
    }

    //Constructeur avec arguments
    public Label(boolean marque, double coast, Noeud father, Noeud current) {
        marquage = marque;
        cout = coast;
        pere = father;
        courant = current;
        estimation = 0;
    }


    public double getCout() {
        return this.cout;
    }

    public boolean getMarquage() {
        return this.marquage;
    }

    public Noeud getNoeudCourant() {
        return this.courant;
    }

    public Noeud getPere() {
        return this.pere;
    }

    public double getEstimation(){
        return this.estimation ;
    }

    public void setMarquage(boolean b) {
        this.marquage = b;
    }

    public void setPere(Noeud pere) {
        this.pere = pere;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public void setEstimation(double est){
        this.estimation = est ;
    }


    public int compareTo(Label label) {
        double coutLab = this.getCout() + this.getEstimation() ;
        double coutAutre = label.getCout() + label.getEstimation() ;
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
