package core ;

import java.awt.*;
import java.io.* ;
import java.util.*;

import base.Readarg ;

import javax.swing.*;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;

    protected int zoneDestination ;
    protected int destination ;

    //Nombre de noeuds insérés dans le tas
    protected int nb_noeuds_parcourus;

    //Durée d'éxécution
    protected long duree;

    // Choix du parcours en temps ou distance
    protected boolean coutDistance ; // Vrai => en distance, Faux => en temps

    // HashMap liant chaque noeud a son label, Key = noeud, valeur = label
    protected HashMap<Noeud, Label> map = new HashMap<>() ;

    // Tas de label, on ajoute les noeud visités dans le tas
    protected BinaryHeap<Label> tas = new BinaryHeap<>() ;

    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, boolean aff) {
        super(gr, sortie, readarg) ;

        // Choix de l'affichage ou non
        JOptionPane choixCout = new JOptionPane() ;
        String[] tempsDist = {"Temps", "Distance"} ;
        coutDistance =  (1 == choixCout.showOptionDialog(null,"Voulez-vous afficher la carte ?","Choix de la sortie", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,tempsDist,tempsDist[1])) ;

        // Choix du parcours en temps ou distance
        this.zoneOrigine = gr.getZone () ;
        if (aff) {
            System.out.println("Veuillez cliquer sur le sommet d'origine.") ;
            this.origine = graphe.situerClick() ;
        }
        else {
            // Ouvre un panneau pour choisir le sommet d'origine
            JOptionPane choixOrigine = new JOptionPane() ;
            String str_origine = choixOrigine.showInputDialog(null, "Veuillez choisir le sommet d'origine", "Choix du sommet d'origine", JOptionPane.QUESTION_MESSAGE);
            this.origine = Integer.parseInt(str_origine) ;
        }

        // Demander la zone et le sommet destination.
        this.zoneOrigine = gr.getZone () ;
        if (aff) {
            System.out.println("Veuillez cliquer sur le sommet de destination.") ;
            this.destination = graphe.situerClick() ;
        }
        else {
            // Ouvre un panneau pour choisir le nom du fichier de sortie
            JOptionPane choixOrigine = new JOptionPane() ;
            String str_dest = choixOrigine.showInputDialog(null, "Veuillez choisir le sommet de destination", "Choix du sommet de destination", JOptionPane.QUESTION_MESSAGE);
            this.destination = Integer.parseInt(str_dest) ;
        }

    }

    /**
     * Initialisation de l'algorithme de Dijkstra
     * On crée et insère dans le tas et hashmap le label origine
     * @return Label le label du noeud d'origine
     */
    public Label initialisation() {
        // On insère dans le tas le noeud d'origine
        Noeud or = graphe.getNoeuds()[origine] ;
        Label current = new Label(false,0,null,or) ;
        map.put(or, current) ;
        tas.insert(current) ;

        return current ;
    }

    /** Phase de remontée, création du chemin
     *  Affiche un point sur chaque noeud du chemin
     *  @return Chemin le chemin final, de destination vers origine
     */
    public void remontee() {

        if (tas.isEmpty() ) {
            System.out.println("Aucun chemin n'a été trouvé entre ces deux points") ;
        }
        else {
            Chemin chem = new Chemin(graphe.getIdcarte(), origine, destination) ;
            Noeud dad = graphe.getNoeuds()[destination] ;
            int i = 0 ;
            chem.addNoeud(dad, i) ;

            while (dad != graphe.getNoeuds()[origine]) {
                dad = map.get(dad).getPere() ;
                chem.addNoeud(dad,++i);
            }

            chem.printChemin() ;
            chem.printCheminCarte(graphe);
            if (coutDistance) {
                System.out.println("Cout du noeud destination en distance : " + map.get(graphe.getNoeuds()[destination]).getCout() + " mètres") ;
            }
            else {
                System.out.println("Cout du noeud destination en temps : " + map.get(graphe.getNoeuds()[destination]).getCout()  + " minutes") ;
            }

        }
    }

    /** Affiche le point d'origine et de destination sur la carte et les commentaires dans le terminal
     * Il est identique dans PccStar donc pas redéfini
     * @param algo Nom de l'algo : PCC, PCC-Star
     */
    public void affichage(String algo) {
        System.out.println("Run " + algo + " de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

        // On affiche un point bleu et un texte sur l'origine
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[origine].getLongi(),graphe.getNoeuds()[origine].getLat(),20) ;
        graphe.getDessin().putText(graphe.getNoeuds()[origine].getLongi(),graphe.getNoeuds()[origine].getLat(),"Noeud origine " + graphe.getNoeuds()[origine].getId());

        // On affiche un point bleu et un texte sur la destination
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),20) ;
        graphe.getDessin().putText(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),"Noeud destination " + graphe.getNoeuds()[destination].getId());
    }

    public void algo() {
        //Pour mesurer le temps d'excéution
        duree = System.currentTimeMillis() ;

        // 1) Initialisation
        Label current = initialisation() ;
        nb_noeuds_parcourus = 1; //Compte le nombre de noeuds parcourus, on met le noeud d'origine (iniialisation)

        // 2) Boucle principale : tant que le tas n'est pas vide et qu'on est pas au noeud de destination
        while (!tas.isEmpty() && (current.getNoeudCourant() != graphe.getNoeuds()[destination])) {

            // On récupère le min du tas de succésseurs et on le marque
            current = tas.deleteMin() ;
            current.setMarquage(true) ;

            // Affichage d'un point sur le noeud parcouru
            graphe.getDessin().setColor(Color.BLACK);
            graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),5);

            // Parcourir les noeuds succésseurs de current
            for (Route r : current.getNoeudCourant().getRoutes() ){

                /* Si le label du noeud de destination n'est pas dans le hashmap,
                 *  alors  :
                 *  - on crée le label
                 *  - on l'insère dans le tas, dans le hashmap
                 */
                if (!map.containsKey(r.getDest())) {
                    Label lab_dest ;
                    if (coutDistance) {
                        lab_dest = new Label(false, current.getCout() + r.getDist(), current.getNoeudCourant(), r.getDest()) ;
                    }
                    else {
                        lab_dest = new Label(false, current.getCout() + r.getTemps(), current.getNoeudCourant(), r.getDest()) ;
                    }

                    tas.insert(lab_dest) ;
                    map.put(r.getDest(), lab_dest) ;
                    nb_noeuds_parcourus++ ;
                }
                /* Sinon, si le label est dans le hashmap et que le cout est inférieur alors
                 *  - on met à jour le père et le cout
                 *  - on update le tas car on a changé la valeur des cout
                 */
                else {
                    Label lab_dest = map.get(r.getDest()) ;
                    double nouveau_cout ;
                    if (coutDistance) {
                        nouveau_cout = current.getCout() + r.getDist() ;
                    }
                    else {
                        nouveau_cout = current.getCout() + r.getTemps() ;
                    }
                    if (lab_dest.getCout() > nouveau_cout) {
                        lab_dest.setPere(current.getNoeudCourant()) ;
                        lab_dest.setCout(nouveau_cout) ;
                        tas.update(lab_dest) ;
                    }
                }
            }
        }
    }

    public void run() {

        affichage(this.getClass().getCanonicalName()) ;
        algo() ;
        remontee() ;

        System.out.println(this.getClass().getCanonicalName() + " - Nombre de noeuds parcourus : " + nb_noeuds_parcourus + ".");
        //temps de calcul
        duree=(System.currentTimeMillis()-duree);
        System.out.println(this.getClass().getCanonicalName() + " - Duree d'execution : " + duree + " ms");
    }
}