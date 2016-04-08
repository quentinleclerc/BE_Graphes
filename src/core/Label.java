package core ;

import java.util.HashMap;

/**
 * (marquage, cout, pere, sommet courant)
 */
public class Label implements Comparable<Label> {

    //vrai si le sommet est définitivement ficé par l'algorithme
    private boolean marquage ;

    //Valeur courante du chemin le plus court
    private int cout ;

    //Sommet précédent correponsant au chemin le plus court
    private Noeud pere ;

    //Sommet associé à ce label
    private Noeud courant;


    //Constructeur par défaut
    public Label(){
        marquage = false ;
        cout = 0 ;
        pere = null ;
        courant = null ;
    }

    //Constructeur avec arguments
    public Label(boolean marque, int coast, Noeud father, Noeud current){
        marquage = marque ;
        cout = coast ;
        pere = father ;
        courant = current ;
    }

    public int getCout() {
        return this.cout ;
    }

    public int compareTo(Label label) {
        return  this.cout - label.getCout() ;
    }


}
