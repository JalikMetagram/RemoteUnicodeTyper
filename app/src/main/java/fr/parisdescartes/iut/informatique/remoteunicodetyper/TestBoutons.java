package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.net.Socket;
//import android.support.design.widget.NavigationView;
/*
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
*/
public class TestBoutons extends AppCompatActivity {

    private static Socket client = null;
    public static void setClient(Socket cli){
        client = cli;
    }

   private static Typeface font;
    private static TableLayout tableLayout;
    //private static NavigationView nav;
    private static Menu menu;
    //private static FirebaseDatabase base;
    //private DatabaseReference blocks = base.getReference("block");
    public static boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return /*(!Character.isISOControl(c))

                &&
                */
                block != null &&
                block != Character.UnicodeBlock.SPECIALS
                ;
    }

    public void printButtons(int from, int to){
        if(from > to)
            throw new RuntimeException("Les codePoints passés en paramètre ne sont pas cohérents");
        tableLayout.removeAllViews();
        int total = to - from + 1;
        int column = 4;
        int nbRow = total / column + (column%total==0?0:1);
        boolean ended = false;
        for (int i = 0; i <nbRow; i++) {
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            for(int j = 0 ; j < column ; ++j){
                final int codePoint = from++;
                if(codePoint > to){
                    ended = true;
                    break;
                }
                char[] texte = Character.toChars(codePoint);
                if(!isPrintableChar(texte[0])){
                    break;
                }
                Button btn = new Button(this);
                btn.setId(codePoint);
                btn.setTypeface(font);
                btn.setText(String.copyValueOf(texte));
                btn.setTransformationMethod(null);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        envoieCharactère envoie = new envoieCharactère(codePoint);
                        envoie.execute();
                    }
                });
                row.addView(btn);
            }
            tableLayout.addView(row);
            if(ended)
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_boutons);
        try {
            envoieCharactère.setClient(client);
            font = Typeface.createFromAsset(getAssets(), "fonts/arialuni.ttf");
            tableLayout = (TableLayout) findViewById(R.id.chaise);

            //blocks.addListenerForSingleValueEvent(ValueEventListener);
            //NavigationView navigationView = findViewById(R.id.nav_view);

            //TTESTEUH !
            int from = 100;
            int to = 200;
            printButtons(from, to);
        }catch(Exception e){
            Log.i("exception", e.getMessage());
    }
        }
}
