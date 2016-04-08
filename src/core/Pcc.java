package core ;

import java.io.* ;
import java.util.HashMap;

import base.Readarg ;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;

    protected int zoneDestination ;
    protected int destination ;

    HashMap<Noeud, Label> map ;
    BinaryHeap<Label> tas = new BinaryHeap<Label>() ;

    public static final int infinite = Integer.MAX_VALUE ;

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

        /* A vous d'implementer la recherche de plus court chemin.
         * 1) Parcourir tous les noeuds du graphe et créer un label pour chacun (distance = infinite)
         */

    }

}
