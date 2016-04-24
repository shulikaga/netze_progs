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

	final DatagramSocket socket;
	final int dataSize;

	public Receive_Java(final int port, final int dataSize) throws SocketException{
		socket = new DatagramSocket(port);
		this.dataSize = dataSize;
	}
	
	private void start() throws IOException {
		System.out.println("-------SERVER READY-------------");
		
		while(true){
			DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(dataSize).array(), dataSize);
			
			socket.receive(packet);
		
			final int seqNmb = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
			System.out.println("Packet received: " + seqNmb);
			
			socket.send(new DatagramPacket(packet.getData(), packet.getData().length, packet.getAddress(), packet.getPort()));	
		}	
	}
	
	public static void main(String[] args) throws Exception {
		final int PORT = Integer.valueOf(args[0]);
		final int DATA_SIZE = Integer.valueOf(args[1]);
		
		Receive_Java receiver = new Receive_Java(PORT, DATA_SIZE);
		receiver.start();
	}	
}
