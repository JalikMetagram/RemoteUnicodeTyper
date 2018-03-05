package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.Socket;

public class Connexion extends AppCompatActivity {

    private static final int PORT = 33555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        Log.i("OH", "connexion");
        final Intent goMain = new Intent(Connexion.this, TestBoutons.class);
        final Intent goErreur = new Intent(Connexion.this, ErreurConnexion.class);

        Button connexion = findViewById(R.id.conn);
        final EditText adresse = findViewById(R.id.adress);

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //On doit déléguer les interractions Client-Serveur à un autre
                    //Sinon on a : android.os.NetworkOnMainThreadException
                    connexionServeur client
                            = new connexionServeur(adresse.getText().toString(), PORT);
                    client.execute();

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
        });
    }
}
