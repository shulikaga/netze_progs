/**
 * Transmit_Java.java
 *
 * this program sends a number of datagrampackets via udp to a specified
 * internet adress and port in blocks of 1024 packets. It puts as the first 4 bytes
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
    
    public static void main(String args[])throws IOException, UnknownHostException{
        DatagramSocket socket = new DatagramSocket();
        
        int numberOfPackets = getNumberOfPackets(args[0]);
        boolean[] bitMapReceived = new boolean[1024];
        int blockNumber = 0;
        int numberOfBlocks = (int) Math.ceil(numberOfPackets/1024.0);
        
        while (blockNumber < numberOfBlocks){
        	sendPackets(socket, numberOfPackets, bitMapReceived, blockNumber);
        	
    		DatagramPacket incomingDPacket = new DatagramPacket(new byte[128], 128);
    		//System.out.println("Warte auf Antwort vom Server...");
    		socket.receive(incomingDPacket);
    		//System.out.println("--> Bitmap zurück erhalten"); 
    		bitMapReceived = toBooleanArray(incomingDPacket.getData());
    		
    		// check if all packets of block sent successfully
    		boolean packetsComplete = true;
    		int nmbPacketsInBlock;
    		if (numberOfBlocks == blockNumber+1) {nmbPacketsInBlock = numberOfPackets-((numberOfBlocks-1)*1024);}
    		else {nmbPacketsInBlock = 1024;}
    		for (int i = 0; i < nmbPacketsInBlock && packetsComplete; i++){
    			if (!bitMapReceived[i]) {packetsComplete = false;}
    		}
    		if (packetsComplete) {
    			blockNumber++;
    			System.out.println("-----------------block " + blockNumber + " ---------------------");
    		}
        }
        socket.close(); 
    }
    
    
    private static DatagramPacket buildPacket(int packetNumber) throws UnknownHostException{
    	 InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
    	 int port = 4712;

        byte[] sequentialNr = ByteBuffer.allocate(SEQ_NR_BYTE_LENGTH).putInt(packetNumber).array();
        
        String str ="- Message";
        byte[] message = str.getBytes();
        
        byte[] packet = new byte[1024];
        System.arraycopy(sequentialNr, 0, packet, 0, sequentialNr.length);
        System.arraycopy(message, 0, packet, sequentialNr.length, message.length);
        
        return new DatagramPacket(packet, packet.length, inetAddress, port);
    }
    
    																						
    private static void sendPackets(DatagramSocket socket, int numberOfPackets, boolean[] bitMapReceived, int blockNumber)throws IOException{
        
        int packetNumber = blockNumber * 1024;
        int startingPacketNumber = packetNumber;
        long timeFirstSent = 0;
        boolean isNotMeasured = true;
        int nmbOfSentPackets = 0;
        
        try{
        	while (packetNumber < startingPacketNumber + 1024 && packetNumber < numberOfPackets){
        		
				if (! bitMapReceived[packetNumber % 1024]) {
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