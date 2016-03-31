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