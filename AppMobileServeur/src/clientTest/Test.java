package clientTest;

import java.io.PrintWriter;
import java.net.Socket;

public class Test {

	public static void main(String[] args) {
		try {
			Socket sock = new Socket("localhost", 33555);
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			for(int i = 100 ; i < 101 ; ++i) {
				Thread.sleep(3000);
				out.println(i);
			}
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
