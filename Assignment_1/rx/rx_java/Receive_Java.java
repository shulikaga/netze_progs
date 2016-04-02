
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * this program receives a number of datagrampackets via udp, stores it and
 * prints out the message, as well as the sequence number
 * of the received packets, their size, the time needed for
 * sending, etc
 * @author Matthias Reichinger, Ganna Shulika
 *
 */

public class Receive_Java{

	public static void main(String args[]) throws ClassNotFoundException, IOException {
		
		//final int MAX_NUMBER_OF_PACKETS_TO_BE_RECEIVED = Integer.parseInt(args[0]);
		long timeFirstReceived = 0;
		List<Packet> packets = new ArrayList<>();// to save all the received packets
		BitSet bitSetReceived = new BitSet(1024); 
		int sourceport;
		DatagramSocket server = new DatagramSocket(4712);
		
		System.out.println("------------------------------------------");
		System.out.println("Server is waiting for first datagram...");
		System.out.println("------------------------------------------");
		
		// create paket container for receiving data
		DatagramPacket incomingDPacket = new DatagramPacket(new byte[1024], 1024);
		
		// wait and receive Data
		server.receive(incomingDPacket);
		
		// received time measuring
		long timeReceived = System.currentTimeMillis();
		timeFirstReceived = timeReceived;
		
		int i = 0;
		byte[] data1 = incomingDPacket.getData();
		
		Packet packet = new Packet(i, data1, timeReceived); // getPacketObject(incomingDPacket);
		packets.add(packet);
		
		// set port from where the DPackets are coming
		sourceport = incomingDPacket.getPort();
		
		// set server Socket timeout.
		server.setSoTimeout(1000);
		
		long timeLastReceived = timeReceived;
		
		// set bitSetReceived
		bitSetReceived.set(i);
		
		int nmbOfWaitingCircles = 5;
		
		i++;
		while (nmbOfWaitingCircles > 0) {// waiting to receive
															// a datagram
			System.out.println("------------------------------------------");
			System.out.println("Server is waiting for incoming datagram...");
			System.out.println("------------------------------------------");

			try {
				// wait and receive Data
				server.receive(incomingDPacket);
				
				// reset waiting circles
				nmbOfWaitingCircles = 5;
				
				// received time measuring
				timeReceived = System.currentTimeMillis();
				timeLastReceived = timeReceived;

				byte[] data = incomingDPacket.getData();

				// extract first 4 bytes of the DatagramPacket (this is not the
				// sequence number
				// that is stored in the "packet"-Object. Of course, the two
				// numbers
				// should be equal, because they were initially copied)
				// int seqNr = getSequenceNumber(incomingDPacket); // not in use
				packet = new Packet(i, data, timeReceived);// getPacketObject(incomingDPacket);

				// add to list
				packets.add(packet);

				// set bitSetReceived
				bitSetReceived.set(packet.getSentSeqNr());
				
				// print send/receive-info
				print(incomingDPacket, packet, i + 1);

				i++;
			} catch (SocketTimeoutException e) {
				// send bitset back to sender
				byte[] message = new byte[1024];
				message = bitSetReceived.toByteArray();
				DatagramPacket answer = new DatagramPacket(message, message.length, incomingDPacket.getAddress(), sourceport);
				server.send(answer);
				
				boolean packetsMissing = false;
	    		for (int i1 = 0; i1 < bitSetReceived.length() && !packetsMissing; i1++){
	    			if (!bitSetReceived.get(i1)) {packetsMissing = true;}
	    		}
	    		if (!packetsMissing) bitSetReceived = new BitSet(1024); //todo: if last backmassage of a block gets lost--> problem
				
				nmbOfWaitingCircles--;
				continue;
			}
		}

			System.out.println("Time " + i + " datagrams received: " + (timeLastReceived - timeFirstReceived) + "ms");
	}

	private static void print(DatagramPacket incomingDPacket, Packet packet, int nmbOfReceivedDPackets ) {
		int port = incomingDPacket.getPort();
		int len = incomingDPacket.getLength();
		
		System.out.println("--> " + nmbOfReceivedDPackets + " th DatagramPacket received from " + port);
	    System.out.println("Sequence Number: " + packet.getReceivedSeqNr());
	    System.out.println("Message:         " + packet.getMessage());

        System.out.println("Address:         " + incomingDPacket.getAddress()) ;
        		//+ "\n" + "Port:  " + port + "\n" + "Length:  " + len);
				//+ " byte\n" + "Sending time interval: "
				//+ (packet.getTimeReceived() - packet.getTimeSent()) + "ms\n");
	}

	private static Packet getPacketObject(DatagramPacket incomingDPacket) throws IOException, ClassNotFoundException {
		byte[] packetData = new byte[incomingDPacket.getLength() - 4];
		System.arraycopy(incomingDPacket.getData(), 4, packetData, 0, packetData.length);
		ByteArrayInputStream in = new ByteArrayInputStream(packetData);
		ObjectInputStream is = new ObjectInputStream(in);

		return (Packet) is.readObject();
	}
	

	private static int getSequenceNumber(DatagramPacket incomingDPacket) {
				byte[] seqNmbInBytes = new byte[4];
				System.arraycopy(incomingDPacket.getData(), 0, seqNmbInBytes, 0, 4);
				
		return toInt(seqNmbInBytes);
	}

	private static int toInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
}
