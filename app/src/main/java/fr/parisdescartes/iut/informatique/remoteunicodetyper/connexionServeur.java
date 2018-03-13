package fr.parisdescartes.iut.informatique.remoteunicodetyper;

import android.os.AsyncTask;
import java.io.IOException;
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
    private killerConnexion kc = null;
    public void setKiller(killerConnexion k){
        kc = k;
    }
    public Socket getSocket() throws Exception{
        if(client == null && probleme == null)
            throw new Exception("The connexion is taking to much time... Please be sure that :" +
                    "\n\t- Your computer and your Android Device are connected on the same Network" +
                    "\n\t- You entered the right IP Adress" +
                    "\n\t- You launched the RUTServer on your computer with the same port as the" +
                    " one set in the connexion page (they're both set to 33555 by default)");
        else if(probleme != null)
            throw new Exception(probleme);
        else
            return client; //client est forcément différent de null et probleme est forcément null
    }

    public connexionServeur(String addr, int port) {
        super();
        dstAddress = addr;
        dstPort = port;
    }
    @Override
    protected Void doInBackground(Void... arg0){
        try {
            client = new Socket(dstAddress, dstPort);
            if(kc != null)
                kc.cancel(true);
        }catch(Exception e){
            probleme = e;
        }
        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        try {
            if (client != null)
                client.close();
        }catch(IOException e){}
        super.onCancelled(aVoid);
    }
}
