
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This program receives a number of datagrampackets lossless via udp 
 * in blocks of BLOCK_SIZE datagrampackets. It stores the packets and
 * prints out the message, as well as the sequence number
 * of the received packets, their size, the time needed for
 * sending, etc.
 * After receiving a number of datagrampackets, it sends back a bitmap
 * of BLOCK_SIZE bits. It sets the n-th bit as true, if the n-th datagram in this
 * block was received. After the block is transfered completely i.e. the bitmap 
 * is all true, it waits and receives the next block the same way.
 * 
 * @author Matthias Reichinger, Ganna Shulika
 *
 */


public class Receive_Java{

	static int BLOCK_SIZE;
	final static int SERVER_TIMEOUT = 1;
	static int PORT;
	
	
	/**
	 * the main method processes the command line arguments, opens a socket,
	 * receives the datagramPackets, and then closes the server.
	 * 
	 * @param args[0] the BLOCK_SIZE: the size of the portions, in which a sending will be sent (max. 1024)
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(String args[]) throws ClassNotFoundException, IOException {
		BLOCK_SIZE = Integer.parseInt(args[0]);
		PORT = Integer.parseInt(args[1]);
		//ArrayList<Packet> packets = new ArrayList<>();
		Map<Integer, byte[]> packets = new HashMap <Integer,byte[]>();
		
		DatagramSocket server = new DatagramSocket(PORT);
		receiveDPackets(server, packets);
		server.close();
	}
	
	/**
	 * this method receives the first datagram and saves it in a Map. It then performs in the same way
	 * with the flow of continuously incoming datagrams. At the point, where the flow stops for SERVER_TIMEOUT ms
	 * it sends back to sender a bitmap that reflects, if the n-th datagram is already received.
	 * After this, it awaits the remaining datagrams and so forth.
	 * 
	 * @param server
	 * @param packets
	 * @throws IOException
	 */
	private static void receiveDPackets(DatagramSocket server, Map<Integer, byte[]> packets) throws IOException {

		boolean[] bitMapReceived = new boolean[BLOCK_SIZE];
		
		System.out.println("------------------------------------------");
		System.out.println("Server is waiting for first datagram...");
		System.out.println("------------------------------------------");
		
		DatagramPacket incomingDPacket = new DatagramPacket(new byte[1024], 1024);
		
		// wait and receive first datagram
		server.receive(incomingDPacket);
		int countReceived = 0; 
		
		long timeReceived = System.currentTimeMillis();
		long timeFirstReceived = timeReceived;
		long timeLastReceived = timeReceived;
		
		int sentSeqNr = getSentSeqNr(incomingDPacket);
		packets.put(sentSeqNr, incomingDPacket.getData());
		
		// set port from where the DPackets are coming
		int sourceport = incomingDPacket.getPort();
		
		// set server Socket timeout.
		server.setSoTimeout(SERVER_TIMEOUT);
		
		// set bitMapReceived
		bitMapReceived[sentSeqNr] = true;
		
		// after n-times unsuccessfully waiting for new datagrams
		int nmbOfWaitingCircles = 5;
		
		while (nmbOfWaitingCircles > 0) {
			//System.out.println("------------------------------------------");
			//System.out.println("Server is waiting for incoming datagram...");
			//System.out.println("------------------------------------------");

			try {
				server.receive(incomingDPacket);
				
				countReceived++;
				nmbOfWaitingCircles = 5;
				timeReceived = System.currentTimeMillis();
				timeLastReceived = timeReceived;
				sentSeqNr = getSentSeqNr(incomingDPacket);
				packets.put(sentSeqNr, incomingDPacket.getData());
				
				bitMapReceived[sentSeqNr % BLOCK_SIZE] = true;
				
				//print(incomingDPacket, countReceived + 1);
			
			} catch (SocketTimeoutException e) {
				// send bitmap back to sender
				byte[] message = toByteArray(bitMapReceived);
				DatagramPacket answer = new DatagramPacket(message, message.length, incomingDPacket.getAddress(), sourceport);
				server.send(answer);
				
				// check, if this block is complete
				boolean packetsComplete = true;
	    		for (int i1 = 0; i1 < BLOCK_SIZE && packetsComplete; i1++){
	    			if (!bitMapReceived[i1]) {packetsComplete = false;}
	    		}
	    		if (packetsComplete) {
	    			bitMapReceived = new boolean[BLOCK_SIZE];
	    		}
				
				nmbOfWaitingCircles--;
			}
		}
		System.out.println(countReceived+1 + " datagrams received: " + (timeLastReceived - timeFirstReceived) + "ms");
		System.out.println("Speed: " + (int)speedMeasure(timeFirstReceived, timeLastReceived, packets) + " mbit/s");
	}


	private static double speedMeasure(long timeFirstReceived, long timeLastReceived, Map<Integer, byte[]> packets) {
		long durationMs = timeLastReceived - timeFirstReceived;
		int sizeInByte = 0;
		for (byte[] value : packets.values()) {
			sizeInByte = sizeInByte + value.length;
		}
		return (((double) (sizeInByte * 8) / 1000) / durationMs);
	}

	/**
	 * this method extracts the sequence Number (first four bytes) from the
	 * incoming datagram packet
	 * 
	 * @param incomingDPacket
	 * @return
	 */
	private static Integer getSentSeqNr(DatagramPacket incomingDPacket) {
		byte[] seqNmbInBytes = new byte[4];
		System.arraycopy(incomingDPacket.getData(), 0, seqNmbInBytes, 0, 4);

		return toInt(seqNmbInBytes);
	}

	/**
	 * this method prints out some data from the incoming datagram packet
	 * 
	 * @param incomingDPacket
	 * @param nmbOfReceivedDPackets
	 */
	private static void print(DatagramPacket incomingDPacket, int nmbOfReceivedDPackets) {
		int port = incomingDPacket.getPort();
		int len = incomingDPacket.getLength();

		System.out.println("--> " + nmbOfReceivedDPackets + " th DatagramPacket received from " + port);
		// System.out.println("Received Sequence Number in this sending: " +
		// packet.getReceivedSeqNr());
		System.out.println("Sent Sequence Number in this sending: " + getSentSeqNr(incomingDPacket));

		System.out.println("Address:         " + incomingDPacket.getAddress() + "\n" + "Port:  " + port + "\n"
				+ "Length:  " + len + " byte\n" + "Sending time interval: ");
		// + (packet.getTimeReceived() - packet.getTimeSent()) + "ms\n");
	}

// helpers

	private static int toInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	static boolean[] toBooleanArray(byte[] bytes) {
	    BitSet bits = BitSet.valueOf(bytes);
	    boolean[] bools = new boolean[bytes.length * 8];
	    for (int i = bits.nextSetBit(0); i != -1; i = bits.nextSetBit(i+1)) {
	        bools[i] = true;
	    }
	    return bools;
	}

	static byte[] toByteArray(boolean[] bools) {
	    BitSet bits = new BitSet(bools.length);
	    for (int i = 0; i < bools.length; i++) {
	        if (bools[i]) {
	            bits.set(i);
	        }
	    }
	    return bits.toByteArray();
	}
	
}
