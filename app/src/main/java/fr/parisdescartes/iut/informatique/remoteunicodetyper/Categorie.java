package fr.parisdescartes.iut.informatique.remoteunicodetyper;

/**
 * Created by JALIK on 09/03/2018.
 */

public class Categorie {
    private String nom;
    private String from;
    private String to;
    public Categorie(String n, String f, String t){
        nom = n;
        from = f;
        to = t;
    }

    public String getNom(){
        return nom;
    }

    public String getfrom(){
        return from;
    }

    public String getto(){
        return to;
    }
}
