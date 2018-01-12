package util.messages;

import dataserver.DataServer;
import util.Address;

public class WriteReturnMessage extends Message {
	
	private final String key;
	
	public WriteReturnMessage(Address sender, Address recipient, String reqid, String pcid, String key) {
		super(sender, recipient, reqid, DataServer.WRITE_RECEIPT_FLAG, pcid, key);
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
	
}
