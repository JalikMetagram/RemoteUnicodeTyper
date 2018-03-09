package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErreurConnexion extends AppCompatActivity {

    public static Exception erreur = null;

    final Intent goConnexion = new Intent(ErreurConnexion.this, Connexion.class);

    public static void setException(Exception e){
        erreur = e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erreur_connexion);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(goConnexion);
            }
        });
        Log.i("AH", "erreur");
        Log.i("Message", erreur.toString());
        TextView texte = findViewById(R.id.textViewErreur);
        if(erreur != null)
            texte.setText("Complete error : " + erreur.getMessage());
    }
}
