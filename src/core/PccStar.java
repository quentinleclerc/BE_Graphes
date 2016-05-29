package core ;

import java.awt.Color;
import java.io.* ;
import base.Readarg ;


public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, boolean aff, int mode) {
        super(gr, sortie, readarg, aff, mode ) ;
    }

    /**
     * Initialisation de l'algorithme de Dijkstra
     * On crée et insère dans le tas et dans la hashmap le label origine
     * @return Label le label du noeud d'origine
     */
    public LabelStar initialisation() {
        // On insère dans le tas le noeud d'origine
        Noeud or = graphe.getNoeuds()[origine] ;
        Noeud dest = graphe.getNoeuds()[destination] ;

        double dist = graphe.distance(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),graphe.getNoeuds()[origine].getLongi(),graphe.getNoeuds()[origine].getLat()) ;

        LabelStar current ;

        if (coutDistance) {
            current = new LabelStar(false,0,null,or,dest,dist) ;
        }
        else {
            current = new LabelStar(false,0,null,or,dest,(dist*1000*130)/60) ;
        }
        map.put(or, current) ;
        tas.insert(current) ;

        return current ;
    }


    public void algo() {
        //Pour mesurer le temps d'excéution
        duree = System.currentTimeMillis();

        // 1) Initialisation
        Label current = initialisation() ;
        nb_noeuds_parcourus = 1 ; // Compte le nombre de noeuds parcourus, on met le noeud d'origine (iniialisation)

        // 2) Boucle principale : tant que le tas n'est pas vide et qu'on est pas au noeud de destination
        while (!tas.isEmpty() && (current.getNoeudCourant() != graphe.getNoeuds()[destination])) {

            nb_max_tas = tas.size()>nb_max_tas?tas.size():nb_max_tas ;

            // On récupère le min du tas de succésseurs et on le marque
            current = tas.deleteMin() ;
            current.setMarquage(true) ;

            // Affichage d'un point sur le noeud parcouru
            //graphe.getDessin().setColor(Color.BLUE);
            //graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),2);

            // Parcourir les noeuds successeurs de current
            for (Route r : current.getNoeudCourant().getRoutes() ){
                double estim = graphe.distance(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),r.getDest().getLongi(),r.getDest().getLat()) ;

                // On modifie la vitesse selon le mode choisi
                r.getDescr().setVitMax(vitesseMax) ;

                /* Si le label du noeud de destination n'est pas dans le hashmap,
                *  alors  :
                *  - on crée le label
                *  - on l'insère dans le tas, dans le hashmap
                */
                if (!map.containsKey(r.getDest())) {
                    Label lab_dest ;
                    if (coutDistance) {
                        lab_dest = new LabelStar(false, current.getCout() + r.getDist(), current.getNoeudCourant(), r.getDest(), graphe.getNoeuds()[destination],estim) ;
                    }
                    else {
                        lab_dest = new LabelStar(false, current.getCout() + r.getTemps(vitesseMax), current.getNoeudCourant(), r.getDest(), graphe.getNoeuds()[destination],(estim*60)/(130*1000)) ;
                    }
                    tas.insert(lab_dest) ;
                    map.put(r.getDest(), lab_dest) ;
                    nb_noeuds_parcourus++;
                }
              /* Sinon, si le label est dans le hashmap et que le cout est inférieur alors
               *  - on met à jour le père et le cout
               *  - on update le tas car on a changé la valeur des cout
               */
                else {
                    Label lab_dest = map.get(r.getDest()) ;
                    double nouveau_cout ;

                    if (coutDistance) {
                        nouveau_cout = current.getCout() + r.getDist()  ;
                    }
                    else {
                        nouveau_cout = current.getCout() + r.getTemps(vitesseMax) ;
                    }

                    if (lab_dest.getCout()  > nouveau_cout ) {
                        lab_dest.setPere(current.getNoeudCourant()) ;
                        lab_dest.setCout(nouveau_cout) ;
                        tas.update(lab_dest) ;
                    }
                }
            }
        }
    }

}

