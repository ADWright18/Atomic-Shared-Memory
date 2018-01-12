package util.messages;

import util.Address;

public class OhSamReturnMessage extends Message {

	//<reqid>:<flag>:<pcid>:<seqid>:<key>:<val>
	
	public OhSamReturnMessage(Address sender, Address recipient, String messageParts) {
		super(sender, recipient, messageParts);
	}
	
	public String getKey() {
		return this.parts[4];
	}
	
	public int getSeqid() {
		return Integer.parseInt(this.parts[3]);
	}
	
	public String getVal() {
		return this.parts[5];
	}

}
