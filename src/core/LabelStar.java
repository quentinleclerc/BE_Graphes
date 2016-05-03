package core;

public class LabelStar extends Label {

    public LabelStar(boolean marque, double coast, Noeud father, Noeud current, Noeud dest, double est) {
        super(marque, coast, father, current);
        this.estimation = est ;
    }

    public int compareTo(LabelStar label) {
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
