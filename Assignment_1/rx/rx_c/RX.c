//
//  RX.c


#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include "Packet.h"

in_addr_t inet_addr(const char *cp);
int BLOCK_SIZE;
int SERVER_TIMEOUT = 1;
int PORT;
int UDP_SOCKET, NBYTES;
char BUFFER[1024];
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;



void openSocket(){
    /*Create UDP socket*/
    UDP_SOCKET = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(7891);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Bind socket with address struct*/
    bind(UDP_SOCKET, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverStorage;
}

 void receivePackets(PacketList *packets){
    
    int booleanBitMapReceived[BLOCK_SIZE];
    printf("------------------------------------------\n");
    printf("Server is waiting for first datagram...\n");
    printf("------------------------------------------\n");
    
    NBYTES = recvfrom(UDP_SOCKET,BUFFER,1024,0,(struct sockaddr *)&serverStorage, &addr_size);
    int countReceived = 0;
    clock_t t1  = clock();
    long timeReceived = ((long)t1 / 1000000.0F ) * 1000;
    long timeFirstReceived = timeReceived;
    long timeLastReceived = timeReceived;
    
    int sentSeqNr = getSentSeqNr(BUFFER);
    addPacket(packets, sentSeqNr, BUFFER);
    
    // set port from where the DPackets are coming
    int port = ntohs(clientAddr.sin_port);
    
    // set server Socket timeout.
    //server.setSoTimeout(SERVER_TIMEOUT);
    
    // set bitMapReceived
    booleanBitMapReceived[sentSeqNr] = 1;
    
    // after n-times unsuccessfully waiting for new datagrams
    int nmbOfWaitingCircles = 5;
    
    while (nmbOfWaitingCircles > 0) {
        
    
            NBYTES = recvfrom(UDP_SOCKET,BUFFER,1024,0,(struct sockaddr *)&serverStorage, &addr_size);
            countReceived++;
            nmbOfWaitingCircles = 5;
            clock_t t2  = clock();
            timeReceived = ((long)t2 / 1000000.0F ) * 1000;
            timeLastReceived = timeReceived;
            sentSeqNr = getSentSeqNr(BUFFER);
            //packets.put(sentSeqNr, BUFFER);
            
            booleanBitMapReceived[sentSeqNr % BLOCK_SIZE] = 1;
        
        
            // send bitmap back to sender
            char message = itoa(booleanBitMapReceived);
            sendto(openSocket, message, NBYTES,0,(struct sockaddr *)&serverAddr,addr_size);

        
            // check, if this block is complete
            int booleanPacketsComplete = 1;
            for (int i1 = 0; i1 < BLOCK_SIZE && booleanPacketsComplete; i1++){
                if (!booleanBitMapReceived[i1]) {booleanPacketsComplete = 0;}
            }
            if (booleanPacketsComplete) {
                booleanBitMapReceived[BLOCK_SIZE];
            }
            
            nmbOfWaitingCircles--;
        
    }
    printf("%d %s %d %s\n", countReceived+1, " datagrams received: ", (timeLastReceived - timeFirstReceived), "ms");
    printf("%s %d %s", "Speed: ", (int)speedMeasure(timeFirstReceived, timeLastReceived, packets)," mbit/s");
   
}

int getSentSeqNr(char *incomingDPacket) {
    char seqNmbInBytes[4];
    strncpy(seqNmbInBytes, incomingDPacket, 4);
    return atoi(seqNmbInBytes);
}

int main(int argc, char *argv[]){
    
    if (argc < 2){
        fputs ("usage: Receive < BLOCK_SIZE> <PORT> \n", stderr);
        exit (1);
    }
    
    BLOCK_SIZE = atoi(argv[1]);
    PORT = atoi(argv[2]);
    PacketList *packets = newPacketList();
    
    openSocket();
    receivePackets(packets);
    close(UDP_SOCKET);
   
    return 0;
}
