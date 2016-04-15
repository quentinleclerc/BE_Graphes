package core ;

import java.io.* ;
import java.util.*;

import base.Readarg ;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;

    protected int zoneDestination ;
    protected int destination ;

    // HashMap liant chaque noeud a son label
    HashMap<Noeud, Label> map = new HashMap<Noeud, Label>() ;

    // Tas de label, on ajoute les noeud visités dans le tas
    BinaryHeap<Label> tas = new BinaryHeap<Label>() ;

    // Liste de tous les labels
    ArrayList<Label> labs = new ArrayList<Label>() ;


    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
        super(gr, sortie, readarg) ;

        this.zoneOrigine = gr.getZone () ;
        this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;

        // Demander la zone et le sommet destination.
        this.zoneOrigine = gr.getZone () ;
        this.destination = readarg.lireInt ("Numero du sommet destination ? ");
    }


    public void run() {

        System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

         /* Parcourir tous les noeuds du graphe et créer un label pour chacun (distance = infinite)
          *
          */
        Noeud n = graphe.getNoeuds()[origine] ;
        Label current = new Label(false,0,null,n) ;
        map.put(n, current) ;
        tas.insert(current) ;


        // Tant que le tas n'est pas vide et qu'on est pas au noeud de destination
        while (!tas.isEmpty() && current.getNoeudCourant() != graphe.getNoeuds()[destination]) {
            current = tas.deleteMin() ;
            current.setMarquage(true) ;

            graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),10);
            // Parcourir les noeuds succésseurs de current
            // Pour chaque sommet v appartenant aux voisins de u faire
            for (Route r : current.getNoeudCourant().getRoutes() ){

                Label lab_dest = new Label(r.getDest()) ;

                if (!map.containsValue(lab_dest)) {
                    tas.insert(lab_dest) ;
                    map.put(r.getDest(), lab_dest) ;
                }

                // Cout de la route allant de current au succésseur
                double cout = r.getTemps() + current.getCout() ;

                // Si le cout est inférieur au cout actuel, on maj
                if ( cout < lab_dest.getCout() ) {
                    // MAJ du père et du cout
                    lab_dest.setPere(current.getNoeudCourant());
                    System.out.println(current.getNoeudCourant().getId());
                    lab_dest.setCout(cout) ;
                }

                // Si on est déja passé par ce label, il n'est plus dans le tas
                // donc on ne l'update pas
                if ( ! lab_dest.getMarquage() ) {
                    tas.update(lab_dest) ;
                }
            }
        }

        // Parcours en sens inverse grace au HashMap a partir de destination -> pere -> pere ... -> orgine
        /* Tant qu'il y a un père on continue à remonter, on s'arrete au noeud origine,
           qui à un père null
         */
        Noeud dad = map.get(graphe.getNoeuds()[destination]).getPere() ;
        System.out.println("Destination : "+ dad);
        while (dad != graphe.getNoeuds()[origine]) {
            dad = map.get(dad).getPere() ;
        }
    }
}
