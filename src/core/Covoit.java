package core;

import base.Readarg;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.*;

public class Covoit extends Algo {

    protected Pcc Pcc1 ;
    protected Pcc Pcc2 ;
    protected Pcc PccDestOr ;

    protected double cout1 ;
    protected double cout2 ;

    protected double coutRencontre ;

    protected PccStar PccDest ;

    protected int mode ;

    protected Label pointRencontre ;

    // Appel au constructeur de PccStar, rajout de l'origine du piéton
    public Covoit(Graphe gr, PrintStream sortie, Readarg readarg, boolean aff) {

        super(gr, sortie, readarg) ;


        JOptionPane choixMode = new JOptionPane() ;
        String[] modes = {"Voiture - Voiture",
                "Voiture - Piéton",
                "Voiture - Transport commun" } ;
        String modeChoisi = (String) choixMode.showInputDialog(null, "Choix du mode", "Choix du mode", JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);

        if (modeChoisi.equals("Voiture - Voiture")) {
            mode = 1 ;
        }
        else if (modeChoisi.equals("Voiture - Piéton")) {
            mode = 2 ;
        }
        else if (modeChoisi.equals("Voiture - Transport commun")) {
            mode = 3 ;
        }
        System.out.println("mode = " + mode) ;

        System.out.println("Appel constructeur PCC Voiture") ;
        Pcc1 = new Pcc(gr, sortie, readarg, aff, 1) ;


        System.out.println("Appel constructeur PCC 2ème point") ;
        Pcc2 = new Pcc(gr,sortie, readarg , aff, mode) ;


        System.out.println("Appel constructeur PCC Dest-Origines") ;
        PccDestOr = new Pcc(gr, sortie, readarg, aff, 1) ;

        System.out.println("Appel de PCC Destination") ;
        PccDest = new PccStar(gr, sortie, readarg, aff, 1) ;


        // Choix origine voiture
        if (aff) {
            System.out.println("Veuillez cliquer sur le sommet d'origine de la voiture.");
            Pcc1.setOrigine(graphe.situerClick()) ;
        } else {
            // Ouvre un panneau pour choisir le sommet d'origine
            JOptionPane choixOrigine = new JOptionPane();
            String str_origine = choixOrigine.showInputDialog(null, "Veuillez choisir le sommet d'origine de la voiture", "Choix du sommet d'origine de la voiture", JOptionPane.QUESTION_MESSAGE);
            Pcc1.setOrigine(Integer.parseInt(str_origine)) ;
        }

        // Choix origine piéton
        if (aff) {
            System.out.println("Veuillez cliquer sur le sommet d'origine du deuxième point.");
            Pcc2.setOrigine(graphe.situerClick()) ;
        } else {
            // Ouvre un panneau pour choisir le sommet d'origine
            JOptionPane choixOrigine = new JOptionPane();
            String str_origine = choixOrigine.showInputDialog(null, "Veuillez choisir le sommet d'origine du deuxième point", "Choix du sommet d'origine du deuxième point", JOptionPane.QUESTION_MESSAGE);
            Pcc2.setOrigine(Integer.parseInt(str_origine)) ;
        }

        // Demander la zone et le sommet destination.
        if (aff) {
            System.out.println("Veuillez cliquer sur le sommet de destination.");
            Pcc1.setDestination(graphe.situerClick()) ;
            Pcc2.setDestination(Pcc1.destination) ;
        } else {
            // Ouvre un panneau pour choisir le nom du fichier de sortie
            JOptionPane choixDest = new JOptionPane();
            String str_dest = choixDest.showInputDialog(null, "Veuillez choisir le sommet de destination", "Choix du sommet de destination", JOptionPane.QUESTION_MESSAGE);
            Pcc1.setDestination(Integer.parseInt(str_dest)) ;
            Pcc2.setDestination(Pcc1.destination) ;
        }

        PccDestOr.setOrigine(Pcc2.destination) ;
        PccDestOr.setDestination(Pcc2.origine) ;
    }

    public void affichage() {
        // On affiche un point bleu et un texte sur l'origine voiture
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[Pcc1.origine].getLongi(),graphe.getNoeuds()[Pcc1.origine].getLat(),10) ;
        graphe.getDessin().setColor(Color.RED);
        graphe.getDessin().putText(graphe.getNoeuds()[Pcc1.origine].getLongi(),graphe.getNoeuds()[Pcc1.origine].getLat(),"Noeud origine " + graphe.getNoeuds()[Pcc1.origine].getId());

        // On affiche un point bleu et un texte sur l'origine du deuxième point
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[Pcc2.origine].getLongi(),graphe.getNoeuds()[Pcc2.origine].getLat(),10) ;
        graphe.getDessin().setColor(Color.RED);
        graphe.getDessin().putText(graphe.getNoeuds()[Pcc2.origine].getLongi(),graphe.getNoeuds()[Pcc2.origine].getLat(),"Noeud origine " + graphe.getNoeuds()[Pcc2.origine].getId());


        // On affiche un point bleu et un texte sur la destination
        graphe.getDessin().setColor(Color.BLUE) ;
        graphe.getDessin().drawPoint(graphe.getNoeuds()[PccDest.destination].getLongi(),graphe.getNoeuds()[PccDest.destination].getLat(),10) ;
        graphe.getDessin().setColor(Color.RED);
        graphe.getDessin().putText(graphe.getNoeuds()[PccDest.destination].getLongi(),graphe.getNoeuds()[PccDest.destination].getLat(),"Noeud destination " + graphe.getNoeuds()[PccDest.destination].getId());

        graphe.getDessin().setColor(Color.CYAN);
        graphe.getDessin().drawPoint(pointRencontre.getNoeudCourant().getLongi(),pointRencontre.getNoeudCourant().getLat(),10) ;
    }

    public Label trouverRencontre() {
        Label l1, l2, l3 ;
        Noeud n1, n2, n3 ;

        Label rencontre = new Label() ;

        // Comparaison des HashMap
        for (Map.Entry<Noeud, Label> entry : Pcc1.map.entrySet() ) {
            l1 = entry.getValue() ;
            n1 = entry.getKey() ;
            l2 = Pcc2.map.get(n1) ;
            l3 = PccDestOr.map.get(n1) ;

            if (l2 != null && l3 != null) {
                if (l1.getCout() + l2.getCout() + l3.getCout() < rencontre.getCout() ) {
                    rencontre = l1 ;
                    rencontre.setCout(l1.getCout() + l2.getCout()) ;
                    cout2 = l2.getCout() ;
                    cout1 = l1.getCout() ;
                }
            }
        }
        return rencontre ;
    }


    public void run() {

        // Run des deux PCC
        System.out.println("Run de PCC Voiture") ;
        Pcc1.run() ;
        System.out.println("Run de PCC 2ème point") ;
        Pcc2.run() ;
        System.out.println("Run de PCC Dest-Or") ;
        PccDestOr.run() ;


        System.out.println("Comparaison des HashMap") ;
        this.pointRencontre = trouverRencontre() ;


        System.out.println(pointRencontre.getNoeudCourant().getId()) ;


        // Paramétrage des PCC finaux 
        PccDest.setOrigine(pointRencontre.getNoeudCourant().getId()) ;
        PccDest.setDestination(Pcc1.destination) ;
        Pcc1.setDestination(pointRencontre.getNoeudCourant().getId()) ;
        Pcc2.setDestination(pointRencontre.getNoeudCourant().getId()) ;

        System.out.println("Run de PCC Destination") ;
        PccDest.run() ;
        System.out.println("Run de PCC1") ;
        Pcc1.run() ;
        System.out.println("Run de PCC2") ;
        Pcc2.run() ;

        try {
            Pcc1.remontee() ;
            Pcc2.remontee() ;
            PccDest.remontee() ;
        }
        catch (NullPointerException e) {
            System.out.println("Il n'existe pas de chemins entre ces deux points");
        }

        affichage() ;
    }


}
