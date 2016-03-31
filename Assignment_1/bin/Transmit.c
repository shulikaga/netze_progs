//
//  Transmit.c


#ifndef Transmit_h
#define Transmit_h

#include <stdio.h>

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



int getNumberOfPackets(char[] *strNumber){
    int number = atoi(strNumber);
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
    serverAddr.sin_port = htons(7891);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverAddr;
}

void buildPacket(int packetNumber){
   
    //sequentialNr in bytes
    buffer[0] = (n >> 24) & 0xFF;
    buffer[1] = (n >> 16) & 0xFF;
    buffer[2] = (n >> 8) & 0xFF;
    buffer[3] = n & 0xFF;
    
    int i;
    char** message = "- Message.";
    //message in bytes
    for(i = 4;i<message.length;i++){
        buffer[i] = message[i-4];
    }
    
}

void sendPackets(int numberOfPackets){
    int packetNumber = 0;
    //long timeFirstSent = 0;
    //int isNotMeasured = 1;
   
    
    while(packetNumber < numberOfPackets){
        printf("UPD lauft...");
        buildPacket(packetNumber);//typing a message
        
        nBytes = strlen(buffer) + 1;
        
        sendto(clientSocket,buffer,nBytes,0,(struct sockaddr *)&serverAddr,addr_size);
        
        packetNumber++;
    }
    
    System.out.println("UDP laeuft...");
}



int main(int argc, char *argv[]){
    
     setSocketAddress();

     sendPackets(getNumberOfPackets(args[1]));
    
    close(clientSocket);

    
    return 0;
}