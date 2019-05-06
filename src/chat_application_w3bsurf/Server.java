package chat_application_w3bsurf;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	static Vector<ClientHandler> ar = new Vector<>();
	
	static int i = 0;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(1337);
		
		System.out.println("Server is running...");
		
		Socket s;
		
		// Infinite loop for receiving client requests
		while(true) {
			
			s = ss.accept();
			
			System.out.println("New client request received : " + s);
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			String name;
			boolean taken;
			
			
			System.out.println("Creating new handler for this client...");
			
			while (true) {
				dos.writeUTF("Choose a username:");
				name = dis.readUTF().trim();
				
				if (name.contains("@")) {
					dos.writeUTF("A username may not contain @ characters.");
					continue;
				}
				
				taken = false;
				
				for (ClientHandler mc : Server.ar) {
					if (mc.getName().equals(name)) {
						dos.writeUTF("That username is already taken.");
						taken = true;
					}
				}
				
				if (taken == false) {
					break;
				}	
			}
			
			dos.writeUTF("Welcome " + name + "!");
			
			ClientHandler handler = new ClientHandler(name, dis, dos, s);
			
			Thread t = new Thread(handler);
			
			System.out.println("Adding new client to active client list");
			
			ar.add(handler);
			
			t.start();
			
			i++;
		}
	}
}

class ClientHandler implements Runnable {
	
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	
	public ClientHandler(String name, DataInputStream dis, DataOutputStream dos, Socket s) {
		this.name = name;
		this.dis = dis;
		this.dos = dos;
		this.s = s;
		this.isloggedin = true;
	}
	
	@Override
	public void run() {
		
		String received;
		while (true) {
			
			try {
				received = dis.readUTF();
				
				System.out.println(received);
				
				if(received.equals("/quit")) {
				
					this.dos.writeUTF("See you later " + this.name + "!");
					this.isloggedin = false;
					
					for (ClientHandler mc : Server.ar) {
						if (!mc.getName().contentEquals(this.name) && mc.isloggedin) {
							mc.dos.writeUTF(name + " has left the server.");
						}
					}
					
					this.dis.close();
					Server.ar.remove(this);
					break;
				}
				
				if (received.charAt(0) == '@') {
					String[] message = received.split(" ", 2);
					String recipient = message[0].replace("@", "");
					String MsgToSend = message[1];
					
					int i = 0;
					
					for (ClientHandler mc : Server.ar) {
						
						if (mc.name.equals(recipient) && mc.isloggedin==true) {
							mc.dos.writeUTF(this.name + " : " + MsgToSend);
							i++;
							break;
						}
					}
					
					if (i<1) {
						this.dos.writeUTF("Recipient was not found.");
					}
					
				} else {
					for (ClientHandler mc : Server.ar) {
						
						if (!mc.name.equals(this.name)) {
							mc.dos.writeUTF(this.name + " : " + received);
							break;
						}
					}
				}
					
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
			
		try {
			
			this.dis.close();
			this.dos.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
}
