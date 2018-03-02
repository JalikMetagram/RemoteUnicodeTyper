package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ErreurConnexion extends AppCompatActivity {

    public static Exception erreur = null;

    public static void setException(Exception e){
        erreur = e;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erreur_connexion);
        Log.i("AH", "erreur");
        Log.i("Message", erreur.toString());
        TextView texte = findViewById(R.id.textViewErreur);
        if(erreur != null)
            texte.setText(erreur.getMessage());
        else
            texte.setText("Il y a une erreur mais g pa lu lol git gud");
    }
}
