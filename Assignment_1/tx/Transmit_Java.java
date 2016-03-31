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

public class Transmit_Java{
   
    final static int SEQ_NR_BYTE_LENGTH = 4;
    
    
    private static DatagramPacket buildPacket(int packetNumber) throws UnknownHostException{
    	 InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
    	 int port = 4712;

        
        byte[] sequentialNr = ByteBuffer.allocate(SEQ_NR_BYTE_LENGTH).putInt(packetNumber).array();
        
        String str ="- Message";
        byte[] mesage = str.getBytes();
        
        byte[] packet = new byte[1024];
        System.arraycopy(sequentialNr, 0, packet, 0, sequentialNr.length);
        System.arraycopy(mesage, 0, packet, sequentialNr.length, mesage.length);
        
        return new DatagramPacket(packet, packet.length, inetAddress, port);
    }
    
    
    private static void sendPackets(DatagramSocket socket, int numberOfPackets)throws IOException{
        
        int packetNumber = 0;
        long timeFirstSent = 0;
        boolean isTimeFirstSent = true;
        boolean isNotMeasured = true;
        
        System.out.println("UDP laeuft...");
        try{
        	while (packetNumber < numberOfPackets){
            
        		DatagramPacket packet = buildPacket(packetNumber);
        		socket.send(packet);
            
        		if(isNotMeasured){
        			timeFirstSent = System.currentTimeMillis();
        			isTimeFirstSent = false;
        		}
        		packetNumber++;
        	}
        	System.out.println(packetNumber + " datagrams have been sent.\n"+
                            "Time the first datagram was sent = " + timeFirstSent + "ms.");
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
    

    public static void main(String args[])throws IOException, UnknownHostException{
        
        DatagramSocket socket = new DatagramSocket();
        
        sendPackets(socket, getNumberOfPackets(args[0]));
        
        socket.close();
        
    }

}