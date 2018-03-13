package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;

/**
 * Created by JALIK on 13/03/2018.
 */

public class killerConnexion extends AsyncTask<Void,Void, Void> {

    private connexionServeur conn = null;
    private int millis;
    public killerConnexion(connexionServeur conn, int secondes){
        super();
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
        }
        return null;
    }
}
