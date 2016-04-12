/**
 * Transmit_Java.java
 *
 * this program sends a number of datagrampackets via udp to a specified
 * Internet address and port in blocks of BLOCK_SIZE packets. It puts as the first 4 bytes
 *  a sequence number and from there it puts the message.
 *  After sending a block, it waits for a bitmap of size 1024 bits. If the nth
 *  bit is true, the transfer of the nth datagram packet was successful. After the
 *  block is transfered completely i.e. the bitmap is all true, it sends the next 
 *  block in the same way.
 *
 *@author Matthias Reichinger, Ganna Shulika
 */

import java.net.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class Transmit_Java{
   
    final static int SEQ_NR_BYTE_LENGTH = 4;
    static int BLOCK_SIZE;
    static int PACKET_SIZE;
    static int NUMBER_OF_PACKETS;
    static int PORT;
    final static String IP_ADDRESS = "127.0.0.1";
    
   /**
    * the main method processes the command line parameters, opens a socket,
    * sends all packets lossless via udp, then closes the socket.
    * @param args
    * @throws IOException
    * @throws UnknownHostException
    */
    public static void main(String args[])throws IOException, UnknownHostException{
        NUMBER_OF_PACKETS = getNumberOfPackets(args[0]);
        BLOCK_SIZE = Integer.parseInt(args[1]);
        PORT = Integer.parseInt(args[2]);
        PACKET_SIZE = Integer.parseInt(args[3]);
        
        DatagramSocket socket = new DatagramSocket();
        sendAllPackets(socket);
        socket.close(); 
    }
    
	/**
	 * this method sends ALL packets over UDP in blocks of BLOCK_SIZE 
	 * as quick as possible. For each block, after sending all packets, it waits for a 
	 * bitmap of received packets from server/receiver and then resends the failing packets.
	 * @param socket
	 * @throws IOException
	 */
    private static void sendAllPackets(DatagramSocket socket) throws IOException {
    	boolean[] bitMapReceived = new boolean[BLOCK_SIZE];
        int blockNumber = 0;
        int numberOfBlocks = (int) Math.ceil(NUMBER_OF_PACKETS/(double)BLOCK_SIZE);
        int cycle = 1;
        boolean packetsComplete = true;
        
        while (blockNumber < numberOfBlocks){
        	if (packetsComplete) System.out.println("-----------------block " + blockNumber + " ---------------------");
        	System.out.print(cycle + ": ");
        	
        	sendPacketsOfBlock(socket, bitMapReceived, blockNumber);
        	bitMapReceived = receiveBitmap(socket);
        	packetsComplete = checkIfPacketsComplete(socket, bitMapReceived, numberOfBlocks, blockNumber); 	
    		
    		if (packetsComplete) {
    			cycle = 1;
    			blockNumber++;
    			bitMapReceived = new boolean[BLOCK_SIZE];	
    		}
    		else {
    			cycle++;
    		}
        }
		
	}

    /**
     * This method sends the packets of ONE block as quick as possible over UDP.
     * (It does not check, if the packets are received by the server)
     * 
     * @param socket
     * @param bitMapReceived
     * @param blockNumber
     * @throws IOException
     */
	
	private static void sendPacketsOfBlock(DatagramSocket socket, boolean[] bitMapReceived, int blockNumber)
			throws IOException {
		int packetNumber = blockNumber * BLOCK_SIZE;
		int startingPacketNumber = packetNumber;
		int nmbOfSentPackets = 0;

		try {
			while (packetNumber < startingPacketNumber + BLOCK_SIZE && packetNumber < NUMBER_OF_PACKETS) {
				if (!bitMapReceived[packetNumber % BLOCK_SIZE]) {
					DatagramPacket packet = createDatagramPacket(packetNumber);
					socket.send(packet);
					nmbOfSentPackets++;
				}
				packetNumber++;
			}
			System.out.println(nmbOfSentPackets + " datagrams have been (re)sent.");
		} catch (UnknownHostException e) {
		}
	}
    
    
    /**
     * this method waits on the socket for a new DatagramPacket, which should 
     * encapsulate a bitmap, that is a representation of received datagram packets.
     * It transforms the bitmap to a boolean array and returns it.
     * @param socket
     * @return 
     * @throws IOException
     */
	private static boolean[] receiveBitmap(DatagramSocket socket) throws IOException {
		DatagramPacket incomingDPacket = new DatagramPacket(new byte[128], 128);
		socket.receive(incomingDPacket);
		return toBooleanArray(incomingDPacket.getData());
	}

	/**
	 * this method checks with help of the bitMapReceived, if the server/receiver 
	 * has received all packets of a block
	 * 
	 * @param socket
	 * @param bitMapReceived
	 * @param numberOfBlocks
	 * @param blockNumber
	 * @return true, if all packets received, false otherwise.
	 * @throws IOException
	 */
	private static boolean checkIfPacketsComplete(DatagramSocket socket, boolean[] bitMapReceived, int numberOfBlocks,
			int blockNumber) throws IOException {
		int nmbPacketsInBlock;
		if (numberOfBlocks == blockNumber+1) {nmbPacketsInBlock = NUMBER_OF_PACKETS-((numberOfBlocks-1)*BLOCK_SIZE);}
		else {nmbPacketsInBlock = BLOCK_SIZE;}
		for (int i = 0; i < nmbPacketsInBlock; i++){
			if (!bitMapReceived[i]) {return false;}
		}
		return true;
	}

	/**
	 * this method creates a datagram packet with size PACKET_SIZE. It fills the 
	 * first four bytes of the datagram with the sequence number and the remaining 
	 * space with the message
	 * @param packetNumber
	 * @return the datagram packet
	 * @throws UnknownHostException
	 */
    private static DatagramPacket createDatagramPacket(int packetNumber) throws UnknownHostException{
       byte[] sequentialNr = ByteBuffer.allocate(SEQ_NR_BYTE_LENGTH).putInt(packetNumber).array();
       
       String str ="";
       byte[] message = str.getBytes();
       
       byte[] packet = new byte[PACKET_SIZE];
       System.arraycopy(sequentialNr, 0, packet, 0, sequentialNr.length);
       System.arraycopy(message, 0, packet, sequentialNr.length, message.length);
       
       return new DatagramPacket(packet, packet.length, InetAddress.getByName(IP_ADDRESS), PORT);
   }
   
    
 // helpers
   	
    private static int getNumberOfPackets(String strNumber){
        if(strNumber.matches("-?\\d+(\\.\\d+)?")){
            return Integer.parseInt(strNumber);
        }else{
            System.out.println("Usage: Transmit_Java <number of packets to be sent>");
             System.exit(1);
             return 1;
        }
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