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
    int     server_socket;
    int     nBytes;
    char*   ip;
    char**  allPackets;

}Transmitter;

Transmitter* newTransmitter(int numberOfPackets, int port, char* ip, int dataSize);
void freeTransmitterArrays(Transmitter* transmitter);
void start(Transmitter* transmitter);
void openSocket(Transmitter* transmitter);
void buildPacket(Transmitter* transmitter, char* buffer,int packetNumber);
void sendPackets(Transmitter* transmitter);
void sendOnePacket(Transmitter* transmitter, int packetNr, int times,int numberOfPackets);
int receiveAck(Transmitter* transmitter, int packetNumber, struct sockaddr_in serverAddr);
void putCRC32(Transmitter* transmitter,char* buffer, int packetNumber, int start);
void printbinchar(char* buffer, int start, int end);
void printData(char* buffer, int seqNr);
void messageToString(char* buffer, int start, int end);
void get4Bytes(Transmitter* transmitter,char* buffer,int packetNumber, int start);
#endif /* Transmitter_h */
