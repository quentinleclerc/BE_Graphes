package core;

public class LabelStar extends Label {

    public LabelStar(boolean marque, double coast, Noeud father, Noeud current, Noeud dest, double est) {
        super(marque, coast, father, current);
        this.estimation = est ;
    }

}
