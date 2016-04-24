import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Transmit_Java.java
 *
 * this program sends a number of datagrampackets via udp to a specified
 * Internet address and port in blocks of BLOCK_SIZE packets. It puts as the
 * first 4 bytes a sequence number and from there it puts the message. After
 * sending a block, it waits for a bitmap of size 1024 bits. If the nth bit is
 * true, the transfer of the nth datagram packet was successful. After the block
 * is transfered completely i.e. the bitmap is all true, it sends the next block
 * in the same way.
 *
 * @author Matthias Reichinger, Ganna Shulika
 */

public class Transmit_Java {

	private final int numPackets;
	private final DatagramSocket socket;
	private final int port;
	private final String ip;
	
	public Transmit_Java(final int numPackets, int port, String ip) throws SocketException {
		this.numPackets = numPackets;
		socket = new DatagramSocket();
		this.port = port;
		this.ip = ip;
	}

	public void start() throws IOException {
		/*
		 * für alle Pakete: solange Pakte noch nicht bestätigt do: sende Paket
		 * warte maximal 250 ms auf Ack Ack erhalten Ja: nextes Paket Nein:
		 * Sende erneut (bleibe in while Schleife)
		 * 
		 */

		

		for (int i = 0; i < numPackets; i++) {

			//while (true) {
				sendPacket(i);
				receiveAck(i);
			//}
		}
		
		
		
		
		 socket.close();
	}
	
	private void receiveAck(int i) throws IOException {
		DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(4).array(), 4);
		
		socket.receive(packet);
		
		final int seqNmb = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
		System.out.println("Packet Ack received: " + seqNmb);
		
	}

	private void sendPacket(int i) throws IOException {
	        
		
		byte[] data = ByteBuffer.allocate(4).putInt(i).order(ByteOrder.BIG_ENDIAN).array();
		
		DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
		socket.send(p);
		
	        
	}

	public static void main(String[] args) throws Exception {
		final int NUMBER_OF_PACKETS = 10000;
		final int PORT = 7777;
		final String IP = "127.0.0.1";
		Transmit_Java transmitter = new Transmit_Java(NUMBER_OF_PACKETS, PORT, IP);
		transmitter.start();
	}

}