
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 *
 *
 * @author Matthias Reichinger, Ganna Shulika
 *
 */


public class Receive_Java{
    
    final DatagramSocket socket;
    final int dataSize;
    final Set<Integer> receivedSeqNo;
    
    public Receive_Java(final int port, final int dataSize) throws SocketException{
        socket = new DatagramSocket(port);
        this.dataSize = dataSize;
        receivedSeqNo = new HashSet<>();
    }
    
    private void start() throws IOException {
        System.out.println("-------SERVER READY-------------");
        
        while(true){														// +8 is CRC32-cecksum
            DatagramPacket packet = new DatagramPacket(ByteBuffer.allocate(dataSize+8).array(), dataSize+8);
            
            socket.receive(packet);
            //printByteArray(packet.getData());
            
            ByteBuffer bufferData = ByteBuffer.wrap(packet.getData()).order(ByteOrder.BIG_ENDIAN);
            final int seqNmb = bufferData.getInt();
            
            if(seqNmb == 0){
                long crc = bufferData.getLong();
                System.err.println("Last packet with CRC code received: " + crc);
                checkCRC(crc);
                // TODO lokalen crc berechnen und mit uebertragenen crc verlgleichen
            } else{
                System.out.println("Packet received: " + seqNmb);
                receivedSeqNo.add(seqNmb);
            }
            
            socket.send(new DatagramPacket(packet.getData(), packet.getData().length, packet.getAddress(), packet.getPort()));
        }
    }
    
    private void checkCRC(long receivedCrc) {
        
        ByteBuffer allData = ByteBuffer.allocate(dataSize * receivedSeqNo.size());
        for(Integer i : receivedSeqNo) {
            allData = allData.put(ByteBuffer.allocate(dataSize).putInt(i).order(ByteOrder.BIG_ENDIAN).array());
        }
        byte[] data = allData.array();
        
        Checksum checksum = new CRC32();
        
        checksum.update(data, 0, data.length);
        
        System.out.println("CRC generated from received data: " + checksum.getValue());
        
        if(receivedCrc == checksum.getValue()) {
            System.err.println("CRC check ok. Ready for next transfer.");
        } else {
            System.out.println("CRC check failed. Ready for next transfer");
        }
        
        receivedSeqNo.clear();
    }
    
    private void printByteArray(byte[] bytes){
        for (byte b : bytes) {
            System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
        }
    }
    
    public static void main(String[] args) throws Exception {
        int PORT = 7777;
        int DATA_SIZE = 4;
        if (args.length == 2){
            PORT = Integer.valueOf(args[0]);
            DATA_SIZE = Integer.valueOf(args[1]);
        }
        
        Receive_Java receiver = new Receive_Java(PORT, DATA_SIZE);
        receiver.start();
    }	
}