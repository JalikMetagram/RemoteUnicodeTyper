package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.Socket;

public class Connexion extends AppCompatActivity {

    private static final int PORT = 33555;

    private static final String ADRESSE = "192.168.43.75";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        Log.i("OH", "connexion");
        Intent goMain = new Intent(Connexion.this, TestBoutons.class);
        Intent goErreur = new Intent(Connexion.this, ErreurConnexion.class);
        try{
            connexionServeur client = new connexionServeur(ADRESSE, PORT);
            client.execute(); //On doit déléguer les interractions Client-Serveur à un autre Thread
            //Sinon on a : android.os.NetworkOnMainThreadException
            client.get(); //Permet d'attendre la fin de l'execution
            Socket cli = client.getSocket();

            //Lancer activité Main avec client en paramètre
            TestBoutons.setClient(cli);
            startActivity(goMain);
        }catch(Exception e){
            ErreurConnexion.setException(e);
            startActivity(goErreur);
        }
    }
}
