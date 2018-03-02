package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.net.Socket;

public class Connexion extends AppCompatActivity {

    private static final int PORT = 33555;
    private static final String ADRESSE = "192.168.43.75";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        View v = this.getCurrentFocus();
        Intent goMain = new Intent();
        Intent goErreur = new Intent(v.getContext(), ErreurConnexion.class);
        try{
            Socket client = new Socket(ADRESSE, PORT);
            //Lancer activité Main avec client en paramètre

            startActivity(goMain);
        }catch(IOException e){
            startActivity(goErreur);
        }
    }
}
