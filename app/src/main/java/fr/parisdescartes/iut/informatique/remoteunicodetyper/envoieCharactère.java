package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by JALIK on 02/03/2018.
 */

public class envoieCharactère extends AsyncTask<Void,Void, Void> {
    private static Socket client;
    private int codepoint;

    private static PrintWriter out;

    public static void setClient(Socket cli) throws IOException {
        client = cli;
        out = new PrintWriter(client.getOutputStream ( ), true);
    }

    public envoieCharactère(int code){
        codepoint = code;
    }

    @Override
    protected Void doInBackground(Void... arg0){
        try{
            synchronized (out) {
                out.println(Integer.toString(codepoint));
            }
        }catch(Exception e){
            Log.i("exception", e.getMessage());
        }
        return null;
    }
}
