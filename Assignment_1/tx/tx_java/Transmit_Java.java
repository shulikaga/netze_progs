package tx_java;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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
    private final int dataSize;
    private final int windowSize;
    private int roundTripTime;
    private byte[] data; // this array holds all data we want to transfer
    private long checksum; // holds CRC32 checksum
    
    public Transmit_Java(final int numPackets, int port, String ip, int dataSize, int windowSize, int roundTripTime) throws SocketException {
        this.numPackets = numPackets;
        socket = new DatagramSocket();
        this.port = port;
        this.ip = ip;
        this.dataSize = dataSize;
        this.windowSize = windowSize;
        this.roundTripTime = roundTripTime;
        socket.setSoTimeout(roundTripTime);
    }
    
    public void prepareData() {
        final ByteBuffer allData = ByteBuffer.allocate(dataSize * numPackets);
        for(int i = 1; i <= numPackets; i++){
            allData.put(ByteBuffer.allocate(dataSize).putInt(i).order(ByteOrder.BIG_ENDIAN).array());
        }
        data = allData.array();
    }
    
    public void createCRC32() {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        this.checksum = checksum.getValue();
    }
    
    public void transfer() throws IOException {
        // send first windowsize-1 datapackets
    	for (int i = 0; i < windowSize-1; i++) {
    	sendPacket(i);
    	}
    	// create window and a counter
    	boolean[] window = new boolean[windowSize];
    	int count = windowSize-1; // number of packets in the window that have no ack (-1 because we start as like before shifting)
    	
    	// send rest of the data
    	for (int i = windowSize-1; i < numPackets + windowSize-1; i++) {
    		if(i < numPackets){
    			sendPacket(i);
    		}
    		//"shift" window
    		window[i % windowSize] = false; 
    		count++;
    		int leftmost = (i-(windowSize-1)) % windowSize;
    		
    		// was the first packet in the window sent again?
    	    boolean resent = false;
    	    
    		while(!window[leftmost]){
    			if(!resent){socket.setSoTimeout(((roundTripTime/windowSize)*count)+1);}
    			try {
                    int seqNmb = receiveAck(); // our seqNmbs start with "1"!!
                    if(seqNmb > i-windowSize ){ // for the case, that an ack from a resent packet comes very late after all  
                    	window[(seqNmb-1) % windowSize] = true;
                        count--;
    					}
                }
                catch (SocketTimeoutException e){
                    System.out.println("No Ack for " + (i-(windowSize-2)) + " received. Trying again.");
                    sendPacket((i-(windowSize-1)));
                    socket.setSoTimeout(roundTripTime);
                    resent = true;
                }
    		}	
    	}

        // send CRC32 checksum
        sendCRC32();
        socket.close();
    }
    

	private void sendCRC32() throws IOException {
        byte[] crcPacket = ByteBuffer.allocate(dataSize + 8).putInt(0).putLong(checksum).order(ByteOrder.BIG_ENDIAN).array();
        System.out.println("crc is: " + checksum);
        boolean send = true;
        while(send){
            socket.send(new DatagramPacket(crcPacket, crcPacket.length, InetAddress.getByName(ip), port));
            
            try{
                receiveAck(0);
                send = false;
            }
            catch (SocketTimeoutException e){
                System.out.println("No CRC32 received. Trying again.");
            }
        }
        
    }
    
    private int receiveAck() throws IOException {
        DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(dataSize).array(), dataSize);
        
        socket.receive(packet);
        
        final int seqNmb = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
        System.out.println("Packet Ack received: " + seqNmb);
        return seqNmb;
    }
    
    private void receiveAck(int i) throws IOException {
        DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(dataSize).array(), dataSize);
        
        socket.receive(packet);
        
        final int seqNmb = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN).getInt();
        System.out.println("Packet Ack received: " + seqNmb);
        
    }
    
    private void sendPacket(int i) throws IOException {
        byte[] packetData = Arrays.copyOfRange(data, i*dataSize, i*dataSize+dataSize);
        DatagramPacket p = new DatagramPacket(packetData, packetData.length, InetAddress.getByName(ip), port);
        socket.send(p);
    }
    
    private void printByteArray(byte[] bytes){
        for (byte b : bytes) {
            System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
        }
    }
    
    public static void main(String[] args) throws Exception {
        int NUMBER_OF_PACKETS = 10000;
        int PORT = 7777;
        String IP = "127.0.0.1";
        int DATA_SIZE = 128;
        int WINDOW_SIZE = 10;
        int ROUND_TRIP_TIME = 25;
        
        if (args.length == 6){
            NUMBER_OF_PACKETS = Integer.valueOf(args[0]);
            PORT = Integer.valueOf(args[1]);
            IP = args[2];
            DATA_SIZE = Integer.valueOf(args[3]);
            WINDOW_SIZE = Integer.valueOf(args[4]);
            ROUND_TRIP_TIME = Integer.valueOf(args[5]);
        }
        
        Transmit_Java transmitter = new Transmit_Java(NUMBER_OF_PACKETS, PORT, IP, DATA_SIZE, WINDOW_SIZE, ROUND_TRIP_TIME);
        long startTimeStamp = System.currentTimeMillis();
        transmitter.prepareData();
        transmitter.createCRC32();
        transmitter.transfer();
        long duration = (System.currentTimeMillis()-startTimeStamp);
        int sizeInBit = NUMBER_OF_PACKETS * DATA_SIZE * 8;	
        		
        System.out.println("Speed: " + ((sizeInBit) / 1000.0)/duration + " mbit/s");
    }
    
    
}