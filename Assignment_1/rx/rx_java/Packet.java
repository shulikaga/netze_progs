import java.net.DatagramPacket;

//import java.io.Serializable;

public class Packet{// implements Serializable{
   
    private int receivedSeqNr;
    private byte[] data;
	private  int sentSeqNr;
    private long timeReceived;
    
	private long timeSent;
	private String message;
    
    private int port;
    private int length;

	

	public Packet(int receivedSeqNr, byte[] data,long timeReceived) {
        this.receivedSeqNr = receivedSeqNr;
        this.data = data;
        this.timeReceived = timeReceived;
        this.sentSeqNr = getSentSeqNr(data);
	}



	private int getSentSeqNr(byte[] data2) {
		byte[] seqNmbInBytes = new byte[4];
		System.arraycopy(data2, 0, seqNmbInBytes, 0, 4);

		return toInt(seqNmbInBytes);
	}

	private static int toInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}



	public long getTimeSent() {
		return timeSent;
	}
	
	

	public long getTimeReceived() {
		return timeReceived;
	}


	public int getReceivedSeqNr() {
		return receivedSeqNr;
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