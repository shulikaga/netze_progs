
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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

	public static void main(String args[]) throws ClassNotFoundException, SocketException {
		
			final int MAX_NUMBER_OF_PACKETS_TO_BE_RECEIVED = Integer.parseInt(args[0]);
			long timeFirstReceived = 0;
			List<Packet> packets = new ArrayList<>();// to save all the datagrams
			DatagramSocket server = new DatagramSocket(4712);
			
			long timeLastReceived = 0;
			int i = 0;
			
			try {
			    
				while (i < MAX_NUMBER_OF_PACKETS_TO_BE_RECEIVED) {// waiting to receive a datagram
					System.out.println("Server is waiting for incoming datagram...");

					// create paket container for receiving data
					DatagramPacket incomingDPacket = new DatagramPacket(new byte[1024], 1024); 
					
					// wait and receive Data
					server.receive(incomingDPacket);
					
					// received time measuring
					long timeReceived = System.currentTimeMillis();
					if (timeFirstReceived == 0) timeFirstReceived = timeReceived;
					timeLastReceived = timeReceived;
					
                    byte[] data = incomingDPacket.getData();

					// extract first 4 bytes of the DatagramPacket (this is not the sequence number
					// that is stored in the "packet"-Object. Of course, the two numbers
					// should be equal, because they were initially copied)
					//int seqNr = getSequenceNumber(incomingDPacket); // not in use
                    Packet packet = new Packet(i, data, timeReceived);//getPacketObject(incomingDPacket);
					
					
					// add to list
					packets.add(packet);
					
					// print send/receive-info
					print(incomingDPacket, packet, i + 1);
					
					i++;
				}

			} catch (Exception e) {
				System.out.println(e);
			}

			System.out.println("Time " + i + " datagrams received: " + (timeLastReceived - timeFirstReceived) + "ms");
	}

	private static void print(DatagramPacket incomingDPacket, Packet packet, int nmbOfReceivedDPackets ) {
		int port = incomingDPacket.getPort();
		int len = incomingDPacket.getLength();
		
		System.out.println("RX received new DatagramPacket from " + port);
		System.out.println("Number of Received DatagramPackets: " + nmbOfReceivedDPackets);
		//System.out.println("Sequence Number: " + packet.getSeqNr());
		//System.out.println("Message: " + packet.getMessage());

        System.out.println("Address: " + incomingDPacket.getAddress() + "\n" + "Port:  " + port + "\n" + "Length:  " + len);
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
