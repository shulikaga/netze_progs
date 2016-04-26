//
//  Transmitter.c
//  Tansmit
//
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

#include "Transmitter.h"

in_addr_t inet_addr(const char *cp);
struct sockaddr_in serverAddr, clientAddr;
struct sockaddr_storage serverStorage;
socklen_t addr_size, client_addr_size;


Transmitter* newTransmitter(int numberOfPackets, int port, char* ip, int dataSize){
    Transmitter* transmitter = (Transmitter*) malloc(sizeof(Transmitter));
    transmitter->numberOfPackets = numberOfPackets;
    transmitter->port = port;
    transmitter->ip = ip;
    transmitter->dataSize = dataSize;
    
    return transmitter;
}

void start(Transmitter* transmitter){
    openSocket(transmitter);
    sendPackets(transmitter);
    
    
}

void openSocket(Transmitter* transmitter){
    
    /*Create UDP socket*/
    transmitter->client_socket = socket(PF_INET, SOCK_DGRAM, 0);
    
    /*Configure settings in address struct*/
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(transmitter->port);
    serverAddr.sin_addr.s_addr = inet_addr(transmitter->ip);
    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);
    
    /*Initialize size variable to be used later on*/
    addr_size = sizeof serverAddr;

}

void buildPacket(Transmitter* transmitter, int packetNumber){
 //sequentialNr in bytes
 transmitter->buffer[0] = (packetNumber >> 24) & 0xFF;  //   the         //
 transmitter->buffer[1] = (packetNumber >> 16) & 0xFF;  //   packet      //
 transmitter->buffer[2] = (packetNumber >> 8) & 0xFF;   //   sequential  //
 transmitter->buffer[3] = packetNumber & 0xFF;          //   number      //
 transmitter->buffer[4] = 'M';
 transmitter->buffer[5] = 'e';
 transmitter->buffer[6] = 's';
 transmitter->buffer[7] = 's';
 transmitter->buffer[8] = 'a';
 transmitter->buffer[9] = 'g';
 transmitter->buffer[10]= 'e';
    
    
 }

void sendPackets(Transmitter* transmitter){
    int packetNr = 1;
    transmitter->nBytes = (transmitter->dataSize);
    
    while(packetNr < transmitter->numberOfPackets){
        buildPacket(transmitter, packetNr);
        
        sendto(transmitter->client_socket,
               transmitter->buffer,
               transmitter->nBytes,0,
               (struct sockaddr *)&serverAddr,
               addr_size);
        
        packetNr++;
    }
}



