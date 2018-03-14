package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;

public class Connexion extends AppCompatActivity {
    private static Intent goMain;
    private static Intent goErreur;
    private static Button connexion;
    private static EditText adresse;
    private static TextView enCours;
    private static CheckBox boxPort;
    private static EditText portTexte;
    private static TextView instructions;
    private static TextView cour;
    private static TextView link;

    private static final int PORT = 33555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        goMain = new Intent(Connexion.this, UnicodeTable.class);
        connexion = findViewById(R.id.conn);
        adresse = findViewById(R.id.adress);
        boxPort = findViewById(R.id.checkPort);
        portTexte = findViewById(R.id.textPort);
        instructions = findViewById(R.id.invisibleTextView);
        enCours = findViewById(R.id.tryConnect);
        enCours.setVisibility(View.INVISIBLE);
        cour = findViewById(R.id.cour);
        link = findViewById(R.id.link);
        cour.setText("C:\\[path]\\RUTServer.exe [port]");
        instructions.setText("If you change the port, please be sure to launch the RUTServer on the"
                + " same port ! To do that, open the command prompt on your computer and type :");
        instructions.setVisibility(View.INVISIBLE);
        portTexte.setVisibility(View.INVISIBLE);
        cour.setVisibility(View.INVISIBLE);
        Typeface courrier = Typeface.createFromAsset(getAssets(), "fonts/cour.ttf");
        cour.setTypeface(courrier);
        link.setTypeface(courrier);
        boxPort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    portTexte.setVisibility(View.VISIBLE);
                    instructions.setVisibility(View.VISIBLE);
                    cour.setVisibility(View.VISIBLE);
                }
                else {
                    portTexte.setText("33555");
                    portTexte.setVisibility(View.INVISIBLE);
                    instructions.setVisibility(View.INVISIBLE);
                    cour.setVisibility(View.INVISIBLE);
                }
            }
        });
        final Connexion conn = this;
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connexionServeur client = null;
                try{
                    //On doit déléguer les interractions Client-Serveur à un autre
                    //Sinon on a : android.os.NetworkOnMainThreadException
                    enCours.setText("Trying to connect to the RUTServer...");
                    enCours.setVisibility(View.VISIBLE);

                    int port;
                    try{
                        port = Integer.parseInt(portTexte.getText().toString());
                    }
                    catch(NumberFormatException e){
                        port = PORT;
                        portTexte.setText(Integer.toString(port));
                    }

                    client = new connexionServeur(adresse.getText().toString(), port);
                    //Le killerConnection est sencé arrêter le client s'il prends trop de temps
                    //Cependant nous n'avons pas réussi à la mettre en place : décommenter ce code
                    //bloque la connexion à l'ordinateur, même si l'IP entré est le bon
                    //killerConnexion kc = new killerConnexion(client, 7);
                    //kc.execute();
                    //client.setKiller(kc);
                    //kc = null;
                    //Le code de killerConnexion est toujours disponible en bas de cette page, en commentaire
                    client.execute();
                    Thread.sleep(100); //On laisse le temps d'afficher le texte
                    client.get();
                    Socket cli = client.getSocket();

                    UnicodeTable.setClient(cli);
                    enCours.setVisibility(View.INVISIBLE);
                    startActivity(goMain);

                }catch(Exception e) {
                    //Si il y a une exception, c'est qu'on a pas pu se connecter
                     if (client != null)
                        if (!client.isCancelled())
                            client.cancel(true);
                     enCours.setText("Impossible to connect.\nComplete error : " + e.getMessage());
                }
            }
        });
    }
}
/*
package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;

public class killerConnexion extends AsyncTask<Void,Void, Void> {

    private connexionServeur conn = null;
    private int millis;
    public killerConnexion(connexionServeur conn, int secondes){
        //super();
        this.conn = conn;
        this.millis = secondes * 1000;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.currentThread().sleep(millis);
            if(conn.getStatus()==Status.RUNNING)
                conn.cancel(true);
        }catch(InterruptedException e){
            conn.setKiller(null);
        }
        return null;
    }
}

 */