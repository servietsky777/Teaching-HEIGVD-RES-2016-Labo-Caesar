package ch.heigvd.res.caesar.client;

import ch.heigvd.res.caesar.cipher.CaesarCipher;
import ch.heigvd.res.caesar.protocol.Protocol;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.Scanner;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 */
public class CaesarClient {

	private static final Logger LOG = Logger.getLogger(CaesarClient.class.getName());
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean connected = false;
	private Scanner scanner = new Scanner(System.in);
	private int key;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Client > %5$s%n");
		LOG.info("Caesar client starting...");
		CaesarClient client = new CaesarClient();
		LOG.info("Connecting on server " + Protocol.SERVER_ADDRESS + ":" + Protocol.SERVER_PORT);
		client.connect(Protocol.SERVER_ADDRESS, Protocol.SERVER_PORT);
	}

	/**
	 * This inner class implements the Runnable interface, so that the run()
	 * method can execute on its own thread. This method reads data sent from the
	 * server, line by line, until the connection is closed or lost.
	 */
	class NotificationListener implements Runnable {
		@Override
		public void run() {
			String notification;
			try {
				// Set caesar cipher key
				CaesarCipher cipher = new CaesarCipher(Integer.valueOf(in.readLine()));
				LOG.info("Received the key : " + cipher.getKey());
				// Write confirm message
				out.println(Protocol.CMD_CONFIRM_KEY);
				out.flush();
				// Discussion started
				while (connected) {
					String input = scanner.next();
					out.println(cipher.encryptMessage(input));
					out.flush();
					if (input.equalsIgnoreCase("EXIT")) {
						break;
					}
					String response = cipher.decryptMessage(in.readLine());
					LOG.info("Server responded : " + response);
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Connection problem in client used by {1}", new Object[]{e.getMessage()});
				connected = false;
			} finally {
				cleanup();
			}
		}
	}

	/**
	 * This method is used to connect to the server and to inform the server that
	 * the user "behind" the client has a name (in other words, the HELLO command
	 * is issued after successful connection).
	 * 
	 * @param serverAddress the IP address used by the Presence Server
	 * @param serverPort the port used by the Presence Server
	 * @param userName the name of the user, used as a parameter for the HELLO command
	 */
	public void connect(String serverAddress, int serverPort) {
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());
			connected = true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
			cleanup();
			return;
		}
		// Let us start a thread, so that we can listen for server notifications
		new Thread(new NotificationListener()).start();
	}

	public void disconnect() {
		LOG.log(Level.INFO, "{0} has requested to be disconnected.");
		connected = false;
		out.println("BYE");
		cleanup();
	}

	private void cleanup() {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}

		if (out != null) {
			out.close();
		}

		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
  
}
