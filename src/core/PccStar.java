package core ;

import java.awt.Color;
import java.io.* ;
import base.Readarg ;

public class PccStar extends Pcc {

    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, boolean aff) {
	super(gr, sortie, readarg, aff ) ;
    }
    
    /**
     * Initialisation de l'algorithme de Dijkstra
     * On crée et insère dans le tas et dans la hashmap le label origine
     * @return Label le label du noeud d'origine
     */
    private LabelStar initialisation() {
        // On insère dans le tas le noeud d'origine
        Noeud or = graphe.getNoeuds()[origine] ;
        Noeud dest = graphe.getNoeuds()[destination] ;
        LabelStar current = new LabelStar(false,0,null,or,dest) ;
        map.put(or, current) ;
        tas.insert(current) ;

        return current ;
    }
    
    /** Phase de remontée, création du chemin
     *  Affiche un point sur chaque noeud du chemin
     *  @return Chemin le chemin final, de destination vers origine
     */
    private Chemin remontee() {

        Chemin chem = new Chemin(graphe.getIdcarte(), origine, destination) ;
        Noeud dad = graphe.getNoeuds()[destination] ;
        System.out.println("Cout du noeud destination : " + (map.get(dad).getCout()+map.get(graphe.getNoeuds()[origine]).getEstimation())) ;
        int i = 0 ;
        chem.addNoeud(dad, i) ;

        while (dad != graphe.getNoeuds()[origine]) {
            dad = map.get(dad).getPere() ;
            chem.addNoeud(dad,++i);

            // Affichage un point bleu sur les noeuds du chemin
            graphe.getDessin().setColor(Color.BLUE);
            graphe.getDessin().drawPoint(dad.getLongi(),dad.getLat(),10);
            }

        return chem ;
    }
    
    
    
    public void run() {

    	System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
    	//Pour mesurer le temps d'excéution
      duree=System.currentTimeMillis();
      
    	// 1) Initialisation
      Label current = initialisation() ;
      nb_noeuds_parcourus = 1; //Compte le nombre de noeuds parcourus, on met le noeud d'origine (iniialisation)
      
      // On affiche un point bleu et un texte sur l'origine
      graphe.getDessin().setColor(Color.BLUE);
      graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),20);
      graphe.getDessin().putText(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),"Noeud origine " + current.getNoeudCourant().getId());

      // 2) Boucle principale : tant que le tas n'est pas vide et qu'on est pas au noeud de destination
      while (!tas.isEmpty() && (current.getNoeudCourant() != graphe.getNoeuds()[destination])) {

          // On récupère le min du tas de succésseurs et on le marque
          current = tas.deleteMin() ;
          current.setMarquage(true) ;
          
          // Affichage d'un point sur le noeud parcouru
          graphe.getDessin().setColor(Color.BLACK);
          graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),5);


          /* Debug : Affichage d'un point sur le noeud courant sur la carte et du numéro du noeud
          graphe.getDessin().setColor(Color.BLACK) ;
          graphe.getDessin().drawPoint(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),5);
          graphe.getDessin().putText(current.getNoeudCourant().getLongi(),current.getNoeudCourant().getLat(),"Noeud " + current.getNoeudCourant().getId());
          */

          // Parcourir les noeuds successeurs de current
          for (Route r : current.getNoeudCourant().getRoutes() ){

              /* Si le label du noeud de destination n'est pas dans le hashmap,
               *  alors  :
               *  - on crée le label
               *  - on l'insère dans le tas, dans le hashmap
               */
              if (!map.containsKey(r.getDest())) {
                  Label lab_dest = new LabelStar(false, current.getCout() + r.getTemps(), current.getNoeudCourant(), r.getDest(), graphe.getNoeuds()[destination]) ;
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
                  Double nouveau_cout = current.getCout() + r.getTemps() ;
                  if (lab_dest.getCout() > nouveau_cout) {
                      lab_dest.setPere(current.getNoeudCourant()) ;
                      lab_dest.setCout(nouveau_cout) ;
                      tas.update(lab_dest) ;
                  }
              }
          }
      }

      // On affiche un point bleu et un texte sur la destination
      graphe.getDessin().setColor(Color.BLUE) ;
      graphe.getDessin().drawPoint(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),20) ;
      graphe.getDessin().putText(graphe.getNoeuds()[destination].getLongi(),graphe.getNoeuds()[destination].getLat(),"Noeud destination " + graphe.getNoeuds()[destination].getId());

      // 3) Remontee à partir de la destination
      if (tas.isEmpty() ) {
          System.out.println("Aucun chemin n'a été trouvé entre ces deux points") ;
      }
      else {
          remontee().printChemin() ;
      }
      System.out.println("PCCStar - Nombre de noeuds parcourus : " + nb_noeuds_parcourus + ".");
    	//temps de calcul
    	duree=(System.currentTimeMillis()-duree);
    	System.out.println("PCCStar - Duree d'execution : " + duree + " ms");
	
    }

}
