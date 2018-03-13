package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
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
    private static MenuItem lastItemSelected = null;
    private static Typeface font;
    private static TableLayout tableLayout;
    private static NavigationView nav;
    private static Menu menu;
    private static DrawerLayout mDrawerLayout;
    private static FrameLayout frameLayout;
    private static ArrayList<Categorie> categories;
    private static ScrollView sv;
    private static int color;
    private static FirebaseDatabase base = FirebaseDatabase.getInstance();
    private DatabaseReference blocks = base.getReference();

    public static boolean isPrintableChar( char c ) {
        return true; //Nous n'avons pas réussi à trouver une version fiable de cette fonction
        /*
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS
                ;
        */
    }

    //---------ON CREATE-----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_boutons);

            //On récupère la couleur des boutons
            color = getResources().getColor(R.color.buttonColor);

            //On récupère la police gérant l'Unicode
            font = Typeface.createFromAsset(getAssets(), "fonts/arialuni.ttf");

            //On passe le socket à l'ASynchTask d'envoie de caractère
            envoieCharactère.setClient(client);

            //On récupère les éléments du .xml
            tableLayout = (TableLayout) findViewById(R.id.chaise);
            sv = findViewById(R.id.scrollView);
            nav = findViewById(R.id.nav_view);
            menu = nav.getMenu();
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            frameLayout = (FrameLayout) findViewById(R.id.frame_layout);

            //On créé les catégories Unicode
            categories = new ArrayList<Categorie>();

            //On remplie les catégories unicode avec un appel à la BDD Firebase
            blocks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    menu.removeGroup(Menu.NONE);
                    categories.clear();
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

            //On initialise le code qui se lancera lorsqu'on cliquera sur une catégorie
            nav.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if(lastItemSelected != null) {
                                lastItemSelected.setChecked(false);
                                //CHANGEMENT COULEUR -> NOIR
                                SpannableString spanString = new SpannableString(lastItemSelected.getTitle().toString());
                                spanString.setSpan(new ForegroundColorSpan
                                        (ContextCompat.getColor(
                                                getApplicationContext(),
                                                R.color.blackColor)),
                                        0,
                                        spanString.length(),
                                        0);
                                lastItemSelected.setTitle(spanString);
                            }
                            menuItem.setChecked(true);
                            //CHANGEMENT COULEUR -> BLANC
                            SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
                            spanString.setSpan(new ForegroundColorSpan
                                            (ContextCompat.getColor(
                                                    getApplicationContext(),
                                                    R.color.fontColor)),
                                    0,
                                    spanString.length(),
                                    0);
                            menuItem.setTitle(spanString);
                            lastItemSelected = menuItem;
                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();
                            sv.fullScroll(ScrollView.FOCUS_UP);
                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here
                            Categorie categ = categories.get(menuItem.getItemId());
                            int from = Integer.parseInt(categ.getfrom(), 16 );
                            int to = Integer.parseInt(categ.getto(), 16 );
                            //Initialisationde la récursion
                            try {
                                initRecurPrintButtons(from, to);
                            }catch(Exception e){
                                Log.i("exception", e.toString());
                                finish();
                                System.exit(0);
                            }
                            return true;
                        }
                    });

            //On ouvre les drawers pour que l'utilisateur puisse sélectionner une catégorie
            mDrawerLayout.openDrawer(GravityCompat.START);
        }catch(Exception e){
            Log.i("AAAAAexception", e.toString());
            finish();
            System.exit(0);
        }
    }

    //---------ON BACK PRESSED-----------

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START))
            super.onBackPressed();
        else
            mDrawerLayout.openDrawer(GravityCompat.START);
    }


    //---------FONCTIONS DE GENERATIONS DES BOUTONS------------

    private static final int column = 4;
    private static final int nbButtonsPerPage = column*10;

    //Cette fonction lance la recursion de génération de pages de boutons
    private void initRecurPrintButtons(final int from, final int to)
    throws Exception
    {
        int newTo = (to-from+1) > nbButtonsPerPage ? (from + nbButtonsPerPage - 1) : to;
        printALotOfButtons(from, from, newTo, to);
    }

    //Cette fonction est récursive et calcul s'il faut créer un bouton "previous" et "next"
    //Le cas échéant, elle le fait
    private void printALotOfButtons(final int firstFrom,
                                    final int from, final int to,
                                    final int firstTo)
    throws Exception
    {
        //On supprime tout les boutons précédents
        sv.fullScroll(ScrollView.FOCUS_UP);
        tableLayout.removeAllViews();
        //On génère le bouton "previous"
        if(from > firstFrom) {
            TableRow previousRow = new TableRow(this);
            Button previousButton = new Button(this);
            previousButton.setText("Previous");
            previousButton.setTransformationMethod(null);
            previousButton.setTextColor(color);
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousFrom = from - nbButtonsPerPage;
                    int previousTo = previousFrom + nbButtonsPerPage - 1;
                    try {
                        printALotOfButtons(firstFrom, previousFrom, previousTo, firstTo);
                    }catch(Exception e){
                        Log.i("exception", e.toString());
                        finish();
                        System.exit(0);
                    }
                }
            });
            previousRow.addView(previousButton);
            tableLayout.addView(previousRow);
        }

        //On génère les boutons principaux
        printButtons(firstFrom, from, to, firstTo);

        //On génère le bouton next
        if(to < firstTo){
            TableRow nextrow = new TableRow(this);
            Button nextButton = new Button(this);
            nextButton.setText("Next");
            nextButton.setTransformationMethod(null);
            nextButton.setTextColor(color);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nextTo = to + nbButtonsPerPage;
                    int nextFrom = nextTo - nbButtonsPerPage + 1;
                    try {
                        printALotOfButtons(firstFrom, nextFrom, nextTo, firstTo);
                    }catch(Exception e){
                        Log.i("exception", e.toString());
                        finish();
                        System.exit(0);
                    }
                }
            });
            nextrow.addView(nextButton);
            tableLayout.addView(nextrow);
        }
    }

    //Cette fonction génère les boutons qui enverront au serveur les codePoints Unicode
    private void printButtons(final int firstFrom, int from, int to, final int firstTo)
    throws Exception{
        if(from > to)
            throw new Exception("Les codePoints passés en paramètre ne sont pas cohérents");
        if(from < firstFrom)
            from = firstFrom;
        if(to > firstTo)
            to = firstTo;
        int total = to - from + 1;
        if(total > nbButtonsPerPage)
            throw new Exception("Il y a trop de boutons sur cette page !");

        int nbRow = total / column + (total%column==0?0:1);
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
                /*
                 * Ce code permetterait en théorie de savoir si le caractère est utilisable ou pas
                 * Cependant nous n'avons pas réussi à trouver un code fiable pour faire cela
                 * On affiche donc tout les caractères, même ceux qui ne sont pas affichables
                if(!isPrintableChar(texte[0])){
                    break;
                }
                */
                Button btn = new Button(this);
                btn.setId(codePoint);
                btn.setTypeface(font);
                btn.setText(String.copyValueOf(texte));
                btn.setTransformationMethod(null);
                btn.setBackgroundColor(color);
                /* Essai de redimenssionage de boutons... Echec
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
}
