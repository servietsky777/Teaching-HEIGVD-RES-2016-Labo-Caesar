package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.cipher.CaesarCipher;
import ch.heigvd.res.caesar.client.*;
import ch.heigvd.res.caesar.protocol.Protocol;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 *
 * @author Antoine Drabble & Andrea Cotza
 */
public class CaesarServer {
	
	private static final Logger LOG = Logger.getLogger(CaesarServer.class.getName());

	/**
	* @param args the command line arguments
	*/
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Server > %5$s%n");
		LOG.info("Caesar server starting on port " + Protocol.SERVER_PORT + "...");
		CaesarServer server = new CaesarServer();
		server.serveClients();
	}

	/**
	 * This method initiates the process. The server creates a socket and binds it
	 * to the previously specified port. It then waits for clients in a infinite
	 * loop. When a client arrives, the server will read its input line by line
	 * and send back the data converted to uppercase. This will continue until the
	 * client sends the "BYE" command.
	 */
	public void serveClients() {
		LOG.info("Starting the Receptionist Worker on a new thread...");
		new Thread(new ReceptionistWorker()).start();
	}

	/**
	 * This inner class implements the behavior of the "receptionist", whose
	 * responsibility is to listen for incoming connection requests. As soon as a
	 * new client has arrived, the receptionist delegates the processing to a
	 * "servant" who will execute on its own thread.
	 */
	private class ReceptionistWorker implements Runnable {

		@Override
		public void run() {
			ServerSocket serverSocket;

			try {
				serverSocket = new ServerSocket(Protocol.SERVER_PORT);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
				return;
			}

			while (true) {
				LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", Protocol.SERVER_PORT);
				try {
					Socket clientSocket = serverSocket.accept();
					LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
					new Thread(new ServantWorker(clientSocket)).start();
				} catch (IOException ex) {
					Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}

	/**
	 * This inner class implements the behavior of the "servants", whose
	 * responsibility is to take care of clients once they have connected. This
	 * is where we implement the application protocol logic, i.e. where we read
	 * data sent by the client and where we generate the responses.
	 */
		private class ServantWorker implements Runnable {

			Socket clientSocket;
			BufferedReader in = null;
			PrintWriter out = null;

			public ServantWorker(Socket clientSocket) {
				try {
					this.clientSocket = clientSocket;
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream());
				} catch (IOException ex) {
					Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			@Override
			public void run() {
				String line;
				boolean shouldRun = true;
				CaesarCipher cipher = new CaesarCipher();
				
				try {
					LOG.info("Sending the key until the client confirms it...");
					do{
						out.println(cipher.getKey());
						out.flush();
					}while((shouldRun) && !in.readLine().equalsIgnoreCase(Protocol.CMD_CONFIRM_KEY));
					LOG.info("Reading until client sends EXIT or closes the connection...");
					while ((shouldRun) && (line = in.readLine()) != null) {
						line = cipher.decryptMessage(line);
						if (line.equalsIgnoreCase("EXIT")) {
							shouldRun = false;
						}
						LOG.info("Received a new message : " + line);
						out.println(cipher.encryptMessage(line));
						out.flush();
					}

					LOG.info("Cleaning up resources...");
					clientSocket.close();
					in.close();
					out.close();

				} catch (IOException ex) {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex1) {
							LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
						}
					}
					if (out != null) {
						out.close();
					}
					if (clientSocket != null) {
						try {
							clientSocket.close();
						} catch (IOException ex1) {
							LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
						}
					}
					LOG.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		}
	}
  
}
