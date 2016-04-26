//
//  Transmitter.h
//  Tansmit

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>

#ifndef Transmitter_h
#define Transmitter_h

typedef struct{
    
    int     numberOfPackets;
    int     port;
    int     dataSize;
    int     client_socket;
    int     nBytes;
    char*   ip;
    char    buffer[200];

}Transmitter;

Transmitter* newTransmitter(int numberOfPackets, int port, char* ip, int dataSize);
void start(Transmitter* transmitter);
void openSocket();
void buildPacket(Transmitter* transmitter, int packetNumber);
void sendPackets(Transmitter* transmitter);
#endif /* Transmitter_h */
