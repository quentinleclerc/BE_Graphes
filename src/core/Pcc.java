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

    // Nombre de noeuds total insérés dans le tas
    protected int nb_noeuds_parcourus;

    // Nombre de noeuds maximum insérés dans le tas
    protected int nb_max_tas = 0 ;

    //Durée d'éxécution
    protected long duree;

    // Choix du parcours en temps ou distance
    // Vrai -> en distance, Faux -> en temps
    protected boolean coutDistance ;

    // HashMap liant chaque noeud a son label, Key = Noeud, Valeur = Label
    protected HashMap<Noeud, Label> map = new HashMap<>() ;

    // Tas de label, on ajoute les noeud visités dans le tas
    protected BinaryHeap<Label> tas = new BinaryHeap<>() ;

    /* 0 : Pcc classique
     * 1 : Mode voiture - voiture
     * 2 : Mode voiture - pieton
     * 3 : Mode voiture - transport commun
     */
    protected int mode ;

    // Vitesse max dépendant du mode
    protected int vitesseMax = 130 ;


    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, boolean aff, int mode) {
        super(gr, sortie, readarg) ;

        this.mode = mode ;

        if (mode == 1) {
            this.vitesseMax = 130 ;
        }
        else if (mode == 2) {
            this.vitesseMax = 4 ;
        }
        else if( mode == 3) {
            this.vitesseMax = 80 ;
        }

        if (mode == 0) {
            // Choix du parcours en temps ou distance
            JOptionPane choixCout = new JOptionPane() ;
            String[] tempsDist = {"Temps", "Distance"} ;
            coutDistance =  (1 == choixCout.showOptionDialog(null,"Voulez-vous faire un parcours en temps ou en distance ?","Choix de la sortie", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,tempsDist,tempsDist[1])) ;

            // Demander la zone et le sommet d'origine
            this.zoneOrigine = gr.getZone();
            if (aff) {
                System.out.println("Veuillez cliquer sur le sommet d'origine.") ;
                this.origine = graphe.situerClick() ;
            } else {
                // Ouvre un panneau pour choisir le sommet d'origine
                JOptionPane choixOrigine = new JOptionPane() ;
                String str_origine = choixOrigine.showInputDialog(null, "Veuillez choisir le sommet d'origine", "Choix du sommet d'origine", JOptionPane.QUESTION_MESSAGE);
                this.origine = Integer.parseInt(str_origine);
            }

            // Demander la zone et le sommet destination.
            this.zoneOrigine = gr.getZone();
            if (aff) {
                System.out.println("Veuillez cliquer sur le sommet de destination.");
                this.destination = graphe.situerClick();
            } else {
                // Ouvre un panneau pour choisir le nom du fichier de sortie
                JOptionPane choixDest = new JOptionPane();
                String str_dest = choixDest.showInputDialog(null, "Veuillez choisir le sommet de destination", "Choix du sommet de destination", JOptionPane.QUESTION_MESSAGE);
                this.destination = Integer.parseInt(str_dest);
            }
        }
        else {
            coutDistance = false ;
        }

    }

    public void setOrigine (int origine) {
        this.origine = origine ;
        this.zoneOrigine = graphe.getZone() ;
    }

    public void setDestination (int destination) {
        this.destination = destination ;
        this.zoneDestination = graphe.getZone() ;
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
    public Chemin remontee() throws NullPointerException {
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

        return chem ;
    }

    /** Affiche le point d'origine et de destination sur la carte et les commentaires dans le terminal
     * Il est identique dans PccStar donc pas redéfini
     */
    public void affichage() {
        // On affiche un point bleu et un texte sur l'origine
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[origine].getLongi(),graphe.getNoeuds()[origine].getLat(),20) ;
        graphe.getDessin().setColor(Color.RED);
        graphe.getDessin().putText(graphe.getNoeuds()[origine].getLongi(),graphe.getNoeuds()[origine].getLat(),"Noeud origine " + graphe.getNoeuds()[origine].getId());

        // On affiche un point bleu et un texte sur la destination
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),20) ;
        graphe.getDessin().setColor(Color.RED);
        graphe.getDessin().putText(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),"Noeud destination " + graphe.getNoeuds()[destination].getId());
    }

    public void algo() {
        //Pour mesurer le temps d'excéution
        duree = System.currentTimeMillis() ;

        // 1) Initialisation
        Label current = initialisation() ;
        nb_noeuds_parcourus = 1 ; //Compte le nombre de noeuds parcourus, on met le noeud d'origine (iniialisation)

        // 2) Boucle principale : tant que le tas n'est pas vide et qu'on est pas au noeud de destination
        while (!tas.isEmpty() && (current.getNoeudCourant() != graphe.getNoeuds()[destination])) {

            nb_max_tas = tas.size()>nb_max_tas?tas.size():nb_max_tas ;

            // On récupère le min du tas de succésseurs et on le marque
            current = tas.deleteMin() ;
            current.setMarquage(true) ;

            // Affichage d'un point sur le noeud parcouru
            // graphe.getDessin().setColor(Color.BLACK);
            // graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),2);

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
                        lab_dest = new Label(false, current.getCout() + r.getTemps(vitesseMax), current.getNoeudCourant(), r.getDest()) ;
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
                        nouveau_cout = current.getCout() + r.getTemps(vitesseMax) ;
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

        if (origine == destination) {
            System.out.println("L'origine et la destination sont identiques.");
        }
        else {
            System.out.println("Run " + this.getClass().getCanonicalName() + " de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

            if (mode == 0) {
                affichage();
                algo();
                affichage();


                try {
                    Chemin chem = remontee();

                    System.out.println(this.getClass().getCanonicalName().equals("core.Pcc") ? "Alorithme de Dijkstra - Pcc" : "Algorithme A* - PccStar");
                    if (coutDistance) {
                        System.out.println("Chemin le plus court : ");
                        System.out.println("Distance : " + map.get(graphe.getNoeuds()[destination]).getCout() + " mètres");
                        System.out.println("Temps : " + chem.calculCoutTemps() + " minutes");
                    } else {
                        System.out.println("Chemin le plus rapide : ");
                        System.out.println("Temps : " + map.get(graphe.getNoeuds()[destination]).getCout() + " minutes");
                        System.out.println("Distance : " + chem.calculCoutDistance() + " mètres");
                    }
                    System.out.println("Nombre de noeuds parcourus : " + nb_noeuds_parcourus + ".");
                    System.out.println("Nombre de noeuds maximums dans le tas : " + nb_max_tas + ".");

                    // temps de calcul
                    duree = (System.currentTimeMillis() - duree);
                    System.out.println("Duree d'execution : " + duree + " ms");
                } catch (NullPointerException e) {
                    System.out.println("Il n'existe pas de chemins entre ces deux points");
                }
            }
            else {
                algo() ;
            }
        }
    }



}