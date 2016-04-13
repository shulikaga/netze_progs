//
//  Transmit.c


#ifndef Transmit_h
#define Transmit_h

#include <stdio.h>
#include <stdlib.h>

#endif /* Transmit_h */

#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

//global variables
in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr;
socklen_t addr_size;
int clientSocket, portNum, nBytes;
int BUFFER_SIZE = 1024;
char buffer[1024];



int getNumberOfPackets(int number){
    if(number!=0){
        return number;
    }else{
        fputs("Usage: Transmit_C <number of packets to be sent>", stderr);
        exit(1);
    }
    
}

void setSocketAddress(){
    /*Create UDP socket*/
    clientSocket = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(4712);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverAddr;
}

void buildPacket(int packetNumber){
   
    //sequentialNr in bytes
    buffer[0] = (packetNumber >> 24) & 0xFF;
    buffer[1] = (packetNumber >> 16) & 0xFF;
    buffer[2] = (packetNumber >> 8) & 0xFF;
    buffer[3] = packetNumber & 0xFF;
    buffer[4] = 'M';
    buffer[5] = 'e';
    buffer[6] = 's';
    buffer[7] = 's';
    buffer[8] = 'a';
    buffer[9] = 'g';
    buffer[10] = 'e';
    
}

void sendPackets(int numberOfPackets){
    int packetNumber = 1;
    printf("UPD lauft...\n");
    while(packetNumber <= numberOfPackets){
       
        buildPacket(packetNumber);//typing a message
        
        sendto(clientSocket,buffer,nBytes,0,(struct sockaddr *)&serverAddr,addr_size);
        
        packetNumber++;
    }
    printf("%d have been sent to port %d %\n",packetNumber-1, serverAddr.sin_port);
    
}



int main(int argc, char *argv[]){
    
    NUMBER_OF_PACKETS = getNumberOfPackets(args[1]);
    BLOCK_SIZE = Integer.parseInt(args[2]);
    PORT = Integer.parseInt(args[3]);
    PACKET_SIZE = Integer.parseInt(args[4]);
    
    // setSocketAddress();

    // sendPackets(atoi(argv[1]));
    
   // close(clientSocket);

    
    return 0;
}