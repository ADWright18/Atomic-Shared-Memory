package main;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import dataserver.MemoryDataServer;
import util.Address;

/**
 * 
 * Version 1.0.0 - One-To-Many capable
 * Implemented a -h for command line running
 * Implemented sleep functions for testing
 * 		* wait puts the server to sleep until it is woken up
 * 		* wake wakes the server up from being asleep
 * 
 * Version 0.1.1
 * Ability to give the server an IP address from command line arguments
 * 
 * 
 * Version 0.1.0
 * Storage of data through memory, not disk
 * Reception of messages through listening to a port and receiving UDP packets
 * 
 * 
 * 
 * @author Christian
 *
 */
public class Main {
	
	public static final String OHSAM_FLAG = "ohsam";
	
	public static void main(String[] args) {
		
		// address the developer last had on their machine so they could run the project from the IDE and not
		// the terminal
		
		String address, algorithm;
		int port;
		String[] addresses = {""};
		
		if (args.length == 0) {
			 address = "172.27.165.33";
			 port = 2000;
			 algorithm = "adb";
		}
		
		else if (args[0].equals("-h") || args[0].equals("--h") || args[0].equals("help")) {
			System.out.println(helpString());
			System.exit(0);
			return;
		}
		else {
			address = args[0];
			port = Integer.parseInt(args[1]);
			algorithm = args[2];
			
			if (algorithm.equals("ohsam")) {
				addresses = args[3].split(";");
			}
			
		}


		
		process(address, port, algorithm, addresses);
		
		
	
		


	}
	private static Address getAddressFromPair(String pair) {
		InetAddress inet;
		int port;
		
		if (pair.length() == 0)
			return null;
		
		String[] parts = pair.split(":");
		try {
			inet = InetAddress.getByName(parts[0]);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: '" + parts[0] + "' is not a valid IP Address");
			return null;
		}
		port = Integer.parseInt(parts[1]);
		
		return new Address(inet, port);
	}
	public static void process(String address, int port, String algorithm, String[] addressesStr) {
		
		MemoryDataServer server;
		
		Address[] addresses = new Address[addressesStr.length];
		
		for (int i = 0; i < addresses.length; i++)
			addresses[i] = getAddressFromPair(addressesStr[i]);
		
		
		try {
			server = new MemoryDataServer(0, port, address, algorithm, addresses);
			Thread serverThread = server.start();
			serverThread.join();
			server.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
			process(address, port + 1, algorithm, addressesStr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String helpString() {
		String out =
				"Run this in the form java Main <address> <port>"
						+ "\n" + "-h:			display this help message";
		
		
		return out;	
	}
}
