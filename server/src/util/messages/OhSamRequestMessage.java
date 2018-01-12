package util.messages;

import util.Address;
/**
 * <reqid>:<flag>:<pcid>:<seqid>:<key>
 * @author Christian
 *
 */
public class OhSamRequestMessage extends Message {

	
	
	public OhSamRequestMessage(Address sender, Address recipient, String messageParts) {
		super(sender, recipient, messageParts);
	}
	
	public String getKey() {
		return this.parts[4];
	}
	

}
