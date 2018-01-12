package util.messages;

import dataserver.DataServer;
import util.Address;

public class ReadReturnMessage extends Message {
	
	private final String seqid, val;
	
	public ReadReturnMessage(Address sender, Address recipient, String reqid, String pcid, String val, String seqid) {
		super(sender, recipient, reqid, DataServer.READ_RECEIPT_FLAG, pcid, val, seqid);
		this.seqid = seqid;
		this.val = val;
	}
	
	public String getSeqID() {
		return this.seqid;
	}
	
	public String getVal() {
		return this.val;
	}
	
}
