package dataserver;


import dataserver.DataServer.OhSamRequestHandler;
import util.messages.*;


/**
 * This object is meant to interpret the messages received by 
 * @author Christian
 *
 */
public class MessageParser {
	
	DataServer server;
	
	public MessageParser(DataServer server) {
		this.server = server;
	}
	
	/**
	 * Parses the last command received from datagram packet and performs the operation designated in the request
	 * @param message
	 */
	protected void parse(Message message) {

		System.out.println("message from\t" + message.sender().addr() + "\t:\t" + message);
		 
		// CHECK TO SEE IF WE'RE ASLEEP
		if (!this.server.awake) {
			if (message.getFlag().equals("wake"))
				this.server.wake();
			return;
		}
		
		
		
		
		
		String flag = message.getFlag();
		
		
		if (message instanceof WriteRequestMessage) {
			this.server.write(
					((WriteRequestMessage) message).getKey(), 
					((WriteRequestMessage) message).getVal(), 
					((WriteRequestMessage) message).getSeqId() + "", 
					((WriteRequestMessage) message).sender(),
					((WriteRequestMessage) message).getReqID() + "");
		}
		else if (message instanceof ReadRequestMessage) {
			this.server.read(
					((ReadRequestMessage) message).getKey(), 
					((ReadRequestMessage) message).sender(),
					((ReadRequestMessage) message).getReqID() + "");
			
		}
		else if (message instanceof OhSamReturnMessage) {
			for (OhSamRequestHandler request : this.server.ohsamrequests)
				if (request.key.equals(((OhSamReturnMessage) message).getKey()))
					request.addResponse(((OhSamReturnMessage) message));
		}
		else if (message instanceof OhSamRequestMessage) {
			this.server.ohsamrelay(message.sender(), ((OhSamRequestMessage) message));
		}
		// Test Cases
		else if (flag.equals("respond"))
			this.server.send(new Message(message.recipient(), message.sender(),"response"));
		
		// Replies back to server 
		// TODO deprecate this
		else if (flag.equals("echo"))
			this.server.send(new Message(
					message.recipient(), 
					message.sender(),  
					message.toString()));
		
		// Sleep functions
		else if (flag.equals("wait"))
			this.server.sleep();
		
		else if (flag.equals("set-delay"))
			this.server.delay = Long.parseLong(message.toString().split(":")[1]);
		
		// TODO more if statements... or case? whatever
		
		
		
		return;
		
	}
}
