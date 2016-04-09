import java.net.DatagramPacket;

/**
 * 
 * This class represents a message and a layer
 * for sending data via udp with the specified protocol
 * in Receive_Java.java and Transmit_Java.java
 * 
 * @author Matthias Reichinger, Ganna Shulika
 *
 */

public class Packet{
   
	private  int sentSeqNr;
	private String message;

	public Packet(byte[] data) {
        this.sentSeqNr = getSentSeqNr(data);
	}

	private int getSentSeqNr(byte[] data) {
		byte[] seqNmbInBytes = new byte[4];
		System.arraycopy(data, 0, seqNmbInBytes, 0, 4);

		return toInt(seqNmbInBytes);
	}

	private static int toInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}

    public int getSentSeqNr(){
    	return sentSeqNr;
    }

	public String getMessage() {
		return message;
	}

/*

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		
		if (o == null || getClass() != o.getClass()) return false;
		
		Packet packet = (Packet) o;

		return true;
		
	}
	
	
	
	public int hashCode() {	
		        return seqNr;	
		    }

	
	public String toString() {	
		        return "SeqNr = " + getSeqNr() + " ; Message = " + getMessage();	
		    }

*/
	

}