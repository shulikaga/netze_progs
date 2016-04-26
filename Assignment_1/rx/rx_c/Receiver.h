//
//  Receiver.h
//  Tansmit
//
#include "Packet.h"

#ifndef Receiver_h
#define Receiver_h

//------GLOBAL VARIABLES------------//


typedef struct{
    int blockSize;
    int serverTimeout;
    int port;
    int dataSize;
    int udpSocket, nBytes;
    char buffer[200];
    PacketList* pl;

}Receiver;

Receiver *newReceiver(int port,int dataSize, PacketList* packets);
void start(Receiver* receiver);
void openSocket(Receiver* receiver);
void receivePacket(Receiver* receiver, PacketList* packets);


#endif /* Receiver_h */
