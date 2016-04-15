package core ;

import java.util.HashMap;

/**
 * (marquage, cout, pere, sommet courant)
 */
public class Label implements Comparable<Label> {

    //vrai si le sommet est définitivement ficé par l'algorithme
    private boolean marquage;

    //Valeur courante du chemin le plus court
    private double cout;

    //Sommet précédent correponsant au chemin le plus court
    private Noeud pere;

    //Sommet associé à ce label
    private Noeud courant;


    //Constructeur par défaut
    public Label(Noeud n) {
        marquage = false;
        cout = Integer.MAX_VALUE;
        pere = null;
        courant = n;
    }

    //Constructeur avec arguments
    public Label(boolean marque, int coast, Noeud father, Noeud current) {
        marquage = marque;
        cout = coast;
        pere = father;
        courant = current;
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

    public void setMarquage(boolean b) {
        this.marquage = b;
    }

    public void setPere(Noeud pere) {
        this.pere = pere;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public int compareTo(Label label) {
        return (int) this.cout - (int) label.getCout();
    }

}
