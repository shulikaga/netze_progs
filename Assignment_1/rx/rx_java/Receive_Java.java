
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

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
	static int SERVER_TIMEOUT;
	/**
	 * the main method waits for the first datagram and saves it and also the continuously following
	 * datagrams in an Arraylist of packets. At the point, where the flow stops for SERVER_TIMEOUT ms
	 * it sends back to sender a bitmap that reflects, if the n-th datagram is already received.
	 * After this, it awaits the remaining datagrams and so forth.
	 * 
	 * If a sequence number comes twice, this indicates a new sending. A new packets list will be created.
	 * 
	 * @param args[0] the BLOCK_SIZE: the size of the portions, in which a sending will be sent (max. 1024)
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(String args[]) throws ClassNotFoundException, IOException {
		SERVER_TIMEOUT = 5;
		BLOCK_SIZE = Integer.parseInt(args[0]);
		long timeFirstReceived = 0;
		ArrayList<Packet> packets = new ArrayList<>();// to save a sequence of packets
		List<ArrayList<Packet>> sendings = new ArrayList<ArrayList<Packet>>(); // to save sequences of packets
		sendings.add(packets);
		// the Datagrams will be sent in blocks of BLOCK_SIZE datagrams. this bitmap reflects, if
		// the n-th datagram is already received "bitmapReceived[n] = true"
		boolean[] bitMapReceived = new boolean[BLOCK_SIZE];
		int sourceport; // where to send back the bitMapReceived
		DatagramSocket server = new DatagramSocket(4712);
		
		System.out.println("------------------------------------------");
		System.out.println("Server is waiting for first datagram...");
		System.out.println("------------------------------------------");
		
		// create paket container for receiving data
		DatagramPacket incomingDPacket = new DatagramPacket(new byte[1024], 1024);
		
		// wait and receive first datagram
		server.receive(incomingDPacket);
		int countDPacketsReceived = 0;
		
		// received time measuring
		long timeReceived = System.currentTimeMillis();
		timeFirstReceived = timeReceived;
		long timeLastReceived = timeReceived;
		
		// create packet an add it to the packetslist of this sending
		Packet packet = new Packet(countDPacketsReceived, incomingDPacket.getData(), timeReceived);
		packets.add(packet);
		
		// set port from where the DPackets are coming
		sourceport = incomingDPacket.getPort();
		
		// set server Socket timeout.
		server.setSoTimeout(SERVER_TIMEOUT);
		
		// set bitMapReceived
		bitMapReceived[packet.getSentSeqNr()] = true;
		
		// after n-times unsuccessfully waiting for new datagrams
		int nmbOfWaitingCircles = 5;
		
		while (nmbOfWaitingCircles > 0) {
			System.out.println("------------------------------------------");
			System.out.println("Server is waiting for incoming datagram...");
			System.out.println("------------------------------------------");

			try {
				// wait and receive Data
				server.receive(incomingDPacket);
				countDPacketsReceived++;
				
				// reset waiting circles
				nmbOfWaitingCircles = 5;
				
				// received time measuring
				timeReceived = System.currentTimeMillis();
				timeLastReceived = timeReceived;
				
				// get packet and add to list
				byte[] data = incomingDPacket.getData();
				packet = new Packet(countDPacketsReceived, data, timeReceived);
				if (bitMapReceived[packet.getSentSeqNr() % BLOCK_SIZE] == false){
					packets.add(packet);
					bitMapReceived[packet.getSentSeqNr() % BLOCK_SIZE] = true;
				}
				else {// if a packet with this seqNr has already arrived, this indicates a new sending
					packets = new ArrayList<>();
					sendings.add(packets);
					packets.add(packet);
					bitMapReceived = new boolean[BLOCK_SIZE];
					bitMapReceived[packet.getSentSeqNr() % BLOCK_SIZE] = true;	
				}
				
				print(incomingDPacket, packet, countDPacketsReceived + 1);
			
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
	    		if (packetsComplete) {//todo: if last backmassage of a block gets lost--> problem
	    			bitMapReceived = new boolean[BLOCK_SIZE];
	    		}
				
				nmbOfWaitingCircles--;
				continue;
			}
		}
		server.close();
		System.out.println(countDPacketsReceived+1 + " datagrams received: " + (timeLastReceived - timeFirstReceived) + "ms");
			
	}

	private static void print(DatagramPacket incomingDPacket, Packet packet, int nmbOfReceivedDPackets ) {
		int port = incomingDPacket.getPort();
		int len = incomingDPacket.getLength();
		
		System.out.println("--> " + nmbOfReceivedDPackets + " th DatagramPacket received from " + port);
	    System.out.println("Received Sequence Number: " + packet.getReceivedSeqNr());
	    System.out.println("Sent Sequence Number: " + packet.getSentSeqNr());
	    System.out.println("Message:         " + packet.getMessage());

        System.out.println("Address:         " + incomingDPacket.getAddress()) ;
        		//+ "\n" + "Port:  " + port + "\n" + "Length:  " + len);
				//+ " byte\n" + "Sending time interval: "
				//+ (packet.getTimeReceived() - packet.getTimeSent()) + "ms\n");
	}

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
