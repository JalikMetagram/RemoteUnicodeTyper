package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by JALIK on 02/03/2018.
 */

public class envoieCharactère extends AsyncTask<Void,Void, Void> {
    private Socket client;
    private int codepoint;
    public envoieCharactère(Socket cli, int code){
        client = cli;
        codepoint = code;
    }

    @Override
    protected Void doInBackground(Void... arg0){
        try{
            PrintWriter out = new PrintWriter(client.getOutputStream ( ), true);
            out.println(Integer.toString(codepoint));
        }catch(Exception e){
            Log.i("exception", e.getMessage());
        }
        return null;
    }
}
