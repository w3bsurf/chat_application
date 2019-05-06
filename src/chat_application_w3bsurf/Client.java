package chat_application_w3bsurf;

import java.net.*;
import java.util.*;
import java.io.*;

public class Client {

	final static int ServerPort = 1337;
	static boolean isloggedin = true;
	
	public static void main(String[] args) throws IOException {
		
		Scanner scn = new Scanner(System.in);
		
		InetAddress ip = InetAddress.getByName("localhost");
		
		Socket s = new Socket(ip, ServerPort);
		
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		
		Thread sendMessage = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					
					String msg = scn.nextLine().trim();
					
					if (!msg.isEmpty()) {
						try {
							
							dos.writeUTF(msg);
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
		
		Thread readMessage = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					
					try {
						
						String msg = dis.readUTF();
						System.out.println(msg);
						
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
