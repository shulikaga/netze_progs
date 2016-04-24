import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 
 * 
 * @author Matthias Reichinger, Ganna Shulika
 *
 */


public class Receive_Java{

	DatagramSocket socket;

	public Receive_Java(final int port) throws SocketException{
		socket = new DatagramSocket(port);
	}
	
	private void start() throws IOException {
		System.out.println("-------SERVER READY-------------");
		
		while(true){
			DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(4).array(), 4);
			
			socket.receive(packet);
			
			final int seqNmb = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
			//System.out.println("Packet received: " + seqNmb);
			
			socket.send(new DatagramPacket(packet.getData(), packet.getData().length, packet.getAddress(), packet.getPort()));
			
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		final int PORT = 7777;
		Receive_Java receiver = new Receive_Java(PORT);
		receiver.start();
	}


	
}
