package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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
        final CheckBox boxPort = findViewById(R.id.checkPort);
        final EditText portTexte = findViewById(R.id.textPort);
        final TextView instructions = findViewById(R.id.invisibleTextView);
        final TextView cour = findViewById(R.id.cour);
        cour.setText("C:\\[path]\\RUTServer.exe [port]");
        instructions.setText("If you change the port, please be sure to launch the RUTServer on the"
                + " same port ! To do that, open the command prompt on your computer and type :");
        instructions.setVisibility(View.INVISIBLE);
        portTexte.setVisibility(View.INVISIBLE);
        cour.setVisibility(View.INVISIBLE);
        cour.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/cour.ttf"));
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
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //On doit déléguer les interractions Client-Serveur à un autre
                    //Sinon on a : android.os.NetworkOnMainThreadException
                    int port;
                    try{
                        port = Integer.parseInt(portTexte.getText().toString());
                    }
                    catch(NumberFormatException e){
                        port = PORT;
                    }
                    connexionServeur client
                            = new connexionServeur(adresse.getText().toString(), port);
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
