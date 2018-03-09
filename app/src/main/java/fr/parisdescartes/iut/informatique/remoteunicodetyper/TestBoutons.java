package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.net.Socket;
import java.util.ArrayList;

import android.support.design.widget.NavigationView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestBoutons extends AppCompatActivity {

    private static Socket client = null;
    public static void setClient(Socket cli){
        client = cli;
    }

   private static Typeface font;
    private static TableLayout tableLayout;
    private static NavigationView nav;
    private static Menu menu;
    private static DrawerLayout mDrawerLayout;
    private static ArrayList<Categorie> categories;
    private static int color;
    private static FirebaseDatabase base = FirebaseDatabase.getInstance();
    private DatabaseReference blocks = base.getReference();
    public static boolean isPrintableChar( char c ) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return /*(!Character.isISOControl(c))

                &&
                */
                block != null &&
                block != Character.UnicodeBlock.SPECIALS
                ;
    }

    private void printButtons(int from, int to){
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
                //Widget.Holo.Button.Small
                btn.setBackgroundColor(color);
                /*
                int height = btn.getLayoutParams().height;
                int width = btn.getLayoutParams().width;
                height = new Double( height * 0.80).intValue();
                width = new Double( width * 0.60).intValue();
                btn.getLayoutParams().height = height;
                btn.getLayoutParams().width = width;
                */
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
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_boutons);
            color = getResources().getColor(R.color.buttonColor);
            envoieCharactère.setClient(client);
            font = Typeface.createFromAsset(getAssets(), "fonts/arialuni.ttf");
            tableLayout = (TableLayout) findViewById(R.id.chaise);
            categories = new ArrayList<Categorie>();
            //blocks.addListenerForSingleValueEvent(ValueEventListener);
            nav = findViewById(R.id.nav_view);
            menu = nav.getMenu();
            mDrawerLayout = findViewById(R.id.drawer_layout);
            nav.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here
                            Categorie categ = categories.get(menuItem.getItemId());
                            int from = Integer.parseInt(categ.getfrom(), 16 );
                            int to = Integer.parseInt(categ.getto(), 16 );
                            printButtons(from, to);
                            return true;
                        }
                    });
            blocks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 0;
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        String nomCategorie = data.child("block").getValue().toString();
                        String from = data.child("from").getValue().toString();
                        String to = data.child("to").getValue().toString();
                        menu.add(Menu.NONE, i++, Menu.NONE, nomCategorie);
                        categories.add(new Categorie(nomCategorie, from, to));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Fail", "Fail");
                }
            });
        }catch(Exception e){
            Log.i("exception", e.toString());
            finish();
            System.exit(0);
        }
    }
}
