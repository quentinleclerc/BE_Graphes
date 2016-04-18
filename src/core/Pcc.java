package core ;

import java.awt.*;
import java.io.* ;
import java.util.*;

import base.Readarg ;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;

    protected int zoneDestination ;
    protected int destination ;

    // HashMap liant chaque noeud a son label, Key = noeud, valeur = label
    private HashMap<Noeud, Label> map = new HashMap<Noeud, Label>() ;

    // Tas de label, on ajoute les noeud visités dans le tas
    private BinaryHeap<Label> tas = new BinaryHeap<Label>() ;


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

        // On insère dans le tas le noeud d'origine
        Noeud or = graphe.getNoeuds()[origine] ;
        Label current = new Label(false,0,null,or) ;
        map.put(or, current) ;
        tas.insert(current) ;

        graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),20);

        // Tant que le tas n'est pas vide et qu'on est pas au noeud de destination
        while (!tas.isEmpty() && (current.getNoeudCourant() != graphe.getNoeuds()[destination])) {

            // On récupère le min du tas de succésseurs
            current = tas.deleteMin() ;
            graphe.getDessin().setColor(Color.BLACK);
            graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),5);
            current.setMarquage(true) ;

            /* Debug : Affichage d'un point sur le noeud courant sur la carte et du numéro du noeud
            graphe.getDessin().setColor(Color.BLACK) ;
            graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),5);
            graphe.getDessin().putText(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),"Noeud " + current.getNoeudCourant().getId());
            */

            // Parcourir les noeuds succésseurs de current
            for (Route r : current.getNoeudCourant().getRoutes() ){

                /* Si le label du noeud de destination n'est pas dans le hashmap,
                 *  alors  :
                 *  - on crée le label
                 *  - on l'insère dans le tas, dans le hashmap
                 */
                if (!map.containsKey(r.getDest())) {
                    Label lab_dest = new Label(false, current.getCout() + r.getTemps(), current.getNoeudCourant(), r.getDest()) ;
                    tas.insert(lab_dest) ;
                    map.put(r.getDest(), lab_dest) ;
                }
                /* Sinon si le label est dans le hashmap et que le cout est inférieur alors
                 *  - on met à jour le père et le cout
                 *  - on update le tas car on a changé la valeur des cout
                 */
                else {
                    Label lab_dest = map.get(r.getDest()) ;
                    Double nouveau_cout = current.getCout() + r.getTemps() ;
                    if (lab_dest.getCout() > nouveau_cout) {
                        lab_dest.setPere(current.getNoeudCourant()) ;
                        lab_dest.setCout(nouveau_cout) ;
                        tas.update(lab_dest) ;
                    }
                }
            }
        }

        graphe.getDessin().drawPoint(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),20);

        /**
         *  Tant qu'il y a un père on continue à remonter, on s'arrete au noeud origine
         */
        Noeud dad = map.get(graphe.getNoeuds()[destination]).getPere() ;
        System.out.println("Destination : " + dad.getId() ) ;

        while (map.get(dad).getNoeudCourant() != graphe.getNoeuds()[origine]) {
            dad = map.get(dad).getPere() ;
         //   graphe.getDessin().drawPoint(dad.getLongi(),dad.getLat(),20);
            System.out.println("Père : " + dad.getId() );
        }
    }
}
