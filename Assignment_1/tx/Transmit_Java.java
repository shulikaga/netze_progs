/**
 * Transmit_Java.java
 *
 * this program sends a number of datagrampackets via udp to a specified
 * internet adress and port in blocks of BLOCK_SIZE packets. It puts as the first 4 bytes
 *  a sequence number and from there it puts the message.
 *  After sending a block, it waits for a bitmap of size 1024 bits. If the n-th
 *  bit is true, the transfer of the n-th datagrampacket was successfull. After the
 *  block is tranfered completely i.e. the bitmap is all true, it sends the next 
 *  block in the same way.
 *
 *@author Matthias Reichinger, Ganna Shulika
 */

import java.lang.*;
import java.net.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class Transmit_Java{
   
    final static int SEQ_NR_BYTE_LENGTH = 4;
    static int BLOCK_SIZE;
    final static int PACKET_SIZE = 1024;
    
    public static void main(String args[])throws IOException, UnknownHostException{
        DatagramSocket socket = new DatagramSocket();
        int numberOfPackets = getNumberOfPackets(args[0]);
        System.out.println(numberOfPackets);
        BLOCK_SIZE = Integer.parseInt(args[1]);
        
        
        if (numberOfPackets >= 0) {
        	sendPackets(socket, numberOfPackets, BLOCK_SIZE);
        }
        else {
        	sendPackets(socket, 100, BLOCK_SIZE);
        	sendPackets(socket, 1000, BLOCK_SIZE);
        	sendPackets(socket, 10000, BLOCK_SIZE);
        }   
        socket.close(); 
    }
    
    
    private static void sendPackets(DatagramSocket socket, int numberOfPackets, int bLOCK_SIZE2) throws IOException {
    	boolean[] bitMapReceived = new boolean[BLOCK_SIZE];
        int blockNumber = 0;
        int numberOfBlocks = (int) Math.ceil(numberOfPackets/(double)BLOCK_SIZE);
        int cycle = 1;
        boolean packetsComplete = true;
        
        while (blockNumber < numberOfBlocks){
        	if (packetsComplete) System.out.println("-----------------block " + blockNumber + " ---------------------");
        	System.out.print(cycle + ": ");
        	sendPacketsOfBlock(socket, numberOfPackets, bitMapReceived, blockNumber);
        	
    		DatagramPacket incomingDPacket = new DatagramPacket(new byte[128], 128);
    		socket.receive(incomingDPacket);
    		bitMapReceived = toBooleanArray(incomingDPacket.getData());
    		
    		// check if all packets of block sent successfully
    		packetsComplete = true;
    		int nmbPacketsInBlock;
    		if (numberOfBlocks == blockNumber+1) {nmbPacketsInBlock = numberOfPackets-((numberOfBlocks-1)*BLOCK_SIZE);}
    		else {nmbPacketsInBlock = BLOCK_SIZE;}
    		for (int i = 0; i < nmbPacketsInBlock && packetsComplete; i++){
    			if (!bitMapReceived[i]) {packetsComplete = false;}
    		}
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


	private static DatagramPacket buildPacket(int packetNumber) throws UnknownHostException{
    	 InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
    	 int port = 4712;

        byte[] sequentialNr = ByteBuffer.allocate(SEQ_NR_BYTE_LENGTH).putInt(packetNumber).array();
        
        String str ="- Message";
        byte[] message = str.getBytes();
        
        byte[] packet = new byte[PACKET_SIZE];
        System.arraycopy(sequentialNr, 0, packet, 0, sequentialNr.length);
        System.arraycopy(message, 0, packet, sequentialNr.length, message.length);
        
        return new DatagramPacket(packet, packet.length, inetAddress, port);
    }
    
    																						
    private static void sendPacketsOfBlock(DatagramSocket socket, int numberOfPackets, boolean[] bitMapReceived, int blockNumber)throws IOException{
        
        int packetNumber = blockNumber * BLOCK_SIZE;
        int startingPacketNumber = packetNumber;
        long timeFirstSent = 0;
        boolean isNotMeasured = true;
        int nmbOfSentPackets = 0;
        
        try{
        	while (packetNumber < startingPacketNumber + BLOCK_SIZE && packetNumber < numberOfPackets){
        		
				if (! bitMapReceived[packetNumber % BLOCK_SIZE]) {
					DatagramPacket packet = buildPacket(packetNumber);
					socket.send(packet);
					nmbOfSentPackets++;
					if (isNotMeasured) {
						timeFirstSent = System.currentTimeMillis();
					}
				}
        		packetNumber++;
        	}
        	System.out.println(nmbOfSentPackets + " datagrams have been sent.");
         }	catch(UnknownHostException e){}   			
    }
    
    
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