package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

import java.net.Socket;

public class TestBoutons extends AppCompatActivity {

    private static Socket client = null;

    public static void setClient(Socket cli){
        client = cli;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test_boutons);
            Log.i("AH", "TestBoutons");
            Button bouton_complexSet = findViewById(R.id.button8450);
            Button bouton_woman = findViewById(R.id.buttonWoman);
            Button bouton_brimestone = findViewById(R.id.buttonBrimestone);
            Button bouton_nani = findViewById(R.id.buttonNANI);
            //NANI : 20309
            //WOMAN : 9792
            //BRIMESTONE : 128783
            bouton_complexSet.setText("COMPLEXE" /*(char) 8450*/);
            bouton_woman.setText("FEMINISM");
            bouton_nani.setText("OMAEWA_MO_SHINDERU");
            bouton_brimestone.setText("BRIMESTONE");

            bouton_complexSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("envoie", "Bouton clic");
                    envoieCharactère envoie = new envoieCharactère(client, 8450);
                    envoie.execute();
                }
            });

            bouton_woman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("envoie", "Bouton clic");
                    envoieCharactère envoie = new envoieCharactère(client, 9792);
                    envoie.execute();
                }
            });

            bouton_nani.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("envoie", "Bouton clic");
                    envoieCharactère envoie = new envoieCharactère(client, 20309);
                    envoie.execute();
                }
            });

            bouton_brimestone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("envoie", "Bouton clic");
                    envoieCharactère envoie = new envoieCharactère(client, 128783);
                    envoie.execute();
                }
            });
        }catch(Exception e){
            Log.i("exception", e.getMessage());
        }
    }
}
