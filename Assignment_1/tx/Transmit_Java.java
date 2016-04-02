/**
 * Transmit_Java.java
 *
 * this program sends a number of datagrampackets via udp to a specified
 * internet adress and port. It puts as the first 4 bytes a sequence number,
 * and from there it puts a package-object.
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
    
    																						// the 0st, 1st, 2nd, ... (one block = 1024 packets)
    private static boolean sendPackets(DatagramSocket socket, int numberOfPackets, BitSet bitSetReceived, int blockNumber)throws IOException{
        
        int packetNumber = blockNumber * 1024;
        int startingPacketNumber = packetNumber;
        long timeFirstSent = 0;
        boolean isTimeFirstSent = true;
        boolean isNotMeasured = true;
        boolean sentAtLeastOnePacket = false; // todo: redundant glaub ich. weggeben, braucht man nicht mehr
        
        System.out.println("UDP laeuft...");
        try{
        	while (packetNumber < startingPacketNumber + 1024 && packetNumber < numberOfPackets){
        		
				if (!bitSetReceived.get(packetNumber % 1024)) {
					DatagramPacket packet = buildPacket(packetNumber);
					socket.send(packet);
					sentAtLeastOnePacket = true;

					if (isNotMeasured) {
						timeFirstSent = System.currentTimeMillis();
						isTimeFirstSent = false;
					}
				}
        		packetNumber++;
        	}
        	System.out.println(packetNumber + " datagrams have been sent.\n"+
                            "Time the first datagram was sent = " + timeFirstSent + "ms.");
         }	catch(UnknownHostException e){}
        
        return sentAtLeastOnePacket;
        
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
    

    public static void main(String args[])throws IOException, UnknownHostException{
        
        DatagramSocket socket = new DatagramSocket();
        
        int numberOfPackets = getNumberOfPackets(args[0]);
        BitSet bitSetReceived = new BitSet(1024);
        int blockNumber = 0;
        int numberOfBlocks = (int) Math.ceil(numberOfPackets/1024.0);
        
        while (blockNumber < numberOfBlocks){
        	
        	sendPackets(socket, numberOfPackets, bitSetReceived, blockNumber );
        	
    		DatagramPacket incomingDPacket = new DatagramPacket(new byte[1024], 1024);
    		System.out.println("Warte auf Datagram");
    		socket.receive(incomingDPacket);
    		System.out.println("Datagram zurück erhalten");
    		bitSetReceived = BitSet.valueOf(incomingDPacket.getData());
    		for (int i = 0; i < 1024; i++){
    			System.out.print(bitSetReceived.get(i));
    		}
    		
    		boolean packetsMissing = false;
    		for (int i = 0; i < 1024 && !packetsMissing; i++){
    			if (!bitSetReceived.get(i)) {packetsMissing = true;}
    		}
    		if (!packetsMissing) blockNumber++;
        	
        }
        
        socket.close();
        
    }

}