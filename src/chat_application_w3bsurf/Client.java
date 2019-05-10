package chat_application_w3bsurf;

import java.net.*;
import java.util.*;
import java.io.*;

public class Client {

	final static int ServerPort = 1337;
	static boolean isloggedin = true;
	
	public static void main(String[] args) throws IOException {
		
		Scanner scn = new Scanner(System.in);
		// Client gets ip address of localhost and connects
		InetAddress ip = InetAddress.getByName("localhost");
		Socket s = new Socket(ip, ServerPort);
		
		// Create data input and output streams
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		
		// Create new thread to asynchronously send messages to server
		Thread sendMessage = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					String msg = scn.nextLine().trim();
					
					if (!msg.isEmpty()) {
						try {
							// Sends message to server using data output stream
							dos.writeUTF(msg);
							// If sent messages is "/quit" logout and stop thread
							if (msg.equals("/quit")) {
								isloggedin = false;
								break;
							}
						} catch (IOException i) {
							i.printStackTrace();
						}
					} else {
						continue;
					}
				}
			}
		});
		
		// Create new thread to asynchronously read messages from server
		Thread readMessage = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					try {
						// Read message from server using data input stream
						String msg = dis.readUTF();
						System.out.println(msg);
						
						// If client has logged out, stop thread
						if (isloggedin == false) {
							break;
						}
					} catch (IOException i) {
						i.printStackTrace();
					}
				}	
			}
		});
		
		readMessage.start();
		sendMessage.start();
		
	}
}