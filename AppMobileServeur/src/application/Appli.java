package application;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Appli {
	//cf : https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml?&page=129
	//Port officiellement non utilisé
	private static int PORT = 33555;
	
	private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private static final Robot MrRobot;
	
	static {
		try {
			MrRobot = new Robot();
			//Permet de s'assurer que le robot appuie sur Ctrl+v intantanément
			MrRobot.setAutoDelay(0);
			MrRobot.setAutoWaitForIdle(false);
		} catch (AWTException e) {
			System.out.println("Okaayyy this is REALLY not supposed to happen..."
					+ "\nApparently your system doesn't allow for other programs to use your keyboard (and that's how this app works)"
					+ "\nPlease contact the developers and send them the following message :");
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 1) {
			try {
				int newPort = Integer.parseInt(args[0]);
				if(newPort <= 100 || newPort >= 65535)
					throw new NumberFormatException();
				PORT = newPort;
			} catch (NumberFormatException e1) {
				System.out.println("You tried to change the server port but entered a wrong number.\n"
						+ "Please try again with a number higher than 100 and lower than 65535");
			}
		}
		try {
			ServerSocket serveur = new ServerSocket(PORT);
			
			System.out.println("Server launched on port "+Integer.toString(PORT)+" for the Remote Unicode Typer\n"
					+ "Please connect your Android device to the same Wifi (or network) as this computer"
					+ "\nThen enter the following IP Adress on the app :\n"
					+ Inet4Address.getLocalHost().getHostAddress()
					+ "\nIf it doesn't work, you might want to try one of the following adresses :\n"
					+ getAllAdresses()
					+ "\n\n---Waiting for connexion---");
			
			Socket client = serveur.accept();
			
			System.out.println("\nAndroid device connected !"
					+ "\nYou can now start using the Remote Unicode Typer");
			
			try {
				BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
				
				String lecture;
				
				while(true){
					try {
						lecture = in.readLine();
						//System.out.println("Message recieved : " + lecture); //DEBUG
						synchronized(clipboard) { //Si on ne synchronized pas, il y a des IllegalStateException
							affichage(new Integer(lecture));
						}
					} catch (IOException | NumberFormatException e) {
						//ReadLine va renvoyer "null" si le client s'est déconnecté,
						//il y aura une exception sur le newInteger (d'où le NumberFormatException)
						client.close();
						serveur.close();
						break;
					} catch (IllegalStateException e) {
						//Dans ce cas là, il est probable que le client à tapé sur trop de caractères en même temps
						//On préfère laisser le programme continuer de s'executer : un caractère ne sera pas affiché
						continue;
					} catch (Exception e) {
						client.close();
						serveur.close();
						System.err.println(e);
						break;
					}
				}
			} catch (IOException e) {
				if(!client.isClosed())
					client.close();
				if(!serveur.isClosed())
					serveur.close();
			}
			
			System.out.println("The Android App disconnected from the computer.\n"
					+ "Please restart both the Android app and the Computer executable if you wish to use this service again.");
			if(!client.isClosed())
				client.close();
			if(!serveur.isClosed())
				serveur.close();
		} catch (IOException e) {
			System.out.println("There has been a problem..."
					+ "\nEither your Android device disconnected or the server couldn't start"
					+ "\nPlease be sure that you are using the right server port :"
					+ "\nDefault : 33555"
					+ "\nCurrent : " + Integer.toString(PORT)
					+ "\nIf the problem persists, please be sure that you are using the app right by checking the official Play Store page"
					+ "\nIf you are, please send the following message to the developers"
					+ "\nAnd if you have a minute or two, maybe explain how and when this error showed up :");
			e.printStackTrace();
		}
	}

	//CODE REPRIS DE STACK OVERFLOW
	private static String getAllAdresses() throws SocketException {
		StringBuilder s = new StringBuilder();
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration<InetAddress> ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        if(Character.isDigit(i.getHostAddress().charAt(0))) {
			        s.append(i.getHostAddress());
			        s.append("\n");
		        }
		    }
		}
		return s.toString();
	}
	
	private static void affichage(int integer) throws ClassNotFoundException, UnsupportedFlavorException, IOException, InterruptedException {
		Object oldData = clipboard.getData(DataFlavor.stringFlavor);
		StringSelection oldContents = null;
		if(oldData != null) {
			oldContents = new StringSelection((String) oldData);
		}
		StringSelection selection = new StringSelection(new String(Character.toString((char)integer)));
	    clipboard.setContents(selection, selection);
	    MrRobot.keyPress(KeyEvent.VK_CONTROL);
	    MrRobot.keyPress(KeyEvent.VK_V);
	    MrRobot.keyRelease(KeyEvent.VK_V);
	    MrRobot.keyRelease(KeyEvent.VK_CONTROL);
	    Thread.sleep(20); //Permet de s'assurer que le CTRL+V à été pressé avant que la ligne suivante ne s'execute
	    if(oldData != null) {
	    	clipboard.setContents(oldContents, selection);
	    }
	}

}
