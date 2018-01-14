package dataserver;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import util.Address;
import util.messages.*;

/**
 * This is the abstract object that outlines what a DataServer might look like. The abstract portion of this object is
 * the read() and write(), which are subject to change due to the nature of storing our data; it is possible to store
 * data to and read data from the disk or memory, so the abstraction has been made to accommodate subclasses that specify
 * which method is preferred.
 * 
 * 
 * 
 * @author Christian
 *
 */
public abstract class DataServer {

	public ArrayList<OhSamRequestHandler> ohsamrequests = new ArrayList<OhSamRequestHandler>();
	
	public final static String 
		WRITE_RECEIPT_FLAG = "write-return",
		WRITE_REQUEST_FLAG = "write-request",
		READ_RECEIPT_FLAG = "read-return",
		READ_REQUEST_FLAG = "read-request",
		OHSAM_REQUEST_FLAG = "ohsam-relay",
		OHSAM_RECEIPT_FLAG = "ohsam-return",
		WAIT_COMMAND_FLAG = "wait",
		WAKE_COMMAND_FLAG = "wake";
	
	public final Address localAddress;
	
	protected boolean awake = true;
	
	/**
	 * This is the list of other data servers in the network
	 */
	public final int port, id;
	public long delay = 0;
	public final String algorithm;
	private Address[] addresses;
	
	protected int seqcount = 0;
	public DatagramSocket soc;

	private final static int REFRESH = 10000000;

	/**
	 * The primary constructor for a DataServer object.
	 * @param serverid	The identification number that distinguishes this DataServer from other DataServers
	 * @param ADDRESSES	The other servers in the network; this object is stored as a volatile array and can be updated
	 * @param port	The port at the local address that this object should listen to for UDP messages
	 * @throws UnknownHostException 
	 */
	public DataServer(int serverid, int port, String address, String algorithm, Address[] addresses) throws SocketException, UnknownHostException {

	
		this.id = serverid;
		this.port = port;
		this.algorithm = algorithm;
		this.addresses = addresses;

		
		try {
			this.soc = new DatagramSocket(port, InetAddress.getByName(address));
			System.out.println("Data Server " + this.id + " created: "
					+ "\n\t" + "Port: " + this.soc.getLocalPort()
					+ "\n\t" + "Addr: " + this.soc.getLocalAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.localAddress = new Address(this.soc.getLocalAddress(), this.soc.getLocalPort());

	}

	/**
	 * Delegates port listening to a thread so that methods in this object can be called while it is listening.
	 * Note that the object "thread" is a MessageListenerThread, which is a superclass abstraction of the class
	 * SocketListenerThread. For now, the DataServer will receive messages through UDP messages on a port at the
	 * local address, but it has been expressed to the group that this might not be the case for future versions
	 * of the project.
	 * 
	 * The reasoning behind making this a thread as opposed to a while loop is because this object might listen to
	 * several different objects at the same time that should be checked simultaneously instead of in sequence
	 * 
	 * TODO change this if the method of receiving messages changes
	 */
	public Thread start() throws BindException {
		
		
		
		MessageListenerThread thread = new SocketListenerThread(this, this.soc, DataServer.REFRESH);
		thread.start();
		return thread;
		
	}

	
	protected abstract void read(String key, Address returnAddress, String reqid);
	
	
	/**
	 * Does this server's write algorithm
	 * 
	 * @param key	The key of the key value pair
	 * @param value	The value of the key value pair
	 * @param timestamp	The timestamp showing the freshness of this value
	 * @param returnAddress	The IP/port combination this message came from; used for sending receipts
	 */
	protected void write(String key, String value, String timestamp, Address returnAddress, String reqid) {
		System.out.println("doing a write");
		if (this.algorithm.equals("ohsam")) {
			System.out.println("doing ohsam");
			OhSamRequestHandler newRequest = new OhSamRequestHandler(key, value, timestamp, returnAddress, reqid, this.addresses, this);
			this.ohsamrequests.add(newRequest);
			newRequest.start();
		}
		else
			this.commitData(key, value, timestamp, returnAddress, reqid);
	}
	public abstract void ohsamrelay(Address returnAddress, OhSamRequestMessage message);
	
	protected abstract void commitData(String key, String value, String timestamp, Address returnAddress, String reqid);
	
	/**
	 * Sends a message. The recipient of this message is stored in the Message object
	 * @param message The message to be sent
	 */
	protected void send(Message message) {
		System.out.println("Sending to\t" + message.recipient().addr() + "\t:\t" + message.toString());
		try {
			Address recip = message.recipient();
			this.soc.send(new DatagramPacket(message.toString().getBytes(), message.toString().getBytes().length, recip.addr(), recip.port()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			
			e.printStackTrace();
		}
	}
	
	public void close() {
		this.soc.close();
	}

	public void sleep() {
		this.awake = false;
	}
	public void wake() {
		this.awake = true;
	}

	
	public class OhSamRequestHandler extends Thread {
		
		int count = 0, maxcount;
		String finalValue;
		
		String key, value, timestamp, reqid;
		Address returnAddress;
		Address[] addresses;
		DataServer server;
		
		public OhSamRequestHandler(String key, String value, String timestamp, Address returnAddress, String reqid, Address[] addresses, DataServer server) {
			this.key = key;
			this.value = value;
			this.timestamp = timestamp;
			this.returnAddress = returnAddress;
			this.reqid = reqid;
			this.addresses = addresses;
			
			this.server = server;
			
			this.maxcount = (((int) (this.addresses.length / 2)) + 1);
			
		}
		
		public void run() {
			System.out.println(this.server.localAddress == null);
			for (Address serverAddress : this.addresses) {
				this.server.send(new OhSamRequestMessage(this.server.localAddress, serverAddress, this.reqid + ":ohsam-relay:" + this.server.id + ":" + this.timestamp + ":" + this.key));
			}
		}
		
		public void addResponse(OhSamReturnMessage message) {
			
			if (message.getSeqid() > Integer.parseInt(this.timestamp)) {
				this.value = message.getVal();
				this.timestamp = message.getSeqid() + "";
				this.reqid = message.getReqID() + "";
			}
			
			if (++count == this.maxcount) {
				this.server.commitData(this.key, this.value, this.timestamp, this.returnAddress, this.reqid);
				this.server.send(new ReadReturnMessage(this.server.localAddress, this.returnAddress, this.reqid, this.server.id + "", this.value, this.timestamp));
			}
				
		}
		
		
	}
	

}
