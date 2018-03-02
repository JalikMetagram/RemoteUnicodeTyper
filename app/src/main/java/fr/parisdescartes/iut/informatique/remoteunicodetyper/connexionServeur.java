package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by JALIK on 02/03/2018.
 * /!\ Code repris du site suivant ! :
 * http://androidsrc.net/android-client-server-using-sockets-client-implementation/
 */

public class connexionServeur extends AsyncTask<Void,Void, Void> {

    private String dstAddress;
    private int dstPort;
    private Socket client = null;
    private Exception probleme = null;

    public Socket getSocket() throws Exception{
        if(client == null && probleme == null)
            throw new Exception("La tache n'a pas été lancée !");
        else if(probleme != null)
            throw new Exception(probleme);
        else
            return client; //client est forcément différent de null et probleme est forcément null
    }

    connexionServeur(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
    }
    @Override
    protected Void doInBackground(Void... arg0){
        try {
            client = new Socket(dstAddress, dstPort);
        }catch(Exception e){
            probleme = e;
        }
        return null;
    }
}
