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
    
    private byte[] data; // this array holds all data we want to transfer
    private long checksum; // holds CRC32 checksum
    
    public Transmit_Java(final int numPackets, int port, String ip, int dataSize) throws SocketException {
        this.numPackets = numPackets;
        socket = new DatagramSocket();
        socket.setSoTimeout(250);
        this.port = port;
        this.ip = ip;
        this.dataSize = dataSize;
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
        // send all data
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
        int DATA_SIZE = 4;
        
        if (args.length == 4){
            NUMBER_OF_PACKETS = Integer.valueOf(args[0]);
            PORT = Integer.valueOf(args[1]);
            IP = args[2];
            DATA_SIZE = Integer.valueOf(args[3]);
        }
        
        Transmit_Java transmitter = new Transmit_Java(NUMBER_OF_PACKETS, PORT, IP, DATA_SIZE);
        long startTimeStamp = System.currentTimeMillis();
        transmitter.prepareData();
        transmitter.createCRC32();
        transmitter.transfer();
        long duration = (System.currentTimeMillis()-startTimeStamp);
        int sizeInBit = NUMBER_OF_PACKETS * DATA_SIZE * 8;	
        		
        System.out.println("Speed: " + ((sizeInBit) / 1000)/duration + " mbit/s");
    }
    
    
}