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



public class Transmit_Java{
   
   
	
}