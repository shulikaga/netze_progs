import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Transmit_Java.java
 *
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
		socket.setSoTimeout(250);
		this.port = port;
		this.ip = ip;
	}

	public void start() throws IOException {
		for (int i = 0; i < numPackets; i++) {
			boolean send = true;
			while (send) {
				sendPacket(i);
				try {
					receiveAck(i);
					send = false;
				}
				catch (SocketTimeoutException e){
					System.out.println("No Ack for " + i + " received. Trying again.");
				}
			}
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
		int NUMBER_OF_PACKETS = 10000;
		int PORT = 7777;
		String IP = "127.0.0.1";
		
		if (args.length == 3){
			NUMBER_OF_PACKETS = Integer.valueOf(args[0]);
			PORT = Integer.valueOf(args[1]);
			IP = args[2];
		}
		
		Transmit_Java transmitter = new Transmit_Java(NUMBER_OF_PACKETS, PORT, IP);
		long startTimeStamp = System.currentTimeMillis();
		transmitter.start();
		System.out.println("Duration in ms: " + (System.currentTimeMillis()-startTimeStamp));
	}

}